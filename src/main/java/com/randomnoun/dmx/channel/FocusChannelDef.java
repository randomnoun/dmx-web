package com.randomnoun.dmx.channel;

/** Focus channel definition.
 *  
 * @author knoxg
 */
public class FocusChannelDef extends ChannelDef {

	public FocusChannelDef(int offset) 
	{
		super(offset, 0, 255);
		setHtmlImg("image/channel/focus.png");
		setHtmlLabel("Focus");
	}
	
}
