package com.randomnoun.dmx.channel.dimmer;

public class RedDimmerChannelDef extends DimmerChannelDef {
	public RedDimmerChannelDef(int offset) {
		super(offset, DimmerType.RED);
		setHtmlImg("image/channel/placeholder.png");
		setHtmlLabel("Red dimmer");
	}
}
