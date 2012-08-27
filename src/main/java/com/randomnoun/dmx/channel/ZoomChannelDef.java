package com.randomnoun.dmx.channel;

/** Zoom channel definition.
 *  
 * @author knoxg
 */
public class ZoomChannelDef extends ChannelDef {

	public ZoomChannelDef(int offset, String htmlImg, String htmlLabel) 
	{
		super(offset, 0, 255);
		setHtmlImg("image/channel/zoom.png");
		setHtmlLabel("Zoom");
	}
	
}
