package com.randomnoun.dmx.channelMuxer.timed;

public class TimedMotionDef {
	
	/** the time this colour is displayed, in msec from cycle start */
	long startTime;
	long duration;
	double pan;
	double tilt;
	
	public TimedMotionDef(long startTime, long duration, double pan, double tilt) {
		this.startTime = startTime;
		this.duration = duration;
		this.pan = pan;
		this.tilt = tilt;
	}
	
	public String toString() {
		return "startTime=" + startTime + ", pan=" + pan + ", tilt=" + tilt + 
		  ", duration=" + duration; 
	}
}