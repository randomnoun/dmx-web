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
	public int getOffset() { return offset; }
	
	//public int numChannels() { return numChannels; }
	
}
