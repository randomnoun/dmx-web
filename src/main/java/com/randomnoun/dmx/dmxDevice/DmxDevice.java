package com.randomnoun.dmx.dmxDevice;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.event.UniverseUpdateListener;

/** Abstract class to encapsulate DMX devices; (ie widgets or network-attached 
 * devices that can control DMX universes).
 * 
 * @author knoxg
 */
public abstract class DmxDevice implements ExceptionContainer {

	/** Create a new connection to a DmxDevice. 
	 * 
	 * @param properties device-specific properties.
	 */
	public DmxDevice(Map properties) {
		
	}
	
	/** Obtain any resources required to operate the device 
	 */
	public abstract void open();

	/** Returns a UniverseUpdateListener object which will propagate
	 * changes to that universe to the DMX device.
	 */
	public abstract UniverseUpdateListener getUniverseUpdateListener();
		
	/** Close any resources associated with this audioController */
	public abstract void close();
	
	/** Name returned for UI */
	public abstract String getName();
	
	/** Default properties for this device 
	 *
	 * @return List of PropertyDef objects
	 * 
	 */
	public List getDefaultProperties() {
		return Collections.EMPTY_LIST;
	}
}
