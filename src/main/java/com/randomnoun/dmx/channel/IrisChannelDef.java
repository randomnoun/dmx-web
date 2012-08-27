package com.randomnoun.dmx.channel;

/** Iris channel definition.
 *  
 * @author knoxg
 */
public class IrisChannelDef extends ChannelDef {

	public IrisChannelDef(int offset) 
	{
		super(offset, 0, 255);
		setHtmlImg("image/channel/iris.png");
		setHtmlLabel("Gobo rotation");
	}
	
}
