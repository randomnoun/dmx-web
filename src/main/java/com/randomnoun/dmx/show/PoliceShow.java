package com.randomnoun.dmx.show;

import java.awt.Color;

import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.scripted.MiniWashFixtureDef12.MiniWashFixtureController;

public class PoliceShow extends Show {

	MiniWashFixtureController leftWash;
	MiniWashFixtureController rightWash;
	long startTime;
	Object sleepMonitor;
	
	public PoliceShow(Controller controller) {
		super(controller, "Police", 5000);
		sleepMonitor = new Object();
		leftWash = (MiniWashFixtureController) controller.getFixture(0).getFixtureController();
		rightWash = (MiniWashFixtureController) controller.getFixture(1).getFixtureController();
		
	}
	
	public long getLength() { return length; }
	public void pause() {}
	public void stop() {}
	
	private void reset() {
		startTime = System.currentTimeMillis();
		leftWash.blackOut();
		rightWash.blackOut();
		// @TODO wait until muxers say that the fixtures
		// are in the right position
	}
	
	public void play() {
		reset();
		leftWash.setColor(Color.RED); 
		rightWash.setColor(Color.BLUE);
		leftWash.setMovementSpeed(255);
		rightWash.setMovementSpeed(255);
		leftWash.panTo(leftWash.getFixtureDef().panRange); // ==540
		rightWash.panTo(rightWash.getFixtureDef().panRange); // ==540
		waitUntil(2500); // 2.5 seconds into show
		if (isCancelled()) { return; }
		leftWash.setColor(Color.BLUE);
		rightWash.setColor(Color.RED);
		leftWash.panTo(0);
		rightWash.panTo(255);
		waitUntil(5000); // 5 seconds into show
		if (isCancelled()) { return; }
		leftWash.setColor(Color.BLACK);
		rightWash.setColor(Color.BLACK);
		
	}
	
	public void waitUntil(long millisecondsIntoShow) {
		try { 
			synchronized (sleepMonitor) {
				sleepMonitor.wait(millisecondsIntoShow-(System.currentTimeMillis() - startTime));
			}
			// Thread.sleep(millisecondsIntoShow-(System.currentTimeMillis() - startTime));
		} catch (InterruptedException ie) {
			
		}
	}
	
}	
