package com.randomnoun.common.webapp.taglib;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */
import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import com.randomnoun.common.ErrorList;

/**
 * Tag library support for error headers.
 *
 * <p>This tag will conditionally evaluate it's body <i>only</i> if the
 * current page has errors on it (as determined by checking the ErrorList
 * object found in the request attribute 'errors').
 *
 * <p>This tag is equivalent to <tt>&lt;c:if test="${errors.hasErrors()}"/&gt;...&lt;/c:if&gt;</tt>
 * if JSTL allowed method evaluation.
 *
 * <p>Attributes defined for this tag (in common.tld) are:
 * <attributes>
 * name - Checks to see if the specified field is in error
 * row - checks to see if *any* field starting with this value is in error (used
 *   for errors in tables; the row in this case is the field prefix)
 * text - the text to display if the error condition is true. If this is null, then the 
 *   body of the tag is displayed instead.
 * </attributes>
 *
 * @author knoxg
 * 
 */
public class OnErrorTag
    extends BodyTagSupport {
    public static String _revision = "";
    private String text;
    private String row;


    // getters/setters
    public void setName(String name) { this.id = name; }
    public String getName() { return id; }
    public void setRow(String row) { this.row = row; }
    public String getRow() { return row; }
    public void setText(String text) { this.text = text; }
    public String getText() { return text; }

    public int doStartTag()
        throws jakarta.servlet.jsp.JspException {
        ErrorList errorList;
        boolean hasError = false;

		try {
	        errorList = (ErrorList) pageContext.getAttribute("errors", PageContext.REQUEST_SCOPE);
	        if (errorList != null) {
	            if (row != null) {
	                try {
	                    // System.out.println("Checking errors for id='" + id + "', text='" + text + "'");
	                    // hasError = errorList.hasErrorOn(id);
	                    int numErrors = errorList.size();
	                    for (int i = 0; i < numErrors; i++) {
	                        String field = errorList.getFieldAt(i);
	                        if (field != null && field.startsWith(row)) {
	                            hasError = true;
	                        }
	                    }
	                } catch (Exception ex) {
	                }
	            }
	
	            
	            if (id != null) {
	                try {
	                    // id = (String) ExpressionUtil.evalNotNull("onError", "name", id, String.class, this, pageContext);
	                    // System.out.println("Checking errors for id='" + id + "', text='" + text + "'");
	                    hasError = errorList.hasErrorOn(id);
	                } catch (Exception ex) {
	                }
	            }
	            
	
	            if (id == null && row == null) {
	                hasError = errorList.hasErrors();
	            }
	        }
	
	        if (hasError) {
	            if (text != null) {
	                try {
	                    pageContext.getOut().print(text);
	                } catch (IOException ioe) {
	                }
	            }
	
	            return EVAL_BODY_INCLUDE;
	        } else {
	            return SKIP_BODY;
	        }
		} catch (Throwable t) {
			// WAS does not log exceptions that occur within tag libraries; log and rethrow
			t.printStackTrace();
			throw (JspException) new JspException("Exception occurred in OnErrorTag").initCause(t);
		}
    }

    public int doEndTag()
        throws jakarta.servlet.jsp.JspException {
        id = null;
        text = null;

        return EVAL_PAGE;
    }
}
