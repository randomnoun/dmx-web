package com.randomnoun.dmx.web;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

/**
 * Struts class used to contain file data submitted by user.
 * 
 * @version $Id$
 */
public class StrutsUploadForm extends ActionForm {

	/** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

	private FormFile attachment;
    
	/** Returns the attachment.
	 * 
	 * @see #setAttachment(FormFile)
	 * 
	 * @return Returns the attachment.
	 */
	public FormFile getAttachment() {
		return attachment;
	}
	
	/** Sets the attachment
	 * 
	 * @see #getAttachment()
	 * 
	 * @param attachment The FormFile to set.
	 */
	public void setAttachment(FormFile attachment) {
		this.attachment = attachment;
	}
	 
}
