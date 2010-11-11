package com.randomnoun.dmx.show;

import java.awt.Color;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.AudioController;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureController;
import com.randomnoun.dmx.compiled.MiniWashFixtureDef12.MiniWashFixtureController;

/** The spotlight show throws all lights to 100% white, moves them
 * to point to the front of the stage, and cuts the audio.
 * 
 * @author knoxg
 */
public class SpotlightShow extends Show {

	Logger logger = Logger.getLogger(SpotlightShow.class);
	
	MiniWashFixtureController leftWash;
	MiniWashFixtureController rightWash;
	FixtureController leftPar64;
	FixtureController rightPar64;
	AudioController audioController;
	
	public SpotlightShow(long id, Controller controller, Map properties) {
		super(id, controller, "Spotlight", 1000, properties);
		leftWash = (MiniWashFixtureController) controller.getFixtureControllerByName("leftWash");
		rightWash = (MiniWashFixtureController) controller.getFixtureControllerByName("rightWash");
		leftPar64 = controller.getFixtureControllerByNameNoEx("leftPar");
		rightPar64 = controller.getFixtureControllerByNameNoEx("rightPar");
	}
	
	public void pause() {}
	public void stop() {}
	
	protected void reset() {
		super.reset();
		logger.debug("reset()");
		controller.blackOut();
		controller.getAudioController().stopAudio();
	}
	
	public void play() {
		logger.debug("play()");
		reset();
		leftWash.panTo(0); leftWash.tiltTo(45);
		rightWash.panTo(0); rightWash.tiltTo(45);
		waitUntil(1000);

		if (leftPar64!=null) { leftPar64.setColor(Color.WHITE); }
		if (rightPar64!=null) { rightPar64.setColor(Color.WHITE); }
		leftWash.setColor(Color.WHITE);
		rightWash.setColor(Color.WHITE);
		logger.debug("play() completed");
	}
	

}	
