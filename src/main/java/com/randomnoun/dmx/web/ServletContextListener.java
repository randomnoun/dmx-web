package com.randomnoun.dmx.web;

import jakarta.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.config.AppConfig;

/** Class to record webapp initialisation / destruction in tblServletContextEvent
 * 
 * @author knoxg
 */
public class ServletContextListener
	implements jakarta.servlet.ServletContextListener 
{

	/**
	 * @see jakarta.servlet.ServletContextListener#contextInitialized(jakarta.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("dmx-web contextInitialized start");
		
		try {
			AppConfig.getAppConfig();
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Exception initialising appConfig", t);
		}
		
		// so with luck, this method blocks any client HTTP requests, because this may take some time
		// which supposedly does happen in tomcat
		// appConfig.upgradeDatabase();
		
		Logger logger = Logger.getLogger(ServletContextListener.class);
		try {
			logger.info("dmx-web contextInitialised end");
    	} catch (RuntimeException re) {
    		// tomcat doesn't log exceptions thrown in ServletContextListeners, so let's force that to happen
    		re.printStackTrace();
			throw new RuntimeException("Exception initialising servletContext", re);
    	}

	}

	/** 
	 * @see jakarta.servlet.ServletContextListener#contextDestroyed(jakarta.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
	
		// remove timer tasks, close connection pools, remove JMX beans
		// AppConfig.getAppConfig().shutdown();
		
	}

}
