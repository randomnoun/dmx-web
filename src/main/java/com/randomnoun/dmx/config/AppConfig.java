package com.randomnoun.dmx.config;


import org.apache.log4j.Logger;

import com.randomnoun.common.webapp.struts.AppConfigBase;

/** Holds configuration data for this web application. Is used to look up 
 * resources for use by other application components, including:
 * 
 * <ul>
 * <li>database connections (JdbcTemplates)
 * <li>secuity contexts (for authentication / authorisation)
 * <li>benchmark/performance data
 * </ul>
 * 
 * @author knoxg
 * @version $Id$
 */
public class AppConfig extends AppConfigBase {

	/** Revision string for stacktraces */
    public static String _revision = "$Id$";

    /** Global instance */
    public static AppConfig instance = null;

    public static final String CONFIG_RESOURCE_LOCATION = "/dmx-web.properties";
    
    public static final String SYSTEM_PROPERTY_KEY_CONFIG_PATH = "com.randomnoun.dmx.configPath";
    
    /** Logger instance for this class */
    public static Logger logger = Logger.getLogger(AppConfig.class);
    
	/** Private constructor; this class can only be called via .getInstance() */
	private AppConfig() {
        
	}
    
    /** Initialise the application instance */
    public synchronized void initialise() {
        logger.info("Initialising dmx-web...");
        AppConfig newInstance = new AppConfig();
        newInstance.initHostname();
        newInstance.loadProperties();  // properties depends on hostname
        newInstance.initLogger();      // logger depends on properties
        newInstance.initDatabase();    // db settings also depend on properties
        newInstance.initSecurityContext();

        // if this all succeeded, assign it to the singleton instance
        instance = newInstance;
    }
    
    /** Return a configuration instance 
     *
     * @throws IllegalStateException if the application did not initialise successfully. 
     */
    public static AppConfig getAppConfig() {
        if (instance == null) {
            instance = new AppConfig();
            try {
                instance.initialise();
            } catch (Throwable t) {
                System.out.println("Error initialising");
                t.printStackTrace();
                instance.initialisationFailure = new RuntimeException("Could not initialise application", t);
            }
        }
        if (instance.initialisationFailure!=null) {
            throw (IllegalStateException) new IllegalStateException("Application failed initialisation").
              initCause(instance.initialisationFailure);
        }
        return instance;
    }
	

    
	@Override
	public String getSystemPropertyKeyConfigPath() {
		return SYSTEM_PROPERTY_KEY_CONFIG_PATH; 
	}

	@Override
	public String getConfigResourceLocation() {
		return CONFIG_RESOURCE_LOCATION;
	}


}

