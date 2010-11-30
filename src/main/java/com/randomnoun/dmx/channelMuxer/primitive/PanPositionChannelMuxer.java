package com.randomnoun.dmx.channelMuxer.primitive;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channel.BitResolution;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channel.rotation.PanPositionChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** This muxer requires at least one pan channel definition 
 *
 * >1 channel definition can be used to provide major/minor
 * position control.
 * 
 * @TODO this muxer sets the output of the fixture to the
 * desired pan position immediately, without transitioning
 * through intervening positions. So that's probably something to fix.
 */
public class PanPositionChannelMuxer extends ChannelMuxer {

	Logger logger = Logger.getLogger(PanPositionChannelMuxer.class);
	
	PanPositionChannelDef pcdHigh;  // for BYTE or WORDHIGH PanChannelDefs
	PanPositionChannelDef pcdLow;   // for WORDLOW PanChannelDefs (null otherwise)
	
	double origPanPosition;  // at time target pan position was set
	double targetPanPosition;
	long   timeTargetPanPositionWasSet;
	
	public PanPositionChannelMuxer(Fixture fixture) {
		super(fixture);
		pcdHigh = null;
		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof PanPositionChannelDef) {
				PanPositionChannelDef pcd = (PanPositionChannelDef) cd;
				if (pcd.getBitResolution() == BitResolution.BYTE || pcd.getBitResolution() == BitResolution.WORDHIGH) {
					if (pcdHigh!=null) { 
						throw new IllegalStateException("Can't have a PanPositionChannelDef with bitResolution " + pcdHigh.getBitResolution() + "" +
							" and a PanPositionChannelDef with bitResolution " + pcd.getBitResolution());
					}
					pcdHigh = pcd;
				} else if (pcd.getBitResolution() == BitResolution.WORDLOW) {
					if (pcdLow!=null) {
						throw new IllegalStateException("Cannot have 2 PanPositionChannelDefs with bitResolution " + pcd.getBitResolution());
					}
					pcdLow = pcd;
				} else {
					throw new IllegalStateException("Unknown BitResolution " + pcd.getBitResolution());
				}
			}
		}
		if (pcdHigh==null) { throw new IllegalStateException("PanPositionChannelMuxer on fixture with no pan position channel"); }
	}
	
	
	public FixtureOutput getOutput() {
		// I'm going to assume low & high DMX values of 0-65535 for 2-channel PPCs. 
		
		// final int lowDmxValue, highDmxValue;
		final int panValue;
		if (pcdLow!=null) {
			panValue = fixture.getDmxChannelValue(pcdHigh.getOffset()) * 255 +
				fixture.getDmxChannelValue(pcdLow.getOffset());
		} else {
			panValue = fixture.getDmxChannelValue(pcdHigh.getOffset());
		}
		logger.debug("fixture=" + fixture.getName() + ", panValue=" + panValue);
		return new FixtureOutput() {
			public Color getColor() {
				return null;
			}
			public long getTime() { return fixture.getUniverse().getTime(); }
			public Double getPan() {
				if (pcdLow!=null) {
					return pcdHigh.getMinAngle() +  
						(double) (panValue) /
						(65535) * (pcdHigh.getMaxAngle() - pcdHigh.getMinAngle());
				} else {
					return pcdHigh.getMinAngle() +  
					(double) (panValue - pcdHigh.getLowDmxValue()) /
					(pcdHigh.getHighDmxValue()-pcdHigh.getLowDmxValue()) *
					(pcdHigh.getMaxAngle() - pcdHigh.getMinAngle());
				}
			}
			public Double getTilt() { return null; }
			public Double getDim() { return null; }
			public Double getStrobe() { return null; }
		};
	}
}