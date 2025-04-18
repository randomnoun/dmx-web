package com.randomnoun.dmx.web.struts;

import org.apache.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.AbstractInterceptor;

import com.randomnoun.dmx.config.AppConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** The authentication interceptor
 * 
 */
@SuppressWarnings("serial")
public class AuthenticateInterceptor extends AbstractInterceptor {
    /** Logger instance for this class */
	static Logger logger = Logger.getLogger(AuthenticateInterceptor.class);

	

	
	@Override
    public String intercept(ActionInvocation invocation) throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest(); // ThreadLocal request. Yeesh
		HttpServletResponse response = ServletActionContext.getResponse(); // ThreadLocal response

		AppConfig appConfig;
		HttpSession session;
		try {
			session = request.getSession(true);
		} catch (IllegalStateException ise) {
			// could display a message to the effect that their session has expired, but they
			// may not have had one in the first place
			logger.error("Session already invalidated", ise);
			return "loginPage"; // Action.LOGIN;
		}
			
		// everything's hunky dory. OR IS IT.
		boolean invokeAction = true;
		if (!invokeAction) {
			// should be true for all code paths that reach here
			throw new IllegalStateException("invokeAction==false");
		}

		
		// if (benchmark!=null && timedTask!=null) { benchmark.endTimedTask(timedTask); }
		return invocation.invoke();
    }
	
	
}