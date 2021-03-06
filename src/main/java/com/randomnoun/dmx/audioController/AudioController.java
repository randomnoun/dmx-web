package com.randomnoun.dmx.audioController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.randomnoun.dmx.ExceptionContainer;

/** Class to trigger audio events.
 * 
 * @author knoxg
 */
public abstract class AudioController implements ExceptionContainer {

	/** Create a new AudioController. 
	 * 
	 * @param properties
	 */
	public AudioController(Map properties) {
		
	}

	/** Open any resources associated with this audioController */
	public abstract void open();
	
	/** Close any resources associated with this audioController */
	public abstract void close();

	
	/** Play the supplied file at 100% volume level. Any existing
	 * audio will be stopped.
	 * 
	 * @param filename
	 */
	public abstract void playAudioFile(String filename);
	
	/** Stops any audio that is currently running */
	public abstract void stopAudio();
	
	/** Sets the audio volume, in percent (0.0 - 100.0) */
	public abstract void setVolume(double volumePercent);

	/** Name returned for UI */
	public abstract String getName();
	
	/** Default properties for this audio controller 
	 *
	 * @return List of PropertyDef objects
	 * 
	 */
	public List getDefaultProperties() {
		return Collections.EMPTY_LIST;
	}
	
}
