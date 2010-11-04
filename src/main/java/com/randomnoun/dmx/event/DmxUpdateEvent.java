package com.randomnoun.dmx.event;

import com.randomnoun.dmx.Universe;

// Could use spring events here, perhaps 
/** A DMX update event */
public class DmxUpdateEvent {
	
	/** The universe in which this updated occurrred */
	Universe universe;
	
	/** The channel number on which the value changed  */
	int dmxChannel;
	
	/** The new value */
	int value;
	
	public DmxUpdateEvent(Universe universe, int dmxChannel, int value) {
		this.universe = universe;
		this.dmxChannel = dmxChannel;
		this.value = value;
	}
	
	public Universe getUniverse() { return universe; }
	public int getDmxChannel() { return dmxChannel; }
	public int getValue() { return value; }
}
