package com.randomnoun.common.webapp.taglib;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

import java.io.*;
import java.util.*;
import jakarta.servlet.jsp.*;
import jakarta.servlet.jsp.tagext.*;

import org.apache.log4j.Logger;


/**
 * Container tag to display tabs on a JSP page.
 *
 * <p>This tag allows a set of {@link TabTag} objects to be embedded in a HTML
 * document, any one of which may be active at one time. Only the body of
 * the currently selected tab will be evaluated by the JSP engine, and sent to the
 * end-user.
 *
 * <p>An simple example of this tag follows (note that JSTL may be used in any
 * tabgroup attribute):
 *
 * <pre style="code">
 * &lt;mm:tabgroup currentTabId="${param.selected}" layout="normal" &gt;
 *   &lt;mm:tab id="firstTab" label="Hello there" href="helloThere.jsp?selected=firstTab" &gt;
 *     This is the body of the first tab
 *   &lt;/mm:tab>
 *   &lt;mm:tab id="secondTab" label="Second tab" href="helloThere.jsp?selected=secondTab" &gt;
 *     This is the body of the second tab
 *   &lt;/mm:tab>
 *   &lt;mm:tab id="thirdTab" label="Third tab" href="helloThere.jsp?selected=thirdTab" &gt;
 *     This is the body of the third tab
 *   &lt;/mm:tab&gt;
 * &lt;/mm:tabGroup&gt;
 * </pre>
 *
 * <p>which may be rendered as:
 *
 * <p><img src="doc-files/tabgroupExample.gif" />
 *
 * <p>... changing the layout attribute to '<tt>round</tt>' will display the
 * same tabgroup as:
 *
 * <p><img src="doc-files/tabgroupExample2.gif" />
 *
 * <p>An individual tab may also contain a number of 'sub-pages', which can be rendered underneath
 * the same tab header. These pages are identified by a period following the tab id; e.g. in
 * the example above, we could place two sub-pages under the second tab using the
 * JSP syntax:
 *
 * <pre style="code">
 *   &lt;mm:tab id="secondTab<b>.1</b>" label="Second tab" href="helloThere.jsp?selected=secondTab" &gt;
 *     This is the body of the second tab
 *   &lt;/mm:tab&gt;
 *   &lt;mm:tab id="secondTab<b>.2</b>" label="Second tab" href="helloThere.jsp?selected=secondTab" &gt;
 *     This text may be substited in the second tab.
 *   &lt;/mm:tab&gt;
 * </pre>
 *
 * <p>It is intended that only the servlet that generates the JSP can decide which
 * 'sub-page' is displayed for a given tab. If a subpage identifier is not specified, then
 * the tab will default to subpage '1'.
 *
 * <p>Attributes defined for this tag (in common.tld) are:
 * <attributes>
 * currentTabId - specifies which tab is currently selected. If not set, defaults to the
 *   first tab in the tabgroup
 * layout - specifies how to display the tabgroup. Supported layouts are
 *   <attributes>
 *   normal - square tabs
 *   round - rounded tabs
 *   config - rounded tabs with extra buttons that I will document at some point in the future.  
 *   wizard - rounded tabs with extra buttons that I will document at some point in the future.
 *   </attributes>
 * </attributes>
 *
 * <p>All attributes may include JSTL EL expressions (of the form <tt>${<i>expr</i>}</tt> ).
 *
 * <p>External styles/javascript required for this tag:
 * Each tab label has the "tab" CSS class applied, except for the selected tab, which
 * has the class "selecttab". The body of the tab content is inside a DIV which is
 * assigned the CSS class "tabsheet". It is recommended that the tabsheet style has a
 * fixed height and width assigned to it in order for pages to be displayed consistently
 * between tabs.
 *
 * <p>The Master.css stylesheet contains default styles for these classes.
 *
 * This tag uses no request or session attributes.
 *
 * 
 */
public class TabGroupTag
    extends BodyTagSupport {
    
    
    
    
    /** Logger instance for this class */
    public static final Logger logger = Logger.getLogger(TabGroupTag.class);

    /** The layout type for this tabgroup. */
    private String layout = null;

    /** The id of the currently selected tab. */
    private String currentTabId;
    
    /** The id of the tabsheet */
    private String tabsheetId;
    
    /** Hide tabsheet? */
    private String hideTabsheet;
    
    /** Onscroll handler for the tabsheet */
    private String onscroll;

    // these are now populated by <tab> tags, not by
    // attributes in the group itself.


    /** A list of all the TabTag objects containined in this tab group. */
    private List tabDefs = new ArrayList();

    /** A PrintWriter which is used to render the tabsheet body. */
    private PrintWriter tabPrintWriter;

    /** An internal buffer for tabPrintWriter to write into. */
    private ByteArrayOutputStream tabBuffer;

    /* ***********************************************************************
     * Tag Library attribute methods
     */


    /** Sets the layout type for this tab group.
     *  @param value the layout type for this tab group.
     */
    public void setLayout(String layout) {
        this.layout = layout;
    }

    /** Retrieves the layout type for this tab group.
     *  @return value the layout type for this tab group.
     */
    public String getLayout() {
        return layout;
    }

    /** Sets the id of the currently selected tab. This id must correspond to the
     *  id a TabTag contained within this TabGroupTag.
     *
     * @param currentTabId the id of the currently selected tab.
     */
    public void setCurrentTabId(String currentTabId) {
        this.currentTabId = currentTabId;
    }

    /** Retrieves the id of the currently selected tab.
     * @return the id of the currently selected tab.
     */
    public String getCurrentTabId() {
        return currentTabId;
    }

	/** Sets the onscroll handler of the tabsheet. 
	 *
	 * @param onscroll the onscroll handler of the tabsheet.
	 */
	public void setOnscroll(String onscroll) {
		this.onscroll = onscroll;
	}

	/** Retrieves the onscroll handler of the tabsheet.
	 * @return the onscroll handler of the tabsheet.
	 */
	public String getOnscroll() {
		return onscroll;
	}


	/** Setsthe id of the tabsheet.
	 * @param tabsheetId the id of the tabsheet.
	 */
	public void setTabsheetId(String tabsheetId) {
		this.tabsheetId = tabsheetId;
	}

	/** Retrieves the id of the tabsheet.
	 * @return the id of the tabsheet.
	 */
	public String getTabsheetId() {
		return tabsheetId;
	}

	/** Sets whether the tabsheet DIV should be generated
	 * @param tabsheetId 'false' if it is to be hidden, any other value (including null) otherwise
	 */
	public void setHideTabsheet(String hideTabsheet) {
		this.hideTabsheet = hideTabsheet;
	}

	/** Retrieves whether the tabsheet DIV should be generated.
	 * @return 'false' if it is to be hidden, any other value (including null) otherwise
	 */
	public String getHideTabsheet() {
		return hideTabsheet;
	}


    /* ***********************************************************************
     * End of tag library attribute methods
     */



	/** Sets the number of rows to display in the list boxes
     *  @param value the number of rows to display in the list boxes
     */
    public void addTabDef(TabTag.TabDef tabDef) {
        tabDefs.add(tabDef);
    }

    /** Returns the total number of tabs contained in this tabgroup.
     * @return the total number of tabs contained in this tabgroup.
     */
    public int getNumTabs() {
        return tabDefs.size();
    }

    /**
     * Returns the internal PrintWriter used by TabTag objects to render their content.
     * @return A PrintWriter object that can be used by the currently selected TabTag object.
     */
    public PrintWriter getTabPrintWriter() {
        return tabPrintWriter;
    }

    /**
     * Perform custom tag processing. This method simply sets up a new tabBuffer and
     * tabPrintWriter for use by TabTags within this TabGroupTag.
     *
     * @return always returns BodyTag.EVAL_BODY_BUFFERED, so that the body content of this
     *   tag can be post-processed in the doEndTag() method.
     */
    public int doStartTag()
        throws jakarta.servlet.jsp.JspException 
    {
    	try {
	
	        tabBuffer = new ByteArrayOutputStream();
	        tabPrintWriter = new PrintWriter(tabBuffer);
	
	        return BodyTag.EVAL_BODY_BUFFERED; // EVAL_BODY_INCLUDE ?
		} catch (Throwable t) {
			// WAS does not log exceptions that occur within tag libraries; log and rethrow
			t.printStackTrace();
			throw (JspException) new JspException("Exception occurred in InputTag").initCause(t);
		}
    }

    /**
     * Performs customer afterBody processing. This class does no work.
     *
     * @return always returns BodyTag.SKIPBODY.
     */
    public int doAfterBody() {
        return (SKIP_BODY);
    }

    /**
     * Writes the tab headers and the start of the tabsheet DIV to this JSP's
     * outputStream. This is invoked just prior to generating the HTML for the currently
     * selected tab.
     *
     * @throws jakarta.servlet.jsp.JspException An I/O exception occurred whilst
     *   writing to pageContext.getOut().
     */
    public void constructContainerStart()
        throws JspException {
        JspWriter out = pageContext.getOut();
        TabTag.TabDef tabDef;

        try {
            if (getLayout() == null || getLayout().equals("normal")) {
                out.println("<div class=\"tabgroup\">");

                for (int i = 0; i < tabDefs.size(); i++) {
                    tabDef = (TabTag.TabDef) tabDefs.get(i);

                    if (tabDef.isId(currentTabId)) {
                        out.println("<span class=\"selecttab\">" + tabDef.label + "</span>");
                    } else if (tabDef.isFirstMinor()) {
                        out.println("<span class=\"tab\"><a onclick=\"top.setWait(true)\" href=\"" + 
                          tabDef.href + "\">" + tabDef.label + "</a></span>");
                    }
                }

                out.println("</div>");
                if (!"true".equalsIgnoreCase(hideTabsheet)) {
	                out.println("<div id=\"" + (tabsheetId==null ? "tabsheetId" : tabsheetId) + "\"" +
	                  (onscroll==null ? "" : " onscroll=\"" + onscroll + "\"") + 
	                  " class=\"tabsheet\">");
                }
            } else if (getLayout().equals("wizard")) {
				out.println("<div class=\"wizard\">");
				out.println("<div class=\"wizardHeaderDiv\" id=\"wizardHeaderDiv\"");
				String stepLabel;
				for (int i = 0; i < tabDefs.size(); i++) {
					tabDef = (TabTag.TabDef) tabDefs.get(i);
					stepLabel = "Step " + (i+1) + " of " + tabDefs.size() + " - ";
					// logger.debug("Comparing tabTag '" + tabDef + "' against currentTabId '" + currentTabId + "': " +
					// (tabDef.isId(currentTabId) ? " matches" : " does not match"));
					
					if (tabDef.isId(currentTabId)) {
						if (tabDef.onclick == null) {
							out.println("<p><span class=\"wizardHeader\">" + stepLabel + tabDef.label + "</span><br/><br/>");
							if (tabDef.description != null)
								out.println(tabDef.description);
							out.println("</p>");
						} else {
							out.println("<p><span class=\"wizardHeader\">" + stepLabel + tabDef.label + "</span><br/><br/>");
							if (tabDef.description != null)					
								out.println(tabDef.description);
							out.println("</p>");
						}
					} 
				}
				out.println("</div></div>");
				if (!"true".equalsIgnoreCase(hideTabsheet)) {
					out.println("<div id=\"tabsheetId\" class=\"tabsheet\">");
				}

            } else {
                out.println("<div class=\"tabs\">");
                out.println("<ul>");

                for (int i = 0; i < tabDefs.size(); i++) {
                    tabDef = (TabTag.TabDef) tabDefs.get(i);

					logger.debug("Comparing tabTag " + tabDef + " '" + tabDef.id + "' against currentTabId '" + currentTabId + "': " +
					  (tabDef.isId(currentTabId) ? " matches" : " does not match"));

                    if (tabDef.isId(currentTabId)) {
                        if (tabDef.onclick == null) {
                            out.println("<li id=\"current\"><a onclick=\"top.setWait(true)\" href=\"" + 
                              tabDef.href + "\">" + tabDef.label + "</a></li>");
                        } else {
                            out.println("<li id=\"current\"><a onclick=\"top.setWait(true); " + 
                              tabDef.onclick + "\" href=\"#\">" + tabDef.label + "</a></li>");
                        }
                    } else if (tabDef.isFirstMinor()) {
                        if (tabDef.onclick == null) {
                            out.println("<li><a onclick=\"top.setWait(true)\" href=\"" + 
                              tabDef.href + "\">" + tabDef.label + "</a></li>");
                        } else {
                            out.println("<li><a onclick=\"top.setWait(true); " + 
                              tabDef.onclick + "\" href=\"#\">" + tabDef.label + 
                              "</a></li>");
                        }
                    }
                }

                out.println("</ul></div>");
				if (!"true".equalsIgnoreCase(hideTabsheet)) {
	                out.println("<div id=\"" + (tabsheetId==null ? "tabsheetId" : tabsheetId) + "\"" +
					  (onscroll==null ? "" : " onscroll=\"" + onscroll + "\"") + 
					  " class=\"tabsheet\">");
				}
            }
        } catch (IOException ioe) {
            throw new JspException(ioe);
        }
    }


    /**
     * Closes off the tabsheet DIV. This is invoked just after generating the HTML for the
     * currently selected tab.
     *
     * @throws jakarta.servlet.jsp.JspException An I/O exception occurred whilst
     *   writing to pageContext.getOut().
     */
    public void constructContainerEnd()
        throws JspException {
        JspWriter out = pageContext.getOut();

	    try {
	    	if (!"true".equalsIgnoreCase(hideTabsheet)) {
	    		out.println("</div>");
	    	}
        } catch (IOException ioe) {
            throw new JspException(ioe);
        }
    }

    /** Perform custom tag processing. This method generates the HTML for the
     * tab selection buttons, the tabsheet container and the tabsheet itself.
     *
     * @return This method always returns BodyTag.EVAL_PAGE
     *
     * @throws jakarta.servlet.jsp.JspException An I/O exception occurred whilst
     *   writing to pageContext.getOut().
     */
    public int doEndTag()
        throws jakarta.servlet.jsp.JspException {
        try {
        	constructContainerStart();
			
            // display tab content
            tabPrintWriter.flush();
            tabPrintWriter.close();

            // System.out.println("** size of tabgroup buffer: " + tabBuffer.size());
            pageContext.getOut().print(tabBuffer.toString());
            constructContainerEnd();
		} catch (Throwable t) {
			// WAS does not log exceptions that occur within tag libraries; log and rethrow
			t.printStackTrace();
			throw (JspException) new JspException("Exception occurred in TabGroupTag").initCause(t);
		} finally {
			hideTabsheet = null;
			layout = null;
			currentTabId = null;
			tabsheetId = null;
			onscroll = null;

			tabDefs.clear();
		}

		// in case WAS decides to reuse this tag instance
        return EVAL_PAGE;
    }
}
