package com.randomnoun.dmx;

import java.awt.Color;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.randomnoun.dmx.event.DmxValueDumper;
import com.randomnoun.dmx.event.MuxValueDumper;
import com.randomnoun.dmx.scripted.X0177FixtureDef;
import com.randomnoun.dmx.timeSource.WallClockTimeSource;

public class TestController {

	public void sleep(int seconds) {
		try { 
			Thread.sleep(seconds * 1000); 
		} catch (InterruptedException ie) { 
		
		}
	}
	
	public void testController() {

		DmxValueDumper dvd = null;
		MuxValueDumper mvd = null;
		
		try {
			// types of things
			FixtureDef x0177FixtureDef = new X0177FixtureDef();
			
			// things
			Universe universe = new Universe();
			universe.setTimeSource(new WallClockTimeSource());
			Fixture leftFixture = new Fixture(x0177FixtureDef, universe, 10);
			Fixture rightFixture = new Fixture(x0177FixtureDef, universe, 20);
			
			Controller c = new Controller();
			c.setUniverse(universe);
			c.addFixture(leftFixture);
			c.addFixture(rightFixture);
			
			// add DMX value -> stdout listener
			dvd = new DmxValueDumper();
			dvd.startThread();
			universe.addListener(dvd);
			
			// add fixture colour value -> stdout listener
			mvd = new MuxValueDumper();
			mvd.addFixture("leftFixture", leftFixture);
			mvd.addFixture("rightFixture", rightFixture);
			mvd.startThread();
			universe.addListener(mvd);
			
			// set dmx values via the controller
			c.blackOut();
			c.setDmxChannelValue(10, 255);  // (universe dmx channel) left master to 100%
			c.setDmxChannelValue(11, 255);  // (universe dmx channel) left to red
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
			X0177FixtureDef.X0177FixtureController fc2 = 
				(X0177FixtureDef.X0177FixtureController) fc;
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
	
	public static void main(String args[]) {
		
		Properties props = new Properties();
		props.put("log4j.rootCategory", "INFO, CONSOLE");
		props.put("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
		props.put("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
		props.put("log4j.appender.CONSOLE.layout.ConversionPattern", "[TestController] %d{ABSOLUTE} %-5p %c - %m %n");
		//props.put("log4j.logger.com.randomnoun.dmx","DEBUG");
		PropertyConfigurator.configure(props);
		
		TestController t = new TestController();
		t.testController();
		
	}
	
}
