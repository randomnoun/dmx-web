package com.randomnoun.dmx.config;

import java.io.IOException;
import java.lang.Thread.State;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import bsh.engine.BshScriptEngineFactory;

import com.randomnoun.common.PropertyParser;
import com.randomnoun.common.Text;
import com.randomnoun.common.webapp.struts.AppConfigBase;
import com.randomnoun.dmx.AudioController;
import com.randomnoun.dmx.AudioSource;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.DmxDevice;
import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.dao.FixtureDAO;
import com.randomnoun.dmx.dao.FixtureDefDAO;
import com.randomnoun.dmx.dao.ShowDAO;
import com.randomnoun.dmx.dao.ShowDefDAO;
import com.randomnoun.dmx.event.UniverseUpdateListener;
import com.randomnoun.dmx.event.VlcUniverseUpdateListener;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.show.Show;
import com.randomnoun.dmx.show.ShowThread;
import com.randomnoun.dmx.timeSource.WallClockTimeSource;
import com.randomnoun.dmx.to.FixtureDefTO;
import com.randomnoun.dmx.to.FixtureTO;
import com.randomnoun.dmx.to.ShowDefTO;
import com.randomnoun.dmx.to.ShowTO;

import org.apache.log4j.Logger;

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

    public static final String CONFIG_RESOURCE_LOCATION = "/dmx-web.properties";
    
    public static final String SYSTEM_PROPERTY_KEY_CONFIG_PATH = "com.randomnoun.dmx.configPath";
    
    /** Logger instance for this class */
    public static Logger logger = Logger.getLogger(AppConfig.class);
    
    /** Controller instance for this application */
    private Controller controller;
    
    /** DMX device reference */
    private DmxDevice dmxDevice;
    
    public enum AppConfigState { UNINITIALISED, RUNNING, STOPPING, STOPPED };
    
    private AppConfigState appConfigState = AppConfigState.UNINITIALISED;
    
    /** Script context containing scripted fixture definitions, fixtures, and shows */
    private ScriptContext scriptContext;
    
    /** Accumulated events to broadcast to HTTP clients */
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
    		// @TODO threadIds
    		if (showThread==null) {
    			if (appConfig.appConfigState != AppConfigState.RUNNING) { 
    				throw new IllegalStateException("Cannot create thread when appConfigState=" + appConfig.appConfigState);
    			}
    			showThread = new ShowThread(show);
    			showThread.setName("show-" + showId + "-" + showThread.getId());
    		}
    		if (showThread.getState()==State.TERMINATED) {
    			showThread = new ShowThread(show);
    			showThread.setName("show-" + showId + "-" + showThread.getId());
    		}
    		return showThread;
    	}
    	public Show getShow() {
    		return show;
    	}
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
        logger.info("Initialising dmx-web...");

        AppConfig newInstance = new AppConfig();
    	try {
	        newInstance.initHostname();
	        newInstance.loadProperties();  // properties depends on hostname
	        newInstance.initSecurityManager();
	        newInstance.initLogger();      // logger depends on properties
	        newInstance.initDatabase();    // db settings also depend on properties
	        newInstance.initSecurityContext();
	        newInstance.initScriptContext();
	        newInstance.initController();
	        newInstance.loadFixtures(newInstance.getScriptContext(), newInstance.getController());
	        newInstance.loadShowConfigs(newInstance.getScriptContext(), true);
	        newInstance.loadListeners();
	        //newInstance.initCometEventManager();

	        newInstance.appConfigState = AppConfigState.RUNNING;
	        logger.info("appConfig now in " + newInstance.appConfigState + " state");
    	} catch (Throwable t) {
    		newInstance.initialisationFailure = new RuntimeException("Could not initialise application", t);
    	}

        // assign this to the singleton instance, even if initialisation failed
        instance = newInstance;
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
    
    /** Call this after any fixture definitions, fixtures, show definitions
     * or shows have been modified. 
     * 
     * <p>This method will kill all show threads, reset the controller,
     * stop any audio that's playing, and reload the fixture/show definitions.
     */
    public void reloadFixturesAndShows() {
    	logger.info("reloadFixturesAndShows(): shutting down show threads");
    	shutdownThreads();
    	if (appConfigState!=AppConfigState.STOPPED) {
    		throw new IllegalStateException("Expected appConfig in STOPPED state; found " + appConfigState);
    	}
    	
    	logger.info("reloadFixturesAndShows(): resetting controller");
    	controller.blackOut();
    	
    	logger.info("reloadFixturesAndShows(): stopping audio");
    	controller.getAudioController().stopAudio();
    	
    	// give the listeners 200 msec to actually do something
    	try { Thread.sleep(200); } catch (InterruptedException ie) { }
    	
    	logger.info("reloadFixturesAndShows(): shutting down listeners");
    	shutdownListeners();

    	controller.removeAllFixtures();
    	
    	// reset scriptContext classloader 
    	// see http://www.beanshell.org/manual/classpath.html#Reloading_Classes
    	//String script = "importCommands(\"/bsh/commands\");\n" +
		//	"reloadClasses();\n";
    	//getScriptEngine().eval(script, getScriptContext());
    	
    	// chronic thread safety problems here
    	logger.info("reloadFixturesAndShows(): reloading scriptContext");
        initScriptContext();
        try {
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
    
    
    
    private void initController() throws InstantiationException, IllegalAccessException, ClassNotFoundException, PortInUseException, IOException, TooManyListenersException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

    	Universe universe = new Universe();
		universe.setTimeSource(new WallClockTimeSource());

    	//String portName = getProperty("dmxDevice.portName");
    	//widget = new UsbProWidget(portName);
    	//UsbProWidgetTranslator translator = widget.openPort();

		String dmxClassname = (String) this.get("dmxDevice.class");
		if (dmxClassname==null) {
			dmxClassname = "com.randomnoun.dmx.protocol.nullDevice.NullDmxDevice";
		}
		Map dmxProperties = PropertyParser.restrict(this, "dmxDevice", true);
		Class dmxClass = Class.forName(dmxClassname);
		Constructor dmxConstructor = dmxClass.getConstructor(Map.class);
		dmxDevice = (DmxDevice) dmxConstructor.newInstance(dmxProperties);
    	dmxDevice.open();
    	
		String acClassname = (String) this.get("audioController.class");
		if (acClassname==null) {
			acClassname = "com.randomnoun.dmx.protocol.nullDevice.NullAudioController";
		}
		Map acProperties = PropertyParser.restrict(this, "audioController", true);
		Class acClass = Class.forName(acClassname);
		Constructor acConstructor = acClass.getConstructor(Map.class);
		AudioController audioController = (AudioController) acConstructor.newInstance(acProperties);
		audioController.open();

		String asClassname = (String) this.get("audioSource.class");
		if (asClassname==null) {
			asClassname = "com.randomnoun.dmx.protocol.nullDevice.NullAudioSource";
		}
		Map asProperties = PropertyParser.restrict(this, "audioSource", true);
		Class asClass = Class.forName(asClassname);
		Constructor asConstructor = asClass.getConstructor(Map.class);
		AudioSource audioSource = (AudioSource) acConstructor.newInstance(asProperties);
		audioSource.open();

		
		controller = new Controller();
		controller.setUniverse(universe);
		controller.setAudioController(audioController);
		controller.setAudioSource(audioSource);
		
    }
    
    public void loadFixtures(ScriptContext fixtureScriptContext, Controller fixtureController) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
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
				Fixture fixtureObj = new Fixture(name, fixtureDef, controller.getUniverse(), Integer.parseInt(dmxOffset));
				if (fixtureController!=null) {
					fixtureController.addFixture(fixtureObj);
				}
			}
		}
		
		
		// I'm assuming all this needs to go into a separate classLoader eventually
		Map scriptedFixtureDefs = new HashMap(); // id -> scripted fixtureDef instance
		FixtureDefDAO fixtureDefDAO = new FixtureDefDAO(getJdbcTemplate());
		List fixtureDefsFromDatabase = fixtureDefDAO.getFixtureDefs(null);
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
					// @TODO validate that the scriptContext now contains a 
					// fixture definition of the class specified in the FixtureDefTO
					// @TODO maybe scope the instance here so that it doesn't clobber any other global 'instance' instance
					
					// @TODO is there a way to reset the import declarations specified in a scriptContext ?
					String testScript =
						"import com.randomnoun.dmx.fixture.FixtureDef;\n" +
						"import " + fixtureDef.getFixtureDefClassName() + ";\n" +
						"return new " + fixtureDef.getFixtureDefClassName() + "();\n" ;
					// @TODO check class before instantiating
					Object instance = (Object) getScriptEngine().eval(testScript, fixtureScriptContext);
					if (instance instanceof FixtureDef) {
						scriptedFixtureDefs.put(fixtureDef.getId(), instance);
					} else {
						logger.error("Error instantiating object for fixtureDef " + fixtureDef.getId() + ": '" + fixtureDef.getName() + "'; className='" + fixtureDef.getFixtureDefClassName() + "' does not extend com.randomnoun.dmx.fixture.FixtureDef"); 
					}
					String testScript2 =
						"import com.randomnoun.dmx.fixture.FixtureController;\n" +
						"import " + fixtureDef.getFixtureDefClassName() + ";\n" +
						"return new " + fixtureDef.getFixtureDefClassName() + "();\n" ;
					// @TODO check class before instantiating
					Object instance2 = (Object) getScriptEngine().eval(testScript2, fixtureScriptContext);
					if (instance2 instanceof FixtureController) {
						scriptedFixtureDefs.put(fixtureDef.getId(), instance);
					} else {
						logger.error("Error instantiating object for fixtureDef " + fixtureDef.getId() + ": '" + fixtureDef.getName() + "'; className='" + fixtureDef.getFixtureControllerClassName() + "' does not extend com.randomnoun.dmx.fixture.FixtureController"); 
					}

				
				} catch (ScriptException se) {
					logger.error("Error evaluating script for fixtureDef " + fixtureDef.getId() + ": '" + fixtureDef.getName() + "'", se);
				}
			}
		}

		List fixturesFromDatabase = new FixtureDAO(getJdbcTemplate()).getFixtures(null);
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
					Fixture fixture = new Fixture(fixtureTO.getName(), 
						fixtureDef, 
						controller.getUniverse(), (int) fixtureTO.getDmxOffset());
					if (fixtureController != null) {
						fixtureController.addFixture(fixture);
					}
				}
			}
		}
		
    }
    
    public void loadShowConfigs(ScriptContext showScriptContext, boolean addToAppConfig) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    	showConfigs = new HashMap<Long, ShowConfig>();
    	shows = new ArrayList<Show>();
    	showExceptions = Collections.synchronizedList(new ArrayList<TimestampedShowException>());
    	
    	
		List showsFromProperties = (List) get("shows");
		if (showsFromProperties == null || showsFromProperties.size()==0) {
			logger.warn("No show definitions in appConfig");
		} else {
			for (int i=0; i<showsFromProperties.size(); i++) {
				Map showProperties = (Map) showsFromProperties.get(i);
				String showClassName = (String) showProperties.get("class");
				Class showClass = Class.forName(showClassName);
				Constructor constructor = showClass.getConstructor(long.class, Controller.class, Map.class);
				String onCompleteShowId = (String) showProperties.get("onCompleteShowId");
				String onCancelShowId = (String) showProperties.get("onCancelShowId");
				String name = (String) showProperties.get("name");
				long id = shows.size();
				try {
					Show showObj = (Show) constructor.newInstance(id, controller, showProperties);
					if (onCompleteShowId!=null) { showObj.setOnCompleteShowId(Long.parseLong(onCompleteShowId)); }
					if (onCancelShowId!=null) { showObj.setOnCancelShowId(Long.parseLong(onCancelShowId)); }
					if (!Text.isBlank(name)) { showObj.setName(name); }
					showConfigs.put(new Long(i), new ShowConfig(this, i, showObj));
					shows.add(showObj);
				} catch (Exception e) {
					logger.error("Error whilst instantiating compiled show " + id + ": '" + name + "'", e);
					
				}
				
					
			}
		}
		
		// I'm assuming all this needs to go into a separate classLoader eventually
		// @TODO see all the comments above for fixture defs
		Map scriptedShowDefs = new HashMap(); // id -> scripted show class object
		ShowDefDAO showDefDAO = new ShowDefDAO(getJdbcTemplate());
		List showDefsFromDatabase = showDefDAO.getShowDefs(null);
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
						scriptedShowDefs.put(showDef.getId(), clazz);
					} else {
						logger.error("Error processing show " + showDef.getId() + ": '" + showDef.getName() + "'; className='" + showDef.getClassName() + "' does not extend com.randomnoun.dmx.Show"); 
					}
				} catch (ScriptException se) {
					logger.error("Error evaluating script for show " + showDef.getId() + ": '" + showDef.getName() + "'", se);
				}
			}
		}

		List showsFromDatabase = new ShowDAO(getJdbcTemplate()).getShows(null);
		if (showsFromDatabase == null || showsFromDatabase.size()==0) {
			logger.warn("No show instances in database");
		} else {
			for (int i = 0; i < showsFromDatabase.size(); i++) {
				ShowTO showTO = (ShowTO) showsFromDatabase.get(i);
				long showDefId = showTO.getShowDefId();
				Class showClass = (Class) scriptedShowDefs.get(showDefId);
				if (showClass==null) {
					logger.error("Error whilst creating show " + showTO.getId() + ": '" + showTO.getName() + "'; no show found with id '" + showDefId + "'");
				} else {
					logger.debug("Creating scripted show '" + showTO.getName() + "' from database");
					Constructor constructor = showClass.getConstructor(long.class, Controller.class, Map.class);
					Map showProperties = new HashMap();
					//if (showTO.getOnCompleteShowId()!=null) { showProperties.put("onCompleteShowId", showTO.getOnCompleteShowId().toString()); }
					//if (showTO.getOnCancelShowId()!=null) { showProperties.put("onCancelShowId", showTO.getOnCancelShowId().toString()); }
					//if (!Text.isBlank(showTO.getName())) { showProperties.put("name", showTO.getName()); }
					// @TODO additional properties from database
					Show showObj;
					try {
						if (addToAppConfig) {
							showObj = (Show) constructor.newInstance(showTO.getId(), controller, showProperties);
							if (showTO.getOnCompleteShowId()!=null) { showObj.setOnCompleteShowId(showTO.getOnCompleteShowId().longValue()); }
							if (showTO.getOnCancelShowId()!=null) { showObj.setOnCancelShowId(showTO.getOnCancelShowId().longValue()); }
							if (!Text.isBlank(showTO.getName())) { showObj.setName(showTO.getName()); }
							showConfigs.put(showTO.getId(), new ShowConfig(this, showTO.getId(), showObj));
							shows.add(showObj);
						}
					} catch (Exception e) {
						logger.error("Error whilst instantiating show " + showTO.getId() + ": '" + showTO.getName() + "'", e);
					}
					
					/*
					Fixture fixture = new Fixture(showTO.getName(), 
						fixtureDef, 
						controller.getUniverse(), (int) showTO.getDmxOffset());
					controller.addFixture(fixture);
					*/
				}
			}
		}
    }
    
    public void loadListeners() {
		Universe universe = controller.getUniverse();
    	UniverseUpdateListener updateListener = dmxDevice.getUniverseUpdateListener(); 
		universe.addListener(updateListener);
		updateListener.startThread();
		
		if (getProperty("dev.vlc.host")!=null) {
			// hard-coding fixture name in for debugging
			try {
				updateListener = new VlcUniverseUpdateListener(
						getProperty("dev.vlc.host"), getProperty("dev.vlc.port"));
				((VlcUniverseUpdateListener) updateListener).setFixture(controller.getFixtureByName("leftWash")); 
				universe.addListener(updateListener);
				updateListener.startThread();
				
			} catch (UnknownHostException e) {
				logger.error("Could not attach VLC listener", e);
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
    	return showConfigs.get(showId).getShow();
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
    	} else {
    		thread.start();
    	}
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
    		logger.warn("Not cancelling show " + showId + " '" + showConfig.getShow().getName() + "' since it is not running");
    	}
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
    	controller.getUniverse().stopListeners();
    	controller.getUniverse().removeListeners();
    }

    /** Invoked by servletContextListener to Shuts down the 
     * audio controller and dmxDevices */
    public void shutdownDevices() {
    	AudioController audioController = controller.getAudioController();
    	if (audioController!=null) { audioController.close(); }
    	
    	AudioSource audioSource = controller.getAudioSource();
    	if (audioSource!=null) { audioSource.close(); }
    	
    	// @TODO could possibly even reset the controller before doing this
    	// will also stop listener threads
    	if (dmxDevice!=null) {
			dmxDevice.close();
			// @TODO dump any exceptions in the device's ExceptionContainer interface
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
	
	public List<ExceptionContainer.TimestampedException> getDmxDeviceExceptions() {
		return dmxDevice.getExceptions();
	}

	public List<TimestampedShowException> getShowExceptions() {
		return showExceptions;
	}
	

}

