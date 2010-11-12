package com.randomnoun.dmx.show.compiled;

import java.awt.Color;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.AudioController;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.fixture.compiled.MiniWashFixtureDef12.MiniWashFixtureController;
import com.randomnoun.dmx.show.Show;

/** Flash the lights and play 'smooth criminal'.
 * 
 * If nothing happens in ten seconds, the volume will fade down to 50% over the next 10 seconds. 
 * If it still isn't cancelled, this show will loop the music after three minutes.
 */
public class PerformerEntranceShow extends Show {

	Logger logger = Logger.getLogger(PerformerEntranceShow.class);
	
	MiniWashFixtureController leftWash;
	MiniWashFixtureController rightWash;
	AudioController audioController;
	
	public PerformerEntranceShow(long id, Controller controller, Map properties) {
		super(id, controller, "Performer entrance", Long.MAX_VALUE, properties);
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
		long songStartTime = getShowTime();
		audioController.playAudioFile("smoothCriminal.mp3");
		
		Color[] chaseColors = new Color[]{
			Color.GREEN,
			Color.BLUE,
			Color.RED
		};
		// 20 steps at half a second per chase step
		int i=0;
		while (getShowTime() < 10 * 1000) {
			i++;
			leftWash.setColor(chaseColors[i % 3]);
			rightWash.setColor(chaseColors[(i+1) % 3]);
			waitFor(500);
			if (isCancelled()) { return; }
		}
		
		// fade music down
		double volume = 100;
		long startFadeTime = getShowTime();
		long stopFadeTime = getShowTime() + 20 * 1000;
		while (getShowTime() < stopFadeTime) {
			i++; 
			// volume = volume - (50 / 20);
			volume = 100 - (getShowTime() - startFadeTime) * 50 / (stopFadeTime-startFadeTime);
			leftWash.setColor(chaseColors[i % 3]);
			rightWash.setColor(chaseColors[(i+1) % 3]);
			audioController.setVolume(volume);
			waitFor(500);
			if (isCancelled()) { return; }
		}
		
		while (true) {
			while (getShowTime() - songStartTime < 180 * 1000) {
				i++;
				leftWash.setColor(chaseColors[i % 3]);
				rightWash.setColor(chaseColors[(i+1) % 3]);
				waitFor(500);
				if (isCancelled()) { return; }
			}
			songStartTime = getShowTime();
			audioController.playAudioFile("smoothCriminal.mp3");
			audioController.setVolume(50.0);
		}
		
	}
	

}	
