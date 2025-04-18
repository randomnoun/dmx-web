package com.randomnoun.dmx.web;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.randomnoun.common.MRUCache;
import com.randomnoun.common.StreamUtil;
import com.randomnoun.common.Text;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.dao.FixtureDefAttachmentDAO;
import com.randomnoun.dmx.to.FixtureDefAttachmentTO;

/**
 * Deliver a fixture definition image
 *
 * PathInfo:
 * 
 * nnn/filename
 * 
 * where nnn is the fixtureDefId and filename is the attached file to that
 * fixture definition.
 */
public class FixtureAttachmentServlet extends jakarta.servlet.http.HttpServlet implements jakarta.servlet.Servlet {

     /** Cache of images */
     public static Map cache = new MRUCache(100, 0, null);
     
     /** Logger for this class */
     public static final Logger logger = Logger.getLogger(FixtureAttachmentServlet.class);
     
     
	public FixtureAttachmentServlet() {
		super();
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.debug("Calling init");
	}
	
	
	
	/** Post method; just defers to get
	 * 
	 * @see jakarta.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
    
	/** Process a HTTP request
	 * 
	 * @see jakarta.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
		FixtureDefAttachmentDAO fixtureDefAttachmentDAO = new FixtureDefAttachmentDAO(AppConfig.getAppConfig().getJdbcTemplate());
		
		String pathInfo = request.getPathInfo();
		if (pathInfo==null) { pathInfo=""; }
		if (pathInfo.startsWith("/")) { pathInfo = pathInfo.substring(1); }
		logger.debug("FixtureImageServlet " + pathInfo);
		int pos = pathInfo.indexOf("/");
		try {
			if (pos!=-1) {
				// @TODO caching things
				int fixtureDefId = Integer.parseInt(pathInfo.substring(0, pos));
				String filename = pathInfo.substring(pos+1);
				FixtureDefAttachmentTO fixtureDefAttachment = fixtureDefAttachmentDAO.getFixtureDefAttachment(fixtureDefId, filename);
				response.setContentType(fixtureDefAttachment.getContentType());
	    		InputStream is;
	    		try {
		    		is = fixtureDefAttachmentDAO.getInputStream(fixtureDefAttachment);
		    		response.setContentLength((int) fixtureDefAttachment.getSize());
		    		StreamUtil.copyStream(is, response.getOutputStream());
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