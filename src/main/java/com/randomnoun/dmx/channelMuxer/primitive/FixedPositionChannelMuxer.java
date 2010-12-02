package com.randomnoun.dmx.channelMuxer.primitive;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** This muxer sets the fixture pan/tilt output to a specific value */
public class FixedPositionChannelMuxer extends ChannelMuxer {

	Logger logger = Logger.getLogger(FixedColorChannelMuxer.class);
	
	double pan;
	double tilt;
	
	public FixedPositionChannelMuxer(Fixture fixture, double pan, double tilt) {
		super(fixture);
		this.pan = pan;
		this.tilt = tilt;
	}
	
	public FixtureOutput getOutput() {
		logger.debug("fixture=" + fixture.getName() + ", fixedPositionChannelMuxer");
		return new FixtureOutput() {
			public Color getColor() { return null; }
			public long getTime() { return fixture.getUniverse().getTime(); }
			public Double getPan() { return pan; }
			public Double getTilt() { return tilt; }
			public Double getActualPan() { return pan; }
			public Double getActualTilt() { return tilt; }
			public Double getDim() { return (double) 1; }
			public Double getStrobe() { return null; }
		};
	}
}