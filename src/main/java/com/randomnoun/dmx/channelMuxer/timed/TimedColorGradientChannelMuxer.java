package com.randomnoun.dmx.channelMuxer.timed;

import java.awt.Color;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.timeSource.TimeSource;

/** This muxer transitions between a set of colours over time, with either
 * sharp or gradual transitions between colours. 
 *  
 * @author knoxg
 */
public class TimedColorGradientChannelMuxer extends CyclingTimeBasedChannelMuxer {

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
		long now = getCycleOffset();
		
		int cgdIndex = -1;
		for (int i=0; i<colorGradientDefs.length; i++) {
			ColorGradientDef cgd = colorGradientDefs[i];
			if (now>=cgd.startTime && now<=cgd.startTime + cgd.duration) {
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
			};
		}
		
		final ColorGradientDef nextCgd = colorGradientDefs[cgdIndex==colorGradientDefs.length-1 ? 0 : cgdIndex+1];
		final long timeInTransition = now - currentCgd.startTime;
		return new FixtureOutput() {
			public Color getColor() {
				return new Color(
					currentCgd.color.getRed() + (nextCgd.color.getRed()-currentCgd.color.getRed()) * timeInTransition / currentCgd.duration,
					currentCgd.color.getGreen() + (nextCgd.color.getGreen()-currentCgd.color.getGreen()) * timeInTransition / currentCgd.duration,
					currentCgd.color.getBlue() + (nextCgd.color.getBlue()-currentCgd.color.getBlue()) * timeInTransition / currentCgd.duration);
			}
			public long getTime() { return timeSource.getTime(); }
		};
	}

}