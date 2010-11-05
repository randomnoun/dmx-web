package com.randomnoun.dmx.channelMuxer.primitive;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** This muxer sets the fixture color output to a specific color */
public class FixedColorChannelMuxer extends ChannelMuxer {

	Logger logger = Logger.getLogger(FixedColorChannelMuxer.class);
	Color color;
	
	public FixedColorChannelMuxer(Fixture fixture, Color color) {
		super(fixture);
		this.color = color;
	}
	
	public FixtureOutput getOutput() {
		logger.debug("fixture=" + fixture.getName() + ", fixedColorChannelMuxer");
		return new FixtureOutput() {
			public Color getColor() {
				return color;
			}
			public long getTime() { return fixture.getUniverse().getTime(); }
			public Double getPan() { return null; }
			public Double getTilt() { return null; }
		};
	}
}