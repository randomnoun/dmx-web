package com.randomnoun.dmx.show.compiled;

import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.show.Show;

/** The blackout show resets all the DMX devices attached to the
 * controller, and turns off any audio
 * 
 * @author knoxg
 */
public class BlackoutShow extends Show {

	Logger logger = Logger.getLogger(BlackoutShow.class);
	
	public BlackoutShow(long id, Controller controller, Map properties) {
		super(id, controller, "Blackout", 0L, properties);
	}
	
	public void pause() {}
	public void stop() {}
	
	protected void reset() {
		super.reset();
		logger.debug("reset()");
		getController().blackOut();
		getController().getAudioController().stopAudio();
	}
	
	public void play() {
		logger.debug("play()");
		reset();
		logger.debug("play() completed");
	}
	

}	
