package com.randomnoun.dmx.timeSource;

/** A timeSource is used in all TimeBasedChannelMuxers
 *  
 * @author knoxg
 *
 */
public interface TimeSource {

	public long getTime();
	
}
