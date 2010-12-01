package com.randomnoun.dmx.channelMuxer.timed;

import java.awt.Color;

public class ColorGradientDef {
	/** the time this colour is displayed, in msec from cycle start */
	long startTime;
	long duration; 
	Color color;
	/** last ColorGradientDef will transition to first ColorGradientDef */ 
	ColorGradientTransition transitionToNextColor;
	
	public ColorGradientDef(long startTime, Color color, long duration, ColorGradientTransition transitionToNextColor) {
		this.startTime = startTime;
		this.color = color;
		this.duration = duration;
		this.transitionToNextColor = transitionToNextColor;
	}
	
	public String toString() {
		return "startTime=" + startTime + ", color=[r=" + color.getRed() + ",g=" + 
			color.getGreen() + ",b=" + color.getBlue() + "], duration=" + duration + 
			", transitionToNextColor=" + transitionToNextColor.toString();
	}
}