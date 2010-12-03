package com.randomnoun.dmx.channel.dimmer;

public class MasterDimmerChannelDef extends DimmerChannelDef {
	public MasterDimmerChannelDef(int offset) {
		super(offset, DimmerType.MASTER);
		setHtmlImg("image/channel/dimmer_16x16.png");
		setHtmlLabel("Master dimmer");
	}
	
}
