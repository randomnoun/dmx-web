package com.randomnoun.dmx;

import java.util.Map;

/** Class to trigger audio events, or to trigger light events
 * from audio data
 * 
 * @author knoxg
 */
public abstract class AudioController {

	public AudioController(Map properties) {
		
	}
	
	/** Play the supplied file at current volume level. Any existing
	 * audio will be stopped.
	 * 
	 * @param filename
	 */
	public abstract void playAudioFile(String filename);
	
}
