package com.randomnoun.dmx.channel.dimmer;

public class BlueDimmerChannelDef extends DimmerChannelDef {
	public BlueDimmerChannelDef(int offset) {
		super(offset, DimmerType.BLUE);
	}

	public String getHtmlImg() { 
		return "image/channel/placeholder.gif";
		
	}
	public String getHtmlText() {
		return "Blue dimmer";
	}
}
