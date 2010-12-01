package com.randomnoun.dmx.channelMuxer.primitive;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channel.BitResolution;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.rotation.AngularSpeedChannelDef;
import com.randomnoun.dmx.channel.rotation.PanPositionChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** This muxer requires at least one pan channel definition 
 *
 * >1 channel definition can be used to provide major/minor
 * position control.
 * 
 */

// to actually determine the pan position, we could keep the last few
// pan position requests, and the speed at which we were moving towards them
// at that time

// what I'm actually going to do is keep a local actualPan value, and
// then update it whenever the getActualPan() method is invoked. Which
// will therefore get more inaccurate if you're not staring at it constantly.

public class PanPositionChannelMuxer extends ChannelMuxer {

	Logger logger = Logger.getLogger(PanPositionChannelMuxer.class);
	
	AngularSpeedChannelDef ascd;
	PanPositionChannelDef pcdHigh;  // for BYTE or WORDHIGH PanChannelDefs
	PanPositionChannelDef pcdLow;   // for WORDLOW PanChannelDefs (null otherwise)
	
	/*
	double origPanPosition;  // at time target pan position was set
	double targetPanPosition;
	long   timeTargetPanPositionWasSet;
	*/
	
	double actualPan; // the actual current pan value, as near as we can determine
	long   lastPanUpdateTime;
	
	public PanPositionChannelMuxer(Fixture fixture) {
		super(fixture);
		lastPanUpdateTime = System.currentTimeMillis();
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
			if (cd instanceof AngularSpeedChannelDef) {
				if (ascd!=null) {
					throw new IllegalStateException("Cannot have 2 angular speed channel definitions");
				}
				ascd = (AngularSpeedChannelDef) cd;
			}
		}
		if (pcdHigh==null) { throw new IllegalStateException("PanPositionChannelMuxer on fixture with no pan position channel"); }
	}
	
	
	public FixtureOutput getOutput() {
		// I'm going to assume low & high DMX values of 0-65535 for 2-channel PPCs. 
		
		// final int lowDmxValue, highDmxValue;
		final int panSpeedValue;
		final int panValue;
		if (pcdLow!=null) {
			panValue = fixture.getDmxChannelValue(pcdHigh.getOffset()) * 255 +
				fixture.getDmxChannelValue(pcdLow.getOffset());
		} else {
			panValue = fixture.getDmxChannelValue(pcdHigh.getOffset());
		}
		if (ascd==null) {
			panSpeedValue = 0;
		} else {
			panSpeedValue = fixture.getDmxChannelValue(ascd.getOffset());
		}
		logger.debug("fixture=" + fixture.getName() + ", panValue=" + panValue + ", panSpeedValue=" + panSpeedValue);
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
			public Double getActualPan() {
				double targetPan = getPan();
				if (ascd==null) { return targetPan; }
				if (actualPan==targetPan) { return targetPan; }
				
				int[] dmxValues = ascd.getDmxValues();
				double[] panSpeeds = ascd.getPanSpeedValues();
				double panSpeed = 0;
				for (int i=0; i<dmxValues.length-1; i++) {
					if (dmxValues[i]<=panSpeedValue && dmxValues[i+1]>=panSpeedValue) {
						panSpeed = 
							panSpeeds[i] +
							(panSpeeds[i+1]-panSpeeds[i]) * 
							((panSpeedValue-dmxValues[i])/(dmxValues[i+1]-dmxValues[i]));
						break;
					}
				}
				// if this isn't called regularly, then the calculated positions
				// will be off by quite a bit. If it's called too frequently,
				// then floating point distortions will probably cause it to be
				// off by quite a bit as well.
				// 
				// current thoughts are to just to do the simplest possible thing
				// and live with the inaccuracy.
				long now = System.currentTimeMillis();
				double panDistance = panSpeed * (now-lastPanUpdateTime); 
				if (Math.abs(panDistance) > Math.abs(targetPan-actualPan)) {
					actualPan = targetPan;
				} else {
					if (actualPan < targetPan) {
						actualPan = actualPan + panDistance;
					} else {
						actualPan = actualPan - panDistance;
					}
				}
				lastPanUpdateTime = now;
				return actualPan;
			}
			
			public Double getTilt() { return null; }
			public Double getActualTilt() { return null; }
			public Double getDim() { return null; }
			public Double getStrobe() { return null; }
		};
	}
}