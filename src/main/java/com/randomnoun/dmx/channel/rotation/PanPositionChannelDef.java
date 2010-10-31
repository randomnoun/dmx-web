package com.randomnoun.dmx.channel.rotation;

public class PanPositionChannelDef extends AngularPositionChannelDef {
	
	public PanPositionChannelDef(int offset, double minAngle, double maxAngle) {
		super(offset, AngularPositionType.PAN, minAngle, maxAngle);
	}

}
