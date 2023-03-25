package com.randomnoun.common.webapp.taglib;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

import java.io.*;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;


/**
 * Custom JSP tag which is used to create a group of tabs.
 *
 * TabTags must only ever be declared on a JSP within the body of a {@link TabGroupTag}.
 * TabGroupTags may not (currently) be nested <i>(well, this could conceivably work,
 * but I certainly haven't tested for it)</i>.
 * The content of the TabTag element will only be evaluated if the TabTag in question is
 * the currently selected tab (as specified in the TabGroupTag).
 *
 * Please see the javadoc for {@link TabGroupTag} for usage information, and examples.
 *
 * <p>Attributes defined for this tag (in common.tld) are:
 * <attributes>
 * id - The unique id for this label. An id can comprise of either a text string '<tt>example</tt>',
 *   or a <i>minor</i>.<i>major</i> compound text string e.g. '<tt>example.3</tt>'.
 *   A minor id should always be a number. Tab headings for tabs with minor IDs will not be
 *   rendered unless the currently selected tab is equivalent to the entire 'major.minor' string.
 *   See {@link TabGroupTag} for more information.
 * label - A text string to display in the tab header for this tab.
 * href - A URL to be used in the &lta href&gt; HTML element surrounding this tab header.
 * onclick - Some javascript to execute when the tab is selected. Either href or onclick must be specified, not both
 * </attributes>
 *
 * <p><b>label</b> and <b>id</b> attributes may include JSTL EL expressions
 * (of the form <tt>${<i>expr</i>}</tt> ).
 *
 * This tag uses no request or session attributes.
 *
 * @author knoxg
 * 
 */
public class TabTag
    extends BodyTagSupport
{
    
    
    
	/** A PrintWriter which is used to render the tabsheet body. */
	private PrintWriter tabPrintWriter;

	/** An internal buffer for tabPrintWriter to write into. */
	private ByteArrayOutputStream tabBuffer;

    /** The enclosing TabGroupTag element of this tag. */
    private TabGroupTag tabGroup;

    /** Tab definition object. */
    private TabDef tabDef = new TabDef();

    class TabDef {

    	/** A text string to display in the tab header for this tab. */
        String label; // tab label

    	/** A text string to display the description in the header for the wizard tab. */
    	String description; // tab label

        /** The tab id */
    	String id;
    	
    	/** The minor tab id. (The text after the '.' in a compound id) */
        String idMinor;

        /** The target of the href surrounding this tab's header. */
        String href;

    	/** The onclick attribute for this tab. */
    	String onclick;

    	/** The next action attribute for this tab. */
    	String next;
    	
    	/** The back action attribute for this tab. */
    	String back;
    	
    	/** The finish action attribute for this tab. */
    	String finish;		

        /** Will be set to <tt>true</tt> if this tab is the currently selected tab in the enclosing TabGroup. */
        boolean isSelected = false;

        /** Will be set to <tt>true</tt> if this tab is the first tab displayed in the enclosing TabGroup. */
        boolean isFirst = false;

        /**
         * Returns true if the id of this tab matches the supplied testId.
         * <ul>
         * <li>If the testId supplied is a compound id, then this method will only
         * return true if this tab also has a compound id, and both major and
         * minor elements are equivalent.
         * <li>If the testId supplied is <i>not</i> a compound id, then this method will
         * only return true if this tab is (not a compound id and has the
         * same id as testId) or (is a compound id and and the minor id of
         * this tab is set to '1').
         * </ul>
         *
         * @param testId The id we are using for comparison.
         *
         * @return <tt>true</tt> if the id's match using the rules described above.
         */
        public boolean isId(String testId)
        {
            int pos;
            if (testId == null) {
                return false;
            }

            pos = testId.indexOf(".");
            if (pos == -1) {
                return (testId.equals(id) && (tabDef.idMinor == null || tabDef.idMinor.equals("1")));
            } else {
                String testIdMajor = testId.substring(0, pos);
                String testIdMinor = testId.substring(pos + 1);
                return (testIdMajor.equals(id) && testIdMinor.equals(tabDef.idMinor));
            }
            
            // could do getters/setters here
        }

        /**
         * Returns true if this is the first minor tab (i.e. this tab has a compound
         * id, and the minor component contains '1').
         *
         * @return <tt>true</tt> if this is the first minor tab.
         */
        public boolean isFirstMinor() {
            return tabDef.idMinor == null || tabDef.idMinor.equals("1");
        }

        
    }

    /* ***********************************************************************
     * Tag Library attribute methods
     */

    /**
     * Sets the id of this tab. If this id is compound, then it will be broken into major and
     * minor components.
     *
     * @param id The id passed in from the JSP holding this TabTag.
     */
    public void setId(String id)
    {
        int pos;

        this.id = id;
        this.tabDef.id = id;
        pos = id.indexOf(".");

        if (pos == -1) {
            // this.id = id;
        } else {
            this.id = id.substring(0, pos);
        	this.tabDef.id = id.substring(0, pos);
            this.tabDef.idMinor = id.substring(pos + 1);
        }
    }

    /**
     * Sets the text string to display in the tab header for this tab.
     * @param label the text string to display in the tab header for this tab.
     */
    public void setLabel(String label) {
        this.tabDef.label = label;
    }

    /**
     * Retrieves the text string to display in the tab header for this tab.
     * @return the text string to display in the tab header for this tab.
     */
    public String getLabel() {
        return tabDef.label;
    }

	/**
	 * Sets the text string to display for the wizard  in the tab header for this tab.
	 * @param label the text string to display in the tab header for this tab.
	 */
	public void setDescription(String description) {
		this.tabDef.description = description;
	}

	/**
	 * Retrieves the text string for the wizard to display in the tab header for this tab.
	 * @return the text string to display in the tab header for this tab.
	 */
	public String getDescription() {
		return tabDef.description;
	}

    /**
     * Sets the target of the href surrounding this tab's header.
     * @param href the target of the href surrounding this tab's header.
     */
    public void setHref(String href) {
        this.tabDef.href = href;
    }

    /**
     * Retrieves the target of the href surrounding this tab's header.
     * @return the target of the href surrounding this tab's header.
     */
    public String getHref() {
        return tabDef.href;
    }

	/**
	 * Sets the target of the href surrounding this tab's header.
	 * @param href the target of the href surrounding this tab's header.
	 */
	public void setNext(String next) {
		this.tabDef.next = next;
	}

	/**
	 * Retrieves the target of the href surrounding this tab's header.
	 * @return the target of the href surrounding this tab's header.
	 */
	public String getNext() {
		return tabDef.next;
	}

	/**
	 * @return
	 */
	public String getBack() {
		return tabDef.back;
	}

	/**
	 * @return
	 */
	public String getFinish() {
		return tabDef.finish;
	}

	/**
	 * @param string
	 */
	public void setBack(String string) {
		tabDef.back = string;
	}

	/**
	 * @param string
	 */
	public void setFinish(String string) {
		tabDef.finish = string;
	}

	/**
	 * Sets the onclick attribute for this tab header.
	 * @param href the onclick attribute for this tab header.
	 */
	public void setOnclick(String onclick) {
		this.tabDef.onclick = onclick;
	}

	/**
	 * Retrieves the onclick attribute for this tab header.
	 * @return the onclick attribute for this tab header.
	 */
	public String getOnclick() {
		return tabDef.onclick;
	}


    /* ***********************************************************************
     * End of tag library attribute methods
     */


    /**
     * Perform custom tag processing. This method ensures that this TabTag exists within
     * a TabGroupTag, and conditionally evaluates the body of this tab if it is
     * the 'currently selected' tab specified in the TabGroup. The evaluated body
     * will be placed into the enclosing TabGroupTag's tabPrintWriter by the
     * {@link #doAfterBody()} method.
     *
     * @return will return BodyTag.EVAL_BODY_BUFFERED if this tab is to be evalated,
     *   otherwise BodyTag.SKIP_BODY.
     */
    public int doStartTag()
        throws javax.servlet.jsp.JspException
    {
		tabBuffer = new ByteArrayOutputStream();
		tabPrintWriter = new PrintWriter(tabBuffer);
        
        tabGroup = null;
        tabDef.isSelected = false;
        tabDef.isFirst = false;

        try {
            tabGroup = (TabGroupTag)findAncestorWithClass(this,
              Class.forName("com.randomnoun.common.webapp.taglib.TabGroupTag"));
        } catch (ClassNotFoundException cnfe) {
            // we will treat this as equivalent to the tab not being surrounded by a tabGroup.
        }

        if (tabGroup == null) {
            System.out.println("tab tag not within tabGroup");
            throw new JspException("tab tag must be contained within tabGroup");
        }
        if (tabGroup.getNumTabs() == 0) {
        	tabDef.isFirst = true;
        }
        
        // @XXX: this doesn't work since tomcat's jasper caches tab instances
        tabGroup.addTabDef(tabDef);
        tabDef.isSelected = tabDef.isId(tabGroup.getCurrentTabId());

        if (tabDef.isSelected ||
            (tabGroup.getCurrentTabId() == null && tabDef.isFirst && tabDef.isFirstMinor()))
        {
            tabGroup.setCurrentTabId(getId());

            // System.out.println("** got here (selected tab)");
            return BodyTag.EVAL_BODY_BUFFERED;
        } else {
            // System.out.println("** got here (non-selected tab)");
            return BodyTag.SKIP_BODY;
        }
    }

	/**
	 * Closes off the tabsheet DIV. This is invoked just after generating the HTML for the
	 * currently selected tab.
	 *
	 * @throws javax.servlet.jsp.JspException An I/O exception occurred whilst
	 *   writing to pageContext.getOut().
	 */
	public void constructContainerEnd()
		throws JspException {
		JspWriter out = pageContext.getOut();

		try {

			if (tabGroup.getLayout().equals("config")) {	
				String id = this.getId();	
				out.println("<table><tr>");
				out.println("<td><input type=\"hidden\" name=\"tabType\" value=\"config\"/>");
				out.println("<input type=\"hidden\" name=\"tabId\" value=\"" + id + "\"/></>");																
				out.println("<td><input name=\"ok\" id=\"ok\" type=\"submit\" value=\"OK\" class=\"update-button\" style=\"width: 50px\" /></td>");
				out.println("<td><input name=\"cancel\" id=\"cancel\" type=\"button\" value=\"Cancel\" onclick=\"cancelOption()\" class=\"update-button\" style=\"width: 50px\" /></td>");
				out.println("<td><input name=\"delete\" id=\"delete\" type=\"button\" value=\"Delete\" onclick=\"deleteOption()\" class=\"update-button\" style=\"width: 50px\" /></td>");
				out.println("</tr></table>");   
				out.println("</form>"); 
			} else if (tabGroup.getLayout().equals("wizard")) {
				String back = this.getBack();
				String next = this.getNext();
				String finish = this.getFinish();									
				String id = this.getId();	
				
				out.println("<div class=\"wizardFooterDiv\" id=\"wizardFooterDiv\">");		
				out.println("<table <tr>");

				out.println("<input type=\"hidden\" name=\"tabType\" value=\"wizard\"/>");
				out.println("<td><input type=\"hidden\" name=\"tabId\" value=\"" + id + "\"/>");					
				out.println("<td><input type=\"hidden\" name=\"backAction\" value=\"" + back + "\"/>");
				out.println("<input type=\"hidden\" name=\"nextAction\" value=\"" + next + "\"/>");
				out.println("<input type=\"hidden\" name=\"finishAction\" value=\"" + finish + "\"/></td>");
									
				if (back != null && back.length() > 0) {	
					out.println("<td><input name=\"back\" class=\"wizardButton\" type=\"submit\" value=\"Back\" class=\"update-button\" style=\"width: 50px\" /></td>");
				} else {
					out.println("<td><input name=\"back\" class=\"wizardButton\" disabled=\"true\" type=\"submit\" value=\"Back\" class=\"update-button\" style=\"width: 50px\" /></td>");
				}
				
				if (next != null && next.length() > 0) {						
					out.println("<td><input name=\"next\" class=\"wizardButton\" type=\"submit\" value=\"Next\" class=\"update-button\" style=\"width: 50px\" /></td>");
				} else {
					out.println("<td><input name=\"next\" class=\"wizardButton\" disabled=\"true\" type=\"submit\" value=\"Next\" class=\"update-button\" style=\"width: 50px\" /></td>");
				}
				
				if (finish != null && finish.length() > 0) {						
					out.println("<td><input name=\"finish\" class=\"wizardButton\" type=\"submit\" value=\"Finish\" class=\"update-button\" style=\"width: 50px\" /></td>");
				} else {
					out.println("<td><input name=\"finish\" class=\"wizardButton\" disabled=\"true\" type=\"submit\" value=\"Finish\" class=\"update-button\" style=\"width: 50px\" /></td>");
				}

				out.println("<td><input name=\"cancel\" class=\"wizardButton\" type=\"button\" value=\"Cancel\" onclick=\"cancelOption()\" class=\"update-button\" style=\"width: 50px\" /></td>");
				out.println("</tr></table>");
				out.println("</div>");
			}				
		} catch (IOException ioe) {
			throw new JspException(ioe);
		}
	}

    /**
     * Captures the output of this tab's body if it has been evaluated, and writes it
     * to the TabGroupTag's tabPrintWriter.
     *
     * @return This method always returns BodyTag.SKIP_BODY.
     *
     * @throws javax.servlet.jsp.JspException if an I/O exception occurred writing to the
     *   tabPrintWriter.
     */
    public int doAfterBody()
        throws javax.servlet.jsp.JspException
    {

        // include this tab only if it is selected
        //System.out.println("** including tab [" + getId() + "]");
        //System.out.println("** (contents: " + getBodyContent().getString());
	    try {
			if (tabDef.isSelected) {
				constructContainerEnd();
                getBodyContent().writeOut(tabGroup.getTabPrintWriter());
				tabPrintWriter.flush();
				tabPrintWriter.close();
			}
			return EVAL_PAGE;
		} catch (Throwable t) {
			// WAS does not log exceptions that occur within tag libraries; log and rethrow
			t.printStackTrace();
			throw (JspException) new JspException("Exception occurred in TabTag").initCause(t);
		}
    }

    public int doEndTag() {
    	// ensure next usage of this instance will use a new tab definition object.
    	tabDef = new TabDef();
    	return BodyTagSupport.EVAL_PAGE;
    }
}
