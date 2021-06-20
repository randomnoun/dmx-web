package com.randomnoun.common.webapp.struts;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionServlet;

/**
 * ActionServlet superclass. This class exists solely to prevent the following messages 
 * from appearing in the appserver log file:
 * 
 * <pre>
 * log4j:WARN No appenders could be found for logger (org.apache.struts.util.PropertyMessageResources).
 * log4j:WARN Please initialise the log4j system properly.
 * </pre>
 *  
 * I'm not sure why both this and the AppConfigPlugin exist; surely one has to run before
 * the other ? 
 *  
 * @version $Id: CustomActionServlet.java,v 1.5 2013-09-25 04:42:17 knoxg Exp $
 * @author knoxg
 */
public class CustomActionServlet extends ActionServlet {


    /** Generated serialVersionUID */
	private static final long serialVersionUID = 3575519467042021628L;

	public CustomActionServlet() {
        super();
    }
    
    public void init() throws ServletException {
    	String appLogPrefix = getServletConfig().getInitParameter("appLogPrefix");
    	String appConfigClass = getServletConfig().getInitParameter("appConfigClass");
    	System.out.println("[" + appLogPrefix + "] CustomActionServlet: Initialising AppConfig");
        try {
            Class clazz = Class.forName(appConfigClass);
            Method method = clazz.getMethod("getAppConfig", new Class[] {});
            Object appConfig;
        	appConfig = method.invoke(null);
        } catch (Exception e) {
        	System.err.println("[" + appLogPrefix + "] Failed initialisation");
        	e.printStackTrace();
        }
        super.init();
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setAttribute("isStrutsRequest", "true");
		// @TODO catch NoAppConfigExceptions and forward to a reinitialisation page
		super.doGet(request, response);
    }

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setAttribute("isStrutsRequest", "true");
		super.doGet(request, response);
	}

}
