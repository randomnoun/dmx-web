package com.randomnoun.dmx.config;


import gnu.io.PortInUseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import com.randomnoun.common.webapp.struts.AppConfigBase;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureDef;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidgetUniverseUpdateListener;
import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidget;
import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidgetTranslator;
import com.randomnoun.dmx.timeSource.WallClockTimeSource;

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
    
	/** Private constructor; this class can only be called via .getInstance() */
	private AppConfig() {
        
	}
    
    /** Initialise the application instance 
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws TooManyListenersException 
     * @throws IOException 
     * @throws PortInUseException */
    public synchronized void initialise() throws InstantiationException, IllegalAccessException, ClassNotFoundException, PortInUseException, IOException, TooManyListenersException {
        logger.info("Initialising dmx-web...");
        AppConfig newInstance = new AppConfig();
        newInstance.initHostname();
        newInstance.loadProperties();  // properties depends on hostname
        newInstance.initLogger();      // logger depends on properties
        newInstance.initDatabase();    // db settings also depend on properties
        newInstance.initSecurityContext();
        newInstance.initController();

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
    
    private void initController() throws InstantiationException, IllegalAccessException, ClassNotFoundException, PortInUseException, IOException, TooManyListenersException {
    	String portName = getProperty("controller.portName");
    	widget = new UsbProWidget(portName);
    	UsbProWidgetTranslator translator = widget.openPort();
    	Universe universe = new Universe();
		universe.setTimeSource(new WallClockTimeSource());
		controller = new Controller();
		controller.setUniverse(universe);
		
		List fixtures = (List) get("fixtures");
		for (int i=0; i<fixtures.size(); i++) {
			Map fixture = (Map) fixtures.get(i);
			String fixtureClass = (String) fixture.get("class");
			String name = (String) fixture.get("name");
			String dmxOffset = (String) fixture.get("dmxOffset");
			FixtureDef fixtureDef = (FixtureDef) Class.forName(fixtureClass).newInstance();
			Fixture fixtureObj = new Fixture(name, fixtureDef, universe, Integer.parseInt(dmxOffset));
			controller.addFixture(fixtureObj);
		}
		
		usbProWidgetUniverseUpdateListener = new UsbProWidgetUniverseUpdateListener(translator);
		universe.addListener(usbProWidgetUniverseUpdateListener);
		usbProWidgetUniverseUpdateListener.startThread();
		
		/*
		c.addFixture(leftFixture);
		c.addFixture(rightFixture);
		*/		
    }
	
    
    /** Invoked by servletContextListener to stop any running threads in this application */
    public void shutdownThreads() {
    	logger.info("appConfig.shutdownThreads() invoked");
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

