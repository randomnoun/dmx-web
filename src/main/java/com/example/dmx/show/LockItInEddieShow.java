package com.example.dmx.show;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.example.dmx.fixture.MiniWashFixtureController;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.audioController.AudioController;
import com.randomnoun.dmx.show.Show;
import com.randomnoun.dmx.show.ShowUtils;
import com.randomnoun.dmx.PropertyDef;
  
/** Tilt the washes to 45 degrees, play the 'Who wants to be a millionaire' musical sting,
 * and then swing them down to point ahead.
 *
 * @author knoxg
 */
public class LockItInEddieShow extends Show {

  Logger logger = Logger.getLogger(LockItInEddieShow.class);
  
  MiniWashFixtureController leftWash;
  MiniWashFixtureController rightWash;
  AudioController audioController;
  
  public LockItInEddieShow(long id, Controller controller, Properties properties) {
    super(id, controller, "Lock it in, eddie", 5000L, properties);
    leftWash = (MiniWashFixtureController) controller.getFixtureControllerByName("leftWash1");
    rightWash = (MiniWashFixtureController) controller.getFixtureControllerByName("rightWash1");
    audioController = controller.getAudioController();
  }
  
  public List getDefaultProperties() {
    List properties = new ArrayList();
    properties.add(new PropertyDef("color", "Initial color", "BLUE"));
    return properties;
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
    Color lightBlue;
    if (getProperty("color")==null) {
      lightBlue = new Color(200, 200, 255);
    } else {
      lightBlue = ShowUtils.toColor(getProperty("color"));
    }
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
