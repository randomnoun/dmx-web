package com.randomnoun.dmx.channel.rotation;

import com.randomnoun.dmx.channel.BitResolution;

public class TiltPositionChannelDef extends AngularPositionChannelDef {
	
	public TiltPositionChannelDef(int offset, 
			double minAngle, double maxAngle) {
		super(offset, BitResolution.BYTE, AngularPositionType.TILT, minAngle, maxAngle);
	}

	
	public TiltPositionChannelDef(int offset, BitResolution bitResolution,
			double minAngle, double maxAngle) {
		super(offset, bitResolution, AngularPositionType.TILT, minAngle, maxAngle);
	}

}
