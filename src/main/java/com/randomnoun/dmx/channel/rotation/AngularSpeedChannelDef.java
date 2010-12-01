package com.randomnoun.dmx.channel.rotation;

import com.randomnoun.dmx.channel.ChannelDef;

/** An angular speed channel, controlling pan or tilt speed, presumably.
 * 
 * <p>Speeds are measured in terms of degrees per second. 
 * A speed ramp can be supplied to
 * specify speeds per DMX input value. The ramp is interpolated linearly.
 * 
 * <p>It is assumed that the fixture always moves at a constant speed. 
 * 
 * <p>The list of DMX input values should include 0 and 255, at a minimum.
 * 
 */
public class AngularSpeedChannelDef extends ChannelDef {

	public enum AngularSpeedType { PAN, TILT }
	
	// public AngularSpeedType angularSpeedType;
	
	int[] dmxValues;
	double[] panSpeedValues;
	double[] tiltSpeedValues;
	
	public AngularSpeedChannelDef(int offset,  
			int[] dmxValues,
			double[] panSpeedValues,
			double[] tiltSpeedValues) {
		super(offset, 0, 255);
		// this.angularSpeedType = angularSpeedType;
		this.dmxValues = dmxValues;
		this.panSpeedValues = panSpeedValues;
		this.tiltSpeedValues = tiltSpeedValues;
		setHtmlImg("image/channel/placeholder.png");
		setHtmlLabel("Angular speed");
	}

	public int[] getDmxValues() { return dmxValues; }
	public double[] getPanSpeedValues() { return panSpeedValues; }
	public double[] getTiltSpeedValues() { return tiltSpeedValues; }
	
}
