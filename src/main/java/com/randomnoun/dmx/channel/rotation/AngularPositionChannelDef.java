package com.randomnoun.dmx.channel.rotation;

import com.randomnoun.dmx.channel.BitResolution;
import com.randomnoun.dmx.channel.ChannelDef;

public abstract class AngularPositionChannelDef extends ChannelDef {

	private AngularPositionType angularPositionType;
	private BitResolution bitResolution;
	
	private double minAngle;
	private double maxAngle;
	
	public AngularPositionChannelDef(int offset, BitResolution bitResolution, 
		AngularPositionType angularPositionType, 
		double minAngle, double maxAngle) 
	{
		super(offset, 0, 255);
		this.angularPositionType = angularPositionType;
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
		this.bitResolution = bitResolution;
	}
	
	public AngularPositionType getAngularPositionType() { return angularPositionType; }
	public double getMinAngle() { return minAngle; }
	public double getMaxAngle() { return maxAngle; }
	public BitResolution getBitResolution() { return bitResolution; }
	

	
}
