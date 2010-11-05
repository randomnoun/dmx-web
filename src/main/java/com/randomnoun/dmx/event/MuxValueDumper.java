package com.randomnoun.dmx.event;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;

/** This MuxUpdateListener kicks off a thread, which then logs fixture RGB values
 * to stdout every 200msec
 *  
 * @author knoxg
 */
public class MuxValueDumper implements UniverseUpdateListener {

	MuxValueDumperThread t = null;
	
	// probably need to replicate another Universe object here, but ... 
	//int dmxState[];
	
	List<Fixture> fixtures = new ArrayList<Fixture>();
	
	// @TODO obvious thread safety problems
	public static class MuxValueDumperThread extends Thread {
		MuxValueDumper mvd;
		boolean done = false;
		long startTime = 0;
		
		public MuxValueDumperThread(MuxValueDumper mvd) {
			this.mvd = mvd;
		}
		
		public void run() {
			startTime = System.currentTimeMillis();
			while (!done) {
				String output = "Fixture output: ";
				for (Fixture f : mvd.fixtures) {
					ChannelMuxer mux = f.getChannelMuxer();
					FixtureOutput muxOutput = mux.getOutput();
					
					Color c = muxOutput.getColor();
					
					output += "[" + f.getName() + ":r=" + c.getRed()+ ", g=" + c.getGreen() + ", b=" + c.getBlue() + "] ";
					
				}
				System.out.println((System.currentTimeMillis()-startTime) + ": " + output);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
				
			}
		}
		public void done() {
			done = true;
		}
	}
	
	public MuxValueDumper() {
		//dmxState = new int[Universe.MAX_CHANNELS+1];
	}
	
	public void addFixture(Fixture fixture) {
		fixtures.add(fixture);
	}
	
	public void onEvent(DmxUpdateEvent event) {
		//dmxState[event.getDmxChannel()] = event.getValue();
	}
	
	public void startThread() {
		if (t!=null) { throw new IllegalStateException("Thread already started"); }
		t = new MuxValueDumperThread(this);
		t.start();
	}
	
	public void stopThread() {
		if (t!=null) { t.done(); }
	}
	

}
