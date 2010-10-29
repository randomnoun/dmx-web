package com.randomnoun.dmx;

import java.awt.Color;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef.DimmerType;
import com.randomnoun.dmx.channel.SpeedChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.scripted.X0177FixtureDef;

public class TestController {

	public void sleep(int seconds) {
		try { 
			Thread.sleep(seconds * 1000); 
		} catch (InterruptedException ie) { 
		
		}
	}
	
	public void testController() {
		
		// types of things
		FixtureDef x0177FixtureDef = new X0177FixtureDef();
		
		// things
		Universe universe = new Universe();
		Fixture leftFixture = new Fixture(x0177FixtureDef, universe, 10);
		Fixture rightFixture = new Fixture(x0177FixtureDef, universe, 20);
		
		Controller c = new Controller();
		c.setUniverse(universe);
		c.addFixture(leftFixture);
		c.addFixture(rightFixture);
		
		// set dmx values via the controller
		c.blackOut();
		c.setDmxChannel(10, 255);  // (universe dmx channel) left master to 100%
		c.setDmxChannel(11, 255);  // (universe dmx channel) left to red
		sleep(1);
		
		// set dmx values via the fixture
		c.blackOut();
		leftFixture.setDmxChannel(0, 255);  // (fixture dmx channel) left master to 100%
		leftFixture.setDmxChannel(2, 255);  // (fixture dmx channel) left to green
		sleep(1);
		
		// set dmx values using information in channelDefs
		c.blackOut();
		leftFixture.setColor(Color.BLUE);  // left to blue (automatically sets master to 100%)
		sleep(1);
		
		c.blackOut();
		leftFixture.setColor(Color.WHITE);  // left to white strobe
		leftFixture.strobe();
		sleep(1);
		
		// set dmx values using the FixtureController
		FixtureController fc = leftFixture.getFixtureController();  // left to red
		fc.setColor(Color.RED);
		sleep(1);
		
		// set dmx values using controller cast to this fixture type
		X0177FixtureDef.X0177FixtureController fc2 = 
			(X0177FixtureDef.X0177FixtureController) fc;
		fc2.blackOut();
		fc2.setMacro(1);			// start 2nd macro on left fixture (RBG color change, no transition)
		sleep(1);
		
		c.blackOut();
		
		// now do something with an audio plugin I guess 
	}
	
	public static void main(String args[]) {
		TestController t = new TestController();
		t.testController();
		
	}
	
}
