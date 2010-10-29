package com.randomnoun.dmx.timeSource;

import java.awt.Color;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureDef;
import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** A muxer that returns the time of the current universe
 */ 
public class UniverseTimeSource implements TimeSource {

	Universe universe;
	
	public UniverseTimeSource(Universe universe) {
		
	}
	
	public long getTime() {
		return universe.getTime();
	}
	
}
