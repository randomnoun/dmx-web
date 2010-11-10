package com.randomnoun.dmx.show;

import java.util.Map;

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
	long startTime;
	Object sleepMonitor;
	Map properties;
	String onCancel;
	String onComplete;
	
	protected Show(Controller controller, String name, long length, Map properties) {
		this.controller = controller;
		this.name = name;
		this.length = length;
		this.cancelled = false;
		this.properties = properties;
		onCancel = (String) properties.get("onCancel");
		onComplete = (String) properties.get("onComplete");
		String nameOverride = (String) properties.get("name");
		if (nameOverride!=null) { name = nameOverride; }
		
		sleepMonitor = new Object();
	}
	
	public long getLength() { return length; }
	public String getName() { return name; }
	
	protected void reset() {
		startTime = System.currentTimeMillis();
	}
	
	public abstract void play();
	public abstract void pause();
	public abstract void stop();
	public void cancel() { 
		cancelled = true; 
		sleepMonitor.notify();
	}
	public boolean isCancelled() { return cancelled; }
	
	
	public void waitUntil(long millisecondsIntoShow) {
		try { 
			long timeout = millisecondsIntoShow-(System.currentTimeMillis() - startTime);
			if (timeout < 0) {
				synchronized (sleepMonitor) {
					sleepMonitor.wait(timeout);
				}
			}
			// Thread.sleep(millisecondsIntoShow-(System.currentTimeMillis() - startTime));
		} catch (InterruptedException ie) {
			
		}
	}
	
	
}	
