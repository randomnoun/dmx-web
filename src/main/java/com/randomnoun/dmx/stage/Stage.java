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
	private String fixPanelBackgroundImage;
	
	public Stage(long id, String name, String fixPanelBackgroundImage, boolean active) {
		this.id = id;
		this.name = name;
		this.fixPanelBackgroundImage = fixPanelBackgroundImage; 
		this.active = active;
	}

	public long getId() { return id; }
	public String getName() { return name; }
	public String getFixPanelBackgroundImage() { return fixPanelBackgroundImage; }
	public boolean isActive() { return active; }
	
	
}
