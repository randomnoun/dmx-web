package com.randomnoun.dmx.channelMuxer.filter;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** A muxer which performs no changes on another muxer's input
 * 
 * @author knoxg
 */
public class NullChannelMuxer extends ChannelMuxer {
	ChannelMuxer inputMuxer;
	Logger logger = Logger.getLogger(NullChannelMuxer.class);
	
	public NullChannelMuxer(ChannelMuxer inputMuxer) {
		super(inputMuxer.getFixture());
		this.inputMuxer = inputMuxer;
	}
	
	@Override
	public FixtureOutput getOutput() {
		logger.debug("fixture=" + fixture.getName() + ", mux input " + inputMuxer.getOutput());
		return inputMuxer.getOutput();
	}
}