package com.randomnoun.dmx.timeSource;

import java.awt.Color;

import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** A timesource that returns the time of the current universe
 */ 
public class UniverseTimeSource implements TimeSource {

	Universe universe;
	
	public UniverseTimeSource(Universe universe) {
		this.universe = universe;
	}
	
	public long getTime() {
		return universe.getTime();
	}
	
}
