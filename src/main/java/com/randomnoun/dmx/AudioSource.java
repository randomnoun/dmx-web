package com.randomnoun.dmx;

import java.util.Map;

/** Class to generate events from audio data
 * 
 * @author knoxg
 */
public abstract class AudioSource implements ExceptionContainer {

	/** Create a new AudioController. 
	 * 
	 * @param properties
	 */
	public AudioSource(Map properties) {
		
	}

	/** Open any resources associated with this audioController */
	public abstract void open();
	
	/** Close any resources associated with this audioController */
	public abstract void close();


	/** Returns true if currently on beat, false otherwise */
	public abstract boolean getBeat();
	
	/** Returns an averaged bass, mid and treble spectrum value 
	 * (with some damping). Vales are returned in a float array
	 * (index 0=bass, 1=middle, 2=treble). The
	 * values of the float should have a long-term average of 1.
	 * 
	 * @return a float array, as described above
	 */
	public abstract float[] getBassMidTreble();
	
	/** Returns spectrum data for the currently playing audio.
	 * Values are returned in a float array with the 
	 * first index being the channel (0=left, 1=right) and the
	 * second index being the frequency sample number (0..255),
	 * corresponding to 0Hz-11025Hz range.  
	 * 
	 * @return a float array, as described above
	 */
	public abstract float[][] getSpectrum();
	

}
