package com.randomnoun.dmx.channel.dimmer;

public class RedDimmerChannelDef extends DimmerChannelDef {
	public RedDimmerChannelDef(int offset) {
		super(offset, DimmerType.RED);
	}
	
	public String getHtmlImg() { 
		return "image/channel/placeholder.gif";
		
	}
	public String getHtmlText() {
		return "Red dimmer";
	}
}
