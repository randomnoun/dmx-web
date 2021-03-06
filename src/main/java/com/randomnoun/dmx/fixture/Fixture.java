package com.randomnoun.dmx.fixture;

import java.awt.Color;

import com.randomnoun.dmx.Universe;
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
	
	/** The order that this fixture appears on the 'fixture' panel */
	int sortOrder;
	
	/*
	long timeSinceLastPanUpdate;
	long timeSInceLastTiltUpdate;
	
	/ ** Tilt position of this fixture * /
	double tilt;
	
	/** Pan position of this fixture * /
	double pan;
	*/
	
	/** X position of this fixture */
	float x;
	
	/** Y position of this fixture */
	float y;
	
	/** X position of this fixture */
	float z;

	
	/** Looking-at X position of this fixture */
	float lookingAtX;
	
	/** Looking-at Y position of this fixture */
	float lookingAtY;
	
	/** Looking-at X position of this fixture */
	float lookingAtZ;
	
	/** Up X vector of this fixture */
	float upX;
	
	/** Up Y vector of this fixture */
	float upY;
	
	/** Up Z vector of this fixture */
	float upZ;
	
	/** If set, the X-position of this fixture on the fixture panel */
	Long fixPanelX;

	/** If set, the Y-position of this fixture on the fixture panel */
	Long fixPanelY;

	/** The representation of this fixture on the fixture panel */
	String fixPanelType;


	// could generate these on demand, perhaps
	FixtureController fixtureController;
	ChannelMuxer muxer;
	
	public Fixture(String name, FixtureDef fixtureDef, Universe universe, int startDmxChannel) {
		this.name = name;
		this.fixtureDef = fixtureDef;
		this.universe = universe;
		this.startDmxChannel = startDmxChannel;
	}
	
	/** Sets this fixture's position
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setLookingAt(float x, float y, float z) {
		this.lookingAtX = x;
		this.lookingAtY = y;
		this.lookingAtZ = z;
	}
	
	public void setUpVector(float x, float y, float z) {
		this.upX = x;
		this.upY = y;
		this.upZ = z;
	}
	
	/** Returns this fixture's position in a three-element float array 
	 * (x, y, z)
	 * 
	 * @return this fixture's position in a three-element float array 
	 * (x, y, z)
	 */
	public float[] getPosition() {
		return new float[]{x, y, z};
	}

	/** Returns this fixture's looking-at location in a three-element float array 
	 * (x, y, z)
	 * 
	 * @return this fixture's looking-at location in a three-element float array 
	 * (x, y, z)
	 */
	public float[] getLookingAt() {
		return new float[]{lookingAtX, lookingAtY, lookingAtZ};
	}

	/** Returns this fixture's up vector in a three-element float array 
	 * (x, y, z)
	 * 
	 * @return this fixture's up vector in a three-element float array 
	 * (x, y, z)
	 */
	public float[] getUpVector() {
		return new float[]{upX, upY, upZ};
	}
	
	/** Sets the order that this fixture appears on the 'fixture' panel
	 * 
	 * @param sortOrder the order that this fixture appears on the 'fixture' panel
	 */
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	/** Returns order that this fixture appears on the 'fixture' panel
	 * 
	 * @return the order that this fixture appears on the 'fixture' panel
	 */
	public int getSortOrder() { 
		return sortOrder;
	}

	
	/*
	public void setPan(double pan) { this.pan = pan; timeSinceLastPanUpdate = System.currentTimeMillis(); }
	public double getPan() { return pan; }
	public void setTilt(double tilt) { this.tilt = tilt; timeSinceLastTiltUpdate = System.currentTimeMillis(); }
	public double getTilt() { return tilt; }
	*/
	
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
	 * @param offset from the starting dmx channel (0=first channel of this fixture)
	 * 
	 * @return DMX value of one of this fixture's channels, 
	 * relative to it's starting DMX channel.
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

	public void setFixPanelPosition(Long fixPanelX, Long fixPanelY) {
		this.fixPanelX = fixPanelX;
		this.fixPanelY = fixPanelY;
	}
	
	public Long[] getFixPanelPosition() {
		return new Long[] { fixPanelX, fixPanelY };
	}
	

	/**
	 * @return the fixPanelType
	 */
	public void setFixPanelType(String fixPanelType) {
		this.fixPanelType = fixPanelType;
	}

	/**
	 * @return the fixPanelType
	 */
	public String getFixPanelType() {
		return fixPanelType;
	}
	
	
	
}
