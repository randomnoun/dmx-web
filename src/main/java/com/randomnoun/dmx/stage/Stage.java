package com.randomnoun.dmx.stage;

import java.awt.Color;

import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** This class represents a stage, containing fixtures, (virtual?) universes and shows.
 * 
 * @author knoxg
 */
public class Stage {
	
	private long id;
	private String name;
	private boolean active;
	
	public Stage(long id, String name, boolean active) {
		this.id = id;
		this.name = name;
		this.active = active;
	}

	public long getId() { return id; }
	public String getName() { return name; }
	public boolean isActive() { return active; }
	
}
