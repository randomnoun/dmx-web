package com.randomnoun.dmx.event;

/** An interface which is notified whenever a beat
 * is generated from an AudioSource 
 * 
 * @author knoxg
 */
public interface BeatListener {
	
	/** A notification of an audio beat */
	public void setBeat();
	
}
