package com.randomnoun.dmx.show;

import java.awt.Color;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.AudioController;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.scripted.MiniWashFixtureDef12.MiniWashFixtureController;

public class PerformerEntranceShow extends Show {

	Logger logger = Logger.getLogger(PerformerEntranceShow.class);
	
	MiniWashFixtureController leftWash;
	MiniWashFixtureController rightWash;
	AudioController audioController;
	
	public PerformerEntranceShow(Controller controller, Map properties) {
		super(controller, "Performer entrance", 6000, properties);
		sleepMonitor = new Object();
		leftWash = (MiniWashFixtureController) controller.getFixture(0).getFixtureController();
		rightWash = (MiniWashFixtureController) controller.getFixture(1).getFixtureController();
		audioController = controller.getAudioController();
	}
	
	public void pause() {}
	public void stop() {}
	
	protected void reset() {
		super.reset();
		logger.debug("reset()");
		leftWash.setColor(Color.BLACK);
		rightWash.setColor(Color.BLACK);
		leftWash.setMovementSpeed(0);
		rightWash.setMovementSpeed(0);
		leftWash.panTo(90); leftWash.tiltTo(45);
		rightWash.panTo(270); rightWash.tiltTo(45);
		
		// @TODO wait until muxers say that the fixtures
		// are in the right position
	}
	
	public void play() {
		logger.debug("play()");
		reset();
		waitUntil(1000);
		if (isCancelled()) { return; }
		
		logger.debug("play() part 2");
		audioController.playAudioFile("smoothCriminal.mp3");
		Color[] chaseColors = new Color[]{
			Color.GREEN,
			Color.BLUE,
			Color.RED
		};
		for (int i=0; i<10; i++) {
			leftWash.setColor(chaseColors[i % 3]);
			rightWash.setColor(chaseColors[(i+1) % 3]);
			waitUntil(1000+i*500);
			if (isCancelled()) { return; }
		}
		
		logger.debug("play() completed");
	}
	

}	
