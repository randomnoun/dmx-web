package com.randomnoun.dmx.show;

import java.awt.Color;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.AudioController;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.scripted.MiniWashFixtureDef12.MiniWashFixtureController;

/** The blackout show resets all the DMX devices attached to the
 * controller, and turns off any audio
 * 
 * @author knoxg
 */
public class BlackoutShow extends Show {

	Logger logger = Logger.getLogger(BlackoutShow.class);
	
	public BlackoutShow(Controller controller, Map properties) {
		super(controller, "Blackout", 0, properties);
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
		logger.debug("play() completed");
	}
	

}	
