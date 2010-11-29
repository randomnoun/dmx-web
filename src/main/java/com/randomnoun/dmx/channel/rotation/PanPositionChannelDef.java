package com.randomnoun.dmx.channel.rotation;

import com.randomnoun.dmx.channel.BitResolution;

public class PanPositionChannelDef extends AngularPositionChannelDef {
	
	public PanPositionChannelDef(int offset, double minAngle, double maxAngle) {
		super(offset, BitResolution.BYTE, AngularPositionType.PAN, minAngle, maxAngle);
	}
	
	public PanPositionChannelDef(int offset, BitResolution bitResolution, double minAngle, double maxAngle) {
		super(offset, bitResolution, AngularPositionType.PAN, minAngle, maxAngle);
	}

}
