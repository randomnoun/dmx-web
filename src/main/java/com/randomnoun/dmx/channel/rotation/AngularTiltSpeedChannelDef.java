package com.randomnoun.dmx.channel.rotation;

public class AngularTiltSpeedChannelDef extends AngularSpeedChannelDef {

	public AngularTiltSpeedChannelDef(int offset, int slowValue, int fastValue, double degreesPerSecondWhenSlow, double degreesPerSecondWhenFast) {
		super(offset, AngularSpeedType.TILT, slowValue, fastValue, degreesPerSecondWhenSlow, degreesPerSecondWhenFast);
	}
}
