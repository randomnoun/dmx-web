package com.randomnoun.dmx.show;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.randomnoun.common.Text;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.audioSource.AudioSource;
import com.randomnoun.dmx.event.BeatListener;

/** An audio source which retains audio beat information for each show.
 * Spectrum data will be retrieved from the underlying audioSource.
 *  
 * @author knoxg
 */
public class ShowAudioSource extends AudioSource implements BeatListener {

	AudioSource audioSource;
	boolean beat = false;
	
	public ShowAudioSource(AudioSource audioSource) {
		super(new Properties());
		this.audioSource = audioSource;
		beat = false;
	}

	public List<TimestampedException> getExceptions() {
		throw new UnsupportedOperationException("Exception data not available");
	}

	public void clearExceptions() {
		throw new UnsupportedOperationException("Exception data not available");
	}

	@Override
	public void open() {
		audioSource.addListener(this);
	}

	@Override
	public void close() {
		audioSource.removeListener(this);
	}
	
	public void setBeat() {
		this.beat = true;
	}

	@Override
	public boolean getBeat() {
		boolean result = beat;
		beat = false;
		return result;
	}

	@Override
	public float[] getBassMidTreble() {
		return audioSource.getBassMidTreble();
	}

	@Override
	public float[][] getSpectrum() {
		return audioSource.getSpectrum();
	}

	@Override
	public String getName() {
		return audioSource.getName();
	}

	
}	
