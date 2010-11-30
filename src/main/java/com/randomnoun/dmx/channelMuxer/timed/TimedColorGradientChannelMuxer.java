package com.randomnoun.dmx.channelMuxer.timed;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureOutput;
import com.randomnoun.dmx.timeSource.TimeSource;

/** This muxer transitions between a set of colours over time, with either
 * sharp or gradual transitions between colours. 
 *  
 * <p>When this muxer performs a fade transition, it is just doing a linear
 * fade between 2 RGB values, rather than anything in, say, the HSB space.
 *  
 * @author knoxg
 */
public class TimedColorGradientChannelMuxer extends CyclingTimeBasedChannelMuxer {

	Logger logger = Logger.getLogger(TimedColorGradientChannelMuxer.class);
	
	private long cycleTime;
	private ColorGradientDef[] colorGradientDefs;
	public static enum ColorGradientTransition { SHARP, FADE }
	
	public static class ColorGradientDef {
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
	
	@Override
	public long getCycleTime() {
		return cycleTime;
	}
	
	public TimedColorGradientChannelMuxer(Fixture fixture, TimeSource timeSource, ColorGradientDef[] colorGradientDefs) {
		super(fixture, timeSource);
		this.colorGradientDefs = colorGradientDefs;
		
		if (colorGradientDefs==null) { throw new NullPointerException("Null colorGradientDefs"); }
		if (colorGradientDefs.length==0) { throw new IllegalArgumentException("Null colorGradientDefs"); }
		
		long nextStartTime = -1;
		for (int i = 0; i < colorGradientDefs.length; i++) {
			ColorGradientDef cgd = colorGradientDefs[i]; 
			if (i==0) { 
				if (cgd.startTime!=0) { 
					throw new IllegalArgumentException("First colorGradientDef should have a startTime of 0");
				}
			} else {
				if (cgd.startTime!=nextStartTime) { 
					throw new IllegalArgumentException("Start time of colorGradientDef[" + i + "] should be set to the start time of colorGradientDef[" + (i-1) + "] plus the duration of colorGradientDef[" + (i-1) + "]");
				}
			}
			nextStartTime = cgd.startTime + cgd.duration;
		}
		cycleTime = nextStartTime;
	}
	
	
	public FixtureOutput getOutput() {
		long cycleOffset = getCycleOffset();
		
		int cgdIndex = -1;
		for (int i=0; i<colorGradientDefs.length; i++) {
			ColorGradientDef cgd = colorGradientDefs[i];
			if (cycleOffset>=cgd.startTime && cycleOffset<=cgd.startTime + cgd.duration) {
				cgdIndex = i; break;
			}
		}
		if (cgdIndex==-1) {
			throw new IllegalStateException("Could not determine current ColorGradientDef");
		}
		final ColorGradientDef currentCgd = colorGradientDefs[cgdIndex];
		if (currentCgd.transitionToNextColor==ColorGradientTransition.SHARP) {
			return new FixtureOutput() {
				public Color getColor() { return currentCgd.color; }
				public long getTime() { return timeSource.getTime(); }
				public Double getPan() { return null; }
				public Double getTilt() { return null; }
				public Double getDim() { return (double) 1; }
				public Double getStrobe() { return null; }
			};
		}
		
		final ColorGradientDef nextCgd = colorGradientDefs[cgdIndex==colorGradientDefs.length-1 ? 0 : cgdIndex+1];
		final long timeInTransition = cycleOffset - currentCgd.startTime;
		final double fade = ((float) timeInTransition / (float) currentCgd.duration); 
		
		logger.debug("fixture=" + fixture.getName() + ", mux time=" + timeSource.getTime() + ", cycleOffset=" + cycleOffset + ", currentCgd=[" + currentCgd + "], nextCgd=[" + nextCgd + "], timeInTransition=" + timeInTransition + ", fade=" + fade);
		
		return new FixtureOutput() {
			public Color getColor() {
				return new Color(
					(int) (currentCgd.color.getRed() + (nextCgd.color.getRed()-currentCgd.color.getRed()) * fade),
					(int) (currentCgd.color.getGreen() + (nextCgd.color.getGreen()-currentCgd.color.getGreen()) * fade),
					(int) (currentCgd.color.getBlue() + (nextCgd.color.getBlue()-currentCgd.color.getBlue()) * fade));
			}
			public long getTime() { return timeSource.getTime(); }
			public Double getPan() { return null; }
			public Double getTilt() { return null; }
			public Double getDim() { return (double) 1; }
			public Double getStrobe() { return null; }
		};
	}

}