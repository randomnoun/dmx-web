package com.randomnoun.dmx.channel;

/** Channel def that does nothing but provide a label
 * on the DMX panel
 *  
 * @author knoxg
 */
public class LabelChannelDef extends ChannelDef {

	public LabelChannelDef(int offset, String htmlImg, String htmlLabel) 
	{
		super(offset, 0, 255);
		if (htmlImg!=null) { setHtmlImg(htmlImg); }
		if (htmlLabel!=null) { setHtmlLabel(htmlLabel); }
	}
	
}
