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

import org.apache.commons.fileupload.disk.DiskFileItem;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

/**
 * An extension to the standard apache DiskFileItem that triggers events when it is written to. 
 *
 * This is also instantiated for standard (non-file) fields as well, it appears,
 * making the FileProgressTO.fileIndex field inaccurate.
 *
 * @author knoxg (based on plosson on 06-janv.-2006 10:46:33)
 * @version $Id: MonitoredDiskFileItem.java,v 1.4 2013-09-25 04:42:17 knoxg Exp $ (based on 1.0)
 * 
 */
public class MonitoredDiskFileItem extends DiskFileItem
{
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id: MonitoredDiskFileItem.java,v 1.4 2013-09-25 04:42:17 knoxg Exp $";

	/** The monitored output stream used to store the data for this DiskFileItem */ 
    private transient MonitoredOutputStream mos = null;
    
    /** The listener used as an event sink */
    private transient OutputStreamListener listener;

	/** Create a new MonitoredDiskFileItem class, representing a single file being uploaded
	 * through a HTTP request.
	 * 
	 * @param fieldName the HTTP request field name used to transfer this file  
	 * @param contentType the MIME type of the file
	 * @param isFormField whether or not this item is a plain form field, as opposed to a 
	 *   file upload.
	 * @param fileName the source filename, as set by the end-user's browser 
	 * @param sizeThreshold the threshold, in bytes, below which items will be retained 
	 *   in memory and above which they will be stored as a file.
	 * @param repository the data repository, which is the directory in which files will be created, should the 
	 *   item size exceed the threshold
	 * @param listener the listener we will trigger events on as the request is being processed
	 */
    public MonitoredDiskFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold, File repository, OutputStreamListener listener)
    {
        super(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
        this.listener = listener;
    }

	/** Returns an OutputStream that can be used for storing the contents of the file. 
	 * 
	 */
    public OutputStream getOutputStream() throws IOException {
        if (mos == null) {
            mos = new MonitoredOutputStream(super.getOutputStream(), listener);
        }
        return mos;
    }
}
