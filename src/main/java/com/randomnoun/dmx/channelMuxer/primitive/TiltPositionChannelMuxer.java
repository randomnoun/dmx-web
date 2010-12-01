package com.randomnoun.dmx.channelMuxer.primitive;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channel.BitResolution;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.rotation.AngularSpeedChannelDef;
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
	
	AngularSpeedChannelDef ascd;
	TiltPositionChannelDef pcdHigh;  // for BYTE or WORDHIGH TiltChannelDefs
	TiltPositionChannelDef pcdLow;   // for WORDLOW TiltChannelDefs (null otherwise)
	
	/*
	double origTiltPosition;  // at time target tilt position was set
	double targetTiltPosition;
	long   timeTargetTiltPositionWasSet;
	*/
	
	double actualTilt; // the actual current pan value, as near as we can determine
	long   lastTiltUpdateTime=0;
	
	public TiltPositionChannelMuxer(Fixture fixture) {
		super(fixture);
		lastTiltUpdateTime = System.currentTimeMillis();
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
			if (cd instanceof AngularSpeedChannelDef) {
				if (ascd!=null) {
					throw new IllegalStateException("Cannot have 2 angular speed channel definitions");
				}
				ascd = (AngularSpeedChannelDef) cd;
			}
		}
		if (pcdHigh==null) { throw new IllegalStateException("TiltPositionChannelMuxer on fixture with no tilt position channel"); }
	}
	
	
	public FixtureOutput getOutput() {
		// I'm going to assume low & high DMX values of 0-65535 for 2-channel PPCs. 
		
		// final int lowDmxValue, highDmxValue;
		final int tiltSpeedValue;
		final int tiltValue;
		if (pcdLow!=null) {
			tiltValue = fixture.getDmxChannelValue(pcdHigh.getOffset()) * 255 +
				fixture.getDmxChannelValue(pcdLow.getOffset());
		} else {
			tiltValue = fixture.getDmxChannelValue(pcdHigh.getOffset());
		}
		if (ascd==null) {
			tiltSpeedValue = 0;
		} else {
			tiltSpeedValue = fixture.getDmxChannelValue(ascd.getOffset());
		}
		logger.debug("fixture=" + fixture.getName() + ", tiltValue=" + tiltValue + ", tiltSpeedValue=" + tiltSpeedValue);
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
			public Double getActualTilt() {
				double targetTilt = getTilt();
				if (ascd==null) { return targetTilt; }
				if (actualTilt==targetTilt) { return targetTilt; }
				
				int[] dmxValues = ascd.getDmxValues();
				double[] tiltSpeeds = ascd.getTiltSpeedValues();
				double tiltSpeed = 0;
				for (int i=0; i<dmxValues.length-1; i++) {
					if (dmxValues[i]<=tiltSpeedValue && dmxValues[i+1]>=tiltSpeedValue) {
						tiltSpeed = 
							tiltSpeeds[i] +
							(tiltSpeeds[i+1]-tiltSpeeds[i]) * 
							((tiltSpeedValue-dmxValues[i])/(dmxValues[i+1]-dmxValues[i]));
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
				double tiltDistance = tiltSpeed * (now-lastTiltUpdateTime); 
				if (Math.abs(tiltDistance) > Math.abs(targetTilt-actualTilt)) {
					actualTilt = targetTilt;
				} else {
					if (actualTilt < targetTilt) {
						actualTilt = actualTilt + tiltDistance;
					} else {
						actualTilt = actualTilt - tiltDistance;
					}

				}
				lastTiltUpdateTime = now;
				return actualTilt;
			}
			public Double getActualPan() { return null; }
			public Double getPan() { return null; }
			public Double getDim() { return null; }
			public Double getStrobe() { return null; }
		};
	}
}