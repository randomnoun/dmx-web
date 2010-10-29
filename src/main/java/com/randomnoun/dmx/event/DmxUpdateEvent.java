package com.randomnoun.dmx.event;

import com.randomnoun.dmx.Universe;

// Could use spring events here, perhaps 

public class DmxUpdateEvent {
	Universe universe;
	int dmxChannel;
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
