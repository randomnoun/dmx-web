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

import javax.servlet.http.HttpServletRequest;

/**
 * This object is responsible for updating a ProgressTO object
 * as an InputStream is being read. 
 * 
 * <p>I think this is all a bit
 * over-engineered for my tastes, but it does work. Might go through it later and refactor
 * it a bit. 
 *
 * @author knoxg (based on plosson on 06-janv.-2006 15:05:44)
 * @version $Id: InputStreamProgressListener.java,v 1.3 2013-09-25 04:42:17 knoxg Exp $ (based on 1.0)
 */
public class InputStreamProgressListener implements InputStreamListener
{
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id: InputStreamProgressListener.java,v 1.3 2013-09-25 04:42:17 knoxg Exp $";

    // private HttpServletRequest request;
    private FileProgressTO fileProgress;
    /*
    private long startTime = 0;
    private int totalToRead = 0;
    private int totalBytesRead = 0;
    private int totalFiles = -1;
    */

    public InputStreamProgressListener(FileProgressTO fileProgress) {
    	this.fileProgress = fileProgress;
    }

    public void start() {
    	fileProgress.incrementFileIndex();
        fileProgress.setStatus(ProgressTO.STATUS_START);
    }

    public void error(String message) {
    	// @TODO message is discarded here ?
    	fileProgress.setStatus(ProgressTO.STATUS_ERROR);
    }

	public void bytesRead(long bytesRead) {
		fileProgress.setStatus(ProgressTO.STATUS_PROGRESS);
		fileProgress.addBytesDone(bytesRead);
	}
    
    public void done() {
    	fileProgress.setStatus(ProgressTO.STATUS_DONE);
    }

    // @TODO misnamed
    /*
    private long getDelta() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    private void updateProgress(String status) {
        long delta = (System.currentTimeMillis() - startTime) / 1000;
        request.getSession().setAttribute("uploadInfo", new ProgressTO(totalFiles, totalToRead, totalBytesRead,delta,status));
    }
    */


}
