package com.randomnoun.dmx.web.struts;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

/** A base class for all struts Actions in this web application.
 *  
 * <p>Each action contains a coarse-grained permission which is checked by the AuthoriseInterceptor
 * to ensure that the logged-in user is granted the ability to perform this action.
 * 
 * <p>Further fine-grained permissions are checked within the individual Action classes. 
 *  
 * @author knoxg
 */
public abstract class ActionBase {

	// minimum permission required to perform this action (defined in struts.xml)
	String permission;
	
    /** Shim to allow struts 1 actions to be invoked in struts 2.
     * I could probably use some kind of ActionProxyFactory if I could fathom how that's supposed to work.
     * @throws Exception 
     * 
     * @TODO use ServletRequestAware, ServletResponseAware probably
     */
    public String execute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest(); // struts 2 puts the request in a ThreadLocal
		HttpServletResponse response = ServletActionContext.getResponse(); // ThreadLocal response
		
		DmxHttpRequest vmaintRequest = new DmxHttpRequest(request); 
		
		String forward = execute(vmaintRequest, response);
		return forward;

		// the struts 1 code used to generate it's own streamed output, then return "null" to prevent
		// further struts processing. 
		// struts 2 now pumps its own streams, unless I can find a way of preventing it from doing that
		// (custom result type perhaps)
		/*
		if (result.equals("null")) {
			return result;
		} else {
			return result;
		}
		*/
		
    }
    
    // the struts 1 execute method
    public abstract String execute(DmxHttpRequest request, HttpServletResponse response) throws Exception;

    
    // this stores the values of the 'permission' parameter defined in the struts.xml file
    // it should be set before the AuthoriseInterceptor is called
    public void setPermission(String permission) { this.permission = permission; }
    public String getPermission() { return permission; }

	
}
