package com.randomnoun.dmx.channelMuxer.timed;

import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.fixture.FixtureOutput;
import com.randomnoun.dmx.timeSource.TimeSource;

/** Base class for channel muxers that vary their output over time
 */ 
public abstract class TimeBasedChannelMuxer extends ChannelMuxer {
	
	TimeSource timeSource;
	
	public TimeBasedChannelMuxer(Fixture fixture, TimeSource timeSource) {
		super(fixture);
		this.timeSource = timeSource;
	}

	/*
	public void setTimeGenerator(TimeGenerator timeGenerator) {
		this.timeGenerator = timeGenerator;
	}
	
	public long getTime() {
		return timeGenerator.getTime();
	}
	*/
	
	
}
