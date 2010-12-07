package com.example.dmx.show;

import java.awt.Color;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.AudioSource;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.show.Show;

/** dim the lights based on the bass output
 * 
 * @author knoxg
 */
public class AudioFaderShow extends Show {

	Logger logger = Logger.getLogger(AudioFaderShow.class);

	Fixture fixture1;
	Fixture fixture2;
	FixtureController fix1;
	FixtureController fix2;
	
	public AudioFaderShow(long id, Controller controller, Properties properties) {
		super(id, controller, "Audio Fader", Long.MAX_VALUE, properties);
		
		fixture1 = controller.getFixtureByName("leftWash"); fix1 = fixture1.getFixtureController();
		fixture2 = controller.getFixtureByName("rightWash"); fix2 = fixture2.getFixtureController();
	}
	
	public void pause() {}
	public void stop() {}
	
	protected void reset() {
		super.reset();
		logger.debug("reset()");
	}
	
	public void play() {
		logger.debug("play()");
		AudioSource audioSource = getAudioSource();
		while (!isCancelled()) {
			int fade = (int) Math.min(audioSource.getBassMidTreble()[0]*102, 255);
			setLabel("level " + fade);
			logger.debug("loop fade " + fade);
			Color color = new Color(fade, fade, fade);
			fix1.setColor(color);
			fix2.setColor(color);
			waitFor(30); 
		}
		logger.debug("play() completed");
	}
}	
