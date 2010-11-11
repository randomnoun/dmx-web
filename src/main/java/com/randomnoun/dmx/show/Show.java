package com.randomnoun.dmx.show;

import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.common.Text;
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

	static Logger logger = Logger.getLogger(Show.class);
	
	long length;
	Controller controller;
	String name;
	boolean cancelled;
	long startTime;
	Object sleepMonitor;
	Map properties;
	long onCancelShowId;
	long onCompleteShowId;
	Exception lastException;
	
	protected Show(Controller controller, String name, long length, Map properties) {
		this.controller = controller;
		this.name = name;
		this.length = length;
		this.cancelled = false;
		this.lastException = null;
		this.properties = properties;
		sleepMonitor = new Object();
	}
	
	public void setName(String name) { this.name = name; }
	public void setOnCancelShowId(long onCancelShowId) { this.onCancelShowId = onCancelShowId; }
	public void setOnCompleteShowId(long onCompleteShowId) { this.onCompleteShowId = onCompleteShowId; }
	
	private long parsePropertyLong(Map properties, String key) {
		String value = (String) properties.get(key);
		if (Text.isBlank(value)) { return -1; }
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			logger.warn("Illegal show property '" + key + "': '" + value + "'", nfe);
			return -1;
		}
	}
	
	public long getLength() { return length; }
	public long getOnCancelShowId() { return onCancelShowId; }
	public long getOnCompleteShowId() { return onCompleteShowId; }
	public String getName() { return name; }
	
	/** Resets the show's startTime, cancellation status and 'last
	 * exception' local variable. Show only be called by the ShowThread 
	 * class, just before the play() method is called.
	 */
	void internalReset() {
		startTime = System.currentTimeMillis();
		cancelled = false;
		lastException = null;
	}
	
	protected void reset() { 
		
	}
	
	public abstract void play();
	public abstract void pause();
	public abstract void stop();
	public void cancel() { 
		cancelled = true; 
		try {
			sleepMonitor.notify();
		} catch (IllegalMonitorStateException imse) {
			// don't really care if the monitor wasn't being held.
		}
	}
	public boolean isCancelled() { return cancelled; }
	public void setLastException(Exception e) {
		lastException = e;
	}
	public Exception getLastException() { return lastException; }
	
	/** Returns the number of msec since show started */
	public long getShowTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	public void waitUntil(long millisecondsIntoShow) {
		// as a last resort, if the play() method doesn't return when a cancellation is
		// requested, we can prevent the thread from waitUntil'ing
		if (cancelled) { return; }
		
		try { 
			long timeout = millisecondsIntoShow-(System.currentTimeMillis() - startTime);
			if (timeout > 0) {
				synchronized (sleepMonitor) {
					sleepMonitor.wait(timeout);
				}
			}
			// Thread.sleep(millisecondsIntoShow-(System.currentTimeMillis() - startTime));
		} catch (InterruptedException ie) {
			
		}
	}

	public void waitFor(long milliseconds) {
		// as a last resort, if the play() method doesn't return when a cancellation is
		// requested, we can prevent the thread from waitFor'ing
		if (cancelled) { return; }
		
		try { 
			if (milliseconds > 0) {
				synchronized (sleepMonitor) {
					sleepMonitor.wait(milliseconds);
				}
			}
			// Thread.sleep(millisecondsIntoShow-(System.currentTimeMillis() - startTime));
		} catch (InterruptedException ie) {
			
		}
	}
	
	
	
}	
