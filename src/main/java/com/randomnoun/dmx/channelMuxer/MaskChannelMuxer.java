package com.randomnoun.dmx.channelMuxer;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channelMuxer.timed.CyclingTimeBasedChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.StrobeChannelMuxer;
import com.randomnoun.dmx.channel.StrobeChannelDef;

/** A muxer which takes a set of muxers as input, and
 * combines them into a single FixtureOutput
 * 
 * @author knoxg
 */
public class MaskChannelMuxer extends ChannelMuxer {
	int[] inputMasks;
	ChannelMuxer[] inputMuxers;
	Logger logger = Logger.getLogger(MaskChannelMuxer.class);
	
	public MaskChannelMuxer(int[] inputMasks, ChannelMuxer[] inputMuxers) {
		super(inputMuxers[0].getFixture());
		// @TODO check all inputMuxers refer to the same fixture
		
		this.inputMasks = inputMasks;
		this.inputMuxers = inputMuxers;
		
		if (inputMasks==null) { throw new NullPointerException("Null inputMasks"); }
		if (inputMuxers==null) { throw new NullPointerException("Null inputMuxers"); }
		if (inputMasks.length==0) { throw new IllegalArgumentException("Zero-length inputMasks"); }
		if (inputMuxers.length==0) { throw new IllegalArgumentException("Zero-length inputMuxers"); }
		if (inputMasks.length != inputMuxers.length) { throw new IllegalArgumentException("Number of inputMasks (" + inputMasks.length + ") should equal number of input muxers (" + inputMuxers.length + ")"); }
	}

	
	public static class MaskableFixtureOutput extends FixtureOutput {

		private Color color;
		private Double pan;
		private Double tilt;
		private long time;
		
		public void addFixtureOutput(int mask, ChannelMuxer muxer) {
			FixtureOutput f = muxer.getOutput();
			if ((mask & FixtureOutput.MASK_COLOR) > 0) { color = f.getColor(); }
			if ((mask & FixtureOutput.MASK_PAN) > 0) { pan = f.getPan(); }
			if ((mask & FixtureOutput.MASK_TILT) > 0) { tilt = f.getTilt(); }
			if ((mask & FixtureOutput.MASK_TIME) > 0) { time = f.getTime(); }
		}
		
		@Override
		public Color getColor() { return color; }

		@Override
		public long getTime() { return time; }

		@Override
		public Double getPan() { return pan; }

		@Override
		public Double getTilt() { return tilt; }
		
	}
	
	@Override
	public FixtureOutput getOutput() {
		//logger.debug("mux input " + inputMuxer.getOutput() + ", currentMacroIndex=" + getCurrentMacroIndex());
		//return getCurrentChannelMuxer().getOutput();
		MaskableFixtureOutput fixtureOutput = new MaskableFixtureOutput();
		for (int i=0; i<inputMuxers.length; i++) {
			fixtureOutput.addFixtureOutput(inputMasks[i], inputMuxers[i]);
		}
		return fixtureOutput;
	}
}