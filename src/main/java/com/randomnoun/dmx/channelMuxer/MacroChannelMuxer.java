package com.randomnoun.dmx.channelMuxer;

import java.awt.Color;

import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channelMuxer.timed.CyclingTimeBasedChannelMuxer;
import com.randomnoun.dmx.channel.StrobeChannelDef;

/** A muxer which takes a single macro channel and, if enabled, 
 * will replace another muxer's output
 * 
 * @author knoxg
 */
public class MacroChannelMuxer extends ChannelMuxer {
	ChannelMuxer inputMuxer;
	ChannelMuxer[] outputMuxers;
	MacroChannelDef channelDef;
	
	public MacroChannelMuxer(ChannelMuxer inputMuxer, ChannelMuxer[] outputMuxers) {
		super(inputMuxer.getFixture());
		this.inputMuxer = inputMuxer;
		this.outputMuxers = outputMuxers;
		
		if (inputMuxer==null) { throw new NullPointerException("Null inputMuxer"); }
		if (outputMuxers==null) { throw new NullPointerException("Null outputMuxers"); }
		if (outputMuxers.length==0) { throw new IllegalArgumentException("Null outputMuxers"); }

		
		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof MacroChannelDef) {
				this.channelDef = (MacroChannelDef) cd;
			}
		}
		if (this.channelDef!=null) { 
			throw new IllegalStateException("Cannot apply a macro muxer to a fixture without a macro channel definition");
		}
	}

	/** Returns the index of the macro which is currently running, or -1
	 * if no macro is running
	 *  
	 * @return
	 */
	private int getCurrentMacroIndex() {
		int macroValue = fixture.getChannelValue(channelDef.getOffset());
		for (int i=0; i<channelDef.macros.size(); i++) {
			Macro m = channelDef.macros.get(i);
			if (macroValue >= m.getLowValue() && macroValue <= m.getHighValue()) {
				return i;
			}
		}
		return -1;
	}
	
	
	/** Returns the muxer that is active on this channel, whether that be
	 * the input muxer, or one of the macros defined in this muxer.
	 *  
	 * @return
	 */
	private ChannelMuxer getCurrentChannelMuxer() {
		int currentMacro = getCurrentMacroIndex();
		if (currentMacro == -1) {
			return inputMuxer;
		}
		return outputMuxers[currentMacro];
	}
	
	
	/*
	@Override
	public long getCycleTime() {
		ChannelMuxer activeMuxer = getCurrentChannelMuxer();
		if (activeMuxer instanceof CyclingTimeBasedChannelMuxer) {
			return ((CyclingTimeBasedChannelMuxer) activeMuxer).getCycleTime();
		} else {
			return 0;
		}		
	}
	*/
	
	@Override
	public FixtureOutput getOutput() {
		return getCurrentChannelMuxer().getOutput();
	}
}