package com.randomnoun.dmx;

import java.awt.Color;

import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** This class represents a physically patched fixture.
 * 
 * @author knoxg
 */
public class Fixture {
	
	Universe universe;
	FixtureDef fixtureDef;
	String name;
	int startDmxChannel;
	
	// could generate these on demand, perhaps
	FixtureController fixtureController;
	ChannelMuxer muxer;
	
	public Fixture(String name, FixtureDef fixtureDef, Universe universe, int startDmxChannel) {
		this.name = name;
		this.fixtureDef = fixtureDef;
		this.universe = universe;
		this.startDmxChannel = startDmxChannel;
	}
	
	public String getName() { 
		return name;
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
	public int getDmxChannelValue(int offset) {
		return universe.getDmxChannelValue(startDmxChannel + offset);
	}

	public void setDmxChannelValue(int offset, int value) {
		universe.setDmxChannelValue(startDmxChannel + offset, value);
	}
	public int getStartDmxChannel() {
		return startDmxChannel;
	}

	/** Sets the color of this fixture to the supplied color.
	 * 
	 * <p>Is a shortcut for <code>getFixtureController().setColor(color);</code>
	 * 
	 * @param color
	 * 
	 * @see FixtureController#setColor(Color)
	 */
	public void setColor(Color color) {
		getFixtureController().setColor(color);
	}

	/** Activates the strobe of this fixture.
	 * 
	 * <p>Is a shortcut for <code>getFixtureController().strobe();</code>
	 * 
	 * @see FixtureController#strobe()
	 */
	public void strobe() {
		getFixtureController().strobe();
	}

	/** Sets all DMX controllers for this fixture to zero.
	 * 
	 * <p>Is a shortcut for <code>getFixtureController().blackOut();</code>
	 * 
	 * @see FixtureController#blackOut()
	 */
	public void blackOut() {
		getFixtureController().blackOut();
	}
	
	public ChannelMuxer getChannelMuxer() {
		if (muxer==null) {
			muxer = getFixtureDef().getChannelMuxer(this);
		}
		return muxer;
	}
	
}
