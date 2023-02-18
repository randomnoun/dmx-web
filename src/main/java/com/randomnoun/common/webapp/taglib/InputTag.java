package com.randomnoun.common.webapp.taglib;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

import java.io.*;
import javax.servlet.jsp.*;

import org.apache.log4j.Logger;

import com.randomnoun.common.ErrorList;
import com.randomnoun.common.Text;

/**
 * Render a dynamic HTML INPUT tag
 *
 * <p>
 * @todo document this
 *
 * 
 */
public class InputTag
    extends StandardHtmlTag {
    
    

    /** Logger for this class */
    private final static Logger logger = Logger.getLogger(InputTag.class);

    /** The name of the INPUT tag. */
    private String name;

    /** The current value of the INPUT tag. */
    private String value;

    /** The type of the INPUT tag. */
    private String type;
    private String trueValue;
    
    /** For TEXTAREA input fields */
    private String rows;
    private String cols;

    /* ***********************************************************************
     * Tag Library attribute methods
     */

    /** Sets the name of the SELECT tag.
     *  @param value the name of the SELECT tag.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Retrieves the name of the SELECT tag.
     *  @return   the name of the SELECT tag.
     */
    public String getName() {
        return name;
    }

    /** Sets the current value of the SELECT tag.
     *  @param value the current value of the SELECT tag.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /** Retrieves the current value of the SELECT tag.
     *  @return   the current value of the SELECT tag.
     */
    public String getValue() {
        return value;
    }

    /** Sets the checkbox value that is perceived to be checked
     *  @param
     */
    public void setTrueValue(String trueValue) {
        this.trueValue = trueValue;
    }

    /** Sets the checkbox value that is perceived to be checked
     * @see Struts html:checkbox value
     *  @return   the current value of the SELECT tag.
     */
    public String getTrueValue() {
        return trueValue;
    }

    /** Sets the type of the INPUT tag
     *  @param the type of the iNPUT tag
     */
    public void setType(String type) {
        this.type = type;
    }

    /** Retrieves the type of the INPUT tag.
     *  @return   the type of the INPUT tag.
     */
    public String getType() {
        return type;
    }

	/** Sets the number of rows in a TEXTAREA element.
	 *  @param value the number of rows in a TEXTAREA element.
	 */
	public void setRows(String rows) {
		this.rows = rows;
	}

	/** Retrieves the number of rows in a TEXTAREA element.
	 *  @return   the number of rows in a TEXTAREA element.
	 */
	public String getRows() {
		return rows;
	}


	/** Sets the number of columns in a TEXTAREA element.
	 *  @param value the number of columns in a TEXTAREA element.
	 */
	public void setCols(String cols) {
		this.cols = cols;
	}

	/** Retrieves the number of columns in a TEXTAREA element.
	 *  @return   the number of columns in a TEXTAREA element.
	 */
	public String getCols() {
		return cols;
	}



    /* ***********************************************************************
     * End of tag library attribute methods
     */


    /** Start custom taglibrary processing. Sets internal private variables based on
     *  HttpSession values and defaults, and emits the generating HTML. The body
     *  of this tag will not be evaluated, as declared in the the .tld file.
     *
     *  @todo escape HTML attributes and values
     *
     *  @return This method always returns TagSupport.SKIP_BODY, as required by the
     *   JSP Taglib specification for empty tags
     */
    public int doStartTag()
        throws JspException 
    {
        try {
            evaluateAttributes();

            JspWriter out = pageContext.getOut();
            ErrorList errors = null;

            try {
                errors = (ErrorList) pageContext.getAttribute("errors", PageContext.REQUEST_SCOPE);
            } catch (ClassCastException cce) {
                // just ignore these.
            }

            String fieldStyle;

            if (type.equals("input") || type.equals("submit")) {
                fieldStyle = "update-button";
            } else {
                fieldStyle = (type.equals("checkbox") || type.equals("radio")) ? "inputFieldCheckbox" : "inputField";
                if (errors != null && errors.hasErrorOn(name)) {
                    fieldStyle = (type.equals("checkbox") || type.equals("radio")) ? "inputFieldErrorCheckbox" : "inputFieldError";
                }
            }

            if (attributes.containsKey("class")) {
                attributes.put("class", fieldStyle + " " + attributes.get("class"));
            } else {
                attributes.put("class", fieldStyle);
            }

            // generate the HTML
            if (type.equals("textarea")) {
				out.print("<textarea name=\"" + name + "\" id=\"" + name + "\" rows=\"" + rows + "\" cols=\"" + cols + "\" ");
				out.print(this.getAttributeString() + ">");
				out.print(Text.escapeHtml(value)); 
				out.print("</textarea>");
            } else {
	            out.print("<input name=\"" + name + "\" id=\"" + name + "\" " + this.getAttributeString() + " type=\"" + type + "\"");
	            if (type.equals("checkbox") || type.equals("radio")) {
	                String trueValue = "true";
	                if (getTrueValue() != null) {
	                    trueValue = getTrueValue();
	                }
	                out.print(" value=\"" + trueValue + "\"" + ((value.equals(trueValue)) ? " checked" : ""));
	            } else {
	                out.print(" value=\"" + Text.escapeHtml(value) + "\"");
	            }
	            out.print("/>");
            }
        } catch (IOException ex) {
            // ignore these errors, since they can occur when the user hits 'stop' in their browser
		} catch (Throwable t) {
			// WAS does not log exceptions that occur within tag libraries; log and rethrow
			t.printStackTrace();
			throw (JspException) new JspException("Exception occurred in InputTag").initCause(t);
        }
        // contents of input tags are not evaluated 
        return SKIP_BODY;
    }

    /** End of custom tag library processing. This method performs no work.
     *
     *  @return This tag always returns TagSupport.SKIP_BODY .
     **/
    public int doEndTag() {
        // reset attributes
        id = null;
        name = null;
        value = null;
        type = null;
        clearAttributes();

        return SKIP_BODY;
    }
}
