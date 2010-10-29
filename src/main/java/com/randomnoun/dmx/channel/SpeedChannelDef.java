package com.randomnoun.dmx.channel;

/** A speed channel. Ranges go from 'slow' to 'fast'. Must be used
 * in conjunction with another channel to be useful.
 * 
 * <p>When the value of this channel==slowValue
 * time will progress at the normal rate; as the
 * channel increases to fastValue, time will be multipled up to speedUpFactor.
 * 
 * @see MacroChannelDef
 */
public class SpeedChannelDef extends ChannelDef {

	public int slowValue;
	public int fastValue;
	public double speedUpFactor;
	
	public SpeedChannelDef(int offset, int slowValue, int fastValue, double speedUpFactor) {
		this.offset = offset;
		this.slowValue = slowValue;
		this.fastValue = fastValue;
		this.speedUpFactor = speedUpFactor;
	}
	
	public int getSlowValue() { return slowValue; }
	public int getFastValue() { return fastValue; }
	public double getSpeedUpFactor() { return speedUpFactor; }
	
}
