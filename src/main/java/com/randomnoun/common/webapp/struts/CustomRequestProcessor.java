package com.randomnoun.common.webapp.struts;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.struts.action.*;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.PlugInConfig;
import org.springframework.jdbc.core.JdbcTemplate;

import com.randomnoun.common.ExceptionUtil;
import com.randomnoun.common.Text;
import com.randomnoun.common.ThreadContext;
import com.randomnoun.common.security.Permission;
import com.randomnoun.common.security.SecurityContext;
import com.randomnoun.common.security.User;
import com.randomnoun.common.timer.Benchmark;
import com.randomnoun.common.webapp.AppConfigBase;
import com.randomnoun.common.webapp.struts.PermissionMapping;

/**
 * This class hooks into the struts processing chain before and during
 * specific Action methods are invoked for a request.
 *
 * <p>It is responsible for performing coarse-grained authentication checks on all
 * requests, and benchmarking the web application.
 *
 * @TODO could put a lot of the config data we repeatedly get out of the AppConfig
 * into instance variables here
 *
 *
 * @author knoxg
 * @version $Id: CustomRequestProcessor.java,v 1.25 2013-09-25 04:42:17 knoxg Exp $
 */
public class CustomRequestProcessor
	extends org.apache.struts.action.RequestProcessor {
	/** A revision marker to be used in exception stack traces. */
	public static final String _revision = "$Id: CustomRequestProcessor.java,v 1.25 2013-09-25 04:42:17 knoxg Exp $";

	/** Logger instance for this class */
	private static Logger logger = Logger.getLogger(CustomRequestProcessor.class.getName());

	/** Returns the appConfig in the current servletContext for this request.
	 * Object is returned as an AppConfigBase.
	 * 
	 * @param request servlet request
	 */
	private static AppConfigBase getAppConfig(HttpServletRequest request) {
		AppConfigBase appConfig = (AppConfigBase) request.getSession().getServletContext().getAttribute("com.randomnoun.appConfig");
		if (appConfig==null) {
			Throwable t = (Throwable) request.getSession().getServletContext().getAttribute("com.randomnoun.appConfig.initialisationFailure");
			throw new RuntimeException("No appConfig", t);
		} else {
			return appConfig;
		}
	}

	
	/** This method checks to see whether the supplied
	 * username/password combination has access to this application, and if so,
	 * sets up authentication information in the HttpSession, and loads user
	 * preferences from the database. General user preferences are contained in the
	 * 'userConfig' Map in the session, and user authentication data is contained
	 * in the 'user' User object in the session. The User object should be passed
	 * to all methods that are executed by this end-user.
	 * 
	 * @param request   The HttpServletRequest to store authentication data in
	 * @param username  The username supplied in this request
	 * @param password  The password supplied in this request
	 *
	 * @return null if the login was successful, an error message otherwise.
	 */
	public static String validLogin(
		HttpServletRequest request, 
		HttpServletResponse response, 
		String username, String password) 
    {

		HttpSession session = request.getSession(true);
		
		boolean isValidLogin = false;
		AppConfigBase appConfig = getAppConfig(request);
		SecurityContext securityContext = appConfig.getSecurityContext();
        
		logger.debug("Authenticating user '" + username + "'");
		if (username.equals("")) {
			return "You must enter a username";
		}

		User tmpUser = new User();
		tmpUser.setUsername(username);
		try {
			isValidLogin = securityContext.authenticate(tmpUser, password);
		} catch (IOException ioe) {
			logger.error("Exception authenticating user", ioe);
		}
		
		String restrictClientIPs = (String) appConfig.get("auth.restrictClientIPs");
		//boolean reverseLookup = "true".equals(appConfig.get("auth.reverseLookup")); 
		String clientIp = request.getRemoteAddr();
		//String clientHost = reverseLookup ? request.getRemoteHost() : "";
		if (restrictClientIPs != null) {
			try {
				List ipList = Text.parseCsv(restrictClientIPs);
				if (!ipList.contains(clientIp)) {
					System.out.println("Invalid request IP '" + clientIp + "'");
					return "Invalid request IP (" + clientIp + ") - please contact system administrator";
				}
			} catch (java.text.ParseException pe) {
				throw new RuntimeException("Cannot parse restricted client IP list");
			}
		}
        
		if (isValidLogin && !Text.isBlank(username)) {
			logger.debug("Logging in user '" + username + "'");
			User user = new User();
			user.setUsername(username);
			// user default locale until we can load the real one from the database
			user.setLocale(new Locale((String) appConfig.get("defaultLocale.language"), (String) appConfig.get("defaultLocale.country")));
            
			// abort if we don't have login.application
			if (!appConfig.hasPermission(user, "login.webapp")) {
				logger.info("User '" + username + "' does not have login.webapp permission; refusing entry");
				return  "The user '" + username + "' does not have permission to use this application";
			}

			// get user preferences from database
			// put the user's security information into a session attribute, so that
			// it is accessible through JSTL. We want to preload this, so we can 
			// pro-actively hide actions that the user does not have permission to perform.
			// we don't store resourceCriteria, although we probably could these days
			
			// all these booleans return true, incidentally
			Map<String, Map<String, Boolean>> permissions = new HashMap();
			Map<String, Boolean> roles = new HashMap();

			Map<String, Object> userConfig;
			String userConfigClassname = appConfig.getProperty("auth.userConfig.class");
			if (userConfigClassname==null) {
				userConfig = new HashMap();
			} else {
				try {
					Class clazz = Class.forName(userConfigClassname);
					Constructor constructor = clazz.getConstructor(User.class);
					userConfig = (Map) constructor.newInstance(user);
				} catch (Exception e) {
					throw new RuntimeException("Could not instantiate userConfig class '" + userConfigClassname + "'", e);
				}
			}
			
			// XXX: this should be loaded via the SecurityContext class
			
			if (!"false".equals(appConfig.getProperty("auth.enableSecurityContext"))) { 

				JdbcTemplate jt = appConfig.getJdbcTemplate();
				// load user properties from a user table, containing one row per user
				// e.g. SELECT userId, password, givenname, surname, email FROM users WHERE userId = ?
				String userSql = appConfig.getProperty("auth.user.sql");
				if (userSql!=null) {
					List<Map<String, Object>> details = jt.queryForList( userSql, new Object[] { username } );
					if (details.size()==0) {
						return "No user details recorded for user '" + username + "'";
					} else {
						userConfig.putAll((Map) details.get(0));
					}
				}
				
				// load user properties from a userconfig table, containing one row per configuration item
				String userConfigSql = appConfig.getProperty("auth.userConfig.sql"); 
				if (userConfigSql!=null) {
					String keyColumn = appConfig.getProperty("auth.userConfig.key");
					String valueColumn = appConfig.getProperty("auth.userConfig.value");
					List details = jt.queryForList(userConfigSql, new Object[] { username } );
					for (int i=0; i<details.size(); i++) {
						Map row = (Map) details.get(i);
						String key = (String) row.get(keyColumn);
						String value = (String) row.get(valueColumn);
						userConfig.put(key, value);
					}
				}
				

				// for each role this user is in, add their permissions to the set
				// we probably have a more efficient way of doing this these days
				try {
					for (Iterator<String> i = securityContext.getUserRoles(user).iterator(); i.hasNext();) {
						String role = (String) i.next();
						roles.put(role, Boolean.valueOf(true));
						
						List<Permission> rolePerms = securityContext.getRolePermissions(role);
						for (Iterator<Permission> j = rolePerms.iterator(); j.hasNext();) {
							Permission perm = (Permission) j.next();
							Map<String, Boolean> activityMap = permissions.get(perm.getActivity()); 
							if (activityMap == null) {
								activityMap = new HashMap<String, Boolean>();
								permissions.put(perm.getActivity(), activityMap);
							}
							activityMap.put(perm.getResource(), Boolean.valueOf(true));
						}
					}
		
					// for each permission this user has as well, add their permissions to the set
					// (think this returns role permissions as well now, but hey)
					for (Iterator<Permission> j = securityContext.getUserPermissions(user).iterator(); j.hasNext();) {
						Permission perm = (Permission) j.next();
						Map<String, Boolean> activityMap = permissions.get(perm.getActivity()); 
						if (activityMap == null) {
							activityMap = new HashMap<String, Boolean>();
							permissions.put(perm.getActivity(), activityMap);
						}
						activityMap.put(perm.getResource(), Boolean.valueOf(true));
					}
				} catch (IOException ioe) {
					throw new RuntimeException("IO exception reading user security data", ioe);
				}
			}
			userConfig.put("username", username);
						

			Locale locale;
			String localeLanguage = (String) userConfig.get("language");
			String localeCountry = (String) userConfig.get("country");
			String localeVariant = (String) userConfig.get("variant");
			if (localeLanguage == null) {
				localeLanguage = (String) appConfig.get("defaultLocale.language");
				localeCountry = (String) appConfig.get("defaultLocale.country");
				localeVariant = (String) appConfig.get("defaultLocale.variant");
			}

			if (localeVariant != null) {
				locale = new Locale(localeLanguage, localeCountry, localeVariant);
			} else {
				locale = new Locale(localeLanguage, localeCountry);
			}

			user.setLocale(locale);
            
			// session.setAttribute("javax.servlet.jsp.jstl.fmt.localizationContext", new LocalizationContext());
			// session.setAttribute("org.apache.struts.action.LOCALE", locale);

			session.setAttribute("user", user);
			session.setAttribute("userConfig", userConfig);
			// session.setAttribute("appConfig", appConfig);
			// session.setAttribute("requestIp", clientHost + " (" + clientIp + ")");
			session.setAttribute("requestIp", clientIp);
			session.setAttribute("security", permissions);
			session.setAttribute("securityRoles", roles);

			//MDC.put("username", userConfig.get("username"));
			MDC.put("username", user.getUsername());
			MDC.put("userId", user.getUserId());
			return null;
		} else {
			return "The username/password supplied is invalid";
		}
	}

	/**
	 * This method implements the struts
	 * {@link org.apache.struts.action.RequestProcessor#processPreprocess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * hook, allowing us to perform authentication checks for every request.
	 *
	 * <p>This method sets the 'isStrutsRequest' attribute in the request to
	 * 'true', which is used by the {@link com.randomnoun.common.webapp.taglib.AuthCheckTag}
	 * custom tag to prevent users from accessing JSPs directly.
	 *
	 * @param request  The HttpServletRequest object for this request
	 * @param response The HttpServletResponse object for this request
	 *
	 * @return As per the struts contract, this method returns true if struts is to
	 *   continue processing for this request, and false if it does not (e.g. if
	 *   we have just forwarded to the login page).
	 */
	public boolean processPreprocess(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);

		logger.debug("processPreprocess path='" + request.getServletPath() + "'");
		try {

			// @TODO should check for appConfig reinitialisation servletPaths
			// here and return immediately (as per loginPaths below) 
			
			// first of all we make sure that the application is initialised correctly
			AppConfigBase appConfig;
			
			// exception handling of getAppConfig() now performed in process() method
			appConfig = getAppConfig(request);
			
			// initial request processing. You'd think this would all be in filters.
			Benchmark benchmark = (Benchmark) request.getAttribute("benchmark");
			MDC.remove("username");
			MDC.remove("userId");
			request.setAttribute("isStrutsRequest", "true"); // used in JSPs to check whether this routine has been invoked
	
			// authenticate user 
			User user = (User) session.getAttribute("user");
			if (user == null) { 
				// not logged on!
				// check if username/password has been set in request
				if (request.getParameter("_username") != null) {
					String username = request.getParameter("_username");
					String password = request.getParameter("_password");

					String loginResult = validLogin(request, response, username, password);
					if (loginResult == null) {
						logger.debug("login OK for user '" + username + "'");
					} else if (loginResult.startsWith("OPENID:")) {
						// forward to openId for real authentication
						// String returnUrl = appConfig.getProperty("openId.returnUrl");
						logger.debug("Using openid... redirecting to '" + loginResult.substring(7) + "'");
						try {
							response.sendRedirect(loginResult.substring(7));
						} catch (IOException ioe) {
							// not much we can do about it here
							ioe.printStackTrace();
						}
						return false;
						
					} else {
						request.setAttribute("messageText", loginResult);
						request.setAttribute("_username", username);
						request.setAttribute("_password", password);
					}
				
				} else {
					
					boolean autoLoginEnabled = "true".equals(appConfig.getProperty("auth.autoLogin.enabled"));
					if (autoLoginEnabled && !"disable".equals(request.getParameter("autoLogin"))) {
						// autologin is enabled for this app & not disabled for this request
						String loginResult = validLogin(request, response, appConfig.getProperty("auth.autoLogin.username"), appConfig.getProperty("auth.autoLogin.password"));
						if (loginResult == null) {
							logger.debug("autologin OK");
						}
	
					} else {
						
						boolean isLoginPath = false;
						List loginPaths = (List) appConfig.get("webapp.loginServletPaths");
						isLoginPath = loginPaths != null && loginPaths.contains(request.getServletPath());
						if (isLoginPath) {
							// going to a login path, allow 
							return true;
						} else {
							// non-authenticated and not going to a login path; fall through
							
						}
					}
				}
				
				user = (User) session.getAttribute("user");
			}
	
	
			// process unauthenticated users
			if (user == null) {

				// used to forward to login page here; 
				// only want to throw to login page if this is a page with a PermissionMapping on it
			} else {
	
				// set username in MDC
				logger.debug("logged in as " + user.getUsername());
				MDC.put("username", user.getUsername());
				MDC.put("userId", user.getUserId());
				if (benchmark != null) {
					benchmark.setBenchId(appConfig.get("benchmark.webapp.idPrefix") + "-" + user.getUsername());
				}
				if (user.getLocale()!=null) {
					// TODO: implement this
					// logger.warn("Warning: user '" + user.getUsername() + "' has no locale set");
					response.setLocale(user.getLocale());
				}
		
				//	attempt to get JSTL to use the right locale... (arg!)
				// ... only the last of these statements is probably required
				//request.setAttribute(Config.FMT_LOCALE, user.getLocale());
				//request.setAttribute(Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(null, user.getLocale() ));
				request.setAttribute("locale", user.getLocale());
		
				// mark request as authenticated; NB: this is *not* the same as authorised
				request.setAttribute("authenticated", "true");
			}
	        
			return true;
		} catch (Exception e) {
			// catch preprocessing errors
			showErrorPage(request, response, e);
			return false;
		}
	}
    
	/** Send a login page to the user
	 * 
	 * @param request 
	 * @param response
	 */
	public void showLoginPage(HttpServletRequest request, HttpServletResponse response, String loginPage) {
		if (loginPage==null) { loginPage = "/index.jsp"; }
		RequestDispatcher dispatcher = request.getRequestDispatcher(loginPage);
		try {
			dispatcher.forward(request, response);
		} catch (ServletException se) {
			logger.error("Servlet exception redirecting to login jsp", se);
			/*
			logger.error("-- Servlet exception root cause", se.getRootCause());
			if (se.getRootCause()!=null && se.getRootCause() instanceof ServletException) {
				logger.error("-- Servlet exception root cause(x2)", ((ServletException) se.getRootCause()).getRootCause());
			}
			*/
		} catch (IOException ioe) {
			logger.error("I/O exception redirecting to login jsp", ioe);
		}
	}

	/** Send an error page to the user
	 * 
	 * @param request 
	 * @param response
	 */
	public void showErrorPage(HttpServletRequest request, HttpServletResponse response, Exception e) {
		request.setAttribute("javax.servlet.error.exception", e);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/misc/errorPage.jsp");
		try {
			dispatcher.forward(request, response);
		} catch (ServletException se) {
			logger.error("Servlet exception redirecting to error jsp", se);
			logger.error("-- Exception that triggered initial redirect", e);
			logger.error("-- Servlet exception root cause", se.getRootCause());
			if (se.getRootCause()!=null && se.getRootCause() instanceof ServletException) {
				logger.error("-- Servlet exception root cause(x2)", ((ServletException) se.getRootCause()).getRootCause());
			}
		} catch (IOException ioe) {
			logger.error("I/O exception redirecting to error jsp", ioe);
		}
	}
	
	
	/**
	 * This method implements the struts
	 * {@link org.apache.struts.action.RequestProcessor#processRoles(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMapping) }
	 * hook, allowing us to perform authorisation checks for every request.
	 *
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are creating
	 * @param mapping The mapping we are using
	 *
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet exception occurs
	 */
	protected boolean processRoles(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping)
		throws IOException, ServletException 
	{
		if (mapping instanceof PermissionMapping) {
			User user = (User) request.getSession(true).getAttribute("user");
			AppConfigBase appConfig = getAppConfig(request); 
			PermissionMapping permissionMapping = (PermissionMapping) mapping;
            
			if (user==null) {
				
				// possibly throw exception here instead ? yet another config property
				logger.debug("user null - redirecting to login page");
				
				// used to recreate original login request
				request.setAttribute("originalPath", request.getPathInfo());
				request.setAttribute("originalQueryString", request.getQueryString());
				if (request.getAttribute("messageText") == null) {
					request.setAttribute("messageText", "You must log in to this site");
				}
				showLoginPage(request, response, permissionMapping.getLoginPage());
	
				// no further processing to be performed by struts
				return false;
			}
			
			// we allow comma-separated permissions in our struts config files now
			if (!Text.isBlank(permissionMapping.getPermission())) {
				if (permissionMapping.getPermission().indexOf(",")==-1) {
					// simple case (only one permission)
					if (!appConfig.hasPermission(user, permissionMapping.getPermission())) {
						logger.error("Security alert: User '" + user.getUsername() + "' has attempted to perform struts action '" + mapping.getPath() + "', but does not have permission '" + permissionMapping.getPermission() + "'");
						throw new SecurityException("You are not authorized to perform this action.");
					}
				} else {
					// complex case (multiple permissions; only allow if user has one or more of these)
					boolean preventAction = true;
					String permission = permissionMapping.getPermission();
					try {
						List permissions = Text.parseCsv(permission);
						for (Iterator i = permissions.iterator(); i.hasNext(); ) {
							permission = (String) i.next();
							if (appConfig.hasPermission(user, permission)) {
								preventAction = false;
								break; // don't bother checking other permissions
							}
						}
						if (preventAction) {
							logger.error("Security alert: User '" + user.getUsername() + "' has attempted to perform struts action '" + mapping.getPath() + "', but does not have any of the permissions '" + permissionMapping.getPermission() + "'");
							throw new SecurityException("You are not authorized to perform this action. This activity has been logged.");
						}
					} catch (ParseException pe) {
						throw new IllegalStateException("Permission '" + permission + "' for struts action '" + mapping.getPath() + "' could not be parsed");
					}
				}
			}
		}
        
		return true;
	}

	/**
	 * Struts-provided hook surrounding action body execution. Used for benchmarking.
	 *
	 * <p>This method also ensures that the session timeout information stored
	 * for this user is valid; we can't use the built-in session timeout mechanism
	 * in the application server, since some URLs as fetched automatically by the
	 * webapp.
	 *
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are creating
	 * @param action The Action instance to be used
	 * @param form The ActionForm instance to pass to this Action
	 * @param mapping The ActionMapping instance to pass to this Action
	 *
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet exception occurs
	 */
	protected ActionForward processActionPerform(HttpServletRequest request, HttpServletResponse response, Action action, ActionForm form, ActionMapping mapping)
		throws IOException, ServletException 
	{
		/*
		// don't bother running the action if we've timed out
		AppConfig appConfig = AppConfig.getAppConfig();
		HttpSession session = request.getSession();

		Date date = (Date) session.getAttribute("sessionTimeout");
		long timeoutInMinutes = 0;
		// AppConfig appConfig = AppConfig.getAppConfig(user);
		try { 
			timeoutInMinutes = Long.parseLong(appConfig.getProperty("webapp.sessionTimeout"));
		} catch (Exception e) {
			timeoutInMinutes = 0;
		}
		if (timeoutInMinutes != 0) {
			if (date==null) {
				// no date, must be first URL. We don't perform checking, just add it to the session
				request.getSession(true).setAttribute("sessionTimeout", new Date());
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("---- testing now (" + System.currentTimeMillis() + ") against session time (" + date.getTime() + "); diff=" + 
					   (System.currentTimeMillis() - date.getTime()) + " against " + 
					   (timeoutInMinutes * 60 * 1000));
				}
    
				boolean sessionTimeoutUpdate = true;
				boolean sessionTimeout = true;
				if (mapping instanceof PermissionMapping) {
					sessionTimeoutUpdate = ((PermissionMapping) mapping).getSessionTimeoutUpdateBoolean();
					sessionTimeout = ((PermissionMapping) mapping).getSessionTimeoutBoolean();
				}
				if (sessionTimeout) {
					if (System.currentTimeMillis() - date.getTime() > timeoutInMinutes * 60 * 1000) {
						// we should probably allow the LoginAction to be called here, even if 
						// we've expired things.
						logger.debug("--- expired");
						if (sessionTimeoutUpdate==false) {
							// expired, but automatic URL; just return nothing
						} else {
							// remove everything from the session, so that we can log back in again
							request.setAttribute("messageText", 
							  "The application has been inactive for " + 
							  timeoutInMinutes + " minutes and you have been automatically logged out. " +
							  "To continue, please log in to the application again.");
							showLoginPage(request, response);
						}
						// return null;
						return mapping.findForward("sessionTimeout");
					}
				}
    
				// update this if it's not an automatically generated URL
				if (sessionTimeoutUpdate) {
					logger.debug("Updating sessiontimeout for mapping " + mapping.getName() + ", path=" + mapping.getPath());
					request.getSession(true).setAttribute("sessionTimeout", new Date());
				}
			}
		}
		*/
        
		AppConfigBase appConfig = getAppConfig(request);
		request.setAttribute("appConfig", appConfig);
		
		Benchmark benchmark = (Benchmark) request.getAttribute("benchmark");
		if (benchmark != null) {
			benchmark.checkpoint("mapping path '" + mapping.getPath() + "' to class '" + action.getClass().getName() + "'");
		}
		
		logger.debug("mapping path '" + mapping.getPath() + "' to class '" + action.getClass().getName() + "'");
		ActionForward result = super.processActionPerform(request, response, action, form, mapping);

		/*
		   add a count field to every first-level List and Map request attribute
		   this is actually possible to implement using the following code in the JSP
		   <c:set var="listCount" value="0" />
		   <c:forEach var="listItem" items="${aList}" >
			   <c:set var="listCount" value="${listCount + 1}" />
		   </c:forEach>
		   ... but this is, frankly, ridiculous
		   The following _size field can be removed here and replaced with
		   JSTL .size() methods if we move to JSTL1.2 (requires JSP1.2, though).
		 */

		// put attribute names into a separate List so that we don't get
		// ConcurrentModificationExceptions
		List names = new ArrayList();
		for (Enumeration e = request.getAttributeNames(); e.hasMoreElements();) {
			names.add(e.nextElement());
		}

		for (Iterator i = names.iterator(); i.hasNext();) {
			String key = (String) i.next();
			Object value = request.getAttribute(key);
			if (value instanceof List) {
				request.setAttribute(key + "_size", new Integer(((List) value).size()));
			} else if (value instanceof Map) {
				request.setAttribute(key + "_size", new Integer(((Map) value).size()));
			}
		}

		return result;
	}

	/**
	 * Struts-provided hook surrounding JSP/result servlet execution. Used for benchmarking.
	 *
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are creating
	 * @param action The Action instance to be used
	 * @param form The ActionForm instance to pass to this Action
	 * @param mapping The ActionMapping instance to pass to this Action
	 *
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet exception occurs
	 */
	protected void processForwardConfig(HttpServletRequest request, HttpServletResponse response, ForwardConfig forward)
		throws IOException, ServletException {
		Benchmark benchmark = (Benchmark) request.getAttribute("benchmark");
		if (forward == null && !response.isCommitted()) {
			throw new ServletException("Action must return a non-null Forward; check struts config file");
		}
		if (benchmark != null) {
			benchmark.checkpoint(forward == null ? "(streamed output)" : "forward '" + forward.getPath() + "'");
		}
		if (!response.isCommitted()) {
			// System.out.println("request at this point looks like " + ClassInspector.getClassSignatures(request.getClass()));
			super.processForwardConfig(request, response, forward);
		}
	}

	/**
	 * Override standard processing to set up and destroy benchmarks.
	 *
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are creating
	 *
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a processing exception occurs
	 */
	public void process(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		// AppConfig appConfig = AppConfig.getAppConfig();

		try {    
			User user = (User) request.getSession(true).getAttribute("user");
			AppConfigBase appConfig = null;
			try {
				appConfig = getAppConfig(request);
			} catch (Exception e) {
				if (getAppConfigMissingPath()!=null) {
					String appConfigMissingPath=getAppConfigMissingPath();
					request.setAttribute("exception", e);
					request.setAttribute("stacktrace", 
					  ExceptionUtil.getStackTraceWithRevisions(e, 
					  CustomRequestProcessor.class.getClassLoader(), ExceptionUtil.HIGHLIGHT_HTML, "com.randomnoun."));
					request.setAttribute("isStrutsRequest", "true"); // used in JSPs to check whether this routine has been invoked
					request.setAttribute("stacktraceSummary", ExceptionUtil.getStackTraceSummary(e));
					RequestDispatcher dispatcher = request.getRequestDispatcher(appConfigMissingPath);
					try {
						dispatcher.forward(request, response);
					} catch (ServletException se) {
						logger.error("Servlet exception redirecting to " + appConfigMissingPath, se);
						throw e;
					} catch (IOException ioe) {
						logger.error("I/O exception redirecting to " + appConfigMissingPath, ioe);
						throw e;
					}
					return;
				} else {
					throw e;
				}
				
			}
			if (user != null && "true".equals(appConfig.get("benchmark.webapp.enabled"))) {
				// appConfig.addLongMetric(AppConfig.METRIC_APP_REQUESTS, 1); // could keep this to capture active requests
				long start = System.currentTimeMillis();
				Benchmark benchmark = null;
				try {
					ThreadContext.push();
					benchmark = new Benchmark((String) appConfig.get("benchmark.webapp.idPrefix"), (String) appConfig.get("benchmark.webapp.logFile"));
					ThreadContext.put("benchmark", benchmark);
					String queryString = request.getQueryString();
					benchmark.checkpoint("ip '" + request.getRemoteAddr() + "' request '" + request.getMethod() + " " + request.getRequestURL() + (queryString==null ? "" : "?" + request.getQueryString()));
					// "' free=" + Runtime.getRuntime().freeMemory() + ", total=" + Runtime.getRuntime().totalMemory()
					request.setAttribute("benchmark", benchmark);
					// appConfig.storeBenchmark(benchmark);
					super.process(request, response);
				} finally {
					try {
						if (benchmark != null) {
							benchmark.end();
						}
						request.removeAttribute("benchmark");
						ThreadContext.remove("benchmark");
						MDC.remove("username");
						MDC.remove("userId");
						ThreadContext.pop();
					} catch (IOException ioe) {
						logger.error("IOException writing benchmark", ioe);
						benchmark.cancel();
					}
				}
				// appConfig.addLongMetric(AppConfigBase.METRIC_APP_REQUESTS, 1);
				// appConfig.addLongMetric(AppConfigBase.METRIC_APP_RESPONSE_TIME, System.currentTimeMillis() - start); 
			} else {
				ThreadContext.push();
				super.process(request, response);
				MDC.remove("username");
				MDC.remove("userId");
				ThreadContext.pop();
			}
		} catch (Throwable t) {
			// @TODO: forward to error page is borked. I blame tomcat.
			t.printStackTrace();
			RequestDispatcher rd = request.getRequestDispatcher("/misc/errorPage.jsp");
	        request.setAttribute("sentAlarm", "true");
	        request.setAttribute("javax.servlet.error.exception", t);
	        request.setAttribute("javax.servlet.error.message", t.getMessage());
	        request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
	        rd.forward(request, response);

	        // if this isn't caught by the ExceptionHandler, then uncomment the above 
			// throw new ServletException(t);
			
			// 		
		}
	}

	// eurgh
	public String getAppConfigMissingPath() {
		PlugInConfig configs[] = moduleConfig.findPlugInConfigs();
		for (int i=0; i<configs.length; i++) {
			if (configs[i].getClassName().equals("com.randomnoun.common.webapp.struts.AppConfigPlugin")) {
				return (String) configs[i].getProperties().get("appConfigMissingPath");
			}
		}
		return null;
	}
	
	
}
