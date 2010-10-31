package com.randomnoun.dmx.channel.rotation;

public class TiltPositionChannelDef extends AngularPositionChannelDef {
	
	public TiltPositionChannelDef(int offset, double minAngle, double maxAngle) {
		super(offset, AngularPositionType.TILT, minAngle, maxAngle);
	}

}
