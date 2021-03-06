package com.randomnoun.dmx.fixture;

import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.lightSource.LightSourceDef;

/** A lighting fixture definition.
 * 
 * @author knoxg
 */
public abstract class FixtureDef {

	protected String name;
	protected String vendor;
	protected String model;
	protected String htmlImg16;
	protected String htmlImg32;
	protected String htmlLabel;
	
	/** Length of fixture, in mm */
	protected long length;
	
	/** Width of fixture, in mm */
	protected long width;
	
	/** Height of fixture, in mm */
	protected long height;
	
	/** Weight of fixture, in g */
	protected long weight;
	
	/** Range of pan, in degrees */
	protected int panRange;
	
	/** Range of tilt, in degrees */
	protected int tiltRange;
	
	/** The slowest strobe speed in hertz (cycles per second) */
	protected double minimumStrobeHertz;
	
	/** The fastest strobe speed in hertz (cycles per second) */
	protected double maximumStrobeHertz;
	
	/** Maximum ambient temperator, in degrees */
	protected int maxAmbientTemperature;
	
	/** Maximum wattage, in watts */
	protected int maxWattage;
	
	/** Light source definition */
	protected LightSourceDef lightSourceDef;

	protected int numDmxChannels;

	protected boolean invertPan = false;                 // if false, pan is clockwise looking up the upVector from the fixture
	protected boolean invertTilt = false;                // if false, increasing tilt will point the fixture up the upVector
	protected int pointAtPanStart, pointAtPanEnd;     // if set, preferred ranges for pointAt
	protected int pointAtTiltStart, pointAtTiltEnd;   // if set, preferred ranges for pointAt
	
	private String imagePath;
	
	public void setImagePath(String imagePath) { this.imagePath = imagePath; }
	
	/** Called from with appConfig constructor */
	public FixtureDef init(String name) {
		this.name = name;
		return this;
	}
	
	public String getName() { return name; }
	public String getImagePath() { return imagePath; }
	public String getVendor() { return vendor; }
	public String getModel() { return model; }
	public long getLength() { return length; }
	public long getHeight() { return height; }
	public int getPanRange() { return panRange; }
	public int getTiltRange() { return tiltRange; }
	public double getMinimumStrobeHertz() { return minimumStrobeHertz; }
	public double getMaximumStrobeHertz() { return maximumStrobeHertz; }
	public int getMaxAmbientTemperature() { return maxAmbientTemperature; }
	public int getMaxWattage() { return maxWattage; }
	public LightSourceDef getLightSourceDef() { return lightSourceDef; }
	public int getNumDmxChannels() { return numDmxChannels; }	
	
	public ChannelMuxer muxer;
	
	public List<ChannelDef> channelDefs = new ArrayList<ChannelDef>();
	
	public List<ChannelDef> getChannelDefs() {
		return channelDefs;
	}
	
	public void addChannelDef(ChannelDef channelDef) {
		channelDefs.add(channelDef);
	}
	
	/** Returns the first channelDef in this fixture with the specified 
	 * class.
	 * 
	 * <p>TODO: remember how to use generics
	 * 
	 * @return the channelDef if found, null otherwise
	 */
	public ChannelDef getChannelDefByClass(Class clazz) {
		for (ChannelDef cd : getChannelDefs()) {
			if (clazz.isInstance(cd)) {
				return cd;
			}
		}
		return null;
	}
	
	/** Returns the channelDef which has the supplied offset from the DMX base for this fixture
	 * 
	 * @param offset DMX offset from the fixture base
	 * 
	 * @return the channelDef which has the supplied offset from the DMX base for this fixture
	 */
	public ChannelDef getChannelDefByOffset(int offset) {
		for (ChannelDef cd : getChannelDefs()) {
			if (cd.getOffset()==offset) {
				return cd;
			}
		}
		throw new IllegalArgumentException("Offset " + offset + " not a valid offset for this fixture definition");
	}

	// to be implemented by subclasses of this class
	public abstract ChannelMuxer getChannelMuxer(Fixture fixture);
	public abstract FixtureController getFixtureController(Fixture fixture);
	

	public String getHtmlImg16() {
		if (htmlImg16==null) { return "image/fixturePlaceholder.png"; }
		return getImagePath() + htmlImg16;
	}

	public String getHtmlImg32() {
		if (htmlImg32==null) { return "image/fixturePlaceholder.png"; }
		return getImagePath() + htmlImg32;
	}
	
	public String getHtmlLabel() {
		if (htmlLabel==null) { return this.getClass().getName(); }
		return htmlLabel;
	}
	
}
