package com.randomnoun.dmx.web.struts;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.randomnoun.common.Text;
import com.randomnoun.common.security.SecurityContext;
import com.randomnoun.common.timer.Benchmark;
import com.randomnoun.dmx.config.AppConfig;

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