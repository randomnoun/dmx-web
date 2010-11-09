package com.randomnoun.dmx.protocol.winamp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.AudioController;
import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.ExceptionContainerImpl;

/** An implementation of AudioController which uses Winamp 
 * to generate audio
 * 
 * @author knoxg
 */
public class WinampAudioController extends AudioController 
	implements ExceptionContainer 
{

	Logger logger = Logger.getLogger(WinampAudioController.class);
	ExceptionContainerImpl exceptionContainer;
	
	String host;
	int port;
	int timeout;
	String password;
	String defaultPath;
	
	NGWinAmp winamp;
	
	public WinampAudioController(Map properties) throws IOException {
		super(properties);
		this.host = (String) properties.get("host");
		this.port = Integer.parseInt((String) properties.get("port"));
		this.password = (String) properties.get("password");
		this.timeout = Integer.parseInt((String) properties.get("timeout"));
		this.defaultPath = (String) properties.get("defaultPath");
		if (defaultPath != null && !defaultPath.endsWith("/")) {
			defaultPath += "/";
		}
		exceptionContainer = new ExceptionContainerImpl();
		winamp = new NGWinAmp(host, port, timeout);
		winamp.connect();
		winamp.authenticate(password);
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
		File file = new File(filename);
		if (!file.isAbsolute() && defaultPath!=null) { file = new File(new File(defaultPath), filename); }
		try {
			
			String[] files = winamp.sendGetFiles(NGWinAmp.NGWINAMP_ALL);
			int playlistPosition = -1;
			for (int i=0; i<files.length; i++) {
				if (files[i].equals(filename)) {
					playlistPosition = i; break;
				}
			}
			if (playlistPosition==-1) {
				String newFiles[] = new String[] { filename };
				winamp.sendAddFiles(newFiles);
				files = winamp.sendGetFiles(NGWinAmp.NGWINAMP_ALL);
				for (int i=0; i<files.length; i++) {
					if (files[i].equals(filename)) {
						playlistPosition = i; break;
					}
				}
			} else {
				winamp.sendSwap(playlistPosition, files.length);
				playlistPosition = files.length;
			}
			if (playlistPosition==-1) {
				logger.error("File not added to WinAMP playlist");
				exceptionContainer.addException(new RuntimeException("File not added to WinAMP playlist"));
			} else {
				winamp.sendSetRepeat(false);
				winamp.sendSetPlaylistPosition(playlistPosition);
				winamp.sendSetPosition(0.0);
				winamp.sendPlay();
			}
		} catch (IOException e) {
			logger.error(e);
			exceptionContainer.addException(new RuntimeException("Exception playing audio file", e));
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
		try {
			winamp.disconnect();
		} catch (IOException ioe) {
			logger.error("Exception closing NGWinAmp audioController", ioe);
		}
	}
	
}
