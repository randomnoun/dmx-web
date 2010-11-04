package com.randomnoun.dmx.channelMuxer.primitive;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channel.rotation.PanPositionChannelDef;
import com.randomnoun.dmx.channel.rotation.TiltPositionChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** This muxer requires at least one tilt channel definition 
 *
 * >1 channel definition can be used to provide major/minor
 * position control.
 * 
 */
public class TiltPositionChannelMuxer extends ChannelMuxer {

	Logger logger = Logger.getLogger(TiltPositionChannelMuxer.class);
	
	TiltPositionChannelDef tiltChannelDef;
	
	public TiltPositionChannelMuxer(Fixture fixture) {
		super(fixture);
		tiltChannelDef = null;
		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof TiltPositionChannelDef) {
				TiltPositionChannelDef pcd = (TiltPositionChannelDef) cd;
				if (tiltChannelDef!=null) { 
					throw new IllegalStateException("Can't handle >1 tilt position channels yet");
				}
				tiltChannelDef = pcd;
			}
		}
		if (tiltChannelDef==null) { throw new IllegalStateException("TiltPositionChannelMuxer on fixture with no tilt position channel"); }
	}
	
	public FixtureOutput getOutput() {
		final int tiltValue = fixture.getDmxChannelValue(tiltChannelDef.getOffset());
		logger.debug("tiltValue=" + tiltValue);
		return new FixtureOutput() {
			public Color getColor() {
				return null;
			}
			public long getTime() { return -1; }
			public Double getPan() { return null; }
			public Double getTilt() {
				return tiltValue * 
				(tiltValue - tiltChannelDef.getLowDmxValue()) /
				(tiltChannelDef.getHighDmxValue()-tiltChannelDef.getLowDmxValue()) *
				(tiltChannelDef.getMaxAngle() - tiltChannelDef.getMinAngle());
			}
		};
	}
}