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

	public static int MAX_CHANNELS=512;
	
	// the 0th element of this array is DMX channel one.
	private int dmxValues[] = new int[MAX_CHANNELS];
	private List<UniverseUpdateListener> listeners = new ArrayList<UniverseUpdateListener>();
	private TimeSource timeSource;
	
	/** Returns the DMX value of one channel in this universe
	 *  
	 * @param offset
	 * 
	 * @return
	 */
	public int getDmxChannelValue(int dmxChannelNumber) {
		if (dmxChannelNumber < 1 || dmxChannelNumber > 512) {
			throw new IllegalArgumentException("dmxChannelNumber must be between 1 and 512");
		}
		return dmxValues[dmxChannelNumber-1];
	}
	
	/** Sets the DMX value of a channel in this universe
	 * 
	 * @param dmxChannelNumber
	 * @param value
	 * 
	 * @return
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
	
	public void setTimeSource(TimeSource timeSource) {
		this.timeSource = timeSource;
	}
	
	public TimeSource getTimeSource() { 
		return timeSource; 
	}
	
	public long getTime() {
		return timeSource.getTime();
	}
	
	
	public void addListener(UniverseUpdateListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(UniverseUpdateListener listener) {
		listeners.remove(listener);
	}
	
	public void stopListeners() {
		for (UniverseUpdateListener listener : listeners) {
			listener.stopThread();
		}
	}
	
	public void removeListeners() {
		listeners.clear();
	}
	// @TODO remove and stop all listeners method
	
}
