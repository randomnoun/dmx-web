package com.randomnoun.dmx.channel.dimmer;

public class MasterDimmerChannelDef extends DimmerChannelDef {
	public MasterDimmerChannelDef(int offset) {
		super(offset, DimmerType.MASTER);
	}
	
	public String getHtmlImg() { 
		return "image/channel/placeholder.png";
		
	}
	public String getHtmlLabel() {
		return "Master dimmer";
	}
}
