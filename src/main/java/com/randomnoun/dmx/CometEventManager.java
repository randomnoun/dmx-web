package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randomnoun.dmx.event.DmxUpdateEvent;
import com.randomnoun.dmx.event.UniverseUpdateListener;

/** this class receives notifications from other parts of the
 * system, which is used to broadcast application state 
 * to HTTP clients.
 * 
 * Complete state can be requested, or state changes since
 * a particular point in time. If the number of state changes
 * exceeds the size of the complete current state, then just
 * the current state is sent to the client.
 *  
 * @author knoxg
 */
public class CometEventManager /*implements UniverseUpdateListener*/ {

	// alternatively, I could just keep the entire DMX universe
	// data (all 512 bytes of it) through every transition in the
	// last 20 seconds
	
	// and all the show start/stop data
	
	// which seems like a more sensible option.
	
	
	/*
	public enum EventMask { SHOW_EVENT, DMX_EVENT }
	
	// events
	public static class CometEvent {
		long timestamp;
	}
	Map<EventMask, List<CometEvent>> eventQueues;
	Map<EventMask, Object> eventMonitors;  // semaphores ? latches ? 
	
	// current state
	
	public CometEventManager() {
		eventQueues = new HashMap<EventMask, List<CometEvent>>();
		eventQueues.put(EventMask.SHOW_EVENT, new ArrayList<CometEvent>());
		eventQueues.put(EventMask.DMX_EVENT, new ArrayList<CometEvent>());
		eventMonitors.put(EventMask.SHOW_EVENT, new Object());
		eventMonitors.put(EventMask.DMX_EVENT, new Object());
	}
	public void startShowEvent(long showId) {
		eventQueues.get(EventMask.SHOW_EVENT).add(new CometEvent)
		
	}
	public void stopShowEvent(long showId) {
		
	}
	public void onEvent(DmxUpdateEvent event) {
		// TODO Auto-generated method stub
		
	}
	public void startThread() {
		// no effect; required by UniverseUpdateListener interface
	}
	public void stopThread() {
		// no effect; required by UniverseUpdateListener interface
	}
	*/
	
	
}
