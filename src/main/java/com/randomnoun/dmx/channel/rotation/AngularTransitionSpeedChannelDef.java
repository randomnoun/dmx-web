package com.randomnoun.dmx.channel.rotation;

import com.randomnoun.dmx.channel.ChannelDef;

/** An angular speed channel, which controls how fast a rotating head
 * moves from fixed position to fixed position.
 * 
 * <p>When the value of this channel==slowValue, 
 * the fixture is moving at panDegreesPerSecondWhenSlow and tiltDegreesPerSecondWhenSlow.
 * 
 * When the value of this channel==fastValue,
 * the fixture is moving at panDegreesPerSecondWhenFast and tiltDegreesPerSecondWhenFast.
 * 
 * <p>Outside of this range, the fixture is considered not moving.
 * 
 * <p>Other alternatives could be separate channels per speed type,  
 * transitions by %ages rather than fixed degrees per second,
 * transitions by degreesPerSecond, but with a ramp up/down of speed, etc.
 * 
 * 
 */
public abstract class AngularTransitionSpeedChannelDef extends ChannelDef {

	public int slowValue;
	public int fastValue;
	public double panDegreesPerSecondWhenSlow;
	public double tiltDegreesPerSecondWhenSlow;
	public double panDegreesPerSecondWhenFast;
	public double tiltDegreesPerSecondWhenFast;
	
	public AngularTransitionSpeedChannelDef(int offset, 
		int slowValue, int fastValue,
		double panDegreesPerSecondWhenSlow, double tiltDegreesPerSecondWhenSlow,
		double panDegreesPerSecondWhenFast, double tiltDegreesPerSecondWhenFast) 
	{
		super(offset, 0, 255);
		this.slowValue = slowValue;
		this.fastValue = fastValue;
		this.panDegreesPerSecondWhenSlow = panDegreesPerSecondWhenSlow;
		this.tiltDegreesPerSecondWhenSlow = tiltDegreesPerSecondWhenSlow;
		this.panDegreesPerSecondWhenFast = panDegreesPerSecondWhenFast;
		this.tiltDegreesPerSecondWhenFast = tiltDegreesPerSecondWhenFast;
	}
	
	public int getSlowValue() { return slowValue; }
	public int getFastValue() { return fastValue; }
	public double getPanDegreesPerSecondWhenSlow() { return panDegreesPerSecondWhenSlow; }
	public double getTiltDegreesPerSecondWhenSlow() { return tiltDegreesPerSecondWhenSlow; }
	public double getPanDegreesPerSecondWhenFast() { return panDegreesPerSecondWhenFast; }
	public double getTiltDegreesPerSecondWhenFast() { return tiltDegreesPerSecondWhenFast; }
	
}
