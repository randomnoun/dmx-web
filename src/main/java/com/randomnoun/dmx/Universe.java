package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.event.DmxUpdateEvent;
import com.randomnoun.dmx.event.UniverseUpdateListener;
import com.randomnoun.dmx.timeSource.TimeSource;

/** This object contains all the dmx values of all fixtures on a 
 * single DMX universe (bus).
 *  
 */
public class Universe {

	/** The maximum number of channels. Although woe betide you
	 * if you go over 255.
	 */
	public static int MAX_CHANNELS=512;
	
	// the 0th element of this array is DMX channel one.
	private int dmxValues[] = new int[MAX_CHANNELS];
	private List<UniverseUpdateListener> listeners = new ArrayList<UniverseUpdateListener>();
	private TimeSource timeSource;
	
	/** Returns the DMX value of one channel in this universe.
	 *  
	 * @param offset DMX channel number (1-based)
	 * 
	 * @return DMX value
	 */
	public int getDmxChannelValue(int dmxChannelNumber) {
		if (dmxChannelNumber < 1 || dmxChannelNumber > 512) {
			throw new IllegalArgumentException("dmxChannelNumber must be between 1 and 512");
		}
		return dmxValues[dmxChannelNumber-1];
	}
	
	/** Sets the DMX value of a channel in this universe.
	 * 
	 * @param dmxChannelNumber DMX channel number (1-based)
	 * @param value The new DMX value
	 * 
	 */
	public void setDmxChannelValue(int dmxChannelNumber, int value) {
		if (dmxChannelNumber < 1 || dmxChannelNumber > 512) {
			throw new IllegalArgumentException("dmxChannelNumber must be between 1 and 512");
		}
		dmxValues[dmxChannelNumber-1] = value;
		// @TODO fire some events off to any listeners
		// (for display, sending to the outside world, etc)
		for (UniverseUpdateListener listener : listeners) {
			listener.onEvent(new DmxUpdateEvent(this, dmxChannelNumber, value));
		}
	}
	
	//public int[] getAllDmxChannelValues() {
	//	return dmxValues; // @TODO clone this
	//}
	
	/** Sets the timeSource used in this Universe (timed effects
	 * and macros of Fixtures in this Universe 
	 * should reference this timeSource).
	 * 
	 * @param timeSource the new timeSource
	 */
	public void setTimeSource(TimeSource timeSource) {
		this.timeSource = timeSource;
	}

	/** Returns the timeSource used in this Universe.
	 * 
	 * @return The current timeSource.
	 */
	public TimeSource getTimeSource() { 
		return timeSource; 
	}

	/** Returns the time in this timeSource. Equivalent to
	 * calling <code>getTimeSource().getTime()</code>
	 * 
	 * @return the current time, in msec.
	 */
	public long getTime() {
		return timeSource.getTime();
	}
	

	/** Adds an event listener to this Universe. Any updates
	 * to DMX values in this Universe will be sent to the
	 * supplied listener.
	 * 
	 * @param listener An object interested in receiving 
	 *   DmxUpdateEvents.
	 *   
	 * @see #stopListeners()  
	 * @see #removeListener(UniverseUpdateListener)
	 * @see #removeListeners()
	 */
	public void addListener(UniverseUpdateListener listener) {
		listeners.add(listener);
	}
	
	/** Removes an event listener from this Universe. 
	 * 
	 * @param listener Listener to remove
	 * 
	 * @see #stopListeners()
	 */
	public void removeListener(UniverseUpdateListener listener) {
		listeners.remove(listener);
	}
	
	/** Stops all listener threads */
	public void stopListeners() {
		for (UniverseUpdateListener listener : listeners) {
			listener.stopThread();
		}
	}
	
	/** Removes all listeners from this Universe. */
	public void removeListeners() {
		listeners.clear();
	}
	
}
