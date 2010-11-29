package com.randomnoun.dmx.channel.rotation;

public class PanPositionChannelDef extends AngularPositionChannelDef {
	
	public PanPositionChannelDef(int offset, double minAngle, double maxAngle) {
		super(offset, BitResolution.BYTE, AngularPositionType.PAN, minAngle, maxAngle);
	}
	
	public PanPositionChannelDef(int offset, AngularPositionChannelDef.BitResolution bitResolution, double minAngle, double maxAngle) {
		super(offset, bitResolution, AngularPositionType.PAN, minAngle, maxAngle);
	}

}
