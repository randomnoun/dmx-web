package com.randomnoun.dmx.audioController.xuggle;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.ExceptionContainer.TimestampedException;
import com.randomnoun.dmx.audioController.AudioController;
import com.randomnoun.dmx.audioController.NullAudioController;

public class XuggleAudioController extends AudioController implements
		ExceptionContainer {

	Logger logger = Logger.getLogger(NullAudioController.class);
	ExceptionContainerImpl exceptionContainer;

	public XuggleAudioController(Map properties) throws IOException {
		super(properties);
		exceptionContainer = new ExceptionContainerImpl();
	}

	// TODO: this may block for a shockingly long time
	/**
	 * The NullAudioController will do nothing when requested to play audio.
	 */
	@Override
	public void playAudioFile(String filename) {
	}

	@Override
	public void stopAudio() {
	}

	@Override
	public void setVolume(double volumePercent) {
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

}