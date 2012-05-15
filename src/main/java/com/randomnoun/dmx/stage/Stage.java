package com.randomnoun.dmx.stage;

import java.awt.Color;

import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** This class represents a stage, containing fixtures, (virtual?) universes and shows.
 * 
 * @author knoxg
 */
public class Stage {
	
	private String name;
	private boolean active;
	
	public Stage(String name, boolean active) {
		this.name = name;
		this.active = active;
	}
	
}
