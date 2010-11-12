package com.randomnoun.dmx.channelMuxer.filter;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.FixtureOutput;

// don't think I need any of this now

/** A MacroRangeChannelMuxer is used by MacroChannelMuxers to 
 * wrap other Muxers within a macro range.
 * 
 * e.g. for a strobe/dimmer macro muxer with the following ranges:
 * 0-7     closed
 * 8-134   100-0%
 * 135-239 strobe (slow->fast)
 * 240-255 open
 * 
 * you could create a MacroChannelDef with the four macros described above, plus
 * a MacroChannelMuxer containing four MacroRangeChannelMuxers. 
 * 
 * <p>When executed, each MacroRangeChannelMuxer would pass control to 
 * the specific muxer, with that muxer seeing a virtual channelDef.
 * 
 * <p>i.e. for the example above, the MacroChannelMuxer would wrap:
 * 0-7    ColorChannelMuxer set to Black
 * 8-134  DimmerChannelMuxer  with a virtual MasterDimmerChannelDef with minValue=255 maxValue=0
 * 135-239 StrobeChannelMuxer with a virtual StrobeChannelDef
 * 
 * when the channel is in a certain DMX value range.
 * 
 * @author knoxg
 *
public class MacroRangeChannelMuxer extends ChannelMuxer {

	ChannelMuxer inputMuxer;
	Logger logger = Logger.getLogger(MacroRangeChannelMuxer.class);
	
	public MacroRangeChannelMuxer(ChannelMuxer inputMuxer, ChannelDef channelDef, int zeroDmxValue, int fullDmxValue) {
		super(inputMuxer.getFixture());
		this.inputMuxer = inputMuxer;

		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof DimmerChannelDef) {
				DimmerChannelDef dcd = (DimmerChannelDef) cd;
				if (dcd.dimmerType==DimmerChannelDef.DimmerType.MASTER) { masterOffset = dcd.getOffset(); }
			}
		}
	}
	
	public FixtureOutput getOutput() {
		final FixtureOutput input = inputMuxer.getOutput();
		final int masterValue = fixture.getDmxChannelValue(masterOffset);
		logger.debug("mux input " + input + ", masterValue=" + masterValue);
		return new FixtureOutput() {
			public Color getColor() {
				Color inputColor = input.getColor();
				return new Color(inputColor.getRed() * masterValue/255, // TODO: shifts 
						inputColor.getGreen() * masterValue/255,
						inputColor.getBlue() * masterValue/255);
			}
			public long getTime() { return input.getTime(); }
			public Double getPan() { return input.getPan(); }
			public Double getTilt() { return input.getTilt(); }
		};
	}
}
*/