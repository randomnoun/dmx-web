package com.randomnoun.dmx.timeSource;

import java.awt.Color;

import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** A timesource that returns the current time
 */ 
public class WallClockTimeSource implements TimeSource {

	public WallClockTimeSource() {
	}
	
	public long getTime() {
		return System.currentTimeMillis();
	}
	
}
