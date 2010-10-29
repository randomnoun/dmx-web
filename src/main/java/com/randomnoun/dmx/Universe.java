package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.event.DmxUpdateEvent;
import com.randomnoun.dmx.event.DmxUpdateListener;
import com.randomnoun.dmx.timeSource.TimeSource;

/** This object contains all the dmx values of all fixtures on a 
 * single DMX universe (bus).
 *  
 */
public class Universe {

	public static int MAX_CHANNELS=512;
	
	int dmxValues[] = new int[MAX_CHANNELS];
	List<DmxUpdateListener> listeners = new ArrayList<DmxUpdateListener>();
	TimeSource timeSource;
	
	/** Returns the DMX value of one channel in this universe
	 *  
	 * @param offset
	 * 
	 * @return
	 */
	public int getDmxChannelValue(int dmxChannelNumber) {
		return dmxValues[dmxChannelNumber];
	}
	
	/** Sets the DMX value of a channel in this universe
	 * 
	 * @param dmxChannelNumber
	 * @param value
	 * 
	 * @return
	 */
	public void setDmxChannelValue(int dmxChannelNumber, int value) {
		dmxValues[dmxChannelNumber] = value;
		// @TODO fire some events off to any listeners
		// (for display, sending to the outside world, etc)
		for (DmxUpdateListener listener : listeners) {
			listener.onEvent(new DmxUpdateEvent(this, dmxChannelNumber, value));
		}
	}
	
	public void setTimeSource(TimeSource timeSource) {
		this.timeSource = timeSource;
	}
	
	public long getTime() {
		return timeSource.getTime();
	}
	
	
	public void addListener(DmxUpdateListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(DmxUpdateListener listener) {
		listeners.remove(listener);
	}
	
	
}
