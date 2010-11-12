package com.randomnoun.dmx.channelMuxer.primitive;

import java.awt.Color;

import org.apache.log4j.Logger;

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
	
	PanPositionChannelDef panChannelDef;
	
	double origPanPosition;  // at time target pan position was set
	double targetPanPosition;
	long   timeTargetPanPositionWasSet;
	
	public PanPositionChannelMuxer(Fixture fixture) {
		super(fixture);
		panChannelDef = null;
		for (ChannelDef cd : getFixtureDef().getChannelDefs()) {
			if (cd instanceof PanPositionChannelDef) {
				PanPositionChannelDef pcd = (PanPositionChannelDef) cd;
				if (panChannelDef!=null) { 
					throw new IllegalStateException("Can't handle >1 pan position channels yet");
				}
				panChannelDef = pcd;
			}
		}
		if (panChannelDef==null) { throw new IllegalStateException("PanPositionChannelMuxer on fixture with no pan position channel"); }
	}
	
	
	public FixtureOutput getOutput() {
		final int panValue = fixture.getDmxChannelValue(panChannelDef.getOffset());
		logger.debug("fixture=" + fixture.getName() + ", panValue=" + panValue);
		return new FixtureOutput() {
			public Color getColor() {
				return null;
			}
			public long getTime() { return fixture.getUniverse().getTime(); }
			public Double getPan() { 
				return panChannelDef.getMinAngle() +  
				(double) (panValue - panChannelDef.getLowDmxValue()) /
				(panChannelDef.getHighDmxValue()-panChannelDef.getLowDmxValue()) *
				(panChannelDef.getMaxAngle() - panChannelDef.getMinAngle());
			}
			public Double getTilt() { return null; }
		};
	}
}