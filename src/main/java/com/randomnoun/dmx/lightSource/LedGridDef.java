package com.randomnoun.dmx.lightSource;

/* A 2-dimensional grid of RGB LEDs */
public abstract class LedGridDef extends LightSourceDef {
	
	public static class LedDef {
		int x;
		int y;
		int radius;
		LedType led;
	}
	
	public enum LedType { RED, GREEN, BLUE };
	LedDef[] leds;
	
}
