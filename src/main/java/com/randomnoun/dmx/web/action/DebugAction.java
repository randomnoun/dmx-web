package com.randomnoun.dmx.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import com.randomnoun.common.ErrorList;
import com.randomnoun.common.ExceptionUtil;
import com.randomnoun.common.Struct;
import com.randomnoun.common.Text;
import com.randomnoun.common.log4j.MemoryAppender;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.web.JmxUtils;
import com.randomnoun.dmx.web.struts.ActionBase;
//import com.randomnoun.facebook.dataAccess.WebClientDA;
import com.randomnoun.dmx.web.struts.DmxHttpRequest;

/** Debugging action that exposes various application internals.
 * 
 * This action always returns the 'success' forward
 * 
 * @author knoxg
 * @version $Id$
 */
public class DebugAction extends ActionBase {
	/** Logger instance for this class */
	public static Logger logger = Logger.getLogger(DebugAction.class);

	
    /**
     * Perform this struts action. See the javadoc for this
     * class for more details.
     *
     * @param mapping The struts ActionMapping that triggered this Action
     * @param form An ActionForm (if available) holding user input for this Action
     * @param request The HttpServletRequest for this action
     * @param response The HttpServletResponse for this action
     *
     * @return An ActionForward representing the result to return to the end-user
     *
     * @throws Exception If an exception occurred during action processing
     */
    @SuppressWarnings("unchecked")
	public String execute(DmxHttpRequest request, HttpServletResponse response)
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
            
            List<Map<String, String>> appenders = new ArrayList<>();
            List<Map<String, String>> loggers = new ArrayList<>();
            List<String> levels = new ArrayList<>();
            for (Enumeration<?> e = LogManager.getRootLogger().getAllAppenders(); e.hasMoreElements(); ) {
                Appender appender = (Appender) e.nextElement();
                Map<String, String> appMap = new HashMap();
                appMap.put("name", appender.getName());
                appMap.put("type", Text.getLastComponent(appender.getClass().getName()));
                if (appender instanceof AppenderSkeleton) {
                    Priority priority = ((AppenderSkeleton) appender).getThreshold();
                    appMap.put("threshold", priority == null ? "(null)" : priority.toString());
                }
                appenders.add(appMap);
            }
            for (Enumeration<?> e = LogManager.getCurrentLoggers(); e.hasMoreElements(); ) {
                Logger tmpLogger = (Logger) e.nextElement();
                Map<String, String> loggerMap = new HashMap<>();
                loggerMap.put("name", tmpLogger.getName());
                loggerMap.put("effectiveLevel", tmpLogger.getEffectiveLevel().toString());
                loggerMap.put("level", tmpLogger.getLevel() == null ? "" : tmpLogger.getLevel().toString());
                loggerMap.put("additive", String.valueOf(tmpLogger.getAdditivity()));
                loggerMap.put("parent", tmpLogger.getParent() == null ? "" : tmpLogger.getParent().getName());
                String appString = "";
                for (Enumeration<?> e2 = tmpLogger.getAllAppenders(); e2.hasMoreElements(); ) {
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
			List<Map<String, Object>> newEvents = new ArrayList<>();
			
            MemoryAppender memoryAppender = (MemoryAppender) Logger.getRootLogger().getAppender("MEMORY");
            if (memoryAppender!=null) {
				if (!Text.isBlank(request.getParameter("clearEventLog"))) {
					memoryAppender.clear();
				}
	            List<LoggingEvent> loggingEvents = memoryAppender.getLoggingEvents();
	            for (Iterator<LoggingEvent> i = loggingEvents.iterator(); i.hasNext(); ) {
	                LoggingEvent event = (LoggingEvent)i.next();
	                Map<String, Object> newEvent = new HashMap<>();
	                newEvent.put("timestamp", new Date(event.timeStamp));
	                newEvent.put("username", event.getMDC("username"));
	                newEvent.put("level", event.getLevel());
	                newEvent.put("message", event.getMessage());
	
	                if (event.getThrowableInformation() != null) {
	                    newEvent.put("stackTrace",
	                        ExceptionUtil.getStackTraceWithRevisions(event.getThrowableInformation().getThrowable(),
	                          this.getClass().getClassLoader(), ExceptionUtil.HIGHLIGHT_TEXT, "com.randomnoun."
	                        ));
	                }
	                newEvents.add(newEvent);
	            }
            }
            request.setAttribute("events", newEvents);

        } else if (debugTab.equals("jmx")) {
        	
        	MBeanServer mBeanServer = JmxUtils.getMBeanServer();
			Map objectNames = JmxUtils.getObjectNames(mBeanServer);
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
						objectNames = JmxUtils.describeObjectName(mBeanServer, (ObjectName) object);
					} else if (object instanceof List) {
						objectNames = new HashMap<>();
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
						objectNames = new LinkedHashMap<>();
						if (object instanceof String) {
							objectNames.put(object, null);
						} else if (object instanceof String[]) {
							String[] strings = (String[]) object;
							for (int i = 0; i<strings.length; i++) {
								objectNames.put(strings[i], null);
							}
						} else if (object instanceof Number) {
						    objectNames.put(object.toString(), null);	
						} else {
						    objectNames.put("-- result is of type " + object.getClass().getName(), null);
						}
					}
					pathIndex++;
				}
				
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><nodes>";
				for (Iterator<String> i = objectNames.keySet().iterator(); i.hasNext(); ) {
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
				Map<String, Object> objectNames = new TreeMap<>();
				for (NamingEnumeration<Binding> e = ctx.listBindings(path); e.hasMore(); ) {
					Binding b = (Binding) e.next();
					objectNames.put(b.getName(), b);
				}
				
				String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><nodes>";
				for (Iterator<String> i = objectNames.keySet().iterator(); i.hasNext(); ) {
					String key = (String) i.next();
					Binding b = (Binding) objectNames.get(key);
					xml += "<node name=\"" + b.getName() + "\" className=\"" + b.getClassName() + "\"/>"; 
				}
				xml += "</nodes>";

				request.setAttribute("xml", xml);
				System.out.println(xml);
				forward = "xml";
			}
        	
        	
        } else if (debugTab.equals("test")) {
        } else {
        	throw new IllegalArgumentException("Unknown debugTab '" + debugTab + "'");
        }

        request.setAttribute("debugTab", debugTab);
		return forward;
	
	}

    

    
	
}
