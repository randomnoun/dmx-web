package com.randomnoun.dmx.channelMuxer.primitive;

import java.awt.Color;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** This muxer requires a red green and blue channel */
public class ColorChannelMuxer extends ChannelMuxer {

	int redOffset;
	int greenOffset;
	int blueOffset;
	
	public ColorChannelMuxer(Fixture fixture) {
		super(fixture);
		
		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof DimmerChannelDef) {
				DimmerChannelDef dcd = (DimmerChannelDef) cd;
				if (dcd.dimmerType==DimmerChannelDef.DimmerType.RED) { redOffset = dcd.getOffset(); }
				if (dcd.dimmerType==DimmerChannelDef.DimmerType.GREEN) { greenOffset = dcd.getOffset(); }
				if (dcd.dimmerType==DimmerChannelDef.DimmerType.BLUE) { blueOffset = dcd.getOffset(); }
			}
		}
	}
	
	public FixtureOutput getOutput() {
		final int redValue = fixture.getChannelValue(redOffset);
		final int greenValue = fixture.getChannelValue(greenOffset);
		final int blueValue = fixture.getChannelValue(blueOffset);
		return new FixtureOutput() {
			public Color getColor() {
				return new Color(redValue, greenValue, blueValue);
			}
			public long getTime() { return fixture.getUniverse().getTime(); }
		};
	}
}