package com.randomnoun.dmx.channel.dimmer;

import com.randomnoun.dmx.channel.ChannelDef;

public abstract class DimmerChannelDef extends ChannelDef {

	public enum DimmerType { MASTER, RED, GREEN, BLUE }
	
	public DimmerType dimmerType;
	
	public DimmerChannelDef(int offset, DimmerType dimmerType) {
		this.offset = offset;
		this.dimmerType = dimmerType;
	}
	
}
