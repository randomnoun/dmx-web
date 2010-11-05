package com.randomnoun.dmx.web;

import java.net.URLClassLoader;

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

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("dmx-web servletContext initialised");
	}

	/** 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println("eomail servletContext destroyed");
		AppConfig appConfig = AppConfig.getAppConfig();
		appConfig.shutdownThreads();
	}

}
