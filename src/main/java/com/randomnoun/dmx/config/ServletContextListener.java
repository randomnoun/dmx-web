package com.randomnoun.dmx.config;

import java.net.URLClassLoader;
import java.security.Policy;

import jakarta.servlet.ServletContextEvent;


/**
 * @author knoxg
 * @version $Id$
 */
public class ServletContextListener
	implements jakarta.servlet.ServletContextListener {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    Policy oldPolicy;
    
	/**
	 * @see jakarta.servlet.ServletContextListener#contextInitialized(jakarta.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("dmx-web servletContext initialised");
		if (System.getProperty("com.randomnoun.dmx.securityEnabled")!=null) {
			oldPolicy = Policy.getPolicy();
			Policy.setPolicy(new com.randomnoun.dmx.config.Policy(oldPolicy));
		}
	}

	/** 
	 * @see jakarta.servlet.ServletContextListener#contextDestroyed(jakarta.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println("dmx-web servletContext destroy start");
		// this shouldn't initialise the appConfig if it hasn't been referenced yet
		AppConfig appConfig = AppConfig.getAppConfigNoInit();
		if (appConfig!=null) {
			appConfig.shutdownThreads();
			appConfig.shutdownListeners();
			appConfig.shutdownDevices();
			if (System.getProperty("com.randomnoun.dmx.securityEnabled")!=null) {
				Policy.setPolicy(oldPolicy);
			}
		}
		System.out.println("dmx-web servletContext destroy complete");
	}

}
