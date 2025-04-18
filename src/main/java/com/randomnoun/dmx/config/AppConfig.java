package com.randomnoun.dmx.config;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.lang.Thread.State;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import bsh.engine.BshScriptEngineFactory;

import com.randomnoun.common.ExceptionUtil;
import com.randomnoun.common.PropertyParser;
import com.randomnoun.common.Text;
import com.randomnoun.common.webapp.AppConfigBase;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.audioController.AudioController;
import com.randomnoun.dmx.audioController.NullAudioController;
import com.randomnoun.dmx.audioSource.AudioSource;
import com.randomnoun.dmx.audioSource.NullAudioSource;
import com.randomnoun.dmx.dao.DeviceDAO;
import com.randomnoun.dmx.dao.DevicePropertyDAO;
import com.randomnoun.dmx.dao.FixtureDAO;
import com.randomnoun.dmx.dao.FixtureDefDAO;
import com.randomnoun.dmx.dao.ShowDAO;
import com.randomnoun.dmx.dao.ShowDefDAO;
import com.randomnoun.dmx.dao.ShowPropertyDAO;
import com.randomnoun.dmx.dao.StageDAO;
import com.randomnoun.dmx.dmxDevice.DmxDevice;
import com.randomnoun.dmx.dmxDevice.NullDmxDevice;
import com.randomnoun.dmx.event.UniverseUpdateListener;
import com.randomnoun.dmx.event.VlcUniverseUpdateListener;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.show.Show;
import com.randomnoun.dmx.show.ShowAudioSource;
import com.randomnoun.dmx.show.ShowThread;
import com.randomnoun.dmx.stage.Stage;
import com.randomnoun.dmx.timeSource.WallClockTimeSource;
import com.randomnoun.dmx.to.DevicePropertyTO;
import com.randomnoun.dmx.to.DeviceTO;
import com.randomnoun.dmx.to.FixtureDefTO;
import com.randomnoun.dmx.to.FixtureTO;
import com.randomnoun.dmx.to.ShowDefTO;
import com.randomnoun.dmx.to.ShowPropertyTO;
import com.randomnoun.dmx.to.ShowTO;
import com.randomnoun.dmx.to.StageTO;
import com.randomnoun.dmx.web.action.FancyControllerAction.RecordingPlaybackShow;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import gnu.io.PortInUseException;

/** Holds configuration data for this web application. Is used to look up 
 * resources for use by other application components, including:
 * 
 * <ul>
 * <li>database connections (JdbcTemplates)
 * <li>secuity contexts (for authentication / authorisation)
 * <li>benchmark/performance data
 * </ul>
 * 
 * @author knoxg
 * @version $Id$
 */
public class AppConfig extends AppConfigBase {

	/** Revision string for stacktraces */
    public static String _revision = "$Id$";

    /** Global instance */
    public static AppConfig instance = null;
    
    /** To prevent recursion in scripts that refer to the AppConfig when they shouldn't */
    public static boolean initialising = false;

    public static final String CONFIG_RESOURCE_LOCATION = "/dmx-web.properties";
    
    public static final String SYSTEM_PROPERTY_KEY_CONFIG_PATH = "com.randomnoun.dmx.configPath";

    // @XXX get this from somewhere. the database probably.
    public static final int DEFAULT_UNIVERSE = 0;
    
    /** Logger instance for this class */
    public static Logger logger = Logger.getLogger(AppConfig.class);
    
    /** Controller instance for this application */
    private Controller controller;
    
    /** DMX device references */
    // private DmxDevice dmxDevice;

    /** DMX device reference */
    private AudioSource audioSource;

    /** The active stage (not used) */
    private Stage activeStage = null;
    
    public enum AppConfigState { UNINITIALISED, RUNNING, STOPPING, STOPPED };
    
    private AppConfigState appConfigState = AppConfigState.UNINITIALISED;
    
    // NB: because this thing takes so long to populate, it might be worth
    // have a copy created in the background 'in reserve' so if we change
    // something we can just update that rather than recreating everything from
    // scratch. maybe. dunno. or actually profile the thing and see
    // what the issue is.
    /** Script context containing scripted fixture definitions, fixtures, and shows */
    private ScriptContext scriptContext;
    
    /** AppConfig exceptions (cleared on reload) */
    private ExceptionContainerImpl exceptionContainer;
    
    /* * Accumulated events to broadcast to HTTP clients */
    //private CometEventManager cometEventManager;
    
    // @TODO keep track of which shows require which fixtures,
    //   prevent shows from running which may have resource conflicts
    // (or have some kind of show override type of thing)
    private static class ShowConfig {
    	Show show;
    	long showId;
    	ShowThread showThread;
    	AppConfig appConfig;
    	
    	public ShowConfig(AppConfig appConfig, long showId, Show show) {
    		this.appConfig = appConfig;
    		this.showId = showId;
    		this.show = show;
    	}
    	synchronized public boolean hasThread() {
    		return showThread != null;
    	}
    	synchronized public ShowThread getThread() {
    		// @TODO possibly use thread pools here
    		boolean newThread = false;
    		if (showThread==null) {
    			if (appConfig.appConfigState != AppConfigState.RUNNING) { 
    				throw new IllegalStateException("Cannot create thread when appConfigState=" + appConfig.appConfigState);
    			}
    			newThread = true;
    		} else if (showThread.getState()==State.TERMINATED) {
    			newThread = true;
    		}
    		if (newThread) {
    			ShowAudioSource showAudioSource = new ShowAudioSource(appConfig.getAudioSource());
    			showThread = new ShowThread(show, showAudioSource);
    			showThread.setName("show-" + showId + "-" + showThread.getId());
    		}
    		return showThread;
    	}
    	public Show getShow() {
    		return show;
    	}
    }
    
    private static class DmxDeviceConfig {
    	DmxDevice dmxDevice;
    	int universeIdx;
    	AppConfig appConfig;
    	public DmxDeviceConfig(AppConfig appConfig, int universeIdx, DmxDevice dmxDevice) {
    		this.appConfig = appConfig;
    		this.universeIdx = universeIdx;
    		this.dmxDevice = dmxDevice;
    	}
    	public int getUniverseIdx() { return universeIdx; }
    	public DmxDevice getDmxDevice() { return dmxDevice; }
    }
    
    
    public static class TimestampedShowException extends ExceptionContainer.TimestampedException {
    	Show show;
		public TimestampedShowException(Show show, Exception exception) {
			super(System.currentTimeMillis(), exception);
			this.show = show;
		}
		public Show getShow() {
			return show;
		}
    }
    

    /** List of shows that this application knows about, and their threads */
    private Map<Long, ShowConfig> showConfigs;
    
    /** Same set, with showName as key */
    private Map<String, ShowConfig> showConfigsByName;

    /** DMX devices */
    private List<DmxDeviceConfig> dmxDeviceConfigs;
    
    /** List of shows */
    private List<Show> shows;
    
    private List<TimestampedShowException> showExceptions;
    
    
    /** Private constructor; this class can only be called via .getInstance() */
	private AppConfig() {
        
	}
    
    /** Initialise the application instance 
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws TooManyListenersException 
     * @throws IOException 
     * @throws PortInUseException 
     * @throws InvocationTargetException 
     * @throws NoSuchMethodException 
     * @throws IllegalArgumentException 
     * @throws SecurityException */
    public synchronized static void initialise() {
    	if (instance!=null && 
    		!(instance.appConfigState==AppConfigState.UNINITIALISED ||
    		instance.appConfigState==AppConfigState.STOPPED)) {
    		System.err.println("Cannot initialise appConfig when in " + instance.appConfigState + " state");
    		return;
    	}
    	if (initialising) {
    		// get a trace of where this happened
    		Exception e = new RuntimeException("Cannot re-initialise appConfig: already initialising");
    		System.err.println(ExceptionUtil.getStackTraceWithRevisions(e, AppConfig.class.getClassLoader(), 
    			ExceptionUtil.HIGHLIGHT_TEXT, "com.randomnoun."));
    		return;
    	}
    	
    	initialising = true;
    	instance = null;
    	System.out.println("Initialising dmx-web...");

        AppConfig newInstance = new AppConfig();
    	try {
    		long startTime = System.currentTimeMillis();
    		newInstance.exceptionContainer = new ExceptionContainerImpl();
	        newInstance.initHostname();
	        newInstance.loadProperties();  // properties depends on hostname
	        newInstance.initSecurityManager();
	        newInstance.initLogger();      // logger depends on properties
	        newInstance.initDatabase();    // db settings also depend on properties
	        // if you were really keen, you could do database schema upgrades here
	        
	        newInstance.initSecurityContext();
	        newInstance.initScriptContext();
		    newInstance.initDmxDevices();
	        newInstance.initController();
	        // newInstance.loadActiveStage(); called by initController()
	        newInstance.loadFixtures(newInstance.getScriptContext(), newInstance.getController());
	        newInstance.loadShowConfigs(newInstance.getScriptContext(), true);
	        newInstance.loadListeners();
	        //newInstance.initCometEventManager();

	        newInstance.appConfigState = AppConfigState.RUNNING;
	        logger.info("appConfig now in " + newInstance.appConfigState + " state; " +
	          "initialisation time=" + ((System.currentTimeMillis() - startTime)/1000.0) + " sec");
    	} catch (Throwable t) {
    		newInstance.initialisationFailure = new RuntimeException("Could not initialise application", t);
    	}

        // assign this to the singleton instance, even if initialisation failed
        instance = newInstance;
        initialising = false;
    }

    private void initSecurityManager() {
        if ("true".equals(getProperty("dev.customSecurityManager"))) {
	        java.lang.SecurityManager sm = System.getSecurityManager();
	    	if (sm==null || !(sm instanceof com.randomnoun.dmx.config.SecurityManager)) {
	    		logger.info("Creating new security manager");
	    		System.setSecurityManager(new SecurityManager(sm));
	    	}
        }
    }
    
    protected void initDatabase() {
    	super.initDatabase();
    	JdbcTemplate jt = getJdbcTemplate();
    	List resourceNames = (List) get("database.mysql.resources");
    	List statements;
    	boolean done = false;
    	int i;
    	for (i=resourceNames.size()-1; i>0 && !done; i--) {
    		String resourceName = (String) resourceNames.get(i);
    		try {
				statements = DatabaseUtil.parseStatements(AppConfig.class.getClassLoader().getResourceAsStream(resourceName), false);
			} catch (IOException ioe) {
				throw new IllegalStateException("Could not read resource '" + resourceName + "'", ioe);
			} catch (ParseException pe) {
				throw new IllegalStateException("Could not parse resource '" + resourceName + "'", pe);
			}
    		try {
    			logger.debug("Testing whether database update " + i + " has been applied (sql='" + statements.get(0) + "')");
    			jt.queryForList((String) statements.get(0));
    			// succeeds, try guard
    			try {
    				logger.debug("Testing whether database update " + i + " has been completely applied (sql='" + statements.get(1) + "')");
    				jt.queryForList((String) statements.get(1));
    			} catch (Exception e2) {
    				logger.debug("Database update " + i + " has not been completely applied");
    				throw new IllegalStateException("Inconsistent database state (resource='" + resourceName + "', sql='" + statements.get(1) + "' failed)", e2);
    			}
    			logger.debug("Database update " + i + " has been completely applied");
    			done = true;
    		} catch (DataAccessException e) {
    			// fails, try previous SQL script
    			// logger.debug("Database update " + i + " has not been applied since '" + statements.get(0) + "' fails");
    		}
    	}
    	if (i < resourceNames.size()-2) {
    		logger.info("********* DATABASE UPGRADE REQUIRED");
    		if ("true".equals(getProperty("database.autoUpdate"))) {
	    		for (int j=i+2; j<resourceNames.size(); j++) {
	    			String resourceName = (String) resourceNames.get(j);
	        		try {
	    				statements = DatabaseUtil.parseStatements(AppConfig.class.getClassLoader().getResourceAsStream(resourceName), false);
	    			} catch (IOException ioe) {
	    				throw new IllegalStateException("Could not read resource '" + resourceName + "'", ioe);
	    			} catch (ParseException pe) {
	    				throw new IllegalStateException("Could not parse resource '" + resourceName + "'", pe);
	    			}
	        		logger.info("Applying database update " + j + ": '" + resourceName + "'");
	        		for (int k=2; k<statements.size(); k++) {
	        			logger.info("Applying statement " + k + ": '" + statements.get(k));	
	        			jt.update((String) statements.get(k));
	        		}
	    		}
    		} else {
    			logger.info("(database.autoUpdate property must be set to 'true')");
    		}
    	}
    	
    }
    
    /** Call this after any fixture definitions, fixtures, show definitions
     * or shows have been modified. 
     * 
     * <p>This method will kill all show threads, reset the controller,
     * stop any audio that's playing, and reload the fixture/show definitions.
     * 
     * @see #reloadShows()
     */
    public void reloadDevicesFixturesAndShows(boolean reloadDevices) {
    	logger.info("reloadDevicesFixturesAndShows(): shutting down show threads");
    	shutdownThreads();
    	if (appConfigState!=AppConfigState.STOPPED) {
    		throw new IllegalStateException("Expected appConfig in STOPPED state; found " + appConfigState);
    	}
    	
    	logger.info("reloadDevicesFixturesAndShows(): resetting controller");
    	try {
    		controller.blackOut();
    	} catch (Exception e) {
    		// this might fail if the blackOut() method for a fixture is farged
    	}
    	
    	logger.info("reloadDevicesFixturesAndShows(): stopping audio");
    	controller.getAudioController().stopAudio();
    	
    	logger.info("reloadDevicesFixturesAndShows(): shutting down listeners");
    	shutdownListeners();

    	// give the listeners 200 msec for threads to close down
    	try { Thread.sleep(200); } catch (InterruptedException ie) { }

    	controller.removeAllFixtures();
    	controller.setStage(null);
    	
    	if (reloadDevices) {
    		logger.info("reloadDevicesFixturesAndShows(): shutting down devices");
    		shutdownDevices();
    	}
    	
    	// reset scriptContext classloader 
    	// see http://www.beanshell.org/manual/classpath.html#Reloading_Classes
    	//String script = "importCommands(\"/bsh/commands\");\n" +
		//	"reloadClasses();\n";
    	//getScriptEngine().eval(script, getScriptContext());
    	
    	// chronic thread safety problems here
    	exceptionContainer.clearExceptions();
    	logger.info("reloadDevicesFixturesAndShows(): reloading scriptContext");
        initScriptContext();
        try {
        	if (reloadDevices) {
        		initDmxDevices();
        		initController(); // also calls loadActiveStage()
        	} else {
        		loadActiveStage();
        		controller.setStage(activeStage);
        	}
        	
	        loadFixtures(getScriptContext(), getController());
	        loadShowConfigs(getScriptContext(), true);
        	loadListeners();
        } catch (Throwable t) {
    		initialisationFailure = new RuntimeException("Could not initialise application", t);
    	}
        
        // @TODO should signal to all the fancyControllers out there that the
        // config has changed from underneath them
        appConfigState = AppConfigState.RUNNING;
    	
    }

    /** Call this after any show definitions or shows have been modified. 
     * 
     * <p>This method assumes that the active stage (and any fixtures for it) have not been changed.
     * 
     * <p>This method will kill all show threads, reset the controller,
     * stop any audio that's playing, and reload the show definitions.
     * 
     * @see #reloadFixturesAndShows()
     * @see #reloadDevices()
     */
    public void reloadShows() {
    	logger.info("reloadShows(): shutting down show threads");
    	shutdownThreads();
    	if (appConfigState!=AppConfigState.STOPPED) {
    		throw new IllegalStateException("Expected appConfig in STOPPED state; found " + appConfigState);
    	}
    	
    	logger.info("reloadShows(): resetting controller");
    	controller.blackOut();
    	
    	logger.info("reloadShows(): stopping audio");
    	controller.getAudioController().stopAudio();
    	
    	// give the listeners 200 msec to actually do something
    	try { Thread.sleep(200); } catch (InterruptedException ie) { }
    	
    	logger.info("reloadShows(): shutting down listeners");
    	shutdownListeners();

    	// controller.removeAllFixtures();
    	
    	// reset scriptContext classloader 
    	// see http://www.beanshell.org/manual/classpath.html#Reloading_Classes
    	//String script = "importCommands(\"/bsh/commands\");\n" +
		//	"reloadClasses();\n";
    	//getScriptEngine().eval(script, getScriptContext());
    	
    	// chronic thread safety problems here
    	exceptionContainer.clearExceptions();
    	logger.info("reloadShows(): reloading showConfigs");
        // initScriptContext();
        try {
	        // loadFixtures(getScriptContext(), getController());
	        loadShowConfigs(getScriptContext(), true);
	        loadListeners();
        } catch (Throwable t) {
    		initialisationFailure = new RuntimeException("Could not initialise application", t);
    	}
        
        // @TODO should signal to all the fancyControllers out there that the
        // config has changed from underneath them
        appConfigState = AppConfigState.RUNNING;
    	
    }
    
    
    // used from within security framework and ServletContextListener; 
    // just returns null if no
    // appConfig has been initialised
    static AppConfig getAppConfigNoInit() {
    	return instance;
    }
    
    /** Return a configuration instance 
     *
     * @throws IllegalStateException if the application did not initialise successfully. 
     */
    public static AppConfig getAppConfig() {
        if (instance == null) {
            //instance = new AppConfig();
            try {
                AppConfig.initialise();
            } catch (Throwable t) {
                System.out.println("Error initialising");
                t.printStackTrace();
                //instance.initialisationFailure = new RuntimeException("Could not initialise application", t);
            }
            
        }
        if (instance.initialisationFailure!=null) {
            throw (IllegalStateException) new IllegalStateException("Application failed initialisation").
              initCause(instance.initialisationFailure);
        }
        return instance;
    }
    
    private void initScriptContext() {
        this.scriptContext = getScriptEngine().getContext();
        //engine.eval("print('Hello, World')");
    }
    
    /*
    private void initCometEventManager() {
    	cometEventManager = new CometEventManager();
    }
    */
    
    // @TODO cache this ?
    public ScriptEngine getScriptEngine() {
    	ScriptEngineManager factory = new ScriptEngineManager();
        factory.registerEngineName("Beanshell", new BshScriptEngineFactory());
        ScriptEngine scriptEngine = factory.getEngineByName("Beanshell");
        return scriptEngine;
    }
    
    public ScriptContext getScriptContext() {
    	return scriptContext;
    }
    
    
    private void initDmxDevices() throws InstantiationException, IllegalAccessException, ClassNotFoundException, PortInUseException, IOException, TooManyListenersException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException { 
    	dmxDeviceConfigs = new ArrayList<DmxDeviceConfig>();

    	//String portName = getProperty("dmxDevice.portName");
    	//widget = new UsbProWidget(portName);
    	//UsbProWidgetTranslator translator = widget.openPort();
		
		// TODO: (see MaintainDeviceAction)
		// check that all active universes are sequentially numbered from 1
		// (or possibly put in nulldevices for empty universes?)
		
		JdbcTemplate jt = getJdbcTemplate();
		DeviceDAO deviceDAO = new DeviceDAO(jt);
		DevicePropertyDAO devicePropertyDAO = new DevicePropertyDAO(jt);

		// @TODO same thing as ShowDAO here (Properties, not PropertyCounts)
		List<DeviceTO> devices = deviceDAO.getDevicesWithPropertyCounts("active='Y' AND type='D'");
		Collections.sort(devices, new Comparator<DeviceTO>() {
			public int compare(DeviceTO o1, DeviceTO o2) {
				return o1.getUniverseNumber().compareTo(o2.getUniverseNumber());
			} });
		for (int i=0; i<devices.size(); i++) {
			DeviceTO deviceTO = devices.get(i);
			DmxDevice device = null;
			Map deviceProperties = new HashMap();
			if (deviceTO.getDevicePropertyCount()>0) {
				List<DevicePropertyTO> devicePropertyTOs = devicePropertyDAO.getDeviceProperties("deviceId=" + deviceTO.getId());
				for (DevicePropertyTO deviceProperty : devicePropertyTOs) {
					deviceProperties.put(deviceProperty.getKey(), deviceProperty.getValue());
				}
			}		
			Class clazz = Class.forName(deviceTO.getClassName());
			Constructor con = clazz.getConstructor(Map.class);
			try {
				device = (DmxDevice) con.newInstance(new Object[] { deviceProperties });
				String name = device.getName();
				logger.info("Created device " + deviceTO.getClassName() + " '" + device.getName() + "' on universe " + deviceTO.getUniverseNumber());
			} catch (Exception e) {
				IOException ioe = new IOException("Could not create device '" + deviceTO.getName() + "'", e);
				logger.error("Could not instantiate device " + deviceTO.getClassName () + " '" + deviceTO.getName() + "' on universe " + deviceTO.getUniverseNumber(), ioe);
				addAppConfigException(ioe);
				// @TODO: necessary to create a null device here ?
				device = new NullDmxDevice(deviceProperties);
			}
			// convert to 0-based universe index
			DmxDeviceConfig ddc = new DmxDeviceConfig(this, deviceTO.getUniverseNumber().intValue() - 1, device);
			dmxDeviceConfigs.add(ddc);
			logger.info("Opening device " + deviceTO.getClassName() + " '" + device.getName() + "' on universe " + deviceTO.getUniverseNumber());
			// @XXX: exception handling in device class 
			device.open();
		}
		
		
		/* get dmx device from properties file
		String dmxClassname = (String) this.get("dmxDevice.class");
		if (dmxClassname==null) {
			dmxClassname = "com.randomnoun.dmx.protocol.nullDevice.NullDmxDevice";
		}
		Map dmxProperties = PropertyParser.restrict(this, "dmxDevice", true);
		Class dmxClass = Class.forName(dmxClassname);
		Constructor dmxConstructor = dmxClass.getConstructor(Map.class);
		dmxDevice = (DmxDevice) dmxConstructor.newInstance(dmxProperties);
    	dmxDevice.open();
    	*/

    }
    
    private void initController() throws InstantiationException, IllegalAccessException, ClassNotFoundException, PortInUseException, IOException, TooManyListenersException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

		JdbcTemplate jt = getJdbcTemplate();
		DeviceDAO deviceDAO = new DeviceDAO(jt);
		DevicePropertyDAO devicePropertyDAO = new DevicePropertyDAO(jt);
		AudioController audioController = null;
		AudioSource audioSource = null;
		
    	// this should always create at least one universe (idx 0)
    	List<Universe> universes = new ArrayList<Universe>();
    	int maxUniverseIdx=0;
    	for (int i=0; i<dmxDeviceConfigs.size(); i++) {
    		maxUniverseIdx = Math.max(maxUniverseIdx, dmxDeviceConfigs.get(i).getUniverseIdx());
    	}
    	for (int i=0; i<=maxUniverseIdx; i++) {
    		Universe universe = new Universe(i);
    		universe.setTimeSource(new WallClockTimeSource());
    		universes.add(universe);
    	}
    	
		List<DeviceTO> devices = deviceDAO.getDevicesWithPropertyCounts("active='Y' AND type='C'");
		if (devices.size()>0) {
			DeviceTO deviceTO = devices.get(0);
			Map acProperties = new HashMap();
			if (deviceTO.getDevicePropertyCount()>0) {
				List<DevicePropertyTO> devicePropertyTOs = devicePropertyDAO.getDeviceProperties("deviceId=" + deviceTO.getId());
				for (DevicePropertyTO deviceProperty : devicePropertyTOs) {
					acProperties.put(deviceProperty.getKey(), deviceProperty.getValue());
				}
			}		
			Class clazz = Class.forName(deviceTO.getClassName());
			Constructor con = clazz.getConstructor(Map.class);
			try {
				audioController = (AudioController) con.newInstance(new Object[] { acProperties });
				String name = audioController.getName();
				logger.info("Created audioController " + deviceTO.getClassName() + " '" + audioController.getName() + "'");
			} catch (Exception e) {
				IOException ioe = new IOException("Could not create audioController '" + deviceTO.getName() + "'", e);
				logger.error("Could not instantiate audioController " + deviceTO.getClassName () + " '" + deviceTO.getName() + "'", ioe);
				addAppConfigException(ioe);
				// @TODO: necessary to create a null device here ?
				audioController = new NullAudioController(acProperties);
			}
			try {
				audioController.open();
			} catch (Exception e) {
				// @TODO hmmmmmmmmmmmmmmmmmm.........
				logger.error("Couldn't open audioController", e);
			}
		} else {
			logger.info("No audioController configured; using NullAudioController");
			audioController = new NullAudioController(null);
			audioController.open();
		}
		
    	/* audio controller from properties file
		String acClassname = (String) this.get("audioController.class");
		if (acClassname==null) {
			acClassname = "com.randomnoun.dmx.protocol.nullDevice.NullAudioController";
		}
		Map acProperties = PropertyParser.restrict(this, "audioController", true);
		Class acClass = Class.forName(acClassname);
		Constructor acConstructor = acClass.getConstructor(Map.class);
		AudioController audioController = (AudioController) acConstructor.newInstance(acProperties);
		try {
			audioController.open();
		} catch (Exception e) {
			// @TODO hmmmmmmmmmmmmmmmmmm.........
			logger.error("Couldn't open audioController", e);
		}
		*/

		// @TODO load with earlier thingo
		devices = deviceDAO.getDevicesWithPropertyCounts("active='Y' AND type='S'");
		if (devices.size()>0) {
			DeviceTO deviceTO = devices.get(0);
			Map asProperties = new HashMap();
			if (deviceTO.getDevicePropertyCount()>0) {
				List<DevicePropertyTO> devicePropertyTOs = devicePropertyDAO.getDeviceProperties("deviceId=" + deviceTO.getId());
				for (DevicePropertyTO deviceProperty : devicePropertyTOs) {
					asProperties.put(deviceProperty.getKey(), deviceProperty.getValue());
				}
			}		
			Class clazz = Class.forName(deviceTO.getClassName());
			Constructor con = clazz.getConstructor(Map.class);
			try {
				audioSource = (AudioSource) con.newInstance(new Object[] { asProperties });
				String name = audioSource.getName();
				logger.info("Created audioSource " + deviceTO.getClassName() + " '" + audioSource.getName() + "'");
			} catch (Exception e) {
				IOException ioe = new IOException("Could not create audioSource '" + deviceTO.getName() + "'", e);
				logger.error("Could not instantiate audioSource " + deviceTO.getClassName () + " '" + deviceTO.getName() + "'", ioe);
				addAppConfigException(ioe);
				// @TODO: necessary to create a null device here ?
				audioSource = new NullAudioSource(asProperties);
			}
			try {
				audioSource.open();
			} catch (Exception e) {
				// @TODO hmmmmmmmmmmmmmmmmmm.........
				logger.error("Couldn't open audioController", e);
			}
		} else {
			logger.info("No audioSource configured; using NullAudioSource");
			audioSource = new NullAudioSource(null);
			audioSource.open();
		}
		
		/* audio source from properties file 
		String asClassname = (String) this.get("audioSource.class");
		if (asClassname==null) {
			asClassname = "com.randomnoun.dmx.protocol.nullDevice.NullAudioSource";
		}
		Map asProperties = PropertyParser.restrict(this, "audioSource", true);
		Class asClass = Class.forName(asClassname);
		Constructor asConstructor = asClass.getConstructor(Map.class);
		audioSource = (AudioSource) asConstructor.newInstance(asProperties);
		try {
			audioSource.open();
		} catch (Exception e) {
			// @TODO hmmmmmmmmmmmmmmmmmm.........
			logger.error("Couldn't open audioController", e);
		}
		*/

		
		loadActiveStage();

		this.audioSource = audioSource;
		controller = new Controller();
		controller.setUniverses(universes);
		controller.setAudioController(audioController);
		controller.setStage(activeStage);
		controller.setResourcePath((String) this.get("webapp.resource.path"));
		
    }
    
    public void loadFixtures(ScriptContext fixtureScriptContext, Controller scriptController) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    	
		List fixturesFromProperties = (List) get("fixtures");
		if (fixturesFromProperties == null || fixturesFromProperties.size()==0) {
			logger.warn("No fixtures in appConfig properties");
		} else {
			for (int i = 0; i < fixturesFromProperties.size(); i++) {
				Map fixture = (Map) fixturesFromProperties.get(i);
				String fixtureClass = (String) fixture.get("class");
				String name = (String) fixture.get("name");
				String dmxOffset = (String) fixture.get("dmxOffset");
				// @TODO re-use fixture definition classes
				FixtureDef fixtureDef = (FixtureDef) Class.forName(fixtureClass).newInstance();
				if (fixtureDef.getNumDmxChannels()<=0) {
					logger.error("Fixture " + i + " has no DMX channels");
				}
				// @TODO setImagePath() for fixtures in property files
				Fixture fixtureObj = new Fixture(name, fixtureDef, controller.getUniverse(DEFAULT_UNIVERSE), Integer.parseInt(dmxOffset));
				if (scriptController!=null) {
					scriptController.addFixture(fixtureObj);
				}
			}
		}
		
		
		// I'm assuming all this needs to go into a separate classLoader eventually
		Map scriptedFixtureDefs = new HashMap(); // id -> scripted fixtureDef instance
		FixtureDefDAO fixtureDefDAO = new FixtureDefDAO(getJdbcTemplate());
		List fixtureDefsFromDatabase = fixtureDefDAO.getFixtureDefs(
			"id IN " +
			" (SELECT DISTINCT fixtureDefId FROM fixture WHERE stageId IN " +
			"   (SELECT id FROM stage WHERE active='Y'))");
		if (fixtureDefsFromDatabase == null || fixtureDefsFromDatabase.size()==0) {
			logger.warn("No fixture definitions in database");
		} else {
			for (int i = 0; i < fixtureDefsFromDatabase.size(); i++) {
				FixtureDefTO fixtureDef = (FixtureDefTO) fixtureDefsFromDatabase.get(i);
				// @TODO load this into a separate evaluation context first ?
				// (what about dependencies between fixtureDef objects) ?
				try {
					logger.debug("Loading scripted fixtureDef '" + fixtureDef.getName() + "' from database");
					getScriptEngine().eval(fixtureDef.getFixtureDefScript(), fixtureScriptContext);
					
					logger.debug("Loading scripted fixtureController '" + fixtureDef.getName() + "' from database");
					getScriptEngine().eval(fixtureDef.getFixtureControllerScript(), fixtureScriptContext);

					logger.debug("Loading scripted channelMuxer '" + fixtureDef.getName() + "' from database");
					getScriptEngine().eval(fixtureDef.getChannelMuxerScript(), fixtureScriptContext);
					
					// @TODO validate that the scriptContext now contains a 
					// fixture definition of the class specified in the FixtureDefTO
					// @TODO maybe scope the instance here so that it doesn't clobber any other global 'instance' instance
					
					// @TODO is there a way to reset the import declarations specified in a scriptContext ?
					String testScript =
						"import com.randomnoun.dmx.fixture.FixtureDef;\n" +
						"import " + fixtureDef.getFixtureDefClassName() + ";\n" +
						"return new " + fixtureDef.getFixtureDefClassName() + "().init(\"" + fixtureDef.getName() + "\");\n" ;
					// @TODO check class before instantiating
					Object instance = (Object) getScriptEngine().eval(testScript, fixtureScriptContext);
					if (instance instanceof FixtureDef) {
						((FixtureDef) instance).setImagePath("image/fixture/" + fixtureDef.getId() + "/");
						scriptedFixtureDefs.put(fixtureDef.getId(), instance);
					} else {
						logger.error("Error instantiating object for fixtureDef " + fixtureDef.getId() + ": '" + fixtureDef.getName() + "'; className='" + fixtureDef.getFixtureDefClassName() + "' does not extend com.randomnoun.dmx.fixture.FixtureDef"); 
					}
					
					
					 
					/*
					 still getting things like this on initialisation, so commenting out: 
					 
[dmx-web] 07:34:43,959 DEBUG [http-8080-4] com.randomnoun.dmx.config.AppConfig - Loading scripted channelMuxer 'Ceiling' from database
bsh.InterpreterError: null fromValue
        at bsh.Types.castObject(Types.java:303)
        at bsh.BshMethod.invokeImpl(BshMethod.java:319)
        at bsh.BshMethod.invoke(BshMethod.java:259)
        at bsh.ClassGeneratorUtil.initInstance(ClassGeneratorUtil.java:1021)
        at com.brisbanecomedy.dmx.fixture.CeilingFixtureController.<init>(BeanShell Generated via ASM (www.objectweb.org))
        at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
        at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:39)
        at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:27)
        at java.lang.reflect.Constructor.newInstance(Constructor.java:513)
        at bsh.Reflect.constructObject(Reflect.java:620)
        at bsh.BSHAllocationExpression.constructObject(BSHAllocationExpression.java:123)
        at bsh.BSHAllocationExpression.objectAllocation(BSHAllocationExpression.java:114)
        at bsh.BSHAllocationExpression.eval(BSHAllocationExpression.java:62)
        at bsh.BSHPrimaryExpression.eval(BSHPrimaryExpression.java:102)
        at bsh.BSHPrimaryExpression.eval(BSHPrimaryExpression.java:47)
        at bsh.BSHReturnStatement.eval(BSHReturnStatement.java:48)
        at bsh.Interpreter.eval(Interpreter.java:649)
        at bsh.Interpreter.eval(Interpreter.java:743)
        at bsh.Interpreter.eval(Interpreter.java:732)
        at bsh.engine.BshScriptEngine.evalSource(BshScriptEngine.java:78)
        at bsh.engine.BshScriptEngine.eval(BshScriptEngine.java:46)
        at com.randomnoun.dmx.config.AppConfig.loadFixtures(AppConfig.java:506)
        at com.randomnoun.dmx.config.AppConfig.reloadFixturesAndShows(AppConfig.java:264)
        at com.randomnoun.dmx.web.action.MaintainFixtureDefAction.execute(MaintainFixtureDefAction.java:212)
        at org.apache.struts.action.RequestProcessor.processActionPerform(RequestProcessor.java:425)
        at com.randomnoun.common.webapp.struts.CustomRequestProcessor.processActionPerform(CustomRequestProcessor.java:589)
        at org.apache.struts.action.RequestProcessor.process(RequestProcessor.java:228)
        at com.randomnoun.common.webapp.struts.CustomRequestProcessor.process(CustomRequestProcessor.java:725)
        at org.apache.struts.action.ActionServlet.process(ActionServlet.java:1913)
        at org.apache.struts.action.ActionServlet.doGet(ActionServlet.java:449)
        at com.randomnoun.common.webapp.struts.CustomActionServlet.doPost(CustomActionServlet.java:60)
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:637)
        at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:717)
        at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:290)
        at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)
        at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:233)
        at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:191)
        at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:127)
        at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:102)
        at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:109)
        at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:298)
        at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:859)
        at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:588)
        at org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:489)
        at java.lang.Thread.run(Thread.java:619)

					String testScript2 =
						"import com.randomnoun.dmx.fixture.FixtureController;\n" +
						"import " + fixtureDef.getFixtureControllerClassName() + ";\n" +
						"return new " + fixtureDef.getFixtureControllerClassName() + "(null);\n" ;
					// @TODO check class before instantiating
					Object instance2 = (Object) getScriptEngine().eval(testScript2, fixtureScriptContext);
					if (instance2 instanceof FixtureController) {
						// fine
					} else {
						logger.error("Error instantiating object for fixtureDef " + fixtureDef.getId() + ": '" + fixtureDef.getName() + "'; className='" + fixtureDef.getFixtureControllerClassName() + "' does not extend com.randomnoun.dmx.fixture.FixtureController"); 
					}
					 */

					// check for FixtureController will occur later, perhaps
					// (should only occur if a Fixture of this type has been registered at a DMX offset, though)
				
				} catch (ScriptException se) {
					AppConfigException ace = new AppConfigException("Error evaluating script for fixtureDef " + fixtureDef.getId() + ": '" + fixtureDef.getName() + "'", se);
					exceptionContainer.addException(ace);
					logger.error(ace);
				} catch (Throwable e) {
					AppConfigException ace = new AppConfigException("Error evaluating script for fixtureDef " + fixtureDef.getId() + ": '" + fixtureDef.getName() + "'", e);
					exceptionContainer.addException(ace);
					logger.error(ace);
				}
			}
		}

		if (activeStage==null) {
			logger.warn("No active stage; not loading fixtures from database");
			return;
		}
		
		List fixturesFromDatabase = new FixtureDAO(getJdbcTemplate()).getFixtures("stageId=" + activeStage.getId());
		if (fixturesFromDatabase == null || fixturesFromDatabase.size()==0) {
			logger.warn("No fixtures in database");
		} else {
			for (int i = 0; i < fixturesFromDatabase.size(); i++) {
				FixtureTO fixtureTO = (FixtureTO) fixturesFromDatabase.get(i);
				long fixtureDefId = fixtureTO.getFixtureDefId();
				FixtureDef fixtureDef = (FixtureDef) scriptedFixtureDefs.get(fixtureDefId);
				if (fixtureDef==null) {
					logger.error("Error whilst creating fixture " + fixtureTO.getId() + ": '" + fixtureTO.getName() + "'; no fixtureDef found with id ' " + fixtureDefId + "'");
				} else {
					logger.debug("Creating scripted fixture '" + fixtureTO.getName() + "' at dmxOffset + " + fixtureTO.getDmxOffset() + " from database");
					// check that we have enough universes for this fixture
					int universeId = (int) fixtureTO.getUniverseNumber() - 1;
					while (controller.getUniverses().size() <= universeId) {
						logger.debug("Creating empty universe " + controller.getUniverses().size()); // 0-based
						Universe u = new Universe(controller.getUniverses().size());
			    		u.setTimeSource(new WallClockTimeSource());
						controller.getUniverses().add(u);
					}
					Fixture fixture = new Fixture(fixtureTO.getName(), 
						fixtureDef, controller.getUniverse(universeId), 
						(int) fixtureTO.getDmxOffset());
					if (fixtureTO.getX()!=null) { fixture.setPosition(fixtureTO.getX(), fixtureTO.getY(), fixtureTO.getZ()); }
					if (fixtureTO.getLookingAtX()!=null) { fixture.setLookingAt(fixtureTO.getLookingAtX(), fixtureTO.getLookingAtY(), fixtureTO.getLookingAtZ()); }
					if (fixtureTO.getUpX()!=null) { fixture.setUpVector(fixtureTO.getUpX(), fixtureTO.getUpY(), fixtureTO.getUpZ()); }
					if (fixtureTO.getSortOrder()!=null) { fixture.setSortOrder(fixtureTO.getSortOrder().intValue()); }
					if (fixtureTO.getFixPanelX()!=null) { fixture.setFixPanelPosition(fixtureTO.getFixPanelX(), fixtureTO.getFixPanelY()); }
					fixture.setFixPanelType(fixtureTO.getFixPanelType()); 
					try {
						//hmm... might need to check that scriptController==getController()
						//before doing this ?
						//FixtureController thisFixtureController = fixture.getFixtureController();
						if (scriptController != null) {
							scriptController.addFixture(fixture);
						}
						/*
						 * if the fixtureController contains a ref to the appConfig (bad),
						 * then this prevents appConfig from initialising
						 *
						if (scriptController == getController()) {  // NB: intentionally not .equals()
							FixtureController testFixtureController = fixture.getFixtureController();
						}
						*/
					} catch (Exception e) {
						AppConfigException ace = new AppConfigException("Exception in getFixtureController() for fixture " + fixtureTO.getId() + ": '" + fixtureTO.getName() + "'", e);
						logger.error(ace);
						exceptionContainer.addException(ace);
						scriptController.removeFixture(fixture);
					}
				}
			}
		}
		
    }
    
    public void loadActiveStage() {
		// @TODO may just want to store this in the controller rather than
    	// in both the controller and this class
    	
		StageDAO stageDAO = new StageDAO(getJdbcTemplate());
		Long activeStageId = stageDAO.getActiveStageId();
		
		if (activeStageId==null) {
			activeStage = null;
		} else {
			StageTO activeStageTO = stageDAO.getStage(activeStageId);
			String fpbi = activeStageTO.getFixPanelBackgroundImage();
			activeStage = new Stage(activeStageTO.getId(), activeStageTO.getName(),
				Text.isBlank(fpbi) ? null : "image/stage/" + activeStageTO.getId() + "/" + fpbi,
				true);
		}

    }
    
    public void loadShowConfigs(ScriptContext showScriptContext, boolean addToAppConfig) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    	showConfigs = new HashMap<Long, ShowConfig>();
    	showConfigsByName = new HashMap<String, ShowConfig>();
    	
    	shows = new ArrayList<Show>();
    	showExceptions = Collections.synchronizedList(new ArrayList<TimestampedShowException>());
    	
    	
		List showsFromProperties = (List) get("shows");
		if (showsFromProperties == null || showsFromProperties.size()==0) {
			logger.warn("No show definitions in appConfig properties");
		} else {
			for (int i=0; i<showsFromProperties.size(); i++) {
				long id = shows.size();
				String name = null;
				try {
					Properties showProperties = new Properties();
					showProperties.putAll((Map) showsFromProperties.get(i));
					String showClassName = (String) showProperties.get("class");
					Class showClass = Class.forName(showClassName);
					Constructor constructor = showClass.getConstructor(long.class, Controller.class, Properties.class);
					String onCompleteShowId = (String) showProperties.get("onCompleteShowId");
					String onCancelShowId = (String) showProperties.get("onCancelShowId");
					name = (String) showProperties.get("name");
					Show showObj = (Show) constructor.newInstance(id, controller, showProperties);
					if (onCompleteShowId!=null) { showObj.setOnCompleteShowId(Long.parseLong(onCompleteShowId)); }
					if (onCancelShowId!=null) { showObj.setOnCancelShowId(Long.parseLong(onCancelShowId)); }
					if (!Text.isBlank(name)) { showObj.setName(name); }
					ShowConfig showConfig = new ShowConfig(this, i, showObj);
					showConfigs.put(new Long(i), showConfig);
					showConfigsByName.put(name, showConfig);
					shows.add(showObj);
				} catch (Exception e) {
					AppConfigException ace = new AppConfigException("Error whilst instantiating compiled show " + id + ": '" + name + "'", e);
					exceptionContainer.addException(ace);
					logger.error(ace);
				}
			}
		}
		
		// I'm assuming all this needs to go into a separate classLoader eventually
		// @TODO see all the comments above for fixture defs
		Map<Long, ShowDefTO> scriptedShowDefs = new HashMap<Long, ShowDefTO>(); // id -> showDef object containing scripted show class object
		//Map scriptedShowDescriptions = new HashMap(); // id -> scripted show javadoc
		ShowDefDAO showDefDAO = new ShowDefDAO(getJdbcTemplate());
		
		// NB: slightly different SQL for JET here
		List showDefsFromDatabase = showDefDAO.getShowDefsWithAttachments(
			"showDef.id IN " +
			" (SELECT DISTINCT showDefId FROM `show` WHERE stageId IN " +
			"   (SELECT id FROM stage WHERE active='Y'))");
		if (showDefsFromDatabase == null || showDefsFromDatabase.size()==0) {
			logger.warn("No show definitions in database");
		} else {
			for (int i = 0; i < showDefsFromDatabase.size(); i++) {
				ShowDefTO showDef = (ShowDefTO) showDefsFromDatabase.get(i);
				try {
					logger.debug("Loading scripted show class '" + showDef.getName() + "' from database");
					getScriptEngine().eval(showDef.getScript(), showScriptContext);
					String testScript =
						"import com.randomnoun.dmx.Show;\n" +
						"import " + showDef.getClassName() + ";\n" +
						"return " + showDef.getClassName() + ".class;\n" ;
					Class clazz = (Class) getScriptEngine().eval(testScript, showScriptContext);
					if (Show.class.isAssignableFrom(clazz)) {
						showDef.setShowClass(clazz);
						scriptedShowDefs.put(showDef.getId(), showDef);
						// scriptedShowDescriptions.put(showDef.getId(), showDef.getJavadoc());
					} else {
						logger.error("Error processing show " + showDef.getId() + ": '" + showDef.getName() + "'; className='" + showDef.getClassName() + "' does not extend com.randomnoun.dmx.Show"); 
					}
				} catch (ScriptException se) {
					AppConfigException ace = new AppConfigException("Error evaluating script for show " + showDef.getId() + ": '" + showDef.getName() + "'", se);
					exceptionContainer.addException(ace);
					logger.error(ace);
				} catch (Exception e) {
					AppConfigException ace = new AppConfigException("Error evaluating script for show " + showDef.getId() + ": '" + showDef.getName() + "'", e);
					exceptionContainer.addException(ace);
					logger.error(ace);
				}
			}
		}

		if (activeStage==null) {
			logger.warn("No active stage; not loading shows from database");
			return;
		}
		
		List showsFromDatabase = new ShowDAO(getJdbcTemplate()).getShowsWithProperties("stageId=" + activeStage.getId());
		ShowPropertyDAO showPropertyDAO = new ShowPropertyDAO(getJdbcTemplate());
		if (showsFromDatabase == null || showsFromDatabase.size()==0) {
			logger.warn("No show instances in database");
		} else {
			for (int i = 0; i < showsFromDatabase.size(); i++) {
				ShowTO showTO = (ShowTO) showsFromDatabase.get(i);
				long showDefId = showTO.getShowDefId();
				ShowDefTO showDef = scriptedShowDefs.get(showDefId);
				Constructor constructor = null;
				int constructorType = 0; // 0=no-args, 1=args (deprecated)
				if (showDef == null || showDef.getShowClass()==null) {
					AppConfigException ace = new AppConfigException("Error whilst creating show " + showTO.getId() + ": '" + showTO.getName() + "'; no showDef found with id '" + showDefId + "'");
					logger.error(ace);
				} else {
					Class showClass = showDef.getShowClass();
					try {
						constructorType = 0;
						constructor = showClass.getConstructor();
					} catch (Exception e) {
						try {
							constructorType = 1;
							constructor = showClass.getConstructor(long.class, Controller.class, Properties.class);
							logger.warn("Show " + showTO.getId() + " using deprecated constructor");
						} catch (Exception e2) {
							AppConfigException ace = new AppConfigException("Error whilst instantiating show " + showTO.getId() + ": '" + showTO.getName() + "'", e2);
							exceptionContainer.addException(ace);
							logger.error(ace);
						}
					}
				}
				if (constructor==null) {
					// errors logged above 
				} else {
					logger.debug("Creating scripted show '" + showTO.getName() + "' from database");
					Properties showProperties = new Properties();
					//if (showTO.getOnCompleteShowId()!=null) { showProperties.put("onCompleteShowId", showTO.getOnCompleteShowId().toString()); }
					//if (showTO.getOnCancelShowId()!=null) { showProperties.put("onCancelShowId", showTO.getOnCancelShowId().toString()); }
					//if (!Text.isBlank(showTO.getName())) { showProperties.put("name", showTO.getName()); }
					// @TODO additional properties from database
					Show showObj;
					try {
						if (addToAppConfig) {
							//if (showTO.getShowPropertyCount()>0) {
							//List<ShowPropertyTO> showPropertyTOs = showPropertyDAO.getShowProperties("showId=" + showTO.getId());
							List<ShowPropertyTO> showPropertyTOs = showTO.getShowProperties();
							for (ShowPropertyTO showProperty : showPropertyTOs) {
								showProperties.put(showProperty.getKey(), showProperty.getValue());
							}
							if (constructorType==0) {
								showObj = (Show) constructor.newInstance();
								showObj.setId(showTO.getId());
								showObj.setController(controller);
								showObj.setProperties(showProperties);
							} else {
								showObj = (Show) constructor.newInstance(showTO.getId(), controller, showProperties);
							}
							if (showTO.getOnCompleteShowId()!=null) { showObj.setOnCompleteShowId(showTO.getOnCompleteShowId().longValue()); }
							if (showTO.getOnCancelShowId()!=null) { showObj.setOnCancelShowId(showTO.getOnCancelShowId().longValue()); }
							if (showTO.getShowGroupId()!=null) { showObj.setShowGroupId(showTO.getShowGroupId().longValue()); }
							showObj.setShowDefAttachments(
								new File(getProperty("webapp.fileUpload.path"), "showDefs/" + showDef.getId()), 
								showDef.getShowDefAttachments());
							if (!Text.isBlank(showTO.getName())) { showObj.setName(showTO.getName()); }
							if (!Text.isBlank(showDef.getJavadoc()) && showObj.getDescription()==null) { 
								showObj.setDescription(removeCommentTokens(showDef.getJavadoc())); 
							}
							showObj.init(); // @TODO move most of the constructor code in each show to this method
							ShowConfig showConfig = new ShowConfig(this, showTO.getId(), showObj);
							showConfigs.put(showTO.getId(), showConfig);
							showConfigsByName.put(showObj.getName(), showConfig); 
							shows.add(showObj);
						}
					} catch (Exception e) {
						AppConfigException ace = new AppConfigException("Error whilst instantiating show " + showTO.getId() + ": '" + showTO.getName() + "'", e);
						exceptionContainer.addException(ace);
						logger.error(ace);
					}
				}
			}
		}
    }
    
    public void loadListeners() {
    	/*
		Universe universe = controller.getUniverse(DEFAULT_UNIVERSE);
    	UniverseUpdateListener updateListener = dmxDevice.getUniverseUpdateListener(); 
		universe.addListener(updateListener);
		updateListener.startThread();
		*/
    	
    	// add an update listener to each universe for any devices
    	// configured on those universes
    	for (int i=0; i<dmxDeviceConfigs.size(); i++) {
    		DmxDeviceConfig ddc = dmxDeviceConfigs.get(i);
    		Universe universe = controller.getUniverse(ddc.getUniverseIdx());
    		UniverseUpdateListener updateListener = ddc.getDmxDevice().getUniverseUpdateListener(); 
    		universe.addListener(updateListener);
    		updateListener.startThread();
    	}
    	
		
		if (!Text.isBlank((String) getProperty("dev.vlc.host"))) {
			try {
				Universe universe = controller.getUniverse(DEFAULT_UNIVERSE);
				UniverseUpdateListener updateListener = 
				  new VlcUniverseUpdateListener(
						getProperty("dev.vlc.host"), getProperty("dev.vlc.port"));
				((VlcUniverseUpdateListener) updateListener).setFixture(controller.getFixtureByName(getProperty("dev.vlc.fixtureName"))); 
				universe.addListener(updateListener);
				updateListener.startThread();
				
			} catch (UnknownHostException e) {
				AppConfigException ace = new AppConfigException("Could not attach VLC listener (UnknownHostException)", e);
				exceptionContainer.addException(ace);
				logger.error(ace);
			} catch (Exception e) {
				AppConfigException ace = new AppConfigException("Could not attach VLC listener", e);
				exceptionContainer.addException(ace);
				logger.error(ace);
				
			}
		}
    }
    
    // @TODO private this method ?
    /** The index of shows in this collection is not the showId. The 
     * showId is the showId of the Show objects returned.
     */
    public List<Show> getShows() {
    	return shows;
    }
    
    public Show getShow(long showId) {
    	ShowConfig sc = showConfigs.get(showId); 
    	if (sc==null) { throw new IllegalArgumentException("Unknown showId " + showId); }
    	return sc.getShow();
    }
    
    public Show getShowNoEx(long showId) {
    	ShowConfig sc = showConfigs.get(showId); 
    	if (sc==null) { return null; }
    	return sc.getShow();
    }

    public Show getShowByNameNoEx(String showName) {
    	ShowConfig sc = showConfigsByName.get(showName); 
    	if (sc==null) { return null; }
    	return sc.getShow();
    }
    
    /** Start a show whilst in the recorded show editor.
     * 
     * The show is temporarily added to group 0 whilst running, then
     * removed once it has been cancelled.
     */ 
    public void startRecordingPlaybackShow(RecordingPlaybackShow rps) {
		//if (onCompleteShowId!=null) { showObj.setOnCompleteShowId(Long.parseLong(onCompleteShowId)); }
		//if (onCancelShowId!=null) { showObj.setOnCancelShowId(Long.parseLong(onCancelShowId)); }
		//if (!Text.isBlank(name)) { showObj.setName(name); }
    	
    	long showIdx = rps.getId(); // dragons etc. Should always be set to -1
    	while (showConfigs.get(showIdx)!=null) {
    		logger.info("showIdx " + showIdx + " already exists");
    		return; 
    	}
		ShowConfig showConfig = new ShowConfig(this, showIdx, rps);
		showConfigs.put(rps.getId(), showConfig);
		//showConfigsByName.put(rps.getName(), showConfig); // is only used in API
		shows.add(rps);
		startShow(showIdx);
    }
    
    /** Called when the recorded playback show is complete. Removes this
     * temporary show from the application */
    public void stopRecordingPlaybackShowCallback(RecordingPlaybackShow rps) {
    	logger.info("Removing recordingPlaybackShow");
    	ShowConfig sc = showConfigs.get(rps.getId());
    	showConfigs.remove(rps.getId());
    	//showConfigsByName.remove(rps.getName());
    	shows.remove(rps);
    }
    
    public void startShow(long showId) {
    	ShowConfig showConfig = showConfigs.get(showId);
    	if (showConfig==null) {
    		logger.error("Not starting show " + showId + " - unknown show");
    		return;
    	}
    	ShowThread thread = showConfig.getThread();
    	if (thread.isAlive()) {
    		// @TODO cancel & restart show ?
    		logger.warn("Not starting show " + showId + " '" + showConfig.getShow().getName() + "' since it has already started");
    		return;
    	}
    	// cancel all shows with the same showGroupId
    	if (showConfig.getShow().getShowGroupId()!=-1) {
	    	for (ShowConfig showConfig2 : showConfigs.values()) {
	    		if (showConfig2.getShow().getShowGroupId()==showConfig.getShow().getShowGroupId()) {
	    			ShowThread thread2 = showConfig2.getThread();
	    	    	if (thread2.isAlive()) {
	    	    		thread2.cancel();
	    	    	}
	    		}
	    	}
    	}
    	// should probably wait a small amount of time for shows to cancel properly,
    	// then forcibly terminate them
    	thread.start();
    }
    
    public void cancelShow(long showId) {
    	ShowConfig showConfig = showConfigs.get(showId);
    	if (showConfig==null) {
    		logger.error("Not cancelling show " + showId + " - unknown show");
    		return;
    	}
    	ShowThread thread = showConfig.getThread();
    	if (thread.isAlive()) {
    		// @TODO cancel & restart show ?
    		thread.cancel();
    	} else {
    		if (showConfig.getShow().getState()==Show.State.SHOW_STOPPED_WITH_EXCEPTION) {
    			thread.resetException();
    		} else {
    			logger.warn("Not cancelling show " + showId + " '" + showConfig.getShow().getName() + "' since it is not running");
    		}
    	}
    	// NB: show may not be completely finished at this stage
    }
    
    public void cancelShowGroup(long showGroupId) {
    	// cancel all shows with the same showGroupId
    	for (ShowConfig showConfig2 : showConfigs.values()) {
    		if (showConfig2.getShow().getShowGroupId()==showGroupId) {
    			ShowThread thread2 = showConfig2.getThread();
    	    	if (thread2.isAlive()) {
    	    		thread2.cancel();
    	    	}
    		}
    	}
    	// should probably wait a small amount of time for shows to cancel properly,
    	// then forcibly terminate them
    }
    
 
    public void addShowException(Show show, Exception exception) {
    	showExceptions.add(new TimestampedShowException(show, exception));
    }
    
    public AppConfigState getAppConfigState() {
    	return appConfigState;
    }
    
    /** Invoked by servletContextListener to stop any show threads or
     * universe listeners registered in this application */
    public void shutdownThreads() {
    	
    	appConfigState = AppConfigState.STOPPING;
    	
    	logger.info("appConfig.shutdownThreads() invoked");
    	for (ShowConfig showConfig : showConfigs.values()) {
    		if (showConfig.hasThread() && showConfig.getThread().isAlive()) { 
    			showConfig.getThread().cancel(); 
    		}
    	}
    	// if any shows are still running, give them a few seconds,
    	// then just stop the thread
    	int showsRunning = 0;
    	for (ShowConfig showConfig : showConfigs.values()) {
    		if (showConfig.hasThread() && showConfig.getThread().isAlive()) { 
    			showsRunning++; 
    		}
    	}
    	if (showsRunning > 0) {
    		logger.info("There are " + showsRunning + " shows still running; waiting 5 seconds");
    		try { Thread.sleep(5000); } catch (InterruptedException ie) { }
    		logger.info("Forcing thread shutdown");
        	for (ShowConfig showConfig : showConfigs.values()) {
        		if (showConfig.hasThread()) { 
        			if (showConfig.getThread().isAlive()) { showConfig.getThread().stop(); } 
        		}
        	}
    	}
    	appConfigState = AppConfigState.STOPPED;
    }

    /** Invoked by servletContextListener to stop any 
     * universe listeners registered in this application */
    public void shutdownListeners() {
    	List<Universe> universes = controller.getUniverses();
    	for (int i=0; i<universes.size(); i++) {
    		Universe universe = universes.get(i);
    		universe.stopListeners();
    	}
    	for (int i=0; i<universes.size(); i++) {
    		Universe universe = universes.get(i);
    		universe.removeListeners();
    	}

    }

    /** Invoked by servletContextListener to shut down the 
     * audio controller and dmxDevices */
    public void shutdownDevices() {
    	AudioController audioController = controller.getAudioController();
    	if (audioController!=null) { audioController.close(); }
    	
    	if (audioSource!=null) { 
    		audioSource.close(); 
    	}
    	
    	// @TODO could possibly even reset the controller before doing this
    	// will also stop listener threads
    	/*
    	if (dmxDevice!=null) {
			dmxDevice.close();
			// @TODO dump any exceptions in the device's ExceptionContainer interface
    	} */
    	for (int i=0; i<dmxDeviceConfigs.size(); i++) {
    		DmxDeviceConfig ddc = dmxDeviceConfigs.get(i);
    		ddc.getDmxDevice().close();
    	}
    	
    }

    
	@Override
	public String getSystemPropertyKeyConfigPath() {
		return SYSTEM_PROPERTY_KEY_CONFIG_PATH; 
	}

	@Override
	public String getConfigResourceLocation() {
		return CONFIG_RESOURCE_LOCATION;
	}

	public Controller getController() {
		return controller;
	}
	
	// don't get beat information from this audioSource, 'cos it'll be wrong
	public AudioSource getAudioSource() { 
		return audioSource; 
	}
	
	public List<ExceptionContainer.TimestampedException> getDmxDeviceExceptions() {
		//return dmxDevice.getExceptions();
		List allExceptions = new ArrayList<ExceptionContainer.TimestampedException>();
		for (int i=0; i<dmxDeviceConfigs.size(); i++) {
    		DmxDeviceConfig ddc = dmxDeviceConfigs.get(i);
    		allExceptions.addAll(ddc.getDmxDevice().getExceptions());
    	}
		return allExceptions;
	}

	public List<TimestampedShowException> getShowExceptions() {
		return showExceptions;
	}
	
	public List<ExceptionContainer.TimestampedException> getAudioSourceExceptions() {
		return audioSource.getExceptions();
	}

	public List<ExceptionContainer.TimestampedException> getAppConfigExceptions() {
		return exceptionContainer.getExceptions();
	}
	public void addAppConfigException(Throwable t) {
		exceptionContainer.addException(t);
	}
	public void clearAppConfigExceptions() {
		exceptionContainer.clearExceptions();
	}
	
	public void clearAudioSourceExceptions() {
		audioSource.clearExceptions();
	}
	
	public void clearDmxDeviceExceptions() {
		// dmxDevice.clearExceptions();
		for (int i=0; i<dmxDeviceConfigs.size(); i++) {
    		DmxDeviceConfig ddc = dmxDeviceConfigs.get(i);
    		ddc.getDmxDevice().clearExceptions();
    	}
	}
	
	public void clearShowExceptions() {
		showExceptions.clear();	
	}
	
	public int getLogCount() {
		int count = exceptionContainer.getExceptions().size() +
			controller.getAudioController().getExceptions().size() + 
			showExceptions.size() + 
			audioSource.getExceptions().size();
		for (int i=0; i<dmxDeviceConfigs.size(); i++) {
    		count += dmxDeviceConfigs.get(i).getDmxDevice().getExceptions().size();
    	}
		return count;
	}
	
	
	/** Removes the leading block and line comment markers from a piece of javadoc comment.
	 * 
	 * @param comment The comment to clean for display
	 * 
	 * @return
	 */
	private String removeCommentTokens(String comment) {
		try {
			LineNumberReader lnr = new LineNumberReader(new StringReader(comment));
			String line = lnr.readLine();
			String cleanComment = "";
			while (line!=null) {
				line = line.trim();
				if (line.startsWith("/**")) { line = line.substring(3); }
				else if (line.startsWith("*")) { line = line.substring(1); }
				
				// remove @author (etc) tags
				if (line.trim().startsWith("@")) { line=""; }
				
				cleanComment += line;
				line = lnr.readLine();
			}
			cleanComment = cleanComment.trim();
			if (cleanComment.endsWith("/")) { cleanComment = cleanComment.substring(0, cleanComment.length()-1); }
			return cleanComment;
		} catch (IOException ioe) {
			throw new IllegalStateException("IOException in StringReader", ioe);
		}
	}
	
	public Stage getActiveStage() {
		return activeStage;
	}
	
	

}

