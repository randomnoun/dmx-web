package com.randomnoun.dmx.channel;

import java.util.ArrayList;
import java.util.List;

/** Macro channels put the fixture into a preset repeating mode. When
 * enabled, this may or may not ignore other channel settings
 * (not sure if master dimmer is still honoured) 
 * 
 * @TODO check that macro ranges don't overlap once object has finished being initialised
 * 
 * Don't talk to me about spring here.
 * 
 * @author knoxg
 */
public class MacroChannelDef extends ChannelDef {

	public List<Macro> macros = new ArrayList<Macro>();
	
	public static class Macro {
		String name;
		int lowValue;
		int highValue;
		
		public Macro(String name, int lowValue, int highValue) {
			this.name = name;
			this.lowValue = lowValue;
			this.highValue = highValue;
		}
		
		public int getLowValue() { return lowValue; }
		public int getHighValue() { return highValue; }
		
		
	}
	
	public void addMacro(Macro macro) {
		this.macros.add(macro);
	}
	
	public List<Macro> getMacros() {
		return this.macros;
	}
	
	/** Create a new macro channel definition. Use 
	 * {@link MacroChannelDef#addMacro(Macro)} to
	 * add macros to this channel after construction.
	 *  
	 * @param offset
	 * 
	 * @see MacroChannelDef#addMacro(Macro)
	 */
	public MacroChannelDef(int offset) {
		super(offset, 0, 255);
	}
	
	public String getHtmlImg() { 
		return "image/channel/placeholder.gif";
		
	}
	public String getHtmlText() {
		return "Macro channel";
	}

	
}
