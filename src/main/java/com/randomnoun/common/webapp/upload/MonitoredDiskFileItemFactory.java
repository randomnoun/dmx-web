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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.File;

/**
 * An extension of the standard apache DiskFileItemFactory, that allows DiskFileItems
 * created by this factory to be monitored. This is all a bit pointless in my book,
 * but it's required by the ExtendedMultiPartRequestHandler, which is responsible for
 * creating these things. As DiskFileItems are generated and written to, events will
 * be sent to the listener passed as a constructor to this object; this will allow
 * upload progress information to be sent back to the user.
 * 
 * @see com.randomnoun.common.webapp.struts.ExtendedMultiPartRequestHandler
 *
 * @author knoxg (based on plosson on 06-janv.-2006 10:46:26)
 * @version $Id: MonitoredDiskFileItemFactory.java,v 1.3 2013-09-25 04:42:17 knoxg Exp $ (based on 1.0)
 */
public class MonitoredDiskFileItemFactory extends DiskFileItemFactory
{
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id: MonitoredDiskFileItemFactory.java,v 1.3 2013-09-25 04:42:17 knoxg Exp $";

    /** The listener used as an event sink */ 
    private OutputStreamListener listener = null;

	/** Create a new MonitoredDiskFileItemFactory
	 * 
	 * @param listener The listener to use as an event sink
	 */ 
    public MonitoredDiskFileItemFactory(OutputStreamListener listener) {
        super();
        this.listener = listener;
    }

    /** Create a new MonitoredDiskFileItemFactor, using the specified sizeThreshhold 
     * and "repository". Whatever that is.
     * 
     * @param sizeThreshold the threshold, in bytes, below which items will be retained in 
     *   memory and above which they will be stored as a file.
     * @param repository the data repository, which is the directory in which files will 
     *   be created, should the item size exceed the threshold.
     * @param listener The listener to use as an event sink
     */
    public MonitoredDiskFileItemFactory(int sizeThreshold, File repository, 
      OutputStreamListener listener)
    {
        super(sizeThreshold, repository);
        this.listener = listener;
    }

    /** Create a new FileItem
     * 
     * @param fieldName the HTTP request field containing this file
     * @param contentType the MIME type of this upload
     * @param isFormField whether or not this item is a plain form field, as opposed to a 
     *   file upload.
     * @param fileName the source filename of this file, as set by the browser
     */
    public FileItem createItem(String fieldName, String contentType, boolean isFormField, 
      String fileName)
    {
        return new MonitoredDiskFileItem(fieldName, contentType, isFormField, fileName, getSizeThreshold(), getRepository(), listener);
    }
}
