package com.randomnoun.common.webapp.struts;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */


import org.apache.log4j.Logger;

/**
 * This class allows declarative coarse-grained permissions
 * to be specified in the struts-config.xml file. Fine-grained permission
 * control will need to be performed by the individual action classes
 * (or in the EJBs that invoke are invoked by those actions)
 *
 * @author knoxg
 * @version $Id: PermissionMapping.java,v 1.4 2013-09-25 04:42:17 knoxg Exp $
 */
public class PermissionMapping
    extends org.apache.struts.action.ActionMapping
{
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id: PermissionMapping.java,v 1.4 2013-09-25 04:42:17 knoxg Exp $";

    /** Logger instance for this class */
    private static Logger logger = Logger.getLogger(PermissionMapping.class.getName());

    /** The permission required to perform this action. */
    private String permission = null;
    
    /** If set to false, disables the session timeout update (default = true) */
    private boolean sessionTimeoutUpdate = true;

    /** If set to false, disables the session timeout check */
    private boolean sessionTimeout = true;

    /** If set, defines the login page to forward to if not logged in (default = "/index.jsp") */
    private String loginPage = null;

    /** Returns the login page to forward to if not logged in (if null, 
     * should be interpreted as "/index.jsp")
     * 
     * @return the login page to forward to if not logged in
     */
    public String getLoginPage() {
		return loginPage;
	}

    /** Sets the login page to forward to if not logged in
     * 
     * @param loginPage the login page to forward to if not logged in
     */
    public void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}

	/**
     * Create a new PermissionMapping instance.
     */
    public PermissionMapping()
    {
        super();
    }

    /**
     * Sets the permission required to perform this action.
     * @param permission the permission required to perform this action.
     */
    public void setPermission(String permission)
    {
        this.permission = permission;
    }

    /**
     * Retrieves the permission required to perform this action.
     * @return the permission required to perform this action.
     */
    public String getPermission()
    {
        return permission;
    }

    /**
     * Sets the permission required to perform this action.
     * @param permission the permission required to perform this action.
     */
    public void setSessionTimeoutUpdate(String sessionTimeoutUpdate)
    {
        if (sessionTimeoutUpdate.equalsIgnoreCase("true")) {
            this.sessionTimeoutUpdate = true;
        } else if (sessionTimeoutUpdate.equalsIgnoreCase("false")) {
            this.sessionTimeoutUpdate = false;
        } else {
            throw new IllegalArgumentException("Illegal sessionTimeoutUpdate value '" + sessionTimeoutUpdate + "'; expecting true or false");
        }
    }

    /**
     * Sets the permission required to perform this action.
     * @param permission the permission required to perform this action.
     */
    public void setSessionTimeout(String sessionTimeout)
    {
        if (sessionTimeout.equalsIgnoreCase("true")) {
            this.sessionTimeout = true;
        } else if (sessionTimeout.equalsIgnoreCase("false")) {
            this.sessionTimeout = false;
        } else {
            throw new IllegalArgumentException("Illegal sessionTimeout value '" + sessionTimeoutUpdate + "'; expecting true or false");
        }
    }


    /**
     * Retrieves the permission required to perform this action.
     * @return the permission required to perform this action.
     */
    public String getSessionTimeoutUpdate()
    {
        // we need to include a String getter (rather than a boolean), because that's what
        // Struts needs in order to recognise it. Arg.
        return Boolean.valueOf(sessionTimeoutUpdate).toString();
    }

    /**
     * Retrieves the permission required to perform this action.
     * @return the permission required to perform this action.
     */
    public boolean getSessionTimeoutUpdateBoolean()
    {
        // we need to include a String getter (rather than a boolean), because that's what
        // Struts needs in order to recognise it. Arg.
        return sessionTimeoutUpdate;
    }

    /**
     * Retrieves the permission required to perform this action.
     * @return the permission required to perform this action.
     */
    public String getSessionTimeout()
    {
        // we need to include a String getter (rather than a boolean), because that's what
        // Struts needs in order to recognise it. Arg.
        return Boolean.valueOf(sessionTimeout).toString();
    }

    /**
     * Retrieves the permission required to perform this action.
     * @return the permission required to perform this action.
     */
    public boolean getSessionTimeoutBoolean()
    {
        // we need to include a String getter (rather than a boolean), because that's what
        // Struts needs in order to recognise it. Arg.
        return sessionTimeout;
    }
    
    


}
