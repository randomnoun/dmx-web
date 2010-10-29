package com.randomnoun.dmx;

import java.awt.Color;

/** This class represents a physically patched fixture.
 * 
 * @author knoxg
 */
public class Fixture {
	
	Universe universe;
	FixtureDef fixtureDef;
	int startDmxChannel;
	
	// could generate these on demand, perhaps
	FixtureController fixtureController;
	
	public Fixture(FixtureDef fixtureDef, Universe universe, int startDmxChannel) {
		this.fixtureDef = fixtureDef;
		this.universe = universe;
		this.startDmxChannel = startDmxChannel;
	}
	
	public FixtureDef getFixtureDef() { 
		return fixtureDef;
	}
	
	public Universe getUniverse() {
		return universe;
	}
	
	public FixtureController getFixtureController() {
		if (fixtureController==null) { 
			fixtureController = fixtureDef.getFixtureController(this);
		} 
		return fixtureController;
	}

	
	/** Returns the DMX value of one of this fixture's channels, 
	 * relative to it's starting DMX channel.
	 *  
	 * @param offset
	 * 
	 * @return
	 */
	public int getChannelValue(int offset) {
		return universe.getChannelValue(startDmxChannel + offset);
	}

	public void setDmxChannel(int offset, int value) {
		universe.setChannelValue(startDmxChannel + offset, value);
		
	}

	public void setColor(Color color) {
		getFixtureController().setColor(color);
	}

	public void strobe() {
		getFixtureController().strobe();
	}

	public void blackOut() {
		getFixtureController().blackOut();
	}
}
