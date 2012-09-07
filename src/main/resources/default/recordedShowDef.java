package {PACKAGENAME_GOES_HERE};

import java.awt.Color;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.audioController.AudioController;

import com.randomnoun.dmx.show.editor.Command;
import com.randomnoun.dmx.show.editor.Frame;
import com.randomnoun.dmx.show.editor.RecordedShow;

import com.randomnoun.dmx.show.editor.*;

/** A show recorded by {USERNAME_GOES_HERE} at {TIMESTAMP_GOES_HERE}.
 * 
 */
public class {CLASSNAME_GOES_HERE} extends RecordedShow {

    Logger logger = Logger.getLogger({CLASSNAME_GOES_HERE}.class);
  
    Recording recording;
    AudioController audioController;
  
    public void init() {
	    setLength(Long.MAX_VALUE);
	    audioController = controller.getAudioController();
	    recording = getRecording(controller);
    }
  
	// MODIFY WITH CAUTION
	// 
	// IT APPEARS THAT YOU'RE VIEWING THE SOURCE FOR A RECORDED SHOW.
	// 
	// You can edit this file, but any lines between the 
	// "RECORDING DEFINITION START" and "RECORDING DEFINITION END" markers
	// below will be replaced if the show is edited using the 'Get show recording'
	// button from the show definitions maintenance page.
	//
	// You can prevent this from occurring by changing the 'RecordedShow' 
	// superclass to 'Show' at the beginning of this class definition.
	//
    public Recording getRecording(Controller controller) {
    	// *** RECORDING DEFINITION START -- DO NOT REMOVE THIS LINE ***
{CODE_GOES_HERE}
		// *** RECORDING DEFINITION END -- DO NOT REMOVE THIS LINE ***
    }
  
    public void pause() {}
    public void stop() {}
    protected void reset() {
        super.reset();
        logger.debug("reset()");
    }
  
    public void play() {
	    logger.debug("play()");
	    reset();
	    while (!isCancelled()) {
			int frameCount = 0;
			for (Frame f : recording.getFrames()) {
		    	frameCount++;
		    	setLabel("Frame " + frameCount + "/" + recording.getFrames().size());
		        for (Command c : f.getCommands()) {
		            c.run();
		        }
		        if (isCancelled()) { break; }
		        waitFor({DEFAULT_DELAY_GOES_HERE}); // @TODO allow speed to be set somewhere
			}
	    }
    }
    
}  