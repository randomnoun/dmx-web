package com.randomnoun.common.webapp.struts;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.config.ModuleConfig;

/**
 * Perform any web container-specific initialisation / destruction code here.
 * 
 * <p>This class initialises the AppConfig class for an application, and then
 * places the singleton instance of that object into the servlet context. 
 * 
 * <p>Any application-specific initialisation should go into AppConfig itself.
 *
 * @author knoxg
 * @version $Id$
 */

/* NB: As configuration properties are set in the struts-config.xml file, setters/getters
 * should be added here to pass this through to AppConfig
 */
public class AppConfigPlugin
    implements org.apache.struts.action.PlugIn
{
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(AppConfigPlugin.class);
    
    /** The name of the appConfig class */
    private String appConfigClass = null;
    
    /** The log prefix */
    private String appLogPrefix = null;
    
    private String appConfigMissingPath = null;

    /** Set up the AppConfig object (which contains an appConfig instance for
     *  every customer running in the site).
     *  
     *  @param servlet servlet responsible for performing struts processing
     *  @param config struts configuration object
     */
    public void init(ActionServlet servlet, ModuleConfig config) {

    	System.out.println("[" + appLogPrefix + "] AppConfigPlugin: Initialising AppConfig");
        // will initialise if this hasn't already occurred
        try {
            Class clazz = Class.forName(appConfigClass);
            Method method = clazz.getMethod("getAppConfig", new Class[] {});
            Object appConfig;
        	appConfig = method.invoke(null);
            servlet.getServletContext().setAttribute("com.randomnoun.appConfig", appConfig);
        } catch (Exception e) {
        	System.err.println("[" + appLogPrefix + "] Failed initialisation");
            servlet.getServletContext().setAttribute("com.randomnoun.appConfig.initialisationFailure", e);
        	e.printStackTrace();
        }
    }
    
    /** Sets the name of the AppConfig class for this application. This class must 
     * have a public static method called 'getAppConfig()' which takes no parameters.
     * This method will be called during initialisation.
     * 
     * @param appConfigClass the name of the AppConfig class
     * 
     * @see #getAppConfigClass()
     */
    public void setAppConfigClass(String appConfigClass) {
    	this.appConfigClass = appConfigClass;
    }
    
    /** Returns the name of the appConfig class
     * 
     * @return the name of the appConfig class
     * 
     * @see #setAppConfigClass(String)
     */
    public String getAppConfigClass() { 
    	return appConfigClass;
    }
    
    /** Sets a short string which is used to prefix any messages to System.out before
     * the log4j system is initialised
     * 
     * @param appLogPrefix the log prefix
     * 
     * @see #getAppLogPrefix()
     */
    public void setAppLogPrefix(String appLogPrefix) {
    	this.appLogPrefix = appLogPrefix;
    }
    
    /** Returns the log prefix
     * 
     * @return appLogPrefix the log prefix
     * 
     * @see #setAppLogPrefix(String)
     */
    public String getAppLogPrefix() {
    	return appLogPrefix;
    }

	public void setAppConfigMissingPath(String appConfigMissingPath) {
		this.appConfigMissingPath = appConfigMissingPath;
	}
	
	public String getAppConfigMissingPath() {
		return appConfigMissingPath;
	}
	

    
    /** Destroy this object */
    public void destroy() {
    }
}
