package com.randomnoun.dmx.channelMuxer.primitive;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channel.rotation.AngularPositionChannelDef.BitResolution;
import com.randomnoun.dmx.channel.rotation.TiltPositionChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** This muxer requires at least one tilt channel definition 
 *
 * >1 channel definition can be used to provide major/minor
 * position control.
 * 
 * @TODO this muxer sets the output of the fixture to the
 * desired tilt position immediately, without transitioning
 * through intervening positions. So that's probably something to fix.
 */
public class TiltPositionChannelMuxer extends ChannelMuxer {

	Logger logger = Logger.getLogger(TiltPositionChannelMuxer.class);
	
	TiltPositionChannelDef pcdHigh;  // for BYTE or WORDHIGH TiltChannelDefs
	TiltPositionChannelDef pcdLow;   // for WORDLOW TiltChannelDefs (null otherwise)
	
	double origTiltPosition;  // at time target tilt position was set
	double targetTiltPosition;
	long   timeTargetTiltPositionWasSet;
	
	public TiltPositionChannelMuxer(Fixture fixture) {
		super(fixture);
		pcdHigh = null;
		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof TiltPositionChannelDef) {
				TiltPositionChannelDef pcd = (TiltPositionChannelDef) cd;
				if (pcd.getBitResolution() == BitResolution.BYTE || pcd.getBitResolution() == BitResolution.WORDHIGH) {
					if (pcdHigh!=null) { 
						throw new IllegalStateException("Can't have a TiltPositionChannelDef with bitResolution " + pcdHigh.getBitResolution() + "" +
							" and a TiltPositionChannelDef with bitResolution " + pcd.getBitResolution());
					}
					pcdHigh = pcd;
				} else if (pcd.getBitResolution() == BitResolution.WORDLOW) {
					if (pcdLow!=null) {
						throw new IllegalStateException("Cannot have 2 TiltPositionChannelDefs with bitResolution " + pcd.getBitResolution());
					}
					pcdLow = pcd;
				} else {
					throw new IllegalStateException("Unknown BitResolution " + pcd.getBitResolution());
				}
			}
		}
		if (pcdHigh==null) { throw new IllegalStateException("TiltPositionChannelMuxer on fixture with no tilt position channel"); }
	}
	
	
	public FixtureOutput getOutput() {
		// I'm going to assume low & high DMX values of 0-65535 for 2-channel PPCs. 
		
		// final int lowDmxValue, highDmxValue;
		final int tiltValue;
		if (pcdLow!=null) {
			tiltValue = fixture.getDmxChannelValue(pcdHigh.getOffset()) * 255 +
				fixture.getDmxChannelValue(pcdLow.getOffset());
		} else {
			tiltValue = fixture.getDmxChannelValue(pcdHigh.getOffset());
		}
		logger.debug("fixture=" + fixture.getName() + ", tiltValue=" + tiltValue);
		return new FixtureOutput() {
			public Color getColor() {
				return null;
			}
			public long getTime() { return fixture.getUniverse().getTime(); }
			public Double getTilt() {
				if (pcdLow!=null) {
					return pcdHigh.getMinAngle() +  
						(double) (tiltValue) /
						(65535) * (pcdHigh.getMaxAngle() - pcdHigh.getMinAngle());
				} else {
					return pcdHigh.getMinAngle() +  
					(double) (tiltValue - pcdHigh.getLowDmxValue()) /
					(pcdHigh.getHighDmxValue()-pcdHigh.getLowDmxValue()) *
					(pcdHigh.getMaxAngle() - pcdHigh.getMinAngle());
				}
			}
			public Double getPan() { return null; }
			public Double getDim() { return null; }
		};
	}
}