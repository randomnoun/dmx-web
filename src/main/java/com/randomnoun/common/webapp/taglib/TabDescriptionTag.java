package com.randomnoun.common.webapp.taglib;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

import java.io.*;

import jakarta.servlet.jsp.*;
import jakarta.servlet.jsp.tagext.*;

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
public class TabDescriptionTag
    extends BodyTagSupport
{
    
    
    
	/** A PrintWriter which is used to render the tabsheet body. */
	private PrintWriter tabPrintWriter;

	/** An internal buffer for tabPrintWriter to write into. */
	private ByteArrayOutputStream tabBuffer;

    /** The enclosing TabGroupTag element of this tag. */
    private TabTag tab;

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
        throws jakarta.servlet.jsp.JspException
    {
		tabBuffer = new ByteArrayOutputStream();
		tabPrintWriter = new PrintWriter(tabBuffer);
        
        try {
            tab = (TabTag)findAncestorWithClass(this,
                    Class.forName("com.randomnoun.common.webapp.taglib.TabTag"));
        } catch (ClassNotFoundException cnfe) {
            // we will treat this as equivalent to the tab not being surrounded by a tabGroup.
        }
        if (tab == null) {
            System.out.println("tabDescription tag not within tab");
            throw new JspException("tabDescription tag must be contained within tab");
        }
        return BodyTag.EVAL_BODY_BUFFERED;
    }

    /**
     * Captures the output of this tab's body if it has been evaluated, and writes it
     * to the TabGroupTag's tabPrintWriter.
     *
     * @return This method always returns BodyTag.SKIP_BODY.
     *
     * @throws jakarta.servlet.jsp.JspException if an I/O exception occurred writing to the
     *   tabPrintWriter.
     */
    public int doAfterBody()
        throws jakarta.servlet.jsp.JspException
    {
		try {
			getBodyContent().writeOut(tabPrintWriter);
			tabPrintWriter.flush();
			tabPrintWriter.close();
			tab.setDescription(tabBuffer.toString());
		} catch (IOException ex) {
			// ignore these errors, since they can occur when the user hits 'stop' in their browser
		} catch (Throwable t) {
			// WAS does not log exceptions that occur within tag libraries; log and rethrow
			t.printStackTrace();
			throw (JspException) new JspException("Exception occurred in TabDescriptionTag").initCause(t);
		}
		return EVAL_PAGE;
    }
}
