package com.randomnoun.dmx.channel;

/** Prism channel definition.
 *  
 * @author knoxg
 */
public class PrismChannelDef extends ChannelDef {

	public PrismChannelDef(int offset) 
	{
		super(offset, 0, 255);
		setHtmlImg("image/channel/prism.png");
		setHtmlLabel("Prism");
	}
	
}
