package com.randomnoun.dmx.event;

import com.randomnoun.dmx.Universe;

/** This DmxUpdateListener kicks off a thread, which then logs any non-zero DMX values
 * to stdout every 200msec
 *  
 * @author knoxg
 */
public class DmxValueDumper implements UniverseUpdateListener {

	DmxValueDumperThread t = null;
	
	// could get these from the universe, but ... 
	int dmxState[];
	
	// @TODO obvious thread safety problems
	public static class DmxValueDumperThread extends Thread {
		DmxValueDumper dvd;
		boolean done = false;
		long startTime = 0;
		
		public DmxValueDumperThread(DmxValueDumper dvd) {
			this.dvd = dvd;
			this.setName(getName() + "-DmxValueDumper");
		}
		
		public void run() {
			startTime = System.currentTimeMillis();
			while (!done) {
				String output = "DMX values:     ";
				for (int i=1; i<=Universe.MAX_CHANNELS; i++) {
					if (dvd.dmxState[i]!=0) {
						output += "[" + i + ":" + dvd.dmxState[i] + "] ";
					}
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
	
	public DmxValueDumper() {
		dmxState = new int[Universe.MAX_CHANNELS+1];
	}
	
	public void onEvent(DmxUpdateEvent event) {
		dmxState[event.getDmxChannel()] = event.getValue();
	}
	
	public void startThread() {
		if (t!=null) { throw new IllegalStateException("Thread already started"); }
		t = new DmxValueDumperThread(this);
		t.start();
	}
	
	public void stopThread() {
		if (t!=null) { t.done(); }
	}
	

}
