package com.randomnoun.dmx;

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

	public String vendor;
	public String model;
	
	/** Length of fixture, in mm */
	public long length;
	
	/** Width of fixture, in mm */
	public long width;
	
	/** Height of fixture, in mm */
	public long height;
	
	/** Weight of fixture, in mm */
	public long weight;
	
	/** Range of pan, in degrees */
	public int panRange;
	
	/** Range of tilt, in degrees */
	public int tiltRange;
	
	/** Maximum ambient temperator, in degrees */
	public int maxAmbientTemperature;
	
	/** Maximum wattage, in watts */
	public int maxWattage;
	
	/** Light source definition */
	public LightSourceDef lightSourceDef;


	protected int numDmxChannels;
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

	public int getNumDmxChannels() { return numDmxChannels; }
	
	
	// to be implemented by subclasses of this class
	public abstract ChannelMuxer getChannelMuxer(Fixture fixture);
	public abstract FixtureController getFixtureController(Fixture fixture);
	
}
