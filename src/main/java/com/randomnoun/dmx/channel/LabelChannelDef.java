package com.randomnoun.dmx.channel;

/** Channel def that does nothing but provide a label
 * on the DMX panel
 *  
 * @author knoxg
 */
public class LabelChannelDef extends ChannelDef {

	private String htmlImg;
	private String htmlText;
	
	public LabelChannelDef(int offset, String htmlImg, String htmlText) 
	{
		super(offset, 0, 255);
		htmlImg = "image/channel/placeholder.gif";
		htmlText = "Unknown channel";
		if (htmlImg!=null) { this.htmlImg = htmlImg; }
		if (htmlText!=null) { this.htmlText = htmlText; }
	}
	
	public String getHtmlImg() { 
		return htmlImg;
		
	}
	public String getHtmlLabel() {
		return htmlText;
	}

	
	
}
