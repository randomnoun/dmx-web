package com.randomnoun.dmx.channelMuxer.timed;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureDef;
import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.timeSource.TimeSource;

/** A time-based channel muxer that has a cycle time
 */ 
public abstract class CyclingTimeBasedChannelMuxer extends TimeBasedChannelMuxer {
	
	public CyclingTimeBasedChannelMuxer(Fixture fixture, TimeSource timeSource) {
		super(fixture, timeSource);
	}

	/** Returns the cycle length of this muxer, in milliseconds */
	public abstract long getCycleTime();

	/** Returns the offset into the current cycle, in milliseconds */
	public long getCycleOffset() {
		return timeSource.getTime() % getCycleTime(); 
	}
	
}
