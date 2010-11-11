package com.randomnoun.dmx.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;

import com.randomnoun.common.MRUCache;
import com.randomnoun.common.Text;

/**
 * Deliver a page containing multiple javascript files
 *
 * Parameters:
 * js - csv list of javascript files. 
 *      in this list of files the ".js" extension is optional
 *      files will be retrieved from "/js" relative directory 
 */
public class MultiJavascriptServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

     /** Cache of combined js */
     public static Map cache = new MRUCache(100, 0, null);
     
     /** Logger for this class */
     public static final Logger logger = Logger.getLogger(MultiJavascriptServlet.class);
     
     
	public MultiJavascriptServlet() {
		super();
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.debug("Calling init");
	}
	
	
    public static String classLoaderToString(ClassLoader classLoader) {
    	
    	if (classLoader instanceof URLClassLoader) {
    		URLClassLoader c = (URLClassLoader ) classLoader;
    		String result = "urls:\n";
    		for (int i=0; i<c.getURLs().length; i++) {
    			result += "  " + c.getURLs()[i] + "\n";
    		}
    		return result;
    	} else {
    		return classLoader.toString();
    	}
    }
    
    public static String getClassLoaderTrace(Class clazz) {
        String result = "";
        ClassLoader classLoader = clazz.getClassLoader();
        while (classLoader!=null) {
            result += "(" + classLoader.getClass().getName() + ") " + classLoaderToString(classLoader) + "\n";
            classLoader = classLoader.getParent();
        }
        return result;
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
		// dump the classpath
		/*
		System.out.println("*************************");
		System.out.println(getClassLoaderTrace(MultiJavascriptServlet.class));
		System.out.println("*************************");
		*/
		String result = null;
		// could cache these results, or respond to If-Modified-Since headers
		// TODO: Content-Length headers
		// TODO: maybe store result as byte arrays rather than strings
		// TODO: try/catch & log everything
		
        String js = request.getParameter("js");
        if (js==null || js.equals("")) { return; }
        
        PrintWriter pw = response.getWriter();
        
        result = (String) cache.get(js);
        if (result!=null) { 
        	pw.println(result);
        	return;
        }
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        List<String> files;
		try {
			files = Text.parseCsv(js);
		} catch (ParseException e) {
			logger.error("Invalid js parameter '" + js + "'", e);
			throw new IllegalArgumentException("Invalid js parameter");
		}
        for (String file : files) {
        	if (!file.endsWith(".js")) {
        		file+=".js";
        	}
        	// @TODO get relative base from somewhere. referrer ?
        	// request param ? servler init param ?
        	/*InputStream is = MultiJavascriptServlet.class.getClassLoader().
        	  getResourceAsStream("./stocktake/js/" + file);*/
        	InputStream is = getServletContext().getResourceAsStream("/js/" + file);
        	if (is==null) {
        		logger.error("Unknown file '" + file + "'");
        		throw new IllegalArgumentException("Unknown file '" + file + "'");
        	}
        	StreamUtils.copy(is, os);
        	is.close();
        }
        os.flush();
        result = os.toString();
        cache.put(js, result);
        pw.println(result);
	}  	
}