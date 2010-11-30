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

	protected String vendor;
	protected String model;
	
	/** Length of fixture, in mm */
	protected long length;
	
	/** Width of fixture, in mm */
	protected long width;
	
	/** Height of fixture, in mm */
	protected long height;
	
	/** Weight of fixture, in mm */
	protected long weight;
	
	/** Range of pan, in degrees */
	protected int panRange;
	
	/** Range of tilt, in degrees */
	protected int tiltRange;
	
	/** Maximum ambient temperator, in degrees */
	protected int maxAmbientTemperature;
	
	/** Maximum wattage, in watts */
	protected int maxWattage;
	
	/** Light source definition */
	protected LightSourceDef lightSourceDef;

	protected int numDmxChannels;
	
	public String getVendor() { return vendor; }
	public String getModel() { return model; }
	public long getLength() { return length; }
	public long getHeight() { return height; }
	public int getPanRange() { return panRange; }
	public int getTiltRange() { return tiltRange; }
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
	 * @return
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
	

	public String getHtmlImg() {
		return "image/fixture/placeholder.gif";
	}

	public String getHtmlLabel() {
		return this.getClass().getName();
	}
	
	
	
	
	
	
}
