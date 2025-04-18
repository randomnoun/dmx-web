package com.randomnoun.common.webapp.upload;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

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

import jakarta.servlet.http.HttpServletRequest;

/**
 * This object is responsible for updating the 'uploadInfo' session attribute by 
 * listening to events raised by a MonitoredDiskFileItem via the OutputStreamListener
 * interface. 
 * 
 * <p>Note that the methods in this class refer to bytes being read, when they are
 * in fact being written to the OutputStreamListener (prior to being handed over
 * to the struts action code, which will then read them in again). 
 * 
 * <p>I think this is all a bit
 * over-engineered for my tastes, but it does work. Might go through it later and refactor
 * it a bit. 
 *
 * @author knoxg (based on plosson on 06-janv.-2006 15:05:44)
 * @version $Id: MultipartRequestProgressListener.java,v 1.3 2013-09-25 04:42:17 knoxg Exp $ (based on 1.0)
 */
public class MultipartRequestProgressListener implements OutputStreamListener
{
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id: MultipartRequestProgressListener.java,v 1.3 2013-09-25 04:42:17 knoxg Exp $";

    /*
    private HttpServletRequest request;
    private long startTime = 0;
    private int totalToRead = 0;
    private int totalBytesRead = 0;
    private int totalFiles = -1;
    */

    private FileProgressTO fileProgress;
    
    // is called once for each request body
    public MultipartRequestProgressListener(FileProgressTO fileProgress) {
        this.fileProgress = fileProgress;
    	/*
        this.request = request;
        totalToRead = request.getContentLength();
        this.startTime = System.currentTimeMillis();
        */
    }

    // is called for each fileitem in the request body
    public void start() {
    	// @TODO this would cause status to flicker between start/in progress 
    	// as files are processed
    	// fileProgress.setStatus(ProgressTO.STATUS_START);  // really ? 
        fileProgress.incrementFileIndex();
    }

    public void bytesWritten(int bytesRead) {
    	fileProgress.setStatus(ProgressTO.STATUS_PROGRESS);
    	fileProgress.addBytesDone(bytesRead);
    	
        //updateProgress(ProgressTO.STATUS_PROGRESS);
    }

    public void error(String message) {
    	// @TODO message is discarded here ?
    	fileProgress.setStatus(ProgressTO.STATUS_ERROR);
        // updateProgress(ProgressTO.STATUS_ERROR);
    }

    public void done() {
        fileProgress.setStatus(ProgressTO.STATUS_DONE);
    }

    /*
    private long getDelta() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    private void updateProgress(String status) {
        long delta = (System.currentTimeMillis() - startTime) / 1000;
        // @TODO should probably update existing TO; this only works if no subtasks
        // have been created at this point.
        request.getSession().setAttribute(DwrProgressMonitor.PROGRESS_SESSION_KEY, 
        	new FileProgressTO(totalFiles, totalToRead, totalBytesRead, delta, status));
    }
    */

}
