
import java.awt.Color;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.audioController.AudioController;
import com.randomnoun.dmx.show.Show;

/** This documentation will be displayed as a tooltip in the Show panel when
 * the user hovers their mouse over the button representing this show.
 * 
 */
public class DefaultShow extends Show {

    Logger logger = Logger.getLogger(DefaultShow.class);
  
    // FixtureController demoLight;
  
    public DefaultShow(long id, Controller controller, Properties properties) {
        super(id, controller, "Performer entrance", Long.MAX_VALUE, properties);
        audioController = controller.getAudioController();
    
        // demoLight = controller.getFixtureControllerByName("shiny light");
    }
  
    public void pause() {}
    public void stop() {}
  
    protected void reset() {
		super.reset();
		logger.debug("reset()");
		
		// demoLight.setColor(Color.BLACK);
		// demoLight.setMovementSpeed(0);
		// demoLightWash.panTo(90); demoLight.tiltTo(45);
    }
  
    public void play() {
    
    	logger.debug("play()");
    	reset();
    	
    	/* demo show
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
			demoLight.setColor(chaseColors[i % 3]);
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
			demoLight.setColor(chaseColors[i % 3]);
			audioController.setVolume(volume);
			waitFor(500);
			if (isCancelled()) { return; }
	    }

	    while (true) {
	    	while (getShowTime() - songStartTime < 180 * 1000) {
				i++;
				demoLight.setColor(chaseColors[i % 3]);
				waitFor(500);
				if (isCancelled()) { return; }
		    }
			songStartTime = getShowTime();
			audioController.playAudioFile("smoothCriminal.mp3");
			audioController.setVolume(50.0);
		}
		*/
    }
}  