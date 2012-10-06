package com.randomnoun.dmx.audioController.mce6;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.common.Text;
import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.audioController.AudioController;
import com.randomnoun.dmx.audioController.winampNg.WinampNG;

/** An implementation of AudioController which uses the Microsoft
 * Media Centre Edition 6 API. 
 *
 * <pre>
 * the command line
 * </pre>
 *
 * @author knoxg
 */
public class Mce6AudioController extends AudioController 
	implements ExceptionContainer 
{

	Logger logger = Logger.getLogger(Mce6AudioController.class);
	ExceptionContainerImpl exceptionContainer;
	
	String defaultPath;
	boolean connected = false;
	
	public Mce6AudioController(Map properties) throws IOException {
		super(properties);
		if (properties==null) { return; } // when called from maintain devices page

		this.defaultPath = (String) properties.get("defaultPath");
		if (defaultPath != null && !defaultPath.endsWith("/")) {
			defaultPath += "/";
		}
		connected = false;
		exceptionContainer = new ExceptionContainerImpl();
	}
	
	public String getName() { return "Microsoft Media Center APIv6"; }
	
	public void open() {
	}

	// TODO: this may block for a shockingly long time
	/** Adds a file to the end of the winamp playlist, turns off repeat, and
	 * then plays it.
	 * 
	 * If it already exists in the playlist, then it is moved to 
	 * the end of the playlist.
	 */
	@Override
	public void playAudioFile(String filename) {
		logger.debug("Playing audio file '" + filename + "'");
		if (connected) {
		}
	}

	@Override
	public void stopAudio() {
		logger.debug("Stopping audio");
		if (connected) {
		}
	}

	@Override
	public void setVolume(double volumePercent) {
		logger.debug("Setting volume");
		if (connected) {
		}
	}

	
	public List<TimestampedException> getExceptions() {
		return exceptionContainer.getExceptions();
	}

	public void clearExceptions() {
		exceptionContainer.clearExceptions();
	}

	@Override
	public void close() {
		/* nuthin */
	}
	
	

	
}
