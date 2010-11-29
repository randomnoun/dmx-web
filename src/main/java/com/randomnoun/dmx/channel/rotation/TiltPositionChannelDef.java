package com.randomnoun.dmx.channel.rotation;

public class TiltPositionChannelDef extends AngularPositionChannelDef {
	
	public TiltPositionChannelDef(int offset, 
			double minAngle, double maxAngle) {
		super(offset, BitResolution.BYTE, AngularPositionType.TILT, minAngle, maxAngle);
	}

	
	public TiltPositionChannelDef(int offset, AngularPositionChannelDef.BitResolution bitResolution,
			double minAngle, double maxAngle) {
		super(offset, bitResolution, AngularPositionType.TILT, minAngle, maxAngle);
	}

}
