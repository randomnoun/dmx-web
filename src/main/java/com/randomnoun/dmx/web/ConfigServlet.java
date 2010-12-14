package com.randomnoun.dmx.web;

import gnu.io.CommPortIdentifier;
import gnu.io.RXTXCommDriver;
import gnu.io.RXTXVersion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.randomnoun.common.ErrorList;
import com.randomnoun.common.ExceptionUtils;
import com.randomnoun.common.IniFile;
import com.randomnoun.common.MRUCache;
import com.randomnoun.common.Registry;
import com.randomnoun.common.Struct;
import com.randomnoun.common.Text;
import com.randomnoun.common.webapp.struts.CustomRequestProcessor;
import com.randomnoun.dmx.config.AppConfig;

/**
 * Perform application initialisation.
 *
 * Parameters:
 * 
 */
public class ConfigServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

     /** Cache of images */
     public static Map cache = new MRUCache(100, 0, null);
     
     /** Logger for this class */
     public static final Logger logger = Logger.getLogger(ConfigServlet.class);
     
     
	public ConfigServlet() {
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
		String jspForward = "";
		String action = request.getParameter("action");
		if (action==null) { action = ""; }
		Exception e = (Exception) request.getAttribute("exception");
		ErrorList errors = new ErrorList();
		
		// other properties just used during configuration
		String[] configProperties = new String[] {
				"databaseAdminUsername", "databaseAdminPassword",
				"databaseAdminUrl"
		};
		
		// properties to be written to dmx-web.properties
		// changing '.'s to '_'s to avoid JSP EL conflicts
		String[] fileProperties = new String[] {
				"database_driver", "database_url", "database_username",
				"database_password",
				"dmxDevice_class", "dmxDevice_portName",
				"audioController_class", 
				"audioController_host", "audioController_port", "audioController_password",  
				"audioController_defaultPath",
				
				"audioSource_class", "audioSource_host", "audioSource_port",
				
				"webapp_fileUpload_tempDir",
				"webapp_fileUpload_path",
				"log4j_logDirectory",
				"audioController_defaultPath"
				
		};
		
		if (action.equals("maintain")) {
			Properties form = new Properties();
			Struct.setFromRequest(form, request, configProperties);
			Struct.setFromRequest(form, request, fileProperties);
			
			// validate DB connection
			try {
				Class.forName(form.getProperty("database_driver"));
				if (Text.isBlank(form.getProperty("database_username")) && Text.isBlank(form.getProperty("database_password"))) {
					Connection conn = DriverManager.getConnection(form.getProperty("database_url"));
					conn.close();
				} else {
					Connection conn = DriverManager.getConnection(form.getProperty("database_url"), form.getProperty("database_username"), form.getProperty("database_password"));
					conn.close();
				}
			} catch (Exception e2) {
				errors.addError("database_driver,database_url,database_username,database_password",
					"Database error", "There was a problem connecting to the database: " +
					ExceptionUtils.getStackTraceSummary(e2));
			}
			
			// if creating database, validate admin connection
			if ("y".equals(form.getProperty("createSchema"))) {
				try {
					if (Text.isBlank(form.getProperty("databaseAdminUsername")) && Text.isBlank(form.getProperty("databaseAdminPassword"))) {
						Connection conn = DriverManager.getConnection(form.getProperty("databaseAdminUrl"));
						conn.close();
					} else {
						Connection conn = DriverManager.getConnection(form.getProperty("databaseAdminUrl"), form.getProperty("databaseAdminUsername"), form.getProperty("databaseAdminPassword"));
						conn.close();
					}
				} catch (Exception e2) {
					errors.addError("databaseAdminUrl,databaseAdminUsername,databaseAdminPassword",
						"Database error", "There was a problem connecting to the database using the admin credentials: " +
						ExceptionUtils.getStackTraceSummary(e2));
				}
			}

			// check file paths look OK
        	String dirName = form.getProperty("webapp_fileUpload_tempDir");
        	File file = new File(dirName);
        	if (!file.exists()) {
        		boolean mkdir = file.mkdirs();
        		if (mkdir) { errors.addError("Directory created", "The directory '" + dirName + "' has been created", ErrorList.SEVERITY_OK);
        		} else { errors.addError("webapp_fileUpload_tempDir", "Directory creation failed", "The directory '" + dirName + "' could not be created", ErrorList.SEVERITY_INVALID); }
        	}
	        
        	dirName = form.getProperty("webapp_fileUpload_path"); file = new File(dirName);
        	if (!file.exists()) {
        		boolean mkdir = file.mkdirs();
        		if (mkdir) { errors.addError("Directory created", "The directory '" + dirName + "' has been created", ErrorList.SEVERITY_OK);
        		} else { errors.addError("webapp_fileUpload_path", "Directory creation failed", "The directory '" + dirName + "' could not be created", ErrorList.SEVERITY_INVALID); }
        	}

        	dirName = form.getProperty("log4j_logDirectory"); file = new File(dirName);
        	if (!file.exists()) {
        		boolean mkdir = file.mkdirs();
        		if (mkdir) { errors.addError("Directory created", "The directory '" + dirName + "' has been created", ErrorList.SEVERITY_OK);
        		} else { errors.addError("log4j_logDirectory", "Directory creation failed", "The directory '" + dirName + "' could not be created", ErrorList.SEVERITY_INVALID); }
        	}

        	dirName = form.getProperty("audioController_defaultPath"); file = new File(dirName);
        	if (!file.exists()) {
        		boolean mkdir = file.mkdirs();
        		if (mkdir) { errors.addError("Directory created", "The directory '" + dirName + "' has been created", ErrorList.SEVERITY_OK);
        		} else { errors.addError("audioController_defaultPath", "Directory creation failed", "The directory '" + dirName + "' could not be created", ErrorList.SEVERITY_INVALID); }
        	}

			// check hosts and ports look OK
			try { InetAddress address = InetAddress.getByName(form.getProperty("audioController_host")); }
			catch (Exception e2) { 
				errors.addError("Invalid host", "The audio controller host is invalid: " + e2.getMessage());
			}
			try { long port = Long.parseLong(form.getProperty("audioController_port")); }
			catch (NumberFormatException nfe) { 
				errors.addError("Invalid number", "The audio controller port number must be numeric");
			}

			try { InetAddress address = InetAddress.getByName(form.getProperty("audioSource_host")); }
			catch (Exception e2) { 
				errors.addError("Invalid host", "The audio source host is invalid: " + e2.getMessage());
			}
			try { long port = Long.parseLong(form.getProperty("audioSource_port")); }
			catch (NumberFormatException nfe) { 
				errors.addError("Invalid number", "The audio source port number must be numeric");
			}
			
			

			
		} else if (e==null) {
			// possibly allow this ?
			Exception e2 = new IllegalStateException("Configuration not required");
			request.setAttribute("stacktrace", 
			ExceptionUtils.getStackTraceWithRevisions(e2, 
			  CustomRequestProcessor.class.getClassLoader(), ExceptionUtils.HIGHLIGHT_HTML, "com.randomnoun."));
			request.setAttribute("isStrutsRequest", "true"); // used in JSPs to check whether this routine has been invoked
			request.setAttribute("stacktraceSummary", ExceptionUtils.getStackTraceSummary(e2));
			jspForward = "misc/error.jsp";
		} else if (!e.getMessage().equals("No appConfig")) {
			jspForward = "misc/error.jsp";
		} else {
			
			// do some config magic here
			String configPath = System.getProperty(AppConfig.SYSTEM_PROPERTY_KEY_CONFIG_PATH);
	    	if (configPath==null) { configPath = "."; }
	        File configFile = new File(configPath + AppConfig.CONFIG_RESOURCE_LOCATION);
	        File uploadDir = new File(configPath + "/upload");
	        File audioDir = new File(configPath + "/audio");
	        File logDir = new File(System.getProperty("catalina.base") + "/logs");
	        File tempDir = new File(System.getProperty("java.io.tmpdir") + "/dmx-web");
	        
	        if (configFile.exists()) {
	        	// hrm.
	        }
	        
	        File serverConfigFile = new File(System.getProperty("catalina.base") + "/conf/server.xml");
	        
	        // for getting tomcat version. eventually.
	        // MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

	        String winAmpLocation = Registry.getSystemValue("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Winamp", "UninstallString");
	        if (winAmpLocation!=null) {
		        if (winAmpLocation.startsWith("\"")) {
		        	winAmpLocation = winAmpLocation.substring(1, winAmpLocation.length()-2);
		        }
		        winAmpLocation = new File(winAmpLocation).getParentFile().getCanonicalPath();
	        } else {
	        	winAmpLocation = "";
	        }
	        
	        File dmxPlugin = new File(winAmpLocation, "Plugins\\vis_DmxWebPlugin.dll");
	        File dmxPluginCfg = new File(winAmpLocation, "Plugins\\vis_DmxWebPlugin.ini");
	        File ngPlugin = new File(winAmpLocation, "Plugins\\gen_ngwinamp.dll");
	        File ngPluginCfg = new File(winAmpLocation, "Plugins\\gen_ngwinampsv.cfg");
	        
	        Map version = getVersionData();
	        request.setAttribute("configFileLocation", configFile.getCanonicalPath());
	        request.setAttribute("dmxWebVersion", version.get("release"));
	        request.setAttribute("dmxWebBuild", version.get("buildNumber"));
	        request.setAttribute("javaVersion", System.getProperty("java.version"));
	        request.setAttribute("tomcatVersion", "Unknown");
	        request.setAttribute("rxtxJarVersion", version.get("rxtxJarVersion"));
	        request.setAttribute("rxtxDllVersion", version.get("rxtxDllVersion"));
	        request.setAttribute("winAmpLocation", winAmpLocation);
	        request.setAttribute("ngWinAmpInstalled", ngPlugin.exists() ? "Yes" : "No");
	        request.setAttribute("dmxWebWinAmpInstalled", dmxPlugin.exists() ? "Yes" : "No");
	        
	        request.setAttribute("serverConfigFileWritable", (canWrite(serverConfigFile) ? "Yes" : "No") + " (" + serverConfigFile.getCanonicalPath() + ")" );
	        request.setAttribute("dmxConfigFileWritable", (canWrite(configFile) ? "Yes" : "No") + " (" + configFile.getCanonicalPath() + ")");
	        
	        // get existing database types
	        List driversList = new ArrayList();;
	        Map exampleConnectionStrings = new HashMap();
	        Map exampleAdminConnectionStrings = new HashMap();
	        for (Enumeration ed = DriverManager.getDrivers(); ed.hasMoreElements(); ) {
	        	Driver d = (Driver) ed.nextElement();
	        	String className = d.getClass().getName();
	        	driversList.add(d.getClass().getName());
	        	if (className.equals("com.mysql.jdbc.Driver")) {
	        		exampleConnectionStrings.put(className, "jdbc:mysql://localhost/dmxweb?zeroDateTimeBehavior=convertToNull&autoReconnect=true");
	        		exampleAdminConnectionStrings.put(className, "jdbc:mysql://localhost/information_schema");
	        	} else if (className.equals("sun.jdbc.odbc.JdbcOdbcDriver")) {
	        		exampleConnectionStrings.put(className, "jdbc:odbc:dsn_name");
	        		exampleAdminConnectionStrings.put(className, "jdbc:odbc:dsn_name");
	        	} else {
	        		exampleConnectionStrings.put(className, "Refer to driver documentation");
	        		exampleAdminConnectionStrings.put(className, "jdbc:odbc:dsn_name");
	        	}
	        }
	        // should put MySQL at top of list
	        // TODO: try to register some common database types
	        String defaultDriver = driversList.contains("com.mysql.jdbc.Driver") ? "com.mysql.jdbc.Driver" : (String) driversList.get(0);
	        String defaultConnString = Text.strDefault((String) exampleConnectionStrings.get(defaultDriver), "Refer to driver documentation");
	        String defaultAdminConnString = Text.strDefault((String) exampleAdminConnectionStrings.get(defaultDriver), "Refer to driver documentation");
	        request.setAttribute("jdbcDriversAvailable", Text.join(driversList, ", "));
	        request.setAttribute("createSchema", "y");
	        request.setAttribute("databaseSchema", "dmxweb");
	        request.setAttribute("databaseDrivers", driversList);
	        request.setAttribute("database_driver", defaultDriver);
	        request.setAttribute("database_url", defaultConnString);
	        request.setAttribute("database_username", "dmxweb");
	        request.setAttribute("database_password", "dmxweb");
	        request.setAttribute("database_connectionType", "simple");
	        
	        request.setAttribute("databaseAdminUsername", "root");
	        request.setAttribute("databaseAdminPassword", "abc123");
	        request.setAttribute("databaseAdminUrl", defaultAdminConnString);
	        
	        List dmxDevicePortNames = new ArrayList();
	        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
			while (portList.hasMoreElements()) {
				CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
			    	dmxDevicePortNames.add(portId.getName());
			    }
			}
	        request.setAttribute("comPortsAvailable", Text.join(dmxDevicePortNames, ","));
	        request.setAttribute("installOK", "true");
	        
	        request.setAttribute("webapp_fileUpload_tempDir", tempDir.getCanonicalPath());
	        request.setAttribute("webapp_fileUpload_path", uploadDir.getCanonicalPath());
	        request.setAttribute("log4j_logDirectory", logDir.getCanonicalPath());
	        request.setAttribute("audioController_defaultPath", audioDir.getCanonicalPath());
	        
	        
	        List deviceTypes = new ArrayList();
	        deviceTypes.add("com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidget");
	        request.setAttribute("dmxDeviceTypes", deviceTypes);

	        List audioControllerTypes = new ArrayList();
	        audioControllerTypes.add("com.randomnoun.dmx.protocol.ngWinamp.WinampAudioController");
	        request.setAttribute("audioControllerTypes", audioControllerTypes);
	        request.setAttribute("audioController_class", "com.randomnoun.dmx.protocol.ngWinamp.WinampAudioController");

	        List audioSourceTypes = new ArrayList();
	        audioSourceTypes.add("com.randomnoun.dmx.protocol.dmxWinamp.WinampAudioSource");
	        request.setAttribute("audioSourceTypes", audioControllerTypes);
	        request.setAttribute("audioSource_class", "com.randomnoun.dmx.protocol.dmxWinamp.WinampAudioSource");

	        
	        request.setAttribute("dmxDevicePortNames", dmxDevicePortNames);

	        if (!"".equals(winAmpLocation)) {
		        Properties ngPluginProperties = new Properties();
		        if (ngPluginCfg.exists()) {
		        	LineNumberReader lnr = new LineNumberReader(new FileReader(ngPluginCfg));
		        	String line = lnr.readLine();
		        	while (line!=null) {
		        		line = line.trim();
		        		if (line.startsWith("//")) { 
		        			// ignore comments
		        		} else if (line.equals("")) { 
		        			// ignore blank lines
		        		} else {
			        		int pos = line.indexOf("=");
			        		if (pos!=-1) {
			        			ngPluginProperties.put(line.substring(0, pos).trim(), line.substring(pos + 1).trim());
			        		}
		        		}
		        		line = lnr.readLine();
		        	}
		        	String host = ngPluginProperties.getProperty("sv_host");
		        	host = Text.strDefault(host, "localhost");
		        	if (host.equals("0.0.0.0")) { host = "localhost"; }
			        request.setAttribute("audioController_host", host);
			        request.setAttribute("audioController_port", ngPluginProperties.getProperty("sv_port"));
			        request.setAttribute("audioController_password", ngPluginProperties.getProperty("sv_pass"));
		        } else {
			        request.setAttribute("audioController_host", "localhost");
			        request.setAttribute("audioController_port", "18443");
			        request.setAttribute("audioController_password", "abc123");
		        }
		        
		        Properties dmxPluginProperties = new Properties();
		        if (dmxPluginCfg.exists()) {
		        	IniFile iniFile = new IniFile();
		        	try {
						iniFile.load(dmxPluginCfg);
				        request.setAttribute("audioSource_host", "localhost");
				        request.setAttribute("audioSource_port", iniFile.get("settings", "port_number"));
					} catch (ParseException e1) {
						// fall-through
					}	
		        }
		        if (dmxPluginProperties.keySet().size()==0) {
			        request.setAttribute("audioSource_host", "localhost");
			        request.setAttribute("audioSource_port", "58273");
		        }
	        }
	        
	        
	        
	        
	        // TODO:
	        // check that dmx-web.properties is writeable
	        // do some sanity checks on various components (DLLs exist)
	        // if no database configured, prompt for defaults
	        // prompt for fileUpload directory + temp directory
	        // prompt for audioController directory
	        // prompt for DMX properties
	        // prompt for WinAmpDMX/NGWinAmp properties
	        // 
	        // warn on browser size
	        
			jspForward="config/config.jsp";
		};
		
		RequestDispatcher dispatcher = request.getRequestDispatcher(jspForward);
		dispatcher.forward(request, response);
	}
	
	
    Map getVersionData() throws IOException {
    	Map version = new HashMap();
	    InputStream is = ConfigServlet.class.getClassLoader().getResourceAsStream("/build.properties");
    	Properties props = new Properties();
    	if (is==null) {
    		props.put("error", "Missing build.properties");
    	} else {
	    	props.load(is);
	    	is.close();
    	}
    	version.put("release", props.get("maven.pom.version"));
    	version.put("buildNumber", props.get("bamboo.buildNumber"));
    	String jarVersion = "unknown";
    	try {
    		jarVersion = RXTXVersion.getVersion();
    	} catch (Error e0) {
    		jarVersion = "Exception determining version: " + e0.getMessage();
    	}
    	String dllVersion = "unknown";
    	try {
    		dllVersion = RXTXVersion.nativeGetVersion();
    	} catch (Error e1) {
    		try {
    			dllVersion = RXTXCommDriver.nativeGetVersion();
    		} catch (Exception e2) {
    			logger.error("Exception 1 determining version: ", e1);
    			logger.error("Exception 2 determining version: ", e2);
    			dllVersion = "Exception determining version: " + e1.getMessage();
    		}
    	}
    	version.put("rxtxJarVersion", jarVersion);
    	version.put("rxtxDllVersion", dllVersion);
    	return version;
    }
    
    public boolean canWrite(File file) {
    	if (file.exists()) { return file.canWrite(); }
    	try {
			OutputStream os = new FileOutputStream(file);
			os.close();
			file.delete();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
    	
    }

	
}