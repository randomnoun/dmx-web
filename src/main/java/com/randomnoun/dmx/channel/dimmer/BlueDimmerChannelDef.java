package com.randomnoun.dmx.channel.dimmer;

public class BlueDimmerChannelDef extends DimmerChannelDef {
	public BlueDimmerChannelDef(int offset) {
		super(offset, DimmerType.BLUE);
		setHtmlImg("image/channel/placeholder.png");
		setHtmlLabel("Blue dimmer");
	}

}
