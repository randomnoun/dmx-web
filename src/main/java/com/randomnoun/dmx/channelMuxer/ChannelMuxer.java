package com.randomnoun.dmx.channelMuxer;

import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** A ChannelMuxer is able to determine what the state of a fixture is, based on
 * the DMX values of the universe in which it is currently contained.
 */
public abstract class ChannelMuxer {

	protected Fixture fixture;
	
	public ChannelMuxer(Fixture fixture) {
		this.fixture = fixture;
	}
	
	public Fixture getFixture() { return fixture; }
	public FixtureDef getFixtureDef() { return fixture.getFixtureDef(); }
	
	public abstract FixtureOutput getOutput();
	
}
