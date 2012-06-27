package com.randomnoun.dmx.audioSource;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.audioController.winampNg.WinampNG;

public class NullAudioSource extends AudioSource {

	public static Logger logger = Logger.getLogger(NullAudioSource.class);
	
    private ExceptionContainerImpl exceptionContainer;
    float bassMidTreble[];
    float spectrum[][];
    
	public NullAudioSource(Map properties) {
		super(properties);
		exceptionContainer = new ExceptionContainerImpl();
		bassMidTreble = new float[3];
		spectrum = new float[2][256];
	}

	public List<TimestampedException> getExceptions() {
		return exceptionContainer.getExceptions();
	}

	public void clearExceptions() {
		exceptionContainer.clearExceptions();
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
	}

	@Override
	public boolean getBeat() {
		return false;
	}

	@Override
	public float[] getBassMidTreble() {
		return bassMidTreble;
	}

	@Override
	public float[][] getSpectrum() {
		return spectrum;
	}

}
