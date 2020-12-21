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

import java.io.OutputStream;
import java.io.IOException;

/**
 * A wrapper for an OutputStream which allows an OutputStreamListener to attach to it.
 * 
 * @see com.misys.meridian.messagemanager.webapp.upload.OutputStreamListener
 * 
 * @author knoxg (based on plosson on 06-janv.-2006 10:44:18)
 * @version $Id: MonitoredOutputStream.java,v 1.4 2013-09-25 04:42:17 knoxg Exp $ (based on 1.0)
 */
public class MonitoredOutputStream extends OutputStream
{
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id: MonitoredOutputStream.java,v 1.4 2013-09-25 04:42:17 knoxg Exp $";

	/** The wrapped OutputStream object */
    private OutputStream target;
    
    /** The listener that will be used as an event sink */
    private OutputStreamListener listener;

	/** Create a new MonitoredOutputStream
	 * 
	 * @param target the OutputStream to monitor
	 * @param listener The listener to which we will send monitoring events
	 */
    public MonitoredOutputStream(OutputStream target, OutputStreamListener listener) {
        this.target = target;
        this.listener = listener;
        this.listener.start();
    }

    /** @inherit */
    public void write(byte b[], int off, int len) throws IOException {
        target.write(b,off,len);
        listener.bytesWritten(len - off);
    }

	/** @inherit */
    public void write(byte b[]) throws IOException {
        target.write(b);
        listener.bytesWritten(b.length);
    }

	/** @inherit */
    public void write(int b) throws IOException {
        target.write(b);
        listener.bytesWritten(1);
    }

	/** @inherit */
    public void close() throws IOException {
        target.close();
        listener.done();
    }

	/** @inherit */
    public void flush() throws IOException {
        target.flush();
    }
}
