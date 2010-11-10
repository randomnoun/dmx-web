package com.randomnoun.dmx.show;

import java.awt.Color;
import java.util.Map;

import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.scripted.MiniWashFixtureDef12.MiniWashFixtureController;

public class PoliceShow extends Show {

	MiniWashFixtureController leftWash;
	MiniWashFixtureController rightWash;
	
	public PoliceShow(Controller controller, Map properties) {
		super(controller, "Police", 5000, properties);
		sleepMonitor = new Object();
		leftWash = (MiniWashFixtureController) controller.getFixture(0).getFixtureController();
		rightWash = (MiniWashFixtureController) controller.getFixture(1).getFixtureController();
		
	}
	
	public void pause() {}
	public void stop() {}
	
	protected void reset() {
		super.reset();
		leftWash.blackOut();
		rightWash.blackOut();
		// @TODO wait until muxers say that the fixtures
		// are in the right position
	}
	
	public void play() {
		reset();
		leftWash.setColor(Color.RED); 
		rightWash.setColor(Color.BLUE);
		leftWash.setMovementSpeed(0);
		rightWash.setMovementSpeed(0);
		leftWash.panTo(leftWash.getFixtureDef().panRange); // ==540
		rightWash.panTo(rightWash.getFixtureDef().panRange); // ==540
		waitUntil(2500); // 2.5 seconds into show
		if (isCancelled()) { return; }
		leftWash.setColor(Color.BLUE);
		rightWash.setColor(Color.RED);
		leftWash.panTo(0);
		rightWash.panTo(0);
		waitUntil(5000); // 5 seconds into show
		if (isCancelled()) { return; }
		leftWash.setColor(Color.BLACK);
		rightWash.setColor(Color.BLACK);
		
	}
	
	
}	
