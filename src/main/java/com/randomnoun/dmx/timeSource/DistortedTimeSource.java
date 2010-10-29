package com.randomnoun.dmx.timeSource;

import java.awt.Color;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureDef;
import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.SpeedChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** A timesource which can be sped up or slowed down with a SpeedChannelDef
 */ 
public class DistortedTimeSource implements TimeSource {
	
	Fixture fixture;
	TimeSource inputTimeSource;
	SpeedChannelDef channelDef;
	
	public DistortedTimeSource(Fixture fixture, TimeSource inputTimeSource) {
		this.fixture = fixture; 
		this.inputTimeSource = inputTimeSource;
		this.channelDef = (SpeedChannelDef) fixture.getFixtureDef().getChannelDefByClass(SpeedChannelDef.class);
		if (this.channelDef==null) { 
			throw new IllegalStateException("DistortedTimeSource requires a fixture with a speedChannel definition");
		}
	}

	public long getTime() {
		final long inputTime = inputTimeSource.getTime();
		final int speedUpValue = fixture.getDmxChannelValue(channelDef.getOffset());
		if (speedUpValue < channelDef.getSlowValue() || speedUpValue > channelDef.getFastValue()) {
			return inputTime;
		}
		
		final double speedUpFactor =
			channelDef.getSpeedUpFactorWhenSlow() + 
			( (speedUpValue - channelDef.getSlowValue()) / (channelDef.getFastValue()-channelDef.getSlowValue())  *
			  (channelDef.getSpeedUpFactorWhenFast() - channelDef.getSpeedUpFactorWhenSlow() ) );
		return (long) (inputTime * speedUpFactor);
	}

	
}
