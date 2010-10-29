package com.randomnoun.dmx.timeSource;

import java.awt.Color;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureDef;
import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** A timesource that returns the current time
 */ 
public class WallClockTimeSource implements TimeSource {

	public WallClockTimeSource() {
	}
	
	public long getTime() {
		return System.currentTimeMillis();
	}
	
}
