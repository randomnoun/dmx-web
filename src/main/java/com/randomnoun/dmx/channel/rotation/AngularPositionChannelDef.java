package com.randomnoun.dmx.channel.rotation;

import com.randomnoun.dmx.channel.ChannelDef;

public class AngularPositionChannelDef extends ChannelDef {

	public enum AngularPositionType { PAN, TILT }
	
	public AngularPositionType angularPositionType;
	
	public AngularPositionChannelDef(int offset, AngularPositionType angularPositionType, 
		double minAngle, double maxAngle) 
	{
		super(offset, 0, 255);
		this.angularPositionType = angularPositionType;
		
	}

	
}
