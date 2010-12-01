package com.randomnoun.dmx.channelMuxer.primitive;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** This muxer requires a red green and blue channel */
public class ColorChannelMuxer extends ChannelMuxer {

	Logger logger = Logger.getLogger(ColorChannelMuxer.class);
	
	int redOffset;
	int greenOffset;
	int blueOffset;
	
	public ColorChannelMuxer(Fixture fixture) {
		super(fixture);
		redOffset = -1; greenOffset = -1; blueOffset = -1;
		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof DimmerChannelDef) {
				DimmerChannelDef dcd = (DimmerChannelDef) cd;
				if (dcd.dimmerType==DimmerChannelDef.DimmerType.RED) { redOffset = dcd.getOffset(); }
				if (dcd.dimmerType==DimmerChannelDef.DimmerType.GREEN) { greenOffset = dcd.getOffset(); }
				if (dcd.dimmerType==DimmerChannelDef.DimmerType.BLUE) { blueOffset = dcd.getOffset(); }
			}
		}
		if (redOffset==-1) { logger.warn("ColorChannelMuxer on fixture with no red channel"); }
		if (greenOffset==-1) { logger.warn("ColorChannelMuxer on fixture with no green channel"); }
		if (blueOffset==-1) { logger.warn("ColorChannelMuxer on fixture with no blue channel"); }
	}
	
	public FixtureOutput getOutput() {
		final int redValue = redOffset == -1 ? 0 : fixture.getDmxChannelValue(redOffset);
		final int greenValue = greenOffset == -1 ? 0 : fixture.getDmxChannelValue(greenOffset);
		final int blueValue = blueOffset == -1 ? 0 : fixture.getDmxChannelValue(blueOffset);
		logger.debug("fixture=" + fixture.getName() + ", redValue=" + redValue + ", greenValue=" + greenValue + ", blueValue=" + blueValue);
		return new FixtureOutput() {
			public Color getColor() {
				return new Color(redValue, greenValue, blueValue);
			}
			public long getTime() { return fixture.getUniverse().getTime(); }
			public Double getPan() { return null; }
			public Double getTilt() { return null; }
			public Double getActualPan() { return null; }
			public Double getActualTilt() { return null; }
			public Double getDim() { return (double) 1; }
			public Double getStrobe() { return null; }
		};
	}
}