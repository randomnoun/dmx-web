package com.randomnoun.dmx.channel.dimmer;

public class MasterDimmerChannelDef extends DimmerChannelDef {
	public MasterDimmerChannelDef(int offset) {
		super(offset, DimmerType.MASTER);
		setHtmlImg("image/channel/placeholder.png");
		setHtmlLabel("Master dimmer");
	}
	
}
