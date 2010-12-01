package com.randomnoun.dmx.channel;

/** A (time-varying) speed channel. Ranges go from 'slow' to 'fast'. Must be used
 * in conjunction with another channel to be useful.
 * 
 * <p>When the value of this channel==slowValue
 * time will progress at the the speedUpFactorWhenSlow * normal rate; as the
 * channel increases to fastValue, time will be multipled up to speedUpFactorWhenFast * normal rate.
 * 
 * @see MacroChannelDef
 */
public class SpeedChannelDef extends ChannelDef {

	public int slowValue;
	public int fastValue;
	public double speedUpFactorWhenSlow;
	public double speedUpFactorWhenFast;
	
	public SpeedChannelDef(int offset, int slowValue, int fastValue, double speedUpFactorWhenSlow, double speedUpFactorWhenFast) {
		super(offset, 0, 255);
		this.slowValue = slowValue;
		this.fastValue = fastValue;
		this.speedUpFactorWhenSlow = speedUpFactorWhenSlow;
		this.speedUpFactorWhenFast = speedUpFactorWhenFast;
		setHtmlImg("image/channel/placeholder.png");
		setHtmlLabel("Speed dimmer");
	}
	
	public int getSlowValue() { return slowValue; }
	public int getFastValue() { return fastValue; }
	public double getSpeedUpFactorWhenSlow() { return speedUpFactorWhenSlow; }
	public double getSpeedUpFactorWhenFast() { return speedUpFactorWhenFast; }
	
	public String getHtmlImg() { 
		return "image/channel/placeholder.png";
		
	}
	public String getHtmlLabel() {
		return "Speed channel";
	}

	
}
