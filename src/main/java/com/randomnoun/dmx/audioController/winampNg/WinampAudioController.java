package com.randomnoun.dmx.audioController.winampNg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.common.Text;
import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.PropertyDef;
import com.randomnoun.dmx.audioController.AudioController;

/** An implementation of AudioController which uses WinAMP 
 * to generate audio. The WinAMP application is controlled using the 
 * WinampNG socket interface, which must be installed for this to work.
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
	boolean connected = false;
	
	WinampNG winamp;
	
	public WinampAudioController(Map properties) throws IOException {
		super(properties);
		if (properties!=null) {
			this.host = (String) properties.get("host");
			this.port = Integer.parseInt((String) properties.get("port"));
			this.password = (String) properties.get("password");
			this.timeout = Integer.parseInt((String) properties.get("timeout"));
			this.defaultPath = (String) properties.get("defaultPath");
			if (defaultPath != null && !defaultPath.endsWith("/")) {
				defaultPath += "/";
			}
		}
		connected = false;
		exceptionContainer = new ExceptionContainerImpl();
	}
	
	public String getName() { return "WinAMP (NG interface)"; }
	
	public List getDefaultProperties() {
        List properties = new ArrayList();
        properties.add(new PropertyDef("host", "Host name", "localhost"));
        properties.add(new PropertyDef("port", "Port", "18443"));
        properties.add(new PropertyDef("timeout", "Timeout (msec)", "3000"));
        properties.add(new PropertyDef("password", "Password", "abc123"));
        // @TODO make this more sane, i.e. /home/somewhere/audio for unix 
        // and C:\somewhere windowsy\My Music for windows, say
        properties.add(new PropertyDef("defaultPath", "Default path", "C:\\DCB\\audio"));
        return properties;
    }  
	
	public void open() {
		winamp = new WinampNG(host, port, timeout, true);
		try {
			winamp.connect();
			winamp.authenticate(password);
			connected = true;
		} catch (Exception e) {
			logger.error("Exception connecting to Winamp audio controller", e);
			exceptionContainer.addException(new RuntimeException("Exception connecting to WinAMP audio controller"));			
		}
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
			try {
				File file = new File(filename);
				if (!file.isAbsolute() && defaultPath!=null) { 
					file = new File(new File(defaultPath), filename);
					filename = file.getCanonicalPath();
				}
				filename = Text.replaceString(filename, "\\", "/");
				
				
				String[] files = winamp.sendGetFiles(WinampNG.NGWINAMP_ALL);
				int playlistPosition = -1;
				for (int i=0; i<files.length; i++) {
					if (files[i].equals(filename)) {
						playlistPosition = i; break;
					}
				}
				if (playlistPosition==-1) {
					String newFiles[] = new String[] { filename };
					winamp.sendAddFiles(newFiles);
					files = winamp.sendGetFiles(WinampNG.NGWINAMP_ALL);
					for (int i=0; i<files.length; i++) {
						if (files[i].equals(filename)) {
							playlistPosition = i; break;
						}
					}
				} else {
					// the check-end-of-list here should work but doesn't
					//if (playlistPosition!=files.length-1) {
						
						// we can't play audio at this position, since it
						// will then go on to play everything else in the playlist.
						
						// attempt 1: swap with position at end of list (doesn't work)
						// winamp.sendSwap(playlistPosition, files.length-1); 
						
						// attempt 2: delete and re-add audio file (doesn't work and throws a SocketException)
						/*
						winamp.sendDeletePlaylist(new int[] { playlistPosition });
						String newFiles[] = new String[] { filename };
						winamp.sendAddFiles(newFiles);
						files = winamp.sendGetFiles(NGWinAmp.NGWINAMP_ALL);
						for (int i=0; i<files.length; i++) {
							if (files[i].equals(filename)) {
								playlistPosition = i; break;
							}
						}
						*/
						
						// attempt 3: clear playlist and re-add file
						// (not great, since whoever's on the desk will need to reset the playlist
						// when people start getting their groove on, but does seem to work)
						winamp.sendClearPlaylist();
						String newFiles[] = new String[] { filename };
						winamp.sendAddFiles(newFiles);
						files = winamp.sendGetFiles(WinampNG.NGWINAMP_ALL);
						for (int i=0; i<files.length; i++) {
							if (files[i].equals(filename)) {
								playlistPosition = i; break;
							}
						}
						
					//}
				}
				if (playlistPosition==-1) {
					logger.error("File not added to WinAMP playlist");
					exceptionContainer.addException(new RuntimeException("File not added to WinAMP playlist"));
				} else {
					winamp.sendSetRepeat(false);
					winamp.sendSetPlaylistPosition(playlistPosition);
					winamp.sendSetPosition(0);
					winamp.sendPlay();
					winamp.sendSetVolume(100.0);
				}
			} catch (IOException e) {
				logger.error(e);
				exceptionContainer.addException(new RuntimeException("Exception playing audio file", e));
			}
		}
	}

	@Override
	public void stopAudio() {
		logger.debug("Stopping audio");
		if (connected) {
			try {
				winamp.sendStop();
			} catch (IOException e) {
				logger.error(e);
				exceptionContainer.addException(new RuntimeException("Exception stopping audio file", e));
			}
		}
	}

	@Override
	public void setVolume(double volumePercent) {
		logger.debug("Setting volume");
		if (connected) {
			try {
				winamp.sendSetVolume(volumePercent);
			} catch (IOException e) {
				logger.error(e);
				exceptionContainer.addException(new RuntimeException("Exception stopping audio file", e));
			}
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
		if (connected) {
			try {
				winamp.disconnect();
			} catch (IOException ioe) {
				logger.error("Exception closing WinAMP audioController", ioe);
			}
			connected = false;
		}
	}
	
	

	
}
