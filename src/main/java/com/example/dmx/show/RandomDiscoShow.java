package com.example.dmx.show;

import java.awt.Color;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.show.Show;

/** 2 seconds of a random disco show.
 * 
 * @author knoxg
 */
public class RandomDiscoShow extends Show {

	Logger logger = Logger.getLogger(RandomDiscoShow.class);

	/*
	static class TimelineStep {
		int cueType;    // DELAY, TIMESTAMP, FIXTURE_POS
		long cueValue;  // for DELAY, TIMESTAMP = msec
		Fixture fix;    // fixture in this step
		
	}
	
	static class TimelineShow {
		public TimelineShow(TimelineSteps[] steps) {
			this.steps = steps;
		}
	}
	*/
	
	Fixture fixture1;
	Fixture fixture2;
	FixtureController fix1;
	FixtureController fix2;
	
	public RandomDiscoShow(long id, Controller controller, Properties properties) {
		super(id, controller, "Random Disco", 2000L, properties);
		
		fixture1 = controller.getFixtureByName("leftWash1"); fix1 = fixture1.getFixtureController();
		fixture2 = controller.getFixtureByName("rightWash1"); fix2 = fixture2.getFixtureController();
		
		/*
		TimelineShow show0 = new TimelineShow(
			new TimelineSteps[]{
				new TimelineStep(
			}
		*/
	}
	
	public void pause() {}
	public void stop() {}
	
	protected void reset() {
		super.reset();
		logger.debug("reset()");
		// will be called continuously, don't stop the music
		// getController().blackOut();
		// getController().getAudioController().stopAudio();
	}
	
	public void play() {
		logger.debug("play()");
		int showNumber = (int) Math.floor((Math.random()*5));
		setLabel("Disco " + showNumber);
		switch (showNumber) {
			case 0: 
				fix1.setColor(Color.GREEN); fix2.setColor(Color.GREEN);
				waitUntil(2000);
				break;
			case 1:
				fix1.setColor(Color.PINK); fix2.setColor(Color.PINK);
				waitUntil(2000);
				break;
			case 2:
				fix1.setColor(Color.WHITE); fix2.setColor(Color.WHITE);
				waitUntil(2000);
				break;
			case 3:
				fix1.setColor(Color.YELLOW); fix2.setColor(Color.YELLOW);
				waitUntil(2000);
				break;
			case 4:
				fix1.setColor(Color.BLUE); fix2.setColor(Color.BLUE);
				waitUntil(2000);
				break;
		}
		logger.debug("play() completed");
	}
	

}	
