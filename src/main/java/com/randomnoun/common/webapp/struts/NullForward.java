package com.randomnoun.common.webapp.struts;

import org.apache.struts.action.ActionForward;

/** This class exists in order to prevent warnings appearing in struts when 
 * creating (non-JSP) dynamic content within an Action class.
 * 
 * <p>This class has the same constructors as ActionForward, but 
 * any name and path parameters supplied will be disregarded. 
 * 
 * <p>(According to the struts docs, I should be using a ForwardConfig instead of an
 * ActionForward, but I don't believe them.)
 * 
 * @author knoxg
 *
 */

public class NullForward extends ActionForward {

    public NullForward() {
        this(null, false);
    }

    /**
     * <p>Construct a new instance with the specified path.</p>
     *
     * @param path Path for this instance
     */
    public NullForward(String path) {
        this(path, false);
    }

    /**
     * <p>Construct a new instance with the specified <code>path</code> and
     * <code>redirect</code> flag.</p>
     *
     * @param path     Path for this instance
     * @param redirect Redirect flag for this instance
     */
    public NullForward(String path, boolean redirect) {
        super();
        //setName(null);
        //setPath(path);
        setRedirect(redirect);
    }

    /**
     * <p>Construct a new instance with the specified <code>name</code>,
     * <code>path</code> and <code>redirect</code> flag.</p>
     *
     * @param name     Name of this instance
     * @param path     Path for this instance
     * @param redirect Redirect flag for this instance
     */
    public NullForward(String name, String path, boolean redirect) {
        super();
        //setName(name);
        //setPath(path);
        setRedirect(redirect);
    }

    /**
     * <p>Construct a new instance with the specified values.</p>
     *
     * @param name     Name of this forward
     * @param path     Path to which control should be forwarded or
     *                 redirected
     * @param redirect Should we do a redirect?
     * @param module   Module prefix, if any
     */
    public NullForward(String name, String path, boolean redirect,
        String module) {
        super();
        //setName(name);
        //setPath(path);
        setRedirect(redirect);
        setModule(module);
    }

    /**
     * <p>Construct a new instance based on the values of another
     * ActionForward.</p>
     *
     * @param copyMe An ActionForward instance to copy
     * @since Struts 1.2.1
     */
    public NullForward(ActionForward copyMe) {
        this(copyMe.getName(), copyMe.getPath(), copyMe.getRedirect(),
            copyMe.getModule());
    }

}
