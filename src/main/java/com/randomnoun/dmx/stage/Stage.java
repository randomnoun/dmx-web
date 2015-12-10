package com.randomnoun.dmx.stage;

/** This class represents a stage, containing fixtures, (virtual?) universes and shows.
 * 
 * <p>All the world is one of these. [citation needed]
 * 
 * @author knoxg
 */
public class Stage {
	
	private long id;
	private String name;
	private boolean active;
	private String fixPanelBackgroundImage; // fixture panel background image
	
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
