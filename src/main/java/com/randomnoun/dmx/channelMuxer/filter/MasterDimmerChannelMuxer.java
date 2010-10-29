package com.randomnoun.dmx.channelMuxer.filter;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

public class MasterDimmerChannelMuxer extends ChannelMuxer {

	int masterOffset;
	ChannelMuxer inputMuxer;
	Logger logger = Logger.getLogger(MasterDimmerChannelMuxer.class);
	
	public MasterDimmerChannelMuxer(ChannelMuxer inputMuxer) {
		super(inputMuxer.getFixture());
		this.inputMuxer = inputMuxer;

		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof DimmerChannelDef) {
				DimmerChannelDef dcd = (DimmerChannelDef) cd;
				if (dcd.dimmerType==DimmerChannelDef.DimmerType.MASTER) { masterOffset = dcd.getOffset(); }
			}
		}
	}
	
	public FixtureOutput getOutput() {
		final FixtureOutput input = inputMuxer.getOutput();
		final int masterValue = fixture.getDmxChannelValue(masterOffset);
		logger.debug("mux input " + input + ", masterValue=" + masterValue);
		return new FixtureOutput() {
			public Color getColor() {
				Color inputColor = input.getColor();
				return new Color(inputColor.getRed() * masterValue/255, // TODO: shifts 
						inputColor.getGreen() * masterValue/255,
						inputColor.getBlue() * masterValue/255);
			}
			public long getTime() { return input.getTime(); }
		};
	}
}