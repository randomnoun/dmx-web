package com.randomnoun.dmx.channel;

/** Channel definition allowing a gobo to be selected from a fixed list.
 *  
 * @author knoxg
 */
public class GoboWheelChannelDef extends ChannelDef {

	public GoboWheelChannelDef(int offset) 
	{
		super(offset, 0, 255);
		setHtmlImg("image/channel/goboWheel.png");
		setHtmlLabel("Gobo wheel");
	}
	
}
