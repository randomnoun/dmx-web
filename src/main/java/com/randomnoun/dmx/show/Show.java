package com.randomnoun.dmx.show;

import com.randomnoun.dmx.Controller;

/** either this thing is going to invoke methods on the controller
 * directly, or it's going to return dmxvalues which are picked up
 * by some thread, which then invokes the controller on it's behalf.
 * 
 * Actually, probably both.
 *  
 * @author knoxg
 */
public abstract class Show {

	long length;
	Controller controller;
	String name;
	boolean cancelled;
	
	protected Show(Controller controller, String name, long length) {
		this.controller = controller;
		this.name = name;
		this.length = length;
		this.cancelled = false;
	}
	
	public long getLength() { return length; }
	public String getName() { return name; }
	
	public abstract void play();
	public abstract void pause();
	public abstract void stop();
	public void cancel() { cancelled = true; }
	public boolean isCancelled() { return cancelled; }
	
}	
