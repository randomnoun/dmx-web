package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.randomnoun.dmx.event.BeatListener;

/** Class to generate events from audio data. 
 * 
 * <p>Other classes that are interested in being notified of beats
 * within the music can register with this class.
 * 
 * @see com.randomnoun.dmx.show.ShowAudioSource
 * 
 * @author knoxg
 */
public abstract class AudioSource implements ExceptionContainer {

	private List<BeatListener> listeners = new ArrayList<BeatListener>();
	
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


	/** Returns true if a beat has occurred since the last time
	 * this method was called, false otherwise */
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
	

	public void addListener(BeatListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(BeatListener listener) {
		listeners.remove(listener);
	}
	
	public void removeListeners() {
		listeners.clear();
	}
	
	/** Informs all BeatListeners that a beat has occurred */
	public void setBeat() {
		for (BeatListener listener : listeners) {
			listener.setBeat();
		}
	}
	
}
