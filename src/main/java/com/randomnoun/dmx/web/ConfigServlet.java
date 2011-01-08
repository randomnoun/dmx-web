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
import java.io.PrintWriter;
import java.io.Writer;
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

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
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
     

	// other properties just used during configuration
	String[] configProperties = new String[] {
			"pageNumber",
			
			"databaseAdminUsername", 
			"databaseAdminPassword",
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
		try {
			logger.info("ConfigServlet start");
			String jspForward = "";
			String action = request.getParameter("action");
			if (action==null) { action = ""; }
			Exception e = (Exception) request.getAttribute("exception");
			ErrorList errors = new ErrorList();
			long page = -1;
			
			
			if (action.equals("maintain")) {
				Map<String, String> form = new HashMap<String, String>();
				Struct.setFromRequest(form, request, configProperties);
				Struct.setFromRequest(form, request, fileProperties);
				

				Map<String, Object> defaultAttributes = getDefaultAttributes();
				for (String key : defaultAttributes.keySet()) {
					request.setAttribute(key, defaultAttributes.get(key));
				}
		        request.setAttribute("submitIcon", "next");
		        request.setAttribute("backIcon", "back");
		        jspForward="config/config.jsp";
				String pageNumber = form.get("pageNumber");
				
				if (pageNumber==null) {
					throw new IllegalArgumentException("Missing pageNumber parameter");

				} else if (pageNumber.equals("1")) {
					// from initial page
			        request.setAttribute("pageNumber", new Long(2));
					
				} else if (pageNumber.equals("2")) {
					// from checklist page
			        request.setAttribute("pageNumber", new Long(3));

				} else if (pageNumber.equals("3")) {
					// from database page

					// validate DB connection
					try {
						Class.forName(form.get("database_driver"));
						if (Text.isBlank(form.get("database_username")) && Text.isBlank(form.get("database_password"))) {
							Connection conn = DriverManager.getConnection(form.get("database_url"));
							conn.close();
						} else {
							Connection conn = DriverManager.getConnection(form.get("database_url"), form.get("database_username"), form.get("database_password"));
							conn.close();
						}
					} catch (Exception e2) {
						errors.addError("database_driver,database_url,database_username,database_password",
							"Database error", "There was a problem connecting to the database: " +
							ExceptionUtils.getStackTraceSummary(e2));
					}
					
					// if creating database, validate admin connection
					if ("y".equals(form.get("createSchema"))) {
						try {
							if (Text.isBlank(form.get("databaseAdminUsername")) && Text.isBlank(form.get("databaseAdminPassword"))) {
								Connection conn = DriverManager.getConnection(form.get("databaseAdminUrl"));
								conn.close();
							} else {
								Connection conn = DriverManager.getConnection(form.get("databaseAdminUrl"), form.get("databaseAdminUsername"), form.get("databaseAdminPassword"));
								conn.close();
							}
						} catch (Exception e2) {
							errors.addError("databaseAdminUrl,databaseAdminUsername,databaseAdminPassword",
								"Database error", "There was a problem connecting to the database using the admin credentials: " +
								ExceptionUtils.getStackTraceSummary(e2));
						}
					}
		
					// check file paths look OK
		        	String dirName = form.get("webapp_fileUpload_tempDir");
		        	File file = new File(dirName);
		        	if (!file.exists()) {
		        		boolean mkdir = file.mkdirs();
		        		if (mkdir) { errors.addError("Directory created", "The directory '" + dirName + "' has been created", ErrorList.SEVERITY_OK);
		        		} else { errors.addError("webapp_fileUpload_tempDir", "Directory creation failed", "The directory '" + dirName + "' could not be created", ErrorList.SEVERITY_INVALID); }
		        	}
			        
		        	dirName = form.get("webapp_fileUpload_path"); file = new File(dirName);
		        	if (!file.exists()) {
		        		boolean mkdir = file.mkdirs();
		        		if (mkdir) { errors.addError("Directory created", "The directory '" + dirName + "' has been created", ErrorList.SEVERITY_OK);
		        		} else { errors.addError("webapp_fileUpload_path", "Directory creation failed", "The directory '" + dirName + "' could not be created", ErrorList.SEVERITY_INVALID); }
		        	}
		
		        	dirName = form.get("log4j_logDirectory"); file = new File(dirName);
		        	if (!file.exists()) {
		        		boolean mkdir = file.mkdirs();
		        		if (mkdir) { errors.addError("Directory created", "The directory '" + dirName + "' has been created", ErrorList.SEVERITY_OK);
		        		} else { errors.addError("log4j_logDirectory", "Directory creation failed", "The directory '" + dirName + "' could not be created", ErrorList.SEVERITY_INVALID); }
		        	}
		
		        	dirName = form.get("audioController_defaultPath"); file = new File(dirName);
		        	if (!file.exists()) {
		        		boolean mkdir = file.mkdirs();
		        		if (mkdir) { errors.addError("Directory created", "The directory '" + dirName + "' has been created", ErrorList.SEVERITY_OK);
		        		} else { errors.addError("audioController_defaultPath", "Directory creation failed", "The directory '" + dirName + "' could not be created", ErrorList.SEVERITY_INVALID); }
		        	}
		
					// check hosts and ports look OK
					try { InetAddress address = InetAddress.getByName(form.get("audioController_host")); }
					catch (Exception e2) { 
						errors.addError("Invalid host", "The audio controller host is invalid: " + e2.getMessage());
					}
					try { long port = Long.parseLong(form.get("audioController_port")); }
					catch (NumberFormatException nfe) { 
						errors.addError("Invalid number", "The audio controller port number must be numeric");
					}
		
					try { InetAddress address = InetAddress.getByName(form.get("audioSource_host")); }
					catch (Exception e2) { 
						errors.addError("Invalid host", "The audio source host is invalid: " + e2.getMessage());
					}
					try { long port = Long.parseLong(form.get("audioSource_port")); }
					catch (NumberFormatException nfe) { 
						errors.addError("Invalid number", "The audio source port number must be numeric");
					}

					if (errors.hasErrors()) {
						request.setAttribute("pageNumber", new Long(3));
					} else {
						request.setAttribute("pageNumber", new Long(4));
					}

				} else {
					throw new IllegalArgumentException("Invalid pageNumber parameter '" + pageNumber + "'");
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

				Map<String, Object> defaultAttributes = getDefaultAttributes();
				for (String key : defaultAttributes.keySet()) {
					request.setAttribute(key, defaultAttributes.get(key));
				}
		        request.setAttribute("pageNumber", new Long(1));
		        request.setAttribute("submitIcon", "next");
				jspForward="config/config.jsp";
			};
			
			RequestDispatcher dispatcher = request.getRequestDispatcher(jspForward);
			dispatcher.forward(request, response);
		} catch (Exception e) {
			logger.error("Exception in ConfigServlet", e);
			// response.sendError(200, "Configuration error");
			PrintWriter writer = response.getWriter();
			writer.println("<h1>Configuration error</h1>");
			writer.println("An error occurred whilst attempting to configure the server: " + e.getMessage());
			writer.println("<pre>");
			writer.println(ExceptionUtils.getStackTraceWithRevisions(e, ConfigServlet.class.getClassLoader(), ExceptionUtils.HIGHLIGHT_HTML, "com.randomnoun"));
			writer.println("</pre");
			writer.flush();
		}
	}
	
	
    Map<String, String> getVersionData() throws IOException {
    	Map<String, String> version = new HashMap<String, String>();
	    InputStream is = ConfigServlet.class.getClassLoader().getResourceAsStream("/build.properties");
    	Properties props = new Properties();
    	if (is==null) {
    		props.put("error", "Missing build.properties");
    	} else {
	    	props.load(is);
	    	is.close();
    	}
    	version.put("release", (String) props.get("maven.pom.version"));
    	version.put("buildNumber", (String) props.get("bamboo.buildNumber"));
    	String jarVersion = "unknown";
    	try {
    		version.put("rxtxJarVersion", RXTXVersion.getVersion());
    	} catch (Error e0) {
    		logger.error("Exception determining RXTX JAR version", e0);
    		version.put("rxtxJarVersionError", e0.getMessage());
    	}
    	String dllVersion = "unknown";
    	try {
    		version.put("rxtxDllVersion", RXTXVersion.nativeGetVersion());
    	} catch (Throwable t) {
    		logger.error("Exception determining RXTX DLL version", t);
    		version.put("rxtxDllVersionError", t.getMessage());
    	}
    	String tomcatVersion = "unknown";
    	try {
        	MBeanServer mBeanServer = JmxUtils.getMBeanServer();
        	ObjectName catalinaName = new ObjectName("Catalina:type=Server");
        	//ObjectInstance catalinaInstance = mBeanServer.getObjectInstance(catalinaName);
        	tomcatVersion = (String) mBeanServer.getAttribute(catalinaName, "serverInfo");
        	version.put("tomcatVersion", tomcatVersion);
    	} catch (Throwable t) {
    		logger.error("Exception determining tomcat version", t);
    		version.put("tomcatVersionError", t.getMessage());
    	}
    	
    	
    	return version;
    }
    
    @SuppressWarnings("unchecked")
	Map<String, Object> getDefaultAttributes() throws IOException, ParseException {
    	Map<String, Object> defaultAttributes = new HashMap<String, Object>();
    	
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
        File serverLibDir = new File(System.getProperty("catalina.base") + "/lib");
        
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
        
        String dmxWebLocation = Registry.getSystemValue("SOFTWARE\\Randomnoun\\DMX-web", "InstallDir");
        if (dmxWebLocation!=null) {
	        if (dmxWebLocation.startsWith("\"")) {
	        	dmxWebLocation = dmxWebLocation.substring(1, dmxWebLocation.length()-2);
	        }
	        dmxWebLocation = new File(dmxWebLocation).getCanonicalPath();
        } else {
        	dmxWebLocation = "";
        }
        
        File dmxPlugin = new File(winAmpLocation, "Plugins\\vis_DmxWebPlugin.dll");
        File dmxPluginCfg = new File(winAmpLocation, "Plugins\\vis_DmxWebPlugin.ini");
        File ngPlugin = new File(winAmpLocation, "Plugins\\gen_ngwinamp.dll");
        File ngPluginCfg = new File(winAmpLocation, "Plugins\\gen_ngwinampsv.cfg");
        
        Map<String, String> version = getVersionData();
        defaultAttributes.put("configFileLocation", configFile.getCanonicalPath());
        defaultAttributes.put("dmxWebVersion", version.get("release"));
        defaultAttributes.put("dmxWebBuild", version.get("buildNumber"));
        defaultAttributes.put("javaVersion", System.getProperty("java.version"));
        if (!System.getProperty("java.version").startsWith("1.6")) {
        	defaultAttributes.put("javaVersion.img", "warn");
        	defaultAttributes.put("javaVersion.text", "Expected version '1.6.x', found '" + System.getProperty("java.version") + "'");
        } else {
        	defaultAttributes.put("javaVersion.img", "ok");
        }
        defaultAttributes.put("tomcatVersion", version.get("tomcatVersion"));
        if (Text.isBlank((String) version.get("tomcatVersion"))) {
        	defaultAttributes.put("tomcatVersion", "unknown");
        	defaultAttributes.put("tomcatVersion.img", "error");
        	defaultAttributes.put("tomcatVersion.text", "The tomcat version could not be determined; error message='" + version.get("tomcatVersionError") + "'");
        } else if (!((String) version.get("tomcatVersion")).startsWith("Apache Tomcat/6.0")) {
        	defaultAttributes.put("tomcatVersion.img", "warn");
        	defaultAttributes.put("tomcatVersion.text", "Expected version 'Apache Tomcat/6.0.x', found '" + version.get("tomcatVersion") + "'");
        } else {
        	defaultAttributes.put("tomcatVersion.img", "ok");
        }
        
        defaultAttributes.put("rxtxJarVersion", version.get("rxtxJarVersion"));
        if (Text.isBlank((String) version.get("rxtxJarVersion"))) {
        	defaultAttributes.put("rxtxJarVersion", "unknown");
        	defaultAttributes.put("rxtxJarVersion.img", "error");
        	defaultAttributes.put("rxtxJarVersion.text", "The RXTX JAR version could not be determined; error message='" + version.get("rxtxJarVersionError") + "'");
        } else if (!version.get("rxtxJarVersion").equals("RXTX-2.1-7")) {
        	defaultAttributes.put("rxtxJarVersion.img", "warn");
        	defaultAttributes.put("rxtxJarVersion.text", "Expected version 'RXTX-2.1-7', found '" + version.get("rxtxJarVersion") + "'");
        } else {
        	defaultAttributes.put("rxtxJarVersion.img", "ok");
        }

        defaultAttributes.put("rxtxDllVersion", version.get("rxtxDllVersion"));
        if (Text.isBlank((String) version.get("rxtxDllVersion"))) {
        	defaultAttributes.put("rxtxDllVersion", "unknown");
        	defaultAttributes.put("rxtxDllVersion.img", "error");
        	defaultAttributes.put("rxtxDllVersion.text", "The RXTX DLL version could not be determined; error message='" + version.get("rxtxDllVersionError") + "'");
        } else if (!version.get("rxtxDllVersion").equals("RXTX-2.1-7")) {
        	defaultAttributes.put("rxtxDllVersion.img", "warn");
        	defaultAttributes.put("rxtxDllVersion.text", "Expected version 'RXTX-2.1-7', found '" + version.get("rxtxJarVersion") + "'");
        } else {
        	defaultAttributes.put("rxtxDllVersion.img", "ok");
        }

        defaultAttributes.put("winAmpLocation", winAmpLocation);
        defaultAttributes.put("ngWinAmpInstalled", ngPlugin.exists() ? "Yes" : "No");
        defaultAttributes.put("dmxWebWinAmpInstalled", dmxPlugin.exists() ? "Yes" : "No");
        
        if (canWrite(serverConfigFile)) {
        	defaultAttributes.put("serverConfigFileWritable", "Yes" + " (" + serverConfigFile.getCanonicalPath() + ")" );
        	defaultAttributes.put("serverConfigFileWritable.img", "ok");
        } else {
        	defaultAttributes.put("serverConfigFileWritable", "No" + " (" + serverConfigFile.getCanonicalPath() + ")" );
        	defaultAttributes.put("serverConfigFileWritable.img", "error");
        }
        
        if (canWrite(configFile)) {
        	defaultAttributes.put("dmxConfigFileWritable", "Yes" + " (" + configFile.getCanonicalPath() + ")");
        	defaultAttributes.put("dmxConfigFileWritable.img", "ok");
        } else {
        	defaultAttributes.put("dmxConfigFileWritable", "No" + " (" + configFile.getCanonicalPath() + ")");
        	defaultAttributes.put("dmxConfigFileWritable.img", "error");
        }

        
        defaultAttributes.put("database_config", "standard");
        
        // check for embedded MySQL 5.1 install
        File mysqlIniLocation1 = new File(dmxWebLocation, "mysql\\my.ini");
        if (mysqlIniLocation1.exists()) {
        	IniFile iniFile = new IniFile();
	        iniFile.load(mysqlIniLocation1);
	        String mysqlPort = iniFile.get("mysqld", "port");  // default to 3306 ?
        }
        
        // check for any registered MySQL 5.1 installs
        String mysqlLocation = Registry.getSystemValue("SOFTWARE\\MySQL AB\\MySQL Server 5.1", "Location");
        if (mysqlLocation!=null) {
	        if (mysqlLocation.startsWith("\"")) {
	        	mysqlLocation = mysqlLocation.substring(1, mysqlLocation.length()-2);
	        }
	        // @TODO check services for anything looking like
	        // "C:\Program Files\MySQL\MySQL Server 5.1\bin\mysqld" --defaults-file="C:\Program Files\MySQL\MySQL Server 5.1\my.ini" MySQL
	        // see C:\setup\mysql-5.5.7-rc\mysql-5.5.7-rc\packaging\WiX\ca\CustomAction.cpp
	        File mysqlIniLocation = new File(mysqlLocation, "my.ini");
	        IniFile iniFile = new IniFile();
	        iniFile.load(mysqlIniLocation);
	        String mysqlPort = iniFile.get("mysqld", "port");  // default to 3306 ?
 	        
        } else {
        	mysqlLocation = "";
        }
        
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
        defaultAttributes.put("jdbcDriversAvailable", Text.join(driversList, ", "));
        if (driversList.size()==0) {
        	defaultAttributes.put("jdbcDriversAvailable.img", "error");
        	defaultAttributes.put("jdbcDriversAvailable.text", "There are no JDBC drivers available; please download mysql-connector-java-5.1.11.jar and copy it to the server's lib folder (" + serverLibDir.getCanonicalPath() + ")");
        } else if (!driversList.contains("com.mysql.jdbc.Driver")) {
        	defaultAttributes.put("jdbcDriversAvailable.img", "error");
        	defaultAttributes.put("jdbcDriversAvailable.text", "The com.mysql.jdbc.Driver JDBC driver is not available. This is the only supported driver for dmx-web. Please download mysql-connector-java-5.1.11.jar and copy it to the server's lib folder (" + serverLibDir.getCanonicalPath() + ")");
        } else {
        	defaultAttributes.put("jdbcDriversAvailable.img", "ok");
        }
        
        defaultAttributes.put("createSchema", "y");
        defaultAttributes.put("databaseSchema", "dmxweb");
        defaultAttributes.put("databaseDrivers", driversList);
        defaultAttributes.put("database_driver", defaultDriver);
        defaultAttributes.put("database_url", defaultConnString);
        defaultAttributes.put("database_username", "dmxweb");
        defaultAttributes.put("database_password", "dmxweb");
        defaultAttributes.put("database_connectionType", "simple");
        
        defaultAttributes.put("databaseAdminUsername", "root");
        defaultAttributes.put("databaseAdminPassword", "abc123");
        defaultAttributes.put("databaseAdminUrl", defaultAdminConnString);
        
        List dmxDevicePortNames = new ArrayList();
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
		    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
		    	dmxDevicePortNames.add(portId.getName());
		    }
		}
        defaultAttributes.put("comPortsAvailable", Text.join(dmxDevicePortNames, ","));
        if (dmxDevicePortNames.size()==0) {
        	defaultAttributes.put("comPortsAvailable.img", "warn");
        	defaultAttributes.put("comPortsAvailable.text", "No COM ports are available - DMX output will be disabled");
        } else {
        	defaultAttributes.put("comPortsAvailable.img", "ok");
        }
        
        defaultAttributes.put("installOK", "true");
        
        defaultAttributes.put("webapp_fileUpload_tempDir", tempDir.getCanonicalPath());
        defaultAttributes.put("webapp_fileUpload_path", uploadDir.getCanonicalPath());
        defaultAttributes.put("log4j_logDirectory", logDir.getCanonicalPath());
        defaultAttributes.put("audioController_defaultPath", audioDir.getCanonicalPath());
        
        
        List deviceTypes = new ArrayList();
        deviceTypes.add("com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidget");
        defaultAttributes.put("dmxDeviceTypes", deviceTypes);

        List audioControllerTypes = new ArrayList();
        audioControllerTypes.add("com.randomnoun.dmx.protocol.ngWinamp.WinampAudioController");
        defaultAttributes.put("audioControllerTypes", audioControllerTypes);
        defaultAttributes.put("audioController_class", "com.randomnoun.dmx.protocol.ngWinamp.WinampAudioController");

        List audioSourceTypes = new ArrayList();
        audioSourceTypes.add("com.randomnoun.dmx.protocol.dmxWinamp.WinampAudioSource");
        defaultAttributes.put("audioSourceTypes", audioSourceTypes);
        defaultAttributes.put("audioSource_class", "com.randomnoun.dmx.protocol.dmxWinamp.WinampAudioSource");

        
        defaultAttributes.put("dmxDevicePortNames", dmxDevicePortNames);

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
		        defaultAttributes.put("audioController_host", host);
		        defaultAttributes.put("audioController_port", ngPluginProperties.getProperty("sv_port"));
		        defaultAttributes.put("audioController_password", ngPluginProperties.getProperty("sv_pass"));
	        } else {
		        defaultAttributes.put("audioController_host", "localhost");
		        defaultAttributes.put("audioController_port", "18443");
		        defaultAttributes.put("audioController_password", "abc123");
	        }
	        
	        Properties dmxPluginProperties = new Properties();
	        if (dmxPluginCfg.exists()) {
	        	IniFile iniFile = new IniFile();
	        	try {
					iniFile.load(dmxPluginCfg);
			        defaultAttributes.put("audioSource_host", "localhost");
			        defaultAttributes.put("audioSource_port", iniFile.get("settings", "port_number"));
				} catch (ParseException e1) {
					// fall-through
				}	
	        }
	        if (dmxPluginProperties.keySet().size()==0) {
		        defaultAttributes.put("audioSource_host", "localhost");
		        defaultAttributes.put("audioSource_port", "58273");
	        }
        }
        return defaultAttributes;
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