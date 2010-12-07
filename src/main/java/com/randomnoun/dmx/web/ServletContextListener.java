package com.randomnoun.dmx.web;

import java.net.URLClassLoader;
import java.security.Policy;

import javax.servlet.ServletContextEvent;

import com.randomnoun.dmx.config.AppConfig;

/**
 * @author knoxg
 * @version $Id$
 */
public class ServletContextListener
	implements javax.servlet.ServletContextListener {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    Policy oldPolicy;
    
	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("dmx-web servletContext initialised");
		if (System.getProperty("com.randomnoun.dmx.securityEnabled")!=null) {
			oldPolicy = Policy.getPolicy();
			Policy.setPolicy(new com.randomnoun.dmx.config.Policy(oldPolicy));
		}
	}

	/** 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println("dmx-web servletContext destroy start");
		AppConfig appConfig = AppConfig.getAppConfig();
		appConfig.shutdownThreads();
		appConfig.shutdownListeners();
		appConfig.shutdownDevices();
		if (System.getProperty("com.randomnoun.dmx.securityEnabled")!=null) {
			Policy.setPolicy(oldPolicy);
		}
		System.out.println("dmx-web servletContext destroy complete");
	}

}
