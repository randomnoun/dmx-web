package com.randomnoun.dmx.channelMuxer.filter;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** This muxer dims the output of another muxer.
 * 
 * By default, it will use the Dimmer ChannelDef with DimmerType==MASTER
 * if one is available for a fixture.
 * 
 * @author knoxg
 */
public class MasterDimmerChannelMuxer extends ChannelMuxer {

	int masterOffset;
	int dmxLowValue;   // at this level, the muxer will be black
	int dmxHighValue;  // at this level, the muxer will allow all input through
	
	// if dmxLowValue<dmxHighValue, then the range below dmxLowValue is black, above dmxHighValue is transparent
	// if dmxLowValue>dmxHighValue, then the range above dmxLowValue is black, below dmxHighValue is transparent
	
	ChannelMuxer inputMuxer;
	Logger logger = Logger.getLogger(MasterDimmerChannelMuxer.class);
	
	public MasterDimmerChannelMuxer(ChannelMuxer inputMuxer) {
		super(inputMuxer.getFixture());
		this.inputMuxer = inputMuxer;
		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof DimmerChannelDef) {
				DimmerChannelDef dcd = (DimmerChannelDef) cd;
				if (dcd.dimmerType==DimmerChannelDef.DimmerType.MASTER) { 
					masterOffset = dcd.getOffset();
					dmxLowValue = dcd.getLowDmxValue();
					dmxHighValue = dcd.getHighDmxValue();
				}
			}
		}
	}

	public MasterDimmerChannelMuxer(ChannelMuxer inputMuxer, int offset, int dmxLowValue, int dmxHighValue) {
		super(inputMuxer.getFixture());
		this.inputMuxer = inputMuxer;
		masterOffset = offset;
		this.dmxLowValue = dmxLowValue;
		this.dmxHighValue = dmxHighValue;
	}
	
	
	public FixtureOutput getOutput() {
		final FixtureOutput input = inputMuxer.getOutput();
		final int masterValue = fixture.getDmxChannelValue(masterOffset);
		final double dimValue;
		if (dmxLowValue<dmxHighValue) {
			dimValue = masterValue < dmxLowValue ? 0 : 
				((masterValue > dmxHighValue) ? 1 :
				 (masterValue-dmxLowValue)/(double) (dmxHighValue-dmxLowValue));	
		} else {
			dimValue = masterValue > dmxLowValue ? 1 : 
				((masterValue < dmxHighValue) ? 0 :
				 1-(masterValue-dmxHighValue)/(double) (dmxLowValue-dmxHighValue));	
		}
		if (logger.isDebugEnabled()) {
			logger.debug("fixture=" + fixture.getName() + ", mux input " + input + ", masterValue=" + masterValue + ", dimValue=" + dimValue +  
				((dmxLowValue==0 && dmxHighValue==255) ? "" : " (dmxLowValue=" + dmxLowValue + ", dmxHighValue=" + dmxHighValue + ")"));
		}
		return new FixtureOutput() {
			public Color getColor() {
				Color inputColor = input.getColor();
				return new Color((int) (inputColor.getRed() * dimValue), 
					(int) (inputColor.getGreen() * dimValue),
					(int) (inputColor.getBlue() * dimValue));
			}
			public long getTime() { return input.getTime(); }
			public Double getPan() { return input.getPan(); }
			public Double getTilt() { return input.getTilt(); }
			public Double getDim() { return dimValue; }
			public Double getStrobe() { return input.getStrobe(); }
		};
	}
}