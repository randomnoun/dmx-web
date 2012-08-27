package com.randomnoun.dmx.audioController.jmf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.media.CannotRealizeException;
import javax.media.DataSink;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.audioController.AudioController;
import com.randomnoun.dmx.audioController.NullAudioController;

public class JmfAudioController extends AudioController implements
		ExceptionContainer {

	Logger logger = Logger.getLogger(NullAudioController.class);
	ExceptionContainerImpl exceptionContainer;
	Player player = null;
	String defaultPath;

	public JmfAudioController(Map properties) throws IOException {
		super(properties);
		exceptionContainer = new ExceptionContainerImpl();
		this.defaultPath = (String) properties.get("defaultPath");
		if (defaultPath != null && !defaultPath.endsWith("/")) {
			defaultPath += "/";
		}

	}

	// TODO: this may block for a shockingly long time
	/**
	 * The NullAudioController will do nothing when requested to play audio.
	 */
	@Override
	public void playAudioFile(String filename) {
		// close existing player if it exists
		if (player!=null) { player.stop(); }
		try {
			player = Manager.createRealizedPlayer(new MediaLocator("file:///" + defaultPath + filename));
		} catch (CannotRealizeException cre) {
			exceptionContainer.addException(cre);
		} catch (NoPlayerException npe) {
			exceptionContainer.addException(npe);
		} catch (IOException ioe) {
			exceptionContainer.addException(ioe);
		}
		player.start();
	}

	@Override
	public void stopAudio() {
		if (player!=null) { player.stop(); }
		player = null;
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
		// each player is created on demand
	}

	@Override
	public void close() {
		if (player!=null) { player.stop(); }
		player = null;
	}
}
