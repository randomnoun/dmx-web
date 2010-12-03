package com.randomnoun.dmx.channel.dimmer;

public class GreenDimmerChannelDef extends DimmerChannelDef {
	public GreenDimmerChannelDef(int offset) {
		super(offset, DimmerType.GREEN);
		setHtmlImg("image/channel/green_16x16.png");
		setHtmlLabel("Green dimmer");
	}
	
}
