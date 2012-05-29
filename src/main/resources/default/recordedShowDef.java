package {PACKAGENAME_GOES_HERE};

import java.awt.Color;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.AudioController;
import com.randomnoun.dmx.Controller;

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
  
  public {CLASSNAME_GOES_HERE}(long id, Controller controller, Properties properties) {
    super(id, controller, "{SHOWNAME_GOES_HERE}", Long.MAX_VALUE, properties);
    audioController = controller.getAudioController();
    recording = getRecording(controller);
  }
  
  // MODIFY WITH CAUTION
  // 
  // YOU ARE ABLE TO MODIFY THIS METHOD MANUALLY USING THE SCRIPT EDITOR, BUT
  // BE AWARE THAT ANYTHING BETWEEN THE "RECORDING DEFINITION START" AND
  // "RECORDING DEFINITION END" LINES BELOW WILL BE AUTOMATICALLY REPLACED 
  // IF THIS SHOW IS EDITED USING THE 'GET SHOW RECORDING' BUTTON FROM THE 
  // SHOW DEFINITIONS MAINTENANCE PAGE.
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
      for (Frame f : recording.frames) {
        for (Command c : f.commands) {
          c.run();
        }
        if (isCancelled()) { break; }
        waitFor(200); // @TODO allow speed to be set somewhere
      }
    }
  }
  
}  