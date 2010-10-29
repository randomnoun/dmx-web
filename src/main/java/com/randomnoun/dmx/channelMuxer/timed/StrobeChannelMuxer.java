package com.randomnoun.dmx.channelMuxer.timed;

import java.awt.Color;

import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.timeSource.TimeSource;

/** A muxer which takes a single strobe channel and applies it to another
 * muxer's output
 * 
 * @author knoxg
 */
public class StrobeChannelMuxer extends CyclingTimeBasedChannelMuxer {
	ChannelMuxer inputMuxer;
	StrobeChannelDef channelDef;
	
	public StrobeChannelMuxer(ChannelMuxer inputMuxer, TimeSource timeSource) {
		super(inputMuxer.getFixture(), timeSource);
		this.inputMuxer = inputMuxer;
		this.channelDef = (StrobeChannelDef) getFixtureDef().getChannelDefByClass(StrobeChannelDef.class);
		if (this.channelDef!=null) { 
			throw new IllegalStateException("Cannot apply a strobe muxer to a fixture without a strobe channel definition");
		}
	}
	
	@Override
	public long getCycleTime() {
		int strobeValue = fixture.getChannelValue(channelDef.getOffset());
		// NB: assuming that slower strobe values always have lower DMX values than faster strobe values here
		if (strobeValue < channelDef.getMinimumStrobeValue() ||
			strobeValue > channelDef.getMaximumStrobeValue()) {
			return 0;
		}
		// NB: assuming linear increments in hertz between low and high DMX values
		int dmxValueRange = (channelDef.getMaximumStrobeValue()-channelDef.getMinimumStrobeValue());
		int hertz = channelDef.getMinimumStrobeHertz() +
		    (channelDef.getMaximumStrobeHertz() - channelDef.getMinimumStrobeHertz()) *
		    (strobeValue - channelDef.getMinimumStrobeValue()) / dmxValueRange; 
		return 1000/hertz;
	}
	
	@Override
	public FixtureOutput getOutput() {
		final FixtureOutput input = inputMuxer.getOutput();
		final int strobeValue = fixture.getChannelValue(channelDef.getOffset());
		return new FixtureOutput() {
			public Color getColor() {
				Color inputColor = input.getColor();
				if (getCycleOffset() > getCycleTime()/2) {
					return inputColor;
				} else {
					return Color.BLACK;
				}
			}
			public long getTime() { return input.getTime(); }
		};
	}
}