package com.randomnoun.dmx.channel.dimmer;

public class RedDimmerChannelDef extends DimmerChannelDef {
	public RedDimmerChannelDef(int offset) {
		super(offset, DimmerType.RED);
		setHtmlImg("image/channel/red_16x16.png");
		setHtmlLabel("Red dimmer");
	}
}
