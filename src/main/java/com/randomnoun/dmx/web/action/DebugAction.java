package com.randomnoun.dmx.web.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

	
}
