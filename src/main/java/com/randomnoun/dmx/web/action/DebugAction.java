package com.randomnoun.dmx.web.action;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.randomnoun.common.Struct;
import com.randomnoun.common.ErrorList;
import com.randomnoun.common.ExceptionUtils;
import com.randomnoun.common.Text;
import com.randomnoun.common.log4j.MemoryAppender;
import com.randomnoun.common.security.Permission;
import com.randomnoun.common.security.SecurityContext;
import com.randomnoun.common.timer.Benchmark;
import com.randomnoun.dmx.config.AppConfig;
//import com.randomnoun.facebook.dataAccess.WebClientDA;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Debugging action that exposes various application internals.
 * 
 * This action always returns the 'success' forward
 * 
 * @author knoxg
 * @version $Id$
 */
public class DebugAction extends Action {

	/** Logger instance for this class */
	public static Logger logger = Logger.getLogger(DebugAction.class);

	/** Hopefully generic JMX functions */
    public synchronized MBeanServer getMBeanServer() {
        long t1=System.currentTimeMillis();
        MBeanServer server = null;
        if( MBeanServerFactory.findMBeanServer(null).size() > 0 ) {
            server=(MBeanServer)MBeanServerFactory.findMBeanServer(null).get(0);
            if( logger.isDebugEnabled() ) {
            	logger.debug("Using existing MBeanServer " + (System.currentTimeMillis() - t1 ));
            }
        } else {
            server = ManagementFactory.getPlatformMBeanServer();
            if( logger.isDebugEnabled() ) {
            	logger.debug("Creating MBeanServer "+ (System.currentTimeMillis() - t1 ));
            }
        }
        return (server);
    }
	
	public static class PermissionComparator implements Comparator {
		/** 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			Permission p1 = (Permission) o1;
			Permission p2 = (Permission) o2;
			int r1 = p1.getResource().compareTo(p2.getResource());
			if (r1==0) {
				return p1.getActivity().compareTo(p2.getActivity());
			} else {
				return r1;
			}
		}
	
	}

		
    public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception 
    {
        AppConfig appConfig = AppConfig.getAppConfig();
        ErrorList errors = new ErrorList();
        
        String debugTab = request.getParameter("debugTab");
        String action = request.getParameter("action");
        String forward = "success";
        
        if (debugTab==null) {
        	debugTab = "attributes";
        }
        
        if (debugTab.equals("attributes")) {
        	// request.setAttribute("appConfigAttributes", appConfig);
        } else if (debugTab.equals("security")) {

            SecurityContext context = appConfig.getSecurityContext();
            
            if (request.getParameter("resetContext")!=null) {
                appConfig.getSecurityContext().resetSecurityContext();
                request.setAttribute("resetContextResult", "Security context has been reset. Users may need to logout before any changes take effect");
            }

			if (request.getParameter("authUser") != null) {
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				boolean result = appConfig.getSecurityContext().authenticate(username, password);
				if (result == true) {
					request.setAttribute("loginResult", "Login successful");
				} else {
					request.setAttribute("loginResult", "Login failed");
				}
				request.setAttribute("username", username);
				request.setAttribute("password", password);
			}

            
			List permissions = context.getAllPermissions();
			Collections.sort(permissions, new PermissionComparator());
			
			request.setAttribute("permissions", permissions);			
			request.setAttribute("users", context.getAllUsers(false)); // true=return permission data
			request.setAttribute("roles", context.getAllRoles());
			request.setAttribute("resources", context.getAllResources());
        	
        } else if (debugTab.equals("benchmark")) {
			List benchmarks = new ArrayList(appConfig.getBenchmarks());  
        	if (!Text.isBlank(request.getParameter("clearBenchmarks"))) {
        		benchmarks.clear();
        		appConfig.getBenchmarks().clear();
        	}
        	
            Collections.sort(benchmarks, new Benchmark.BenchmarkComparator());
            
            List output = new ArrayList(benchmarks.size());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

            for (Iterator i = benchmarks.iterator(); i.hasNext(); ) {
                Benchmark benchmark = (Benchmark)i.next();
                String string = "<tr><td colspan=3 valign=top><b>" + benchmark.getId() +
                    (benchmark.isActive() ? " <span style=\"color:red\">(ACTIVE)</span>"
                                          : "") + "</b>";
                long firstDate = benchmark.getCheckpointList().get(0).getDate().getTime();
                Iterator j = benchmark.getCheckpointList().iterator();
                Benchmark.Checkpoint checkpoint = (Benchmark.Checkpoint)j.next();
                long lastDate = checkpoint.getDate().getTime();

                string += "<tr><td valign=top><td class='benchTD' valign=top>" + checkpoint.getId() +
                  "<td nowrap>" + dateFormat.format(checkpoint.getDate());
                while (j.hasNext()) {
                    checkpoint = (Benchmark.Checkpoint)j.next();
                    long thisDate = checkpoint.getDate().getTime();
                    if (lastDate != 0) {
                        string += "<td valign=top>" + (thisDate - lastDate) + "<td valign=top>" +
                        (thisDate - firstDate);
                    } else {
                        string += "<td valign=top><td valign=top> ";
                    }

                    lastDate = thisDate;
                    string += "<tr><td valign=top><td class='benchTD' valign=top>" + checkpoint.getId() +
                      "<td nowrap valign=top>" + dateFormat.format(checkpoint.getDate());
                }

                // String string = benchmark.getId() + benchmark.getCheckpointList().toString();
                // logger.debug("adding benchmark: " + string);
                output.add(string.trim());
                
            }
            request.setAttribute("benchmarks", output);
            
        } else if (debugTab.equals("logging")) {
			request.setAttribute("className", "(enter classname here...)");
			String source = (String) request.getSession().getAttribute("debug.logging.source");
            if ("setLevel".equals(action)) {
                String className = request.getParameter("className");
                String level = request.getParameter("level");
                Logger logger = null;
                try {
					logger = className.equals("root") ? Logger.getRootLogger() : Logger.getLogger(className);
					logger.setLevel(Level.toLevel(level));
                } catch (Exception e) {
                    errors.addError("Error setting log4j logger", e.getMessage());
                    request.setAttribute("errors", errors);
                }
                request.setAttribute("className", className);
                request.setAttribute("level", level);
            } else if ("setSource".equals(action)) {
				source = request.getParameter("source");            		
            }
            request.getSession().setAttribute("debug.logging.source", source);
            
            List appenders = new ArrayList();
            List loggers = new ArrayList();
            List levels = new ArrayList();
            for (Enumeration e = LogManager.getRootLogger().getAllAppenders(); e.hasMoreElements(); ) {
                Appender appender = (Appender) e.nextElement();
                Map appMap = new HashMap();
                appMap.put("name", appender.getName());
                appMap.put("type", Text.getLastComponent(appender.getClass().getName()));
                if (appender instanceof AppenderSkeleton) {
                    Priority priority = ((AppenderSkeleton) appender).getThreshold();
                    appMap.put("threshold", priority == null ? "(null)" : priority.toString());
                }
                appenders.add(appMap);
            }
            for (Enumeration e = LogManager.getCurrentLoggers(); e.hasMoreElements(); ) {
                Logger tmpLogger = (Logger) e.nextElement();
                Map loggerMap = new HashMap();
                loggerMap.put("name", tmpLogger.getName());
                loggerMap.put("effectiveLevel", tmpLogger.getEffectiveLevel().toString());
                loggerMap.put("level", tmpLogger.getLevel() == null ? "" : tmpLogger.getLevel().toString());
                loggerMap.put("additive", String.valueOf(tmpLogger.getAdditivity()));
                loggerMap.put("parent", tmpLogger.getParent() == null ? "" : tmpLogger.getParent().getName());
                String appString = "";
                for (Enumeration e2 = tmpLogger.getAllAppenders(); e2.hasMoreElements(); ) {
                    appString = appString += "," + ((Appender) e2.nextElement()).getName();
                }
                loggerMap.put("appenders", appString);
                // could call .getAllAppenders() on each logger here...
                loggers.add(loggerMap);
            }
            Struct.sortStructuredList(loggers, "name");
            
            levels.add(Level.OFF.toString());
            levels.add(Level.FATAL.toString());
            levels.add(Level.ERROR.toString());
            levels.add(Level.WARN.toString());
            levels.add(Level.INFO.toString());
            levels.add(Level.DEBUG.toString());
            levels.add(Level.ALL.toString());
            
            request.setAttribute("repositoryName", appConfig.getProperty("meridian.repositoryName"));
            request.setAttribute("appenders", appenders);
            request.setAttribute("loggers", loggers);
            request.setAttribute("levels", levels);
            request.setAttribute("source", source);
        
        } else if (debugTab.equals("eventLog")) {
			List newEvents = new ArrayList();
			
            MemoryAppender memoryAppender = (MemoryAppender) Logger.getRootLogger().getAppender("MEMORY");
            if (memoryAppender!=null) {
				if (!Text.isBlank(request.getParameter("clearEventLog"))) {
					memoryAppender.clear();
				}
	            List loggingEvents = memoryAppender.getLoggingEvents();
	            for (Iterator i = loggingEvents.iterator(); i.hasNext(); ) {
	                LoggingEvent event = (LoggingEvent)i.next();
	                Map newEvent = new HashMap();
	                newEvent.put("timestamp", new Date(event.timeStamp));
	                newEvent.put("username", event.getMDC("username"));
	                newEvent.put("level", event.getLevel());
	                newEvent.put("message", event.getMessage());
	
	                if (event.getThrowableInformation() != null) {
	                    newEvent.put("stackTrace",
	                        ExceptionUtils.getStackTraceWithRevisions(event.getThrowableInformation().getThrowable(),
	                          this.getClass().getClassLoader(), ExceptionUtils.HIGHLIGHT_TEXT, "com.randomnoun."
	                        ));
	                }
	                newEvents.add(newEvent);
	            }
            }
            request.setAttribute("events", newEvents);

        } else if (debugTab.equals("webClient")) {
        	/*
        	// @TODO add filtering to this page
			List events = WebClientDA.getEvents();
			for (Iterator i = events.iterator(); i.hasNext(); ) {
				Map event = (Map) i.next();
				event.put("requestTime", new Date(((Number) event.get("requestTime")).longValue()));
			}
			request.setAttribute("events", events);
        	*/
        } else if (debugTab.equals("jmx")) {
        	MBeanServer mBeanServer = getMBeanServer();
        	// from the tomcat source tree
        	/*
        	String qry = "*:*";	
            Set names=mBeanServer.queryNames(new ObjectName(qry), null);
            //writer.println("OK - Number of results: " + names.size());
            Iterator it=names.iterator();
            while( it.hasNext()) {
                ObjectName oname=(ObjectName)it.next();
                MBeanInfo minfo=mBeanServer.getMBeanInfo(oname);
                String code=minfo.getClassName();
                if ("org.apache.commons.modeler.BaseModelMBean".equals(code)) {
                    code=(String)mBeanServer.getAttribute(oname, "modelerType");
                }
                //writer.println("modelerType: " + code);
                MBeanAttributeInfo attrs[]=minfo.getAttributes();
                Object value=null;
                for( int i=0; i< attrs.length; i++ ) {
                    if( ! attrs[i].isReadable() ) continue;
                    String attName=attrs[i].getName();
                    if( attName.indexOf( "=") >=0 ||
                            attName.indexOf( ":") >=0 ||
                            attName.indexOf( " ") >=0 ) {
                        continue;
                    }
                    try {
                        value=mBeanServer.getAttribute(oname, attName);
                    } catch( Throwable t) {
                        logger.error("Error getting attribute " + oname + " " + attName + " ", t);
                        continue;
                    }
                    if( value==null ) continue;
                    if( "modelerType".equals( attName)) continue;
                    String valueString=value.toString();
                    //writer.println( attName + ": " + escape(valueString));
                }
            }
            */

			Map objectNames = getObjectNames(mBeanServer);
			// System.out.println(Struct.structuredMapToString("result", objectNames));
			
			
			if ("getNodes".equals(action)) {
				ObjectName objectName;
				String path = request.getParameter("path");
				String[] pathEls = path.split(";");
				int pathIndex = 1;
				while (pathIndex < pathEls.length) {
					String pathEl = pathEls[pathIndex];
					logger.debug("-- navigating through " + pathEl);
					Object object = objectNames.get(pathEl);
					if (object == null) {
						logger.debug("Searching for '" + pathEl + "'");
						logger.debug(Struct.structuredMapToString("objectNames", objectNames)); 
						throw new IllegalArgumentException("Cannot find element '" + pathEl + "' in path '" + path + "'");
					}
					if (object instanceof Map) {
						// ok, we can return a list of keys
						objectNames = (Map) object;
					} else if (object instanceof ObjectName) {
						// get a descriptor map
						objectNames = describeObjectName(mBeanServer, (ObjectName) object);
					} else if (object instanceof List) {
						objectNames = new HashMap();
						for (Iterator i = ((List) object).iterator(); i.hasNext(); ) {
							Object listItem = (Object) i.next();
							if (listItem instanceof ObjectName) {
								objectName = (ObjectName) listItem;
								objectNames.put(objectName.toString(), objectName);
							} else {
								throw new IllegalArgumentException("Don't know how to deal with " + listItem.getClass().getName() + " in JMX list");
							}
						}
						
					} else {
						//	terminating node ?
						System.out.println("-- class " + object.getClass().getName());
						objectNames = new LinkedHashMap();
						if (object instanceof String) {
							objectNames.put(object, null);
						} else if (object instanceof String[]) {
							String[] strings = (String[]) object;
							for (int i = 0; i<strings.length; i++) {
								objectNames.put(strings[i], null);
							}
							// objectNames.put(object, string)
						/* com.ibm.websphere.management.statistics.Stats
						} else if (object instanceof Stats) {
						    Stats stats = (Stats) object;
						    Statistic[] statistics = stats.getStatistics();
						    objectNames.put("numStats", new Long(statistics.length));
						    for (int i = 0; i<statistics.length; i++) {
								objectNames.put(statistics[i].getName() +
								  ", description=" + statistics[i].getDescription() + 
								  ", unit=" + statistics[i].getUnit() +
								  ", getStartTime=" + statistics[i].getStartTime() +								  
								  ", lastSampleTime=" + statistics[i].getLastSampleTime(),
								  null);
						    }
							objectNames.put("-- result is of type " + object.getClass().getName(), null);
						*/
						} else if (object instanceof Number) {
						    objectNames.put(object.toString(), null);	
						} else {
						    objectNames.put("-- result is of type " + object.getClass().getName(), null);
						}
					}
					pathIndex++;
				}
				
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><nodes>";
				for (Iterator i = objectNames.keySet().iterator(); i.hasNext(); ) {
					String key = (String) i.next();
					xml += "<node name=\"" + key + "\"/>"; 
				}
				xml += "</nodes>";
				request.setAttribute("xml", xml);
				logger.debug(xml);
				forward = "xml";
			}

        	
        	
        } else if (debugTab.equals("jndi")) {
			// simple JNDI browser

			// to connect to other server:
			/* Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
				 "com.ibm.websphere.naming.WsnInitialContextFactory");
			env.put(Context.PROVIDER_URL, "corbaloc:iiop:myhost.mycompany.com:2809"); */

			if ("getNodes".equals(action)) {
				String path = request.getParameter("path");
				System.out.println("Viewing JNDI path 1 '" + path + "'");
				if (path.startsWith("topNode")) {
					path = path.substring(7);
				}
				if (path.startsWith("/")) {
					path = path.substring(1);
				}
				System.out.println("Viewing JNDI path 2 '" + path + "'");
				
				InitialContext ctx = new InitialContext();
				Map objectNames = new TreeMap();
				for (NamingEnumeration e = ctx.listBindings(path); e.hasMore(); ) {
					Binding b = (Binding) e.next();
					objectNames.put(b.getName(), b);
				}
				
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><nodes>";
				for (Iterator i = objectNames.keySet().iterator(); i.hasNext(); ) {
					String key = (String) i.next();
					Binding b = (Binding) objectNames.get(key);
					xml += "<node name=\"" + b.getName() + "\" className=\"" + b.getClassName() + "\"/>"; 
				}
				xml += "</nodes>";

				request.setAttribute("xml", xml);
				System.out.println(xml);
				forward = "simpleXml";
			}
        	
        	
        } else if (debugTab.equals("test")) {
        } else {
        	throw new IllegalArgumentException("Unknown debugTab '" + debugTab + "'");
        }

        request.setAttribute("debugTab", debugTab);
		return mapping.findForward(forward);
	
	}

    
	/**
	 * Preloads the ObjectName instances and sorts them into a Map indexed by
	 * domain. WAS only appears to have two built-in domains: "JMImplementation" and
	 * "WebSphere". Within domains, a further map of types name to object names
	 * are created; e.g. within the WebSphere domain, all applications are contained
	 * in a List of ObjectNames contained in a Map with the key "Application"
	 * 
	 * @param server the JMX server to retrieve objects from
	 *
	 * @return map of mbeans in a structure of domain -> type -> list of objectNames
	 * 
	 * @throws MalforedObjectNameException
	 */
	protected Map getObjectNames( MBeanServer server )
	throws MalformedObjectNameException
	{
		// this only ever appears to see resources that already exist (have been used)
		// e.g. MessageManagerDataSource but not MessageManagerDataSourceNoXA
		//   .. although XA will appear after a query has been run. Thanks Websphere. Thanks a lot.
		
	    Map objectNames = new TreeMap();
        Set objectNameSet = server.queryNames( null, /*new ObjectName("WebSphere:type=DataSource,*"),*/ null );
        for( Iterator i = objectNameSet.iterator(); i.hasNext(); ) {
		     ObjectName name = (ObjectName) i.next();
		     
		 	 String domain = name.getDomain();
		 	 Map typeNames = (Map) objectNames.get(domain);
		 	 if (typeNames == null) {
			     typeNames = new TreeMap();
			     objectNames.put( domain, typeNames );
			 }
			 // Search the typeNames map to match the type of this object
			 String typeName = name.getKeyProperty("type");
			 if (typeName == null) typeName = "none";
			 List v = (List) typeNames.get(typeName);
			 if (v==null) {
			 	 v = new ArrayList();
				 typeNames.put(typeName, v);
			 }
			 v.add(name);
        }
	    return objectNames;
	}
	
	/** Retrieves information regarding the JMX object passed in. Used to populate the
	 * JMX tree in the debug pane.
	 * 
	 * This object returns a map with the following structure:
	 * <attributes>
	 * className - (String) the className of the object instance
	 * attributes - a Map describing the attributes associated with this JMX MBean. The
	 *   key for the map is the attribute name, and the value is a map with the following structure:
	 *   <attributes>
	 *     description - a text description of the attribute
	 *     type - the class type of the attribute
	 *     value - the current value of the attribute (or "" if the value is null)
	 *   </attributes>
	 * operations - a Map describing the operations that can be performed in this MBean.
	 *   The key for the map is the operation name, and the value is a Map with the following
	 *   structure:
	 *   <attributes>
	 *     impact - the impact of performing this MBean. This returns one of the
	 *       MBeanOperationInfo constants as a String, i.e. "ACTION", "ACTION_INFO", "INFO" or
	 *       "UNKNOWN"
	 *     return type - the class name of the result of this operation
	 *     params - a Map representing the parameters passed into this operation. The key
	 *       of each entry in the map is the parameter name, and the value is another Map
	 *       with the following structure:
	 *       <attributes>
	 *         name - name of the parameter
	 *         type - the class name of the parameter
	 *       </attributes>
	 *     description - a text desription of the operation
	 *     result - is only present for "INFO"-impact operations with no parameters. Contains 
	 *       the result when the operation is invoked. Is set to "(null)" if the result is null.
	 *   </attributes>
	 * </attributes> 
	 * 
	 * @param server The JMX server to retrieve information from
	 * @param objectName The object we are interested in
	 * 
	 * @return a structure as defined above
	 * 
	 * @throws AttributeNotFoundException
	 * @throws IntrospectionException
	 * @throws ReflectionException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 */
	protected Map describeObjectName(MBeanServer server, ObjectName objectName) 
	throws AttributeNotFoundException, IntrospectionException, ReflectionException, InstanceNotFoundException, MBeanException {
		Map result = new HashMap();

		ObjectInstance objectInstance = server.getObjectInstance(objectName);
		result.put("className", objectInstance.getClassName());

		// list attributes
		Map attributes = new HashMap();
		result.put("attributes", attributes);
		MBeanInfo info = server.getMBeanInfo(objectName);
		MBeanAttributeInfo[] attrInfos = info.getAttributes();
		for (int i = 0; i < attrInfos.length; i++) {
			Map attribute = new HashMap();
			
			MBeanAttributeInfo attrInfo = attrInfos[i];
			String name = attrInfo.getName();
			String type = attrInfo.getType();
			String description = attrInfo.getDescription();
			Object value = server.getAttribute(objectName, name);

			// attribute.put("name", name);
			attribute.put("description", description==null ? "" : description);
			attribute.put("type", type);
			attribute.put("value", value==null ? "" : value);
			attributes.put(name, attribute);
			
			// System.out.println(" attr " + name + " (" + type + ") " + value);
			if (description!=null) { logger.debug("      " + description); } 
		}
		
		// list operations
		Map operations = new HashMap();
		result.put("operations", operations);
		MBeanOperationInfo[] operationInfos = info.getOperations();
		for (int i = 0; i < operationInfos.length; i++) {
			Map operation = new HashMap();
			MBeanOperationInfo operationInfo = operationInfos[i];
			operations.put(operationInfo.getName(), operation);
			operation.put("return type", operationInfo.getReturnType());
			// System.out.print(" op " + Text.getLastComponent(operationInfo.getReturnType()) + " " + operationInfo.getName() + "(");
			Map params = new LinkedHashMap();
			operation.put("params", params);
			MBeanParameterInfo[] paramInfos = operationInfo.getSignature();
			for (int j = 0; j < paramInfos.length; j++) {
				MBeanParameterInfo paramInfo = paramInfos[j];
				Map param = new HashMap();
				param.put("name", paramInfo.getName());
				param.put("type", paramInfo.getType());
				params.put(paramInfo.getName(), param);
			}
			operation.put("impact", getImpact(operationInfo.getImpact()));
			operation.put("description", operationInfo.getDescription());
			Object opResult = null;
			if (operationInfo.getImpact() == MBeanOperationInfo.INFO && paramInfos.length == 0) {
				opResult = server.invoke(objectName, operationInfo.getName(), new Object[] {}, new String[] {});
				if (opResult==null) {
					// newList.add("(null)");
					opResult = "(null)";
				} else if (opResult instanceof ObjectName) {
					List newList = new ArrayList(1);
					newList.add(opResult);
					opResult = newList;
				}
				/* if (opResult instanceof String[]) {
					String[] stringArrayOpResult = (String[]) opResult;
					for (int k = 0; k < stringArrayOpResult.length; k++) {
						System.out.println("   " + k + ": " + stringArrayOpResult[k]);
					}
				} else {
					System.out.println("   " + result);
				} */
			}
			if (opResult!=null) { operation.put("result", opResult); } 
		}
		
		return result;
	}

	private String getImpact(int impact) {
		if (impact == MBeanOperationInfo.ACTION) {
			return "ACTION";
		} else if (impact == MBeanOperationInfo.ACTION_INFO) {
			return "ACTION_INFO";
		} else if (impact == MBeanOperationInfo.INFO) {
			return "INFO";
		} else if (impact == MBeanOperationInfo.UNKNOWN) {
			return "UNKNOWN";
		} else {
			throw new IllegalStateException("Unknown impact '" + impact + "'");
		}
	}
    
	
}
