package com.randomnoun.dmx;

import java.util.Map;

/** Class to trigger audio events, or to trigger light events
 * from audio data
 * 
 * @author knoxg
 */
public abstract class AudioController implements ExceptionContainer {

	/** Create a new AudioController. Any resources requires to operate
	 * the controller should be obtained during construction, using
	 * the properties supplied.
	 * 
	 * @param properties
	 */
	public AudioController(Map properties) {
		
	}
	
	/** Play the supplied file at current volume level. Any existing
	 * audio will be stopped.
	 * 
	 * @param filename
	 */
	public abstract void playAudioFile(String filename);
	
	/** Stops any audio that is currently running */
	public abstract void stopAudio();
	
	/** Close any resources associated with this audioController */
	public abstract void close();
}
