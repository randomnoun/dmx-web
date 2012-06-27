package com.randomnoun.dmx.show;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.randomnoun.common.Text;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.audioSource.AudioSource;

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
	
	public enum State { SHOW_STOPPED, SHOW_STOPPED_WITH_EXCEPTION, SHOW_RUNNING };
	
	long id;
	long length;
	Controller controller;
	String name;
	boolean cancelled;
	long startTime;
	Semaphore waitSemaphore; // this'll be fun if >1 Shows run at the same time. Should this go into the ShowConfig object ?
	Properties properties;
	String label = null;
	long onCancelShowId = -1;
	long onCompleteShowId = -1;
	long showGroupId = -1;
	AudioSource audioSource;
	String description;
	
	Exception lastException;
	State state;
	
	
	protected Show(long id, Controller controller, String name, long length, Map properties) {
		this(id, controller, name, length, (Properties) properties);
	}
	
	protected Show(long id, Controller controller, String name, long length, Properties properties) {
		this.id = id;
		this.controller = controller;
		this.name = name;
		this.length = length;
		this.cancelled = false;
		this.lastException = null;
		this.properties = properties;
		this.label = null;
		this.state = State.SHOW_STOPPED;
		waitSemaphore = new Semaphore(0);
	}
	
	public void setName(String name) { this.name = name; }
	public void setOnCancelShowId(long onCancelShowId) { this.onCancelShowId = onCancelShowId; }
	public void setOnCompleteShowId(long onCompleteShowId) { this.onCompleteShowId = onCompleteShowId; }
	public void setShowGroupId(long showGroupId) { this.showGroupId = showGroupId; }
	public void setAudioSource(AudioSource audioSource) { this.audioSource = audioSource; }
	public void setDescription(String description) { this.description = description; }
	// you don't want to do this whilst a show is running. maybe.
	public void setLength(long length) { this.length = length; }
	
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
	
	public long getId() { return id; }
	public long getShowGroupId() { return showGroupId; }
	public long getLength() { return length; }
	public long getOnCancelShowId() { return onCancelShowId; }
	public long getOnCompleteShowId() { return onCompleteShowId; }
	
	public String getName() { return name; }
	public State getState() { return state; }
	public Controller getController() { return controller; }
	public AudioSource getAudioSource() { return audioSource; }

	/** Returns the description for this show. If this method is not overridden, will return
	 * the javadoc of the Show object.
	 * @return a description of the show.
	 */
	public String getDescription() { return description; }
	
	/** Resets the show's startTime, cancellation status and 'last
	 * exception' local variable. Show only be called by the ShowThread 
	 * class, just before the play() method is called.
	 */
	void internalReset() {
		startTime = System.currentTimeMillis();
		cancelled = false;
		lastException = null;
		waitSemaphore.drainPermits();
	}
	
	protected void reset() { 
		
	}

	public void setLabel(String label) { this.label = label; }
	public String getLabel() { return label; }
	
	public abstract void play();
	public abstract void pause();
	public abstract void stop();
	public void cancel() { 
		cancelled = true; 
		waitSemaphore.release(); // @TODO don't allow >1 permits on this semaphore
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
	
	/** Default properties for this show 
	 *
	 * @return List of PropertyDef objects
	 * 
	 */
	public List getDefaultProperties() {
		return Collections.EMPTY_LIST;
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public void waitUntil(long millisecondsIntoShow) {
		// as a last resort, if the play() method doesn't return when a cancellation is
		// requested, we can prevent the thread from waitUntil'ing
		if (cancelled) { return; }
		
		try { 
			long timeout = millisecondsIntoShow-(System.currentTimeMillis() - startTime);
			if (timeout > 0) {
				boolean acquired = waitSemaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
				if (acquired) { 
					// only occurs during cancellation 
					waitSemaphore.release(); 
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
				boolean acquired = waitSemaphore.tryAcquire(milliseconds, TimeUnit.MILLISECONDS);
				if (acquired) { 
					// only occurs during cancellation
					waitSemaphore.release(); 
				}
			}
			// Thread.sleep(millisecondsIntoShow-(System.currentTimeMillis() - startTime));
		} catch (InterruptedException ie) {
			
		}
	}
	
	
	
}	
