package com.randomnoun.common.webapp.upload;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Original comment text:
 * 
 * Licence:
 *   Use this however/wherever you like, just don't blame me if it breaks anything.
 *
 * Credit:
 *   If you're nice, you'll leave this bit:
 *
 *    Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
 *   email : plosson@users.sourceforge.net
 */

/**
 * A container object for generic progress information (originally used for file
 * uploads).
 *  
 * A javascripted version of this object
 * is sent back to the browser using the DWR remoting toolkit. 
 *
 * @author knoxg (based on plosson on 06-janv.-2006 12:19:14)
 * @version $Id: ProgressTO.java,v 1.5 2013-09-25 04:42:16 knoxg Exp $ (based on 1.0)
 */
public class ProgressTO implements Serializable
{
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id: ProgressTO.java,v 1.5 2013-09-25 04:42:16 knoxg Exp $";

	/** Status string used to denote that progress has begun, but no activity has occurred */
	public static final String STATUS_START = "start";

	/** Status string used to denote that the task is in progress */
	public static final String STATUS_PROGRESS = "progress";
	
	/** Status string used to denote that the task has resulted in an error */
	public static final String STATUS_ERROR = "error";

	/** Status string used to denote that the task has completed */
	public static final String STATUS_DONE = "done";


	// @TODO handle indeterminate progress bars
	/** Percent complete  */
    private double percentDone = 0;
    
    // @TODO after upgrading to a newer DWR, change this to an enum
    /** Current status (one of the STATUS_* constants in this class) */
    private String status = STATUS_DONE;
    
    private List<ProgressTO> subtasks = null;
    
	/** Empty constructor */
    public ProgressTO()
    {
    }

	/** Create a new ProgressTO object
	 * 
	 * @param status a short string expressing the status of this upload 
	 * @param percentDone the percentage of this task which is complete 
	 */
    public ProgressTO(String status, double percentDone)
    {
        this.status = status;
        this.percentDone = percentDone;
    }

	/** Return the status of this upload 
	 * 
	 * @return the status of this upload
	 */
    public String getStatus() {
        return status;
    }

	/** Set the status of this upload
	 * 
	 * @param status the new status of this upload
	 */
    public void setStatus(String status) {
        this.status = status;
    }

    /** Returns true if the status of this progressTO is either 'progress' or 'start'; false otherwise
     * 
     * @return true if the status of this progressTO is either 'progress' or 'start'; false otherwise
     */
    public boolean isInProgress() {
        return STATUS_PROGRESS.equals(status) || STATUS_START.equals(status);
    }
    
    public List getSubtasks() {
    	return subtasks;
    }
    
    // for JavaBeans introspection
    public void setSubtasks(List subtasks) {
    	this.subtasks = subtasks;
    }
    
    public synchronized void addSubtask(ProgressTO subtask) {
    	if (subtasks==null) {  
    		subtasks = Collections.synchronizedList(new ArrayList<ProgressTO>());
    	}
    	subtasks.add(subtask);
    }
    
    public void removeSubtask(ProgressTO subtask) {
    	if (subtasks==null) { throw new IllegalStateException("No subtasks assigned to this task"); }
    	if (!subtasks.remove(subtask)) {
    		throw new IllegalStateException("Subtask [" + subtask.toString() + "] is not a subtask of this task");
    	}
    }

	/**  Returns the percentDone
	 * @return the percentDone
	 */
	public double getPercentDone() {
		return percentDone;
	}

	/** Sets the percentDone
	 * @param percentDone the percentDone to set
	 */
	public void setPercentDone(double percentDone) {
		this.percentDone = percentDone;
	}
	
	public String toString() {
		return "ProgressTO " + super.hashCode() + ": status='" + status + 
			"', percentDone=" + percentDone + ", " +
			(subtasks==null ? "no subtasks" : subtasks.size() + " subtask(s)"); 
	
	}

}
