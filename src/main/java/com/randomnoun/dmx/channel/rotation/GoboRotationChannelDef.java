package com.randomnoun.dmx.channel.rotation;

import com.randomnoun.dmx.channel.ChannelDef;

/** Gobo rotation channel definition. 
 *  
 * @author knoxg
 */
public class GoboRotationChannelDef extends ChannelDef {

	public GoboRotationChannelDef(int offset) 
	{
		super(offset, 0, 255);
		setHtmlImg("image/channel/goboRotation.png");
		setHtmlLabel("Gobo rotation");
	}
	
}
