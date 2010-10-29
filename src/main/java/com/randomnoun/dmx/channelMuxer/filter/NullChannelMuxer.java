package com.randomnoun.dmx.channelMuxer.filter;

import java.awt.Color;

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
	
	public NullChannelMuxer(ChannelMuxer inputMuxer) {
		super(inputMuxer.getFixture());
		this.inputMuxer = inputMuxer;
	}
	
	@Override
	public FixtureOutput getOutput() {
		return inputMuxer.getOutput();
	}
}