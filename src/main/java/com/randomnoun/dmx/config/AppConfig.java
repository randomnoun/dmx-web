package com.randomnoun.dmx.config;

import java.io.IOException;
import java.lang.Thread.State;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import com.randomnoun.common.PropertyParser;
import com.randomnoun.common.webapp.struts.AppConfigBase;
import com.randomnoun.dmx.AudioController;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureDef;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidgetUniverseUpdateListener;
import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidget;
import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidgetTranslator;
import com.randomnoun.dmx.show.Show;
import com.randomnoun.dmx.show.ShowThread;
import com.randomnoun.dmx.timeSource.WallClockTimeSource;

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
    
    /** Widget reference */
    private UsbProWidget widget;
    
    /** Update listener for this application */
    private UsbProWidgetUniverseUpdateListener usbProWidgetUniverseUpdateListener;

    private enum AppConfigState { UNINITIALISED, RUNNING, STOPPING, STOPPED };
    
    private AppConfigState appConfigState = AppConfigState.UNINITIALISED;
    
    // @TODO keep track of which shows require which fixtures,
    //   prevent shows from running which may have resource conflicts
    // (or have some kind of show override type of thing)
    private static class ShowConfig {
    	Show show;
    	ShowThread showThread;
    	AppConfig appConfig;
    	
    	public ShowConfig(AppConfig appConfig, Show show) {
    		this.appConfig = appConfig;
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
    		}
    		if (showThread.getState()==State.TERMINATED) {
    			showThread = new ShowThread(show);
    		}
    		return showThread;
    	}
    	public Show getShow() {
    		return show;
    	}
    }

    /** List of shows that this application knows about, and their threads */
    private List<ShowConfig> showConfigs;
    /** List of shows */
    private List<Show> shows;
    
    
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
    public synchronized void initialise() throws InstantiationException, IllegalAccessException, ClassNotFoundException, PortInUseException, IOException, TooManyListenersException, SecurityException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException {
        logger.info("Initialising dmx-web...");
        AppConfig newInstance = new AppConfig();
        newInstance.initHostname();
        newInstance.loadProperties();  // properties depends on hostname
        newInstance.initLogger();      // logger depends on properties
        newInstance.initDatabase();    // db settings also depend on properties
        newInstance.initSecurityContext();
        newInstance.initController();
        newInstance.initShowConfigs();
        newInstance.appConfigState = AppConfigState.RUNNING;
        logger.info("appConfig now in " + appConfigState + " state");

        // if this all succeeded, assign it to the singleton instance
        instance = newInstance;
    }
    
    /** Return a configuration instance 
     *
     * @throws IllegalStateException if the application did not initialise successfully. 
     */
    public static AppConfig getAppConfig() {
        if (instance == null) {
            instance = new AppConfig();
            try {
                instance.initialise();
            } catch (Throwable t) {
                System.out.println("Error initialising");
                t.printStackTrace();
                instance.initialisationFailure = new RuntimeException("Could not initialise application", t);
            }
        }
        if (instance.initialisationFailure!=null) {
            throw (IllegalStateException) new IllegalStateException("Application failed initialisation").
              initCause(instance.initialisationFailure);
        }
        return instance;
    }
    
    private void initController() throws InstantiationException, IllegalAccessException, ClassNotFoundException, PortInUseException, IOException, TooManyListenersException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
    	String portName = getProperty("controller.portName");

    	widget = new UsbProWidget(portName);
    	UsbProWidgetTranslator translator = widget.openPort();
    	Universe universe = new Universe();
		universe.setTimeSource(new WallClockTimeSource());
		String acClassname = (String) this.get("audioController.class");
		if (acClassname==null) {
			acClassname = "NullAudioController"; // or summink
		}
		Map acProperties = PropertyParser.restrict(this, "audioController", true);
		Class acClass = Class.forName(acClassname);
		Constructor acConstructor = acClass.getConstructor(Map.class);
		AudioController audioController = (AudioController) acConstructor.newInstance(acProperties);
		
		
		controller = new Controller();
		controller.setUniverse(universe);
		controller.setAudioController(audioController);
		
		
		List fixtures = (List) get("fixtures");
		for (int i=0; i<fixtures.size(); i++) {
			Map fixture = (Map) fixtures.get(i);
			String fixtureClass = (String) fixture.get("class");
			String name = (String) fixture.get("name");
			String dmxOffset = (String) fixture.get("dmxOffset");
			// @TODO re-use fixture definition classes
			FixtureDef fixtureDef = (FixtureDef) Class.forName(fixtureClass).newInstance();
			if (fixtureDef.getNumDmxChannels()<=0) {
				logger.error("Fixture " + i + " has no DMX channels");
			}
			Fixture fixtureObj = new Fixture(name, fixtureDef, universe, Integer.parseInt(dmxOffset));
			controller.addFixture(fixtureObj);
		}
		
		usbProWidgetUniverseUpdateListener = new UsbProWidgetUniverseUpdateListener(translator);
		universe.addListener(usbProWidgetUniverseUpdateListener);
		usbProWidgetUniverseUpdateListener.startThread();
    }
    
    private void initShowConfigs() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    	showConfigs = new ArrayList<ShowConfig>();
    	shows = new ArrayList<Show>();
		List showProperties = (List) get("shows");
		if (showProperties == null || showProperties.size()==0) {
			logger.warn("appConfig has no shows defined");
		} else {
			for (int i=0; i<showProperties.size(); i++) {
				Map show = (Map) showProperties.get(i);
				String showClassName = (String) show.get("class");
				Class showClass = Class.forName(showClassName);
				Constructor constructor = showClass.getConstructor(Controller.class);
				Show showObj = (Show) constructor.newInstance(controller);
				showConfigs.add(new ShowConfig(this, showObj));
				shows.add(showObj);
			}
		}
    }
    
    public List<Show> getShows() {
    	return shows;
    }
	
    public void startShow(int showId) {
    	ShowConfig showConfig = showConfigs.get(showId);
    	ShowThread thread = showConfig.getThread();
    	if (thread.isAlive()) {
    		// @TODO cancel & restart show ?
    		logger.warn("Not starting show " + showId + " '" + showConfig.getShow().getName() + "' since it has already started");
    	} else {
    		thread.start();
    	}
    }
    
    public void cancelShow(int showId) {
    	ShowConfig showConfig = showConfigs.get(showId);
    	ShowThread thread = showConfig.getThread();
    	if (thread.isAlive()) {
    		// @TODO cancel & restart show ?
    		thread.cancel();
    	} else {
    		logger.warn("Not cancelling show " + showId + " '" + showConfig.getShow().getName() + "' since it is not running");
    	}
    }
    
    /** Invoked by servletContextListener to stop any running threads in this application */
    public void shutdownThreads() {
    	
    	appConfigState = AppConfigState.STOPPING;
    	logger.info("appConfig.shutdownThreads() invoked");
    	for (ShowConfig showConfig : showConfigs) {
    		if (showConfig.hasThread()) { showConfig.getThread().cancel(); }
    	}
    	// if any shows are still running, give them a few seconds,
    	// then just stop the thread
    	int showsRunning = 0;
    	for (ShowConfig showConfig : showConfigs) {
    		if (showConfig.hasThread()) { 
    			if (showConfig.getThread().isAlive()) { showsRunning++; } 
    		}
    	}
    	if (showsRunning > 0) {
    		logger.info("There are " + showsRunning + " shows still running; waiting 5 seconds");
    		try { Thread.sleep(5000); } catch (InterruptedException ie) { }
    		logger.info("Forcing thread shutdown");
        	for (ShowConfig showConfig : showConfigs) {
        		if (showConfig.hasThread()) { 
        			if (showConfig.getThread().isAlive()) { showConfig.getThread().stop(); } 
        		}
        	}
    	}
    	
    	AudioController audioController = controller.getAudioController();
    	if (audioController!=null) { audioController.close(); }
    	
    	// @TODO could possibly even reset the controller before doing this
    	
    	if (usbProWidgetUniverseUpdateListener!=null) {
    		usbProWidgetUniverseUpdateListener.stopThread();
    	}
    	if (widget!=null) {
    		try {
				widget.close();
			} catch (IOException e) {
				logger.error("Could not close widget", e);
			}
    	}
    	
    	appConfigState = AppConfigState.STOPPED;
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


}

