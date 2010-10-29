package com.randomnoun.dmx.channelMuxer.filter;

import java.awt.Color;

import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

public class MasterDimmerChannelMuxer extends ChannelMuxer {

	int masterOffset;
	ChannelMuxer inputMuxer;
	
	public MasterDimmerChannelMuxer(ChannelMuxer inputMuxer) {
		super(inputMuxer.getFixture());

		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof DimmerChannelDef) {
				DimmerChannelDef dcd = (DimmerChannelDef) cd;
				if (dcd.dimmerType==DimmerChannelDef.DimmerType.MASTER) { masterOffset = dcd.getOffset(); }
			}
		}
	}
	
	public FixtureOutput getOutput() {
		final FixtureOutput input = inputMuxer.getOutput();
		final int masterValue = fixture.getChannelValue(masterOffset);
		return new FixtureOutput() {
			public Color getColor() {
				Color inputColor = input.getColor();
				return new Color(inputColor.getRed() * 255/masterValue, 
						inputColor.getGreen() * 255/masterValue,
						inputColor.getBlue() * 255/masterValue);
			}
			public long getTime() { return input.getTime(); }
		};
	}
}