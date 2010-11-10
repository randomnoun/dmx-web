package com.randomnoun.dmx.show;

import java.awt.Color;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.AudioController;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.compiled.MiniWashFixtureDef12.MiniWashFixtureController;

public class LockItInEddieShow extends Show {

	Logger logger = Logger.getLogger(LockItInEddieShow.class);
	
	MiniWashFixtureController leftWash;
	MiniWashFixtureController rightWash;
	AudioController audioController;
	
	public LockItInEddieShow(Controller controller, Map properties) {
		super(controller, "Lock it in, eddie", 5000, properties);
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
		audioController.playAudioFile("wwm-question.wav");
		Color lightBlue = new Color(200, 200, 255);
		leftWash.setColor(lightBlue); 
		rightWash.setColor(lightBlue);
		leftWash.setMovementSpeed(100);
		rightWash.setMovementSpeed(100);
		
		leftWash.panTo(0); leftWash.tiltTo(0);
		rightWash.panTo(360); rightWash.tiltTo(0);
		waitUntil(5000); // 5 seconds into show
		
		logger.debug("play() completed");
	}
	

}	
