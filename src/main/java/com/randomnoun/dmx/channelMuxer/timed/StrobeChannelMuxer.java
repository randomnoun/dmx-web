package com.randomnoun.dmx.channelMuxer.timed;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.channelMuxer.filter.MasterDimmerChannelMuxer;
import com.randomnoun.dmx.fixture.FixtureOutput;
import com.randomnoun.dmx.timeSource.TimeSource;

/** A muxer which takes a single strobe channel and applies it to another
 * muxer's output
 * 
 * @author knoxg
 */
public class StrobeChannelMuxer extends CyclingTimeBasedChannelMuxer {
	ChannelMuxer inputMuxer;
	StrobeChannelDef channelDef;
	Logger logger = Logger.getLogger(StrobeChannelMuxer.class);
	
	public StrobeChannelMuxer(ChannelMuxer inputMuxer, TimeSource timeSource) {
		super(inputMuxer.getFixture(), timeSource);
		this.inputMuxer = inputMuxer;
		this.channelDef = (StrobeChannelDef) getFixtureDef().getChannelDefByClass(StrobeChannelDef.class);
		if (this.channelDef==null) { 
			throw new IllegalStateException("Cannot apply a strobe muxer to a fixture without a strobeChannel definition");
		}
	}
	
	@Override
	/** Return the cycle time of this strobe
	 *
	 * <p>NB: assuming that slower strobe values always have lower DMX values than faster strobe values here
	 * <p>NB: also assuming linear increments in hertz between low and high DMX values
	 */
	public long getCycleTime() {
		int strobeValue = fixture.getDmxChannelValue(channelDef.getOffset());
		if (strobeValue < channelDef.getMinimumStrobeValue() ||
			strobeValue > channelDef.getMaximumStrobeValue()) {
			return 0;
		}
		int dmxValueRange = (channelDef.getMaximumStrobeValue()-channelDef.getMinimumStrobeValue());
		int hertz = channelDef.getMinimumStrobeHertz() +
		    (channelDef.getMaximumStrobeHertz() - channelDef.getMinimumStrobeHertz()) *
		    (strobeValue - channelDef.getMinimumStrobeValue()) / dmxValueRange; 
		return 1000/hertz;
	}
	
	@Override
	public FixtureOutput getOutput() {
		final FixtureOutput input = inputMuxer.getOutput();
		final int strobeValue = fixture.getDmxChannelValue(channelDef.getOffset());
		logger.debug("fixture=" + fixture.getName() + ", mux input " + input + ", strobeValue=" + strobeValue);
		return new FixtureOutput() {
			public Color getColor() {
				Color inputColor = input.getColor();
				int strobeValue = fixture.getDmxChannelValue(channelDef.getOffset());
				if (strobeValue < channelDef.getMinimumStrobeValue() ||
					strobeValue > channelDef.getMaximumStrobeValue()) {
					return inputColor;
				}
				int dmxValueRange = (channelDef.getMaximumStrobeValue()-channelDef.getMinimumStrobeValue());
				int hertz = channelDef.getMinimumStrobeHertz() +
				    (channelDef.getMaximumStrobeHertz() - channelDef.getMinimumStrobeHertz()) *
				    (strobeValue - channelDef.getMinimumStrobeValue()) / dmxValueRange; 
				
				if (getCycleOffset() > hertz/2) {
					return inputColor;
				} else {
					return Color.BLACK;
				}
			}
			public long getTime() { return input.getTime(); }
			public Double getPan() { return input.getPan(); }
			public Double getTilt() { return input.getTilt(); }
			public Double getDim() { return null; }
		};
	}
}