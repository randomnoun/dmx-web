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

/**
 * An interface used by an object attached to a MonitoredOutputStream that can be used to 
 * trigger events during the writing of that stream.
 *
 * @author knoxg (based on plosson on 06-janv.-2006 9:59:27)
 * @version $Id: OutputStreamListener.java,v 1.4 2013-09-25 04:42:16 knoxg Exp $ (based on 1.0)
 *
 */
public interface OutputStreamListener {
	
	/** Event triggered when the stream is first opened */ 
    public void start();
    
    /** Event triggered when bytes are written to the stream */
    public void bytesWritten(int bytesWritten);
    
    /** Event triggered when an error occurs reading from the stream */
    public void error(String message);
    
    /** EVent triggered when the stream is closed */
    public void done();
}
