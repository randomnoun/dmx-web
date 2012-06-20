package com.randomnoun.dmx;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.example.dmx.fixture.MiniWashFixtureController;
import com.example.dmx.fixture.MiniWashFixtureDef12;
import com.example.dmx.fixture.X0177FixtureController;
import com.example.dmx.fixture.X0177FixtureDef;
import com.randomnoun.dmx.event.DmxValueDumper;
import com.randomnoun.dmx.event.MuxValueDumper;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.timeSource.WallClockTimeSource;

public class ControllerTest extends TestCase {

	public void setUp() throws ClassNotFoundException, SQLException {
		Properties props = new Properties();
		props.put("log4j.rootCategory", "INFO, CONSOLE");
		props.put("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
		props.put("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
		props.put("log4j.appender.CONSOLE.layout.ConversionPattern", "[TestController] %d{ABSOLUTE} %-5p %c - %m %n");
		props.put("log4j.logger.com.randomnoun.dmx","DEBUG");
		PropertyConfigurator.configure(props);
	}

	
	public void sleep(int seconds) {
		try { 
			Thread.sleep(seconds * 1000); 
		} catch (InterruptedException ie) { 
		
		}
	}
	
	public void testX1077Controllers() {

		DmxValueDumper dvd = null;
		MuxValueDumper mvd = null;
		
		try {
			// types of things
			FixtureDef x0177FixtureDef = new X0177FixtureDef();
			
			// things
			List<Universe> universes = new ArrayList<Universe>();
			Universe universe = new Universe(0);
			universe.setTimeSource(new WallClockTimeSource());
			universes.add(universe);
			Fixture leftFixture = new Fixture("leftFixture", x0177FixtureDef, universe, 10);
			Fixture rightFixture = new Fixture("rightFixture", x0177FixtureDef, universe, 20);
			
			
			Controller c = new Controller();
			c.setUniverses(universes);
			c.addFixture(leftFixture);
			c.addFixture(rightFixture);
			
			// add DMX value -> stdout listener
			dvd = new DmxValueDumper();
			dvd.startThread();
			universe.addListener(dvd);
			
			// add fixture colour value -> stdout listener
			mvd = new MuxValueDumper();
			mvd.addFixture(leftFixture);
			mvd.addFixture(rightFixture);
			mvd.startThread();
			universe.addListener(mvd);
			
			// set dmx values via the controller
			c.blackOut();
			c.setDmxChannelValue(0, 10, 255);  // (universe dmx channel) left master to 100%
			c.setDmxChannelValue(0, 11, 255);  // (universe dmx channel) left to red
			sleep(1);
			
			// set dmx values via the fixture
			c.blackOut();
			leftFixture.setDmxChannelValue(0, 255);  // (fixture dmx channel) left master to 100%
			leftFixture.setDmxChannelValue(2, 255);  // (fixture dmx channel) left to green
			sleep(1);
			
			// set dmx values using information in channelDefs
			c.blackOut();
			leftFixture.setColor(Color.BLUE);  // left to blue (automatically sets master to 100%)
			sleep(1);
			
			c.blackOut();
			leftFixture.setColor(Color.WHITE);  // left to white (possibly very slow) strobe
			leftFixture.strobe();
			sleep(1);
			
			// set dmx values using the FixtureController
			FixtureController fc = leftFixture.getFixtureController();  // left to red
			fc.blackOut();
			fc.setColor(Color.RED);
			sleep(1);
			
			// set dmx values using controller cast to this fixture type
			X0177FixtureController fc2 = 
				(X0177FixtureController) fc;
			fc2.blackOut();
			fc2.setMasterDimmer(255);
			fc2.setMacro(0);			// start 1st macro on left fixture (rainbow color change, fade transition)
			sleep(1);
			
			c.blackOut();
			
			// now do something with an audio plugin I guess
		} finally {
			if (dvd!=null) { dvd.stopThread(); }
			if (mvd!=null) { mvd.stopThread(); }
		}
	}
	
	public void testMiniWashControllers() {
		DmxValueDumper dvd = null;
		MuxValueDumper mvd = null;
		
		try {
			// types of things
			FixtureDef miniWashFixtureDef = new MiniWashFixtureDef12();
			
			// things
			List<Universe> universes = new ArrayList<Universe>();
			Universe universe = new Universe(0);
			universe.setTimeSource(new WallClockTimeSource());
			universes.add(universe);
			Fixture leftFixture = new Fixture("leftFixture", miniWashFixtureDef, universe, 21);
			Fixture rightFixture = new Fixture("rightFixture", miniWashFixtureDef, universe, 41);
			
			Controller c = new Controller();
			c.setUniverses(universes);
			c.addFixture(leftFixture);
			c.addFixture(rightFixture);
			
			// add DMX value -> stdout listener
			dvd = new DmxValueDumper();
			dvd.startThread();
			universe.addListener(dvd);
			
			// add fixture colour value -> stdout listener
			mvd = new MuxValueDumper();
			mvd.addFixture(leftFixture);
			mvd.addFixture(rightFixture);
			mvd.startThread();
			universe.addListener(mvd);
			
			// set dmx values using the FixtureController
			FixtureController lfc = leftFixture.getFixtureController();  // left to red and spin
			FixtureController rfc = rightFixture.getFixtureController();  // right to blue and spin
			lfc.blackOut(); rfc.blackOut();
			lfc.setColor(Color.RED); rfc.setColor(Color.BLUE); 
			lfc.panTo(540.0); rfc.panTo(540.0); 
			sleep(4);
			lfc.panTo(0.0); rfc.panTo(0.0);
			sleep(4);
			
			lfc.setColor(Color.GREEN); rfc.setColor(Color.GREEN);
			lfc.tiltTo(180.0); rfc.tiltTo(180.0);
			sleep(4);
			lfc.tiltTo(0.0); rfc.tiltTo(0.0);
			sleep(4);
			
			// set dmx values using controller cast to this fixture type
			MiniWashFixtureController mwfc2 = 
				(MiniWashFixtureController) lfc;
			mwfc2.blackOut();
			mwfc2.setMasterDimmer(255);
			mwfc2.setColorMacro(17);			// start 17th macro on left fixture (color-changemacro1)
			mwfc2.setMovementMacro(4);       // start 4th macro on left fixture (auto program 4)
			sleep(1);
			
			c.blackOut();
			
			// now do something with an audio plugin I guess
		} finally {
			if (dvd!=null) { dvd.stopThread(); }
			if (mvd!=null) { mvd.stopThread(); }
		}
		
	}
	
	
}
