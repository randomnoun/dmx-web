package com.randomnoun.dmx.channel.rotation;

import com.randomnoun.dmx.channel.ChannelDef;

/** An angular speed channel, controlling pan or tilt speed, presumably.
 * 
 * <p>When the value of this channel==slowValue, 
 * the fixture is moving at degreesPerSecondWhenSlow (presumably zero).
 * When the value of this channel==fastValue,
 * the fixture is moving at degreesPerSecondWhenFast.
 * 
 * <p>Outside of this range, the fixture is considered not moving.
 * 
 * <p>Turns out this channel definition isn't used, so it's therefore
 * probably broken
 * 
 */
public abstract class AngularSpeedChannelDef extends ChannelDef {

	public enum AngularSpeedType { PAN, TILT }
	
	public AngularSpeedType angularSpeedType;
	public int slowValue;
	public int fastValue;
	public double degreesPerSecondWhenSlow;
	public double degreesPerSecondWhenFast;
	
	public AngularSpeedChannelDef(int offset, AngularSpeedType angularSpeedType, int slowValue, int fastValue, double degreesPerSecondWhenSlow, double degreesPerSecondWhenFast) {
		super(offset, 0, 255);
		this.angularSpeedType = angularSpeedType;
		this.slowValue = slowValue;
		this.fastValue = fastValue;
		this.degreesPerSecondWhenSlow = degreesPerSecondWhenSlow;
		this.degreesPerSecondWhenFast = degreesPerSecondWhenFast;
	}
	
	public int getSlowValue() { return slowValue; }
	public int getFastValue() { return fastValue; }
	public double getDegreesPerSecondWhenSlow() { return degreesPerSecondWhenSlow; }
	public double getDegreesPerSecondWhenFast() { return degreesPerSecondWhenFast; }
	
}
