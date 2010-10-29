package com.randomnoun.dmx.channel;

/** Defines the definition of a DMX channel
 *
 * @TODO set allowable min, max values for a channel
 * 
 * @author knoxg
 */
public abstract class ChannelDef {

	// Number of channels in this definition. May not necessarily be contiguous */
	//protected int numChannels;
	
	/** 0-based offset from fixture initial DMX channel number */
	protected int offset;
	
	/** The lowest dmx value that will be taken as input to this channelDef (normally 0) */
	protected int lowDmxValue;
	
	/** The highest dmx value that will be taken as input to this channelDef (normally 255) */
	protected int highDmxValue;
	
	public int getOffset() { return offset; }
	public int getLowDmxValue() { return lowDmxValue; }
	public int getHighDmxValue() { return highDmxValue; } 
	
	//public int numChannels() { return numChannels; }
	protected ChannelDef(int offset, int lowDmxValue, int highDmxValue) {
		this.offset = offset;
		this.lowDmxValue = lowDmxValue;
		this.highDmxValue = highDmxValue;
	}
	
}
