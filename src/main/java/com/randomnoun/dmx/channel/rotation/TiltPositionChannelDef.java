package com.randomnoun.dmx.channel.rotation;

import com.randomnoun.dmx.channel.BitResolution;

public class TiltPositionChannelDef extends AngularPositionChannelDef {
	
	public TiltPositionChannelDef(int offset, 
			double minAngle, double maxAngle) {
		super(offset, BitResolution.BYTE, AngularPositionType.TILT, minAngle, maxAngle);
		setHtmlImg("image/channel/tilt_16x16.png");
		setHtmlLabel("Tilt position");
	}

	
	public TiltPositionChannelDef(int offset, BitResolution bitResolution,
			double minAngle, double maxAngle) {
		super(offset, bitResolution, AngularPositionType.TILT, minAngle, maxAngle);
		setHtmlImg("image/channel/tilt_16x16.png");
		setHtmlLabel("Tilt position" + (getBitResolution()==BitResolution.BYTE ? "" : " " + getBitResolution()));
	}

}
