package com.randomnoun.dmx.channel.dimmer;

public class GreenDimmerChannelDef extends DimmerChannelDef {
	public GreenDimmerChannelDef(int offset) {
		super(offset, DimmerType.GREEN);
	}

	public String getHtmlImg() { 
		return "image/channel/placeholder.png";
		
	}
	public String getHtmlLabel() {
		return "Green dimmer";
	}
	
}
