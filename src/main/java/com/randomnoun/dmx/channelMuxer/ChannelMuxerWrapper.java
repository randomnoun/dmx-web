package com.randomnoun.dmx.channelMuxer;

import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** A ChannelMuxerWrapper is a ChannelMuxer which gets it's output from
 * another ChannelMuxer.
 */
public abstract class ChannelMuxerWrapper extends ChannelMuxer {

	private ChannelMuxer channelMuxer;
	
	public ChannelMuxerWrapper(Fixture fixture) {
		super(fixture);
	}
	
	public void setChannelMuxer(ChannelMuxer channelMuxer) { 
		this.channelMuxer = channelMuxer; 
	}
	
	public FixtureOutput getOutput() {
		return channelMuxer.getOutput();
	}
	
}
