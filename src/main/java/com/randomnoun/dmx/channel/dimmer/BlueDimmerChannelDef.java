package com.randomnoun.dmx.channel.dimmer;

public class BlueDimmerChannelDef extends DimmerChannelDef {
	public BlueDimmerChannelDef(int offset) {
		super(offset, DimmerType.BLUE);
	}

	public String getHtmlImg() { 
		return "image/channel/placeholder.png";
		
	}
	public String getHtmlLabel() {
		return "Blue dimmer";
	}
}
