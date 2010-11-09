package com.randomnoun.dmx.show;

import java.awt.Color;

import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.scripted.MiniWashFixtureDef12.MiniWashFixtureController;

public class LockItInEddieShow extends Show {

	MiniWashFixtureController leftWash;
	MiniWashFixtureController rightWash;
	
	public LockItInEddieShow(Controller controller) {
		super(controller, "Lock it in, eddie", 5000);
		sleepMonitor = new Object();
		leftWash = (MiniWashFixtureController) controller.getFixture(0).getFixtureController();
		rightWash = (MiniWashFixtureController) controller.getFixture(1).getFixtureController();
		
	}
	
	public void pause() {}
	public void stop() {}
	
	protected void reset() {
		super.reset();
		leftWash.setColor(Color.BLACK);
		rightWash.setColor(Color.BLACK);
		leftWash.setMovementSpeed(0);
		rightWash.setMovementSpeed(0);
		leftWash.panTo(45); leftWash.tiltTo(90);
		rightWash.panTo(45); leftWash.tiltTo(270);
		
		// @TODO wait until muxers say that the fixtures
		// are in the right position
	}
	
	public void play() {
		reset();
		waitUntil(1000);
		if (isCancelled()) { return; }
		
		Color lightBlue = new Color(200, 200, 255);
		leftWash.setColor(lightBlue); 
		rightWash.setColor(lightBlue);
		leftWash.setMovementSpeed(100);
		rightWash.setMovementSpeed(100);
		
		leftWash.panTo(0); leftWash.tiltTo(0);
		rightWash.panTo(360); rightWash.tiltTo(0);
		waitUntil(5000); // 5 seconds into show
	}
	

}	
