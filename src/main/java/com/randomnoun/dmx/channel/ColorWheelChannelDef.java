package com.randomnoun.dmx.channel;

/** Channel def that allows a fixed set of colors to be selected
 *  
 * @author knoxg
 */
public class ColorWheelChannelDef extends ChannelDef {

	public ColorWheelChannelDef(int offset) 
	{
		super(offset, 0, 255);
		setHtmlImg("image/channel/colorWheel.png");
		setHtmlLabel("Color wheel");
	}
	
}
