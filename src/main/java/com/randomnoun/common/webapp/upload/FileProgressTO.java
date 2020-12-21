package com.randomnoun.common.webapp.upload;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

import java.io.Serializable;

import org.apache.log4j.Logger;

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
 * A container object for upload progress information. A javascripted version of this object
 * is sent back to the browser using the DWR remoting toolkit. 
 *
 * @author knoxg (based on plosson on 06-janv.-2006 12:19:14)
 * @version $Id: FileProgressTO.java,v 1.11 2013-09-25 04:42:17 knoxg Exp $ (based on 1.0)
 */
public class FileProgressTO extends ProgressTO
{
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id: FileProgressTO.java,v 1.11 2013-09-25 04:42:17 knoxg Exp $";

    static Logger logger = Logger.getLogger(FileProgressTO.class);
    
	/** Total size of transfer (includes all files in the HTTP request)  */
    private long totalSize = 0;
    
    /** Total number of bytes read/written (includes all files in the HTTP request) */
    private long bytesDone = 0;
    
    /** Elapsed time, in milliseconds */
    private long elapsedTime = 0;
    
    /** The (zero-based) index of the file currently being processed */
    private int fileIndex = 0;

	/** Empty constructor */
    public FileProgressTO()
    {
    }

	/** Create a new UploadInfo object
	 * 
	 * @param fileIndex which file is currently being processed (if more than one files are
	 *   being uploaded in the same HttpServletRequest)
	 * @param totalSize the total number of bytes being sent as part of this request
	 * @param bytesDone the total number of bytes that have been received by the server
	 * @param elapsedTime the amount of time spent receiving this request, measured in milliseconds
	 * @param status a short string expressing the status of this upload (@TODO make all values an 
	 *   enumerated public static final String type)
	 */
    public FileProgressTO(int fileIndex, long totalSize, long bytesDone, long elapsedTime, String status)
    {
    	super(status, (double) bytesDone / totalSize);
        this.fileIndex = fileIndex;
        this.totalSize = totalSize;
        this.bytesDone = bytesDone;
        this.elapsedTime = elapsedTime;
    }


    /** Returns the total number of bytes being sent as part of this request
     * 
     * @return the total number of bytes being sent as part of this request
     */
    public long getTotalSize()
    {
        return totalSize;
    }
    
    /** Set the the total number of bytes being sent as part of this request
     * 
     * @param totalSize the total number of bytes being sent as part of this request
     */
    public void setTotalSize(long totalSize)
    {
        this.totalSize = totalSize;
        setPercentDone((double) bytesDone / this.totalSize);
    }

	/** Returns the total number of bytes that have been received by the server
	 * 
	 * @return the total number of bytes that have been received by the server
	 */
    public long getBytesDone() {
        return bytesDone;
    }
    
    /** Set the total number of bytes that have been received by the server
     * 
     * @param bytesRead the total number of bytes that have been received by the server
     */
    public void setBytesDone(long bytesDone) {
        this.bytesDone = bytesDone;
        setPercentDone((double) this.bytesDone / totalSize);
    }
    
    public void addBytesDone(long bytesDone) {
    	this.bytesDone += bytesDone;
    	setPercentDone((double) this.bytesDone / totalSize);
    }

	/** Returns the amount of time spent receiving this request, measured in milliseconds
	 * 
	 * @return the amount of time spent receiving this request, measured in milliseconds
	 */
    public long getElapsedTime() {
        return elapsedTime;
    }
    
    /** Set the the amount of time spent receiving this request, measured in milliseconds
     * 
     * @param elapsedTime the amount of time spent receiving this request, measured in milliseconds
     */
    public void setElapsedTime(long elapsedTime)
    {
        this.elapsedTime = elapsedTime;
    }


	/** Return the current (zero-based) file index; i.e. the number of the file currently being
	 *   processed in a multi-file HTTP request
	 * 
	 * @return the current (zero-based) file index
	 */
    public int getFileIndex() {
        return fileIndex;
    }

	/** Set the the current (zero-based) file index; i.e. the number of the file currently being
	 *   processed in a multi-file HTTP request
	 * 
	 * @param fileIndex the current (zero-based) file index
	 */
    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }
    
    public void incrementFileIndex() {
    	this.fileIndex++;
    }

    // shouldn't be necessary, but maybe this is why DWR isn't transferring fields properly
	public double getPercentDone() {
		return super.getPercentDone();
	}

	/** Sets the percentDone
	 * @param percentDone the percentDone to set
	 */
	public void setPercentDone(double percentDone) {
		super.setPercentDone(percentDone);
	}
	
	public String toString() {
		return "fileIndex=" + fileIndex + ", " +
		  "percentDone=" + getPercentDone() + ", " +
		  "totalSize=" + totalSize + ", " +
		  "bytesDone=" + bytesDone;
	}
	

    
}
