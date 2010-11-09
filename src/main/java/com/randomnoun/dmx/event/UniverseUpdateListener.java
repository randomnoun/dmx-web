package com.randomnoun.dmx.event;

/** An interface which is notified whenever the values in a DMX
 * universe change
 * 
 * @author knoxg
 */
public interface UniverseUpdateListener {
	
	/** A notification of a DMX update event */
	public void onEvent(DmxUpdateEvent event);

	/** For UniverseUpdateListeners that propagate events to a 
	 * physical DMX system, start any threads required
	 */
	public void startThread();
	
	/** For UniverseUpdateListeners that propagate events to a 
	 * physical DMX system, stop any threads required
	 */
	public void stopThread();

}
