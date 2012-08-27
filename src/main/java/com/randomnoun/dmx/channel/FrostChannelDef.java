package com.randomnoun.dmx.channel;

/** Frost channel definition.
 *  
 * @author knoxg
 */
public class FrostChannelDef extends ChannelDef {

	public FrostChannelDef(int offset) 
	{
		super(offset, 0, 255);
		setHtmlImg("image/channel/frost.png");
		setHtmlLabel("Frost");
	}
	
}
