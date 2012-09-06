package com.randomnoun.dmx.web;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.randomnoun.common.MRUCache;
import com.randomnoun.common.StreamUtils;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.dao.ShowDefAttachmentDAO;
import com.randomnoun.dmx.to.ShowDefAttachmentTO;

/**
 * Same as FixtureImageServlet, but for Shows. @TODO combine these
 *
 * PathInfo:
 * 
 * nnn/filename
 * 
 * where nnn is the showDefId and filename is the attached file to that
 * show definition.
 */
public class ShowAttachmentServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

     /** Cache of images */
     public static Map cache = new MRUCache(100, 0, null);
     
     /** Logger for this class */
     public static final Logger logger = Logger.getLogger(ShowAttachmentServlet.class);
     
     
	public ShowAttachmentServlet() {
		super();
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.debug("Calling init");
	}
	
	
	
	/** Post method; just defers to get
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
    
	/** Lets get this turkey stand on the road
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
		ShowDefAttachmentDAO ShowDefAttachmentDAO = new ShowDefAttachmentDAO(AppConfig.getAppConfig().getJdbcTemplate());
		
		String pathInfo = request.getPathInfo();
		if (pathInfo==null) { pathInfo=""; }
		if (pathInfo.startsWith("/")) { pathInfo = pathInfo.substring(1); }
		logger.debug("ShowImageServlet " + pathInfo);
		int pos = pathInfo.indexOf("/");
		try {
			if (pos!=-1) {
				// @TODO caching things
				int showDefId = Integer.parseInt(pathInfo.substring(0, pos));
				String filename = pathInfo.substring(pos+1);
				ShowDefAttachmentTO showDefAttachment = ShowDefAttachmentDAO.getShowDefAttachment(showDefId, filename);
				response.setContentType(showDefAttachment.getContentType());
	    		InputStream is;
	    		try {
		    		is = ShowDefAttachmentDAO.getInputStream(showDefAttachment);
		    		response.setContentLength((int) showDefAttachment.getSize());
		    		StreamUtils.copyStream(is, response.getOutputStream());
		    		is.close();
	    		} catch (FileNotFoundException fnfe) {
		    		response.setContentLength(0);
	    			// @TODO: use placeholder image 
	    		}
	    		return;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		
		response.sendError(404, "File Not Found");
	}  	
}