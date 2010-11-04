package com.randomnoun.dmx.channel.rotation;

import com.randomnoun.dmx.channel.ChannelDef;

public abstract class AngularPositionChannelDef extends ChannelDef {

	public enum AngularPositionType { PAN, TILT }
	
	private AngularPositionType angularPositionType;
	private double minAngle;
	private double maxAngle;
	
	public AngularPositionChannelDef(int offset, AngularPositionType angularPositionType, 
		double minAngle, double maxAngle) 
	{
		super(offset, 0, 255);
		this.angularPositionType = angularPositionType;
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
		
	}
	
	public AngularPositionType getAngularPositionType() { return angularPositionType; }
	public double getMinAngle() { return minAngle; }
	public double getMaxAngle() { return maxAngle; }
	

	
}
