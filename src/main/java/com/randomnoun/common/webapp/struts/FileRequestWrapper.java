package com.randomnoun.common.webapp.struts;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.randomnoun.common.webapp.AppConfigBase;

public class FileRequestWrapper extends HttpServletRequestWrapper {
	
	static Logger logger = Logger.getLogger(FileRequestWrapper.class);
	
	Map<String, String[]> parameters = new HashMap<String, String[]>();
	List<FileItem> files;

    /**
     * Converts a size value from a string representation to its numeric value.
     * The string must be of the form nnnm, where nnn is an arbitrary decimal
     * value, and m is a multiplier. The multiplier must be one of 'K', 'M' and
     * 'G', representing kilobytes, megabytes and gigabytes respectively.
     *
     * If the size value cannot be converted, for example due to invalid syntax,
     * the supplied default is returned instead.
     *
     * @param sizeString  The string representation of the size to be converted.
     * @param defaultSize The value to be returned if the string is invalid.
     *
     * @return The actual size in bytes.
     */
    protected long convertSizeToBytes(String sizeString, long defaultSize) {
        int multiplier = 1;

        if (sizeString.endsWith("K")) {
            multiplier = 1024;
        } else if (sizeString.endsWith("M")) {
            multiplier = 1024 * 1024;
        } else if (sizeString.endsWith("G")) {
            multiplier = 1024 * 1024 * 1024;
        }
        if (multiplier != 1) {
            sizeString = sizeString.substring(0, sizeString.length() - 1);
        }

        long size = 0;
        try {
            size = Long.parseLong(sizeString);
        } catch (NumberFormatException nfe) {
			logger.warn("Invalid format for file size ('" + sizeString +
                    "'). Using default.");
            size = defaultSize;
            multiplier = 1;
        }

        return (size * multiplier);
    }

	public FileRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		
		AppConfigBase appConfig = getAppConfig(request);
		// possibly pass in a DefaultFileItemFactory with appConfig settings
    	//long maxFilesize=20971520; // 20MB
    	long diskThreshold=1048576; // 1MB
    	//if (appConfig.getProperty("webapp.fileUpload.maxFilesize")!=null) {
    	//	maxFilesize = convertSizeToBytes(appConfig.getProperty("webapp.fileUpload.maxFilesize"), 20971520);
    	//}
    	if (appConfig.getProperty("webapp.fileUpload.diskThreshold")!=null) {
    		diskThreshold = convertSizeToBytes(appConfig.getProperty("webapp.fileUpload.maxFilesize"), 1048576);
    	}
    	
		File repository = new File(appConfig.getProperty("webapp.fileUpload.tempDir"));
		FileItemFactory factory = new DefaultFileItemFactory((int) diskThreshold, repository);
		ServletFileUpload sfu = new ServletFileUpload(factory);
		try {
			files = (List<FileItem>) sfu.parseRequest(request);
		} catch (FileUploadException e) {
			logger.error("Problem parsing request", e);
			throw new IOException("Problem parsing request", e);
		}
		// params from queryString; may cause problems if param in both queryString & form body
		parameters = new HashMap<String, String[]>(this.getRequest().getParameterMap());
		
        // Partition the items into form fields and files.
        Iterator<FileItem> iter = files.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
                this.addParameter(item.getFieldName(), item.getString());
                iter.remove();
            } else {
            	this.addParameter(item.getFieldName(), "[file upload]");
            	//addFileParameter(item);
            }
        }
	}

	public List<FileItem> getFiles() { return files; }
	
	// returns the FileItem for the first parameter that is set to parameterName,
	// as per normal getParameter() processing. If the supplied parameter is
	// not actually a file, throws an exception.
	public FileItem getFile(String parameterName) {
		for (int i=0; i<files.size(); i++) {
			if (files.get(i).getFieldName().equals(parameterName)) {
				return files.get(i);
			}
		}
		throw new IllegalArgumentException("Parameter '" + parameterName + "' is not a file upload");
	}
	
    public String getParameter(String name) {
    	String[] param = (String[]) parameters.get(name);
    	if (param==null) { return null; }
    	return param[0];
    }
    
    public Map getParameterMap() {
    	return parameters;
    }
    public Enumeration getParameterNames() {
    	return new Vector(parameters.keySet()).elements();
    }
    public String[] getParameterValues(String name) {
    	return (String[]) parameters.get(name);
	}
    public void addParameter(String name, String value) {
    	logger.debug("Adding param '" + name + "' with value '" + value + "'");
        String[] oldArray = (String[]) parameters.get(name);
        String[] newArray;
        if (oldArray != null) {
            newArray = new String[oldArray.length + 1];
            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
            newArray[oldArray.length] = value;
        } else {
            newArray = new String[] { value };
        }
        parameters.put(name, newArray);
    }
    
    /** Returns the appConfig in the current servletContext for this request.
	 * Object is returned as an AppConfigBase.
	 * 
	 * @param request servlet request
 * @version $Id: FileRequestWrapper.java,v 1.4 2013-09-25 04:42:17 knoxg Exp $
	 */
	private static AppConfigBase getAppConfig(HttpServletRequest request) {
		AppConfigBase appConfig = (AppConfigBase) request.getSession().getServletContext().getAttribute("com.randomnoun.appConfig");
		if (appConfig==null) {
			Throwable t = (Throwable) request.getSession().getServletContext().getAttribute("com.randomnoun.appConfig.initialisationFailure");
			throw new RuntimeException("No appConfig", t);
		} else {
			return appConfig;
		}
	}
		
}