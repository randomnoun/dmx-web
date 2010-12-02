package com.randomnoun.dmx.channelMuxer.timed;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureOutput;
import com.randomnoun.dmx.timeSource.TimeSource;

/** This muxer cycles a fixture between a fixed set of 
 * pan/tilt positions over time. 
 *  
 * @author knoxg
 */
public class TimedMotionChannelMuxer extends CyclingTimeBasedChannelMuxer {

	Logger logger = Logger.getLogger(TimedMotionChannelMuxer.class);
	
	private long cycleTime;
	private TimedMotionDef[] timedMotionDefs;
	private int dmxChannel; // universe-based
	
	@Override
	public long getCycleTime() {
		return cycleTime;
	}
	
	// cycle offsets start from when the DMX value was originally set to 
	// initiate this movement macro 
	public long getCycleOffset() {
		long getCycleTime = getCycleTime();
		if (getCycleTime==0) { return 0; }
		return (timeSource.getTime()-
			fixture.getUniverse().getDmxLastChangedTime(dmxChannel)) % getCycleTime; 
	}
	
	
	public TimedMotionChannelMuxer(
		Fixture fixture, 
		ChannelDef channelDef, 
		TimedMotionDef[] timedMotionDefs) 
	{
		super(fixture, fixture.getUniverse().getTimeSource());
		
		this.dmxChannel = channelDef.getOffset() + fixture.getStartDmxChannel();
		this.timedMotionDefs = timedMotionDefs;
		
		if (timedMotionDefs==null) { throw new NullPointerException("Null timedMotionDefs"); }
		if (timedMotionDefs.length==0) { throw new IllegalArgumentException("Null timedMotionDefs"); }
		
		long nextStartTime = -1;
		for (int i = 0; i < timedMotionDefs.length; i++) {
			TimedMotionDef tmd = timedMotionDefs[i]; 
			if (i==0) { 
				if (tmd.startTime!=0) { 
					throw new IllegalArgumentException("First timedMotionDef should have a startTime of 0");
				}
			} else {
				if (tmd.startTime!=nextStartTime) { 
					throw new IllegalArgumentException("Start time of timedMotionDef[" + i + "] should be set to the start time of timedMotionDef[" + (i-1) + "] plus the duration of timedMotionDef[" + (i-1) + "]");
				}
			}
			nextStartTime = tmd.startTime + tmd.duration;
		}
		cycleTime = nextStartTime;
	}
	
	
	public FixtureOutput getOutput() {
		long cycleOffset = getCycleOffset();
		
		int tmdIndex = -1;
		for (int i=0; i<timedMotionDefs.length; i++) {
			TimedMotionDef tmd = timedMotionDefs[i];
			if (cycleOffset>=tmd.startTime && cycleOffset<=tmd.startTime + tmd.duration) {
				tmdIndex = i; break;
			}
		}
		if (tmdIndex==-1) {
			throw new IllegalStateException("Could not determine current TimedMotionDef");
		}
		final TimedMotionDef currentTmd = timedMotionDefs[tmdIndex];
		final TimedMotionDef nextTmd = timedMotionDefs[tmdIndex==timedMotionDefs.length-1 ? 0 : tmdIndex+1];
		final long timeInTransition = cycleOffset - currentTmd.startTime;
		final double ratio = ((float) timeInTransition / (float) currentTmd.duration); 
		
		logger.debug("fixture=" + fixture.getName() + ", mux time=" + timeSource.getTime() + ", cycleOffset=" + cycleOffset + ", currentTmd=[" + currentTmd + "], nextTmd=[" + nextTmd + "], timeInTransition=" + timeInTransition + ", ratio=" + ratio);
		
		return new FixtureOutput() {
			public Color getColor() { return null; }
			public long getTime() { return timeSource.getTime(); }
			public Double getPan() { 
				return nextTmd.pan;
			}
			public Double getTilt() { 
				return nextTmd.tilt;
			}
			public Double getActualPan() {
				return currentTmd.pan + (nextTmd.pan-currentTmd.pan) * ratio;
			}
			public Double getActualTilt() { 
				return currentTmd.tilt + (nextTmd.tilt-currentTmd.tilt) * ratio;
			}
			public Double getDim() { return (double) 1; }
			public Double getStrobe() { return null; }
		};
	}

}