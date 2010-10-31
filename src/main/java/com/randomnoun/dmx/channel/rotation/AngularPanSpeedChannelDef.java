package com.randomnoun.dmx.channel.rotation;

public class AngularPanSpeedChannelDef extends AngularSpeedChannelDef {

	public AngularPanSpeedChannelDef(int offset, int slowValue, int fastValue, double degreesPerSecondWhenSlow, double degreesPerSecondWhenFast) {
		super(offset, AngularSpeedType.PAN, slowValue, fastValue, degreesPerSecondWhenSlow, degreesPerSecondWhenFast);
	}
}
