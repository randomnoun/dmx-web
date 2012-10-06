package com.randomnoun.dmx.audioController.vlcRc;

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

/** An implementation of AudioController which uses an RC (remote control) 
 * socket interface to VLC to generate audio. 
 *
 * <p>You will need to have an instance of VLC running on the target server,
 * with a command line similar to the following: 
 *
 * <pre>
 * the command line
 * </pre>
 *
 * @TODO possibly keep connection open & reconnect if required
 * @TODO possibly use the Audio Bar Graph plugin (http://wiki.videolan.org/Documentation:Play_HowTo/Advanced_Use_of_VLC#Audio_Bar_Graph_over_Video)
 *   to create a VlcRcAudioSource class; see http://svn.tribler.org/vlc/trunk/modules/audio_filter/audiobargraph_a.c 
 *   for protocol
 * 
 * @see http://n0tablog.wordpress.com/2009/02/09/controlling-vlc-via-rc-remote-control-interface-using-a-unix-domain-socket-and-no-programming/
 * @author knoxg
 */
public class VlcAudioController extends AudioController 
	implements ExceptionContainer 
{

	Logger logger = Logger.getLogger(VlcAudioController.class);
	ExceptionContainerImpl exceptionContainer;
	
	String host;
	int port;
	//int timeout;
	//String password;
	String defaultPath;
	boolean connected = false;
	
	public VlcAudioController(Map properties) throws IOException {
		super(properties);
		if (properties==null) { return; } // when called from maintain devices page
		this.host = (String) properties.get("host");
		this.port = Integer.parseInt((String) properties.get("port"));
		//this.password = (String) properties.get("password");
		//this.timeout = Integer.parseInt((String) properties.get("timeout"));
		this.defaultPath = (String) properties.get("defaultPath");
		if (defaultPath != null && !defaultPath.endsWith("/")) {
			defaultPath += "/";
		}
		connected = false;
		exceptionContainer = new ExceptionContainerImpl();
	}
	
	public String getName() { return "VLC (RC interface)"; }
	
	public void open() {
		try {
			Socket client = new Socket(host, port);
	        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	        out.println("stats");
	        String line = in.readLine();
	        logger.info("returned '" + line + "'");
	        
	        out.close();
	    	in.close();
	    	client.close();
	    	connected = true;
        } catch (Exception e) {
        	logger.error("Exception connecting to Winamp audio controller", e);
        	exceptionContainer.addException(new RuntimeException("Exception connecting to VLC audio controller", e));
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
				
				Socket client = new Socket(host, port);
		        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
		        Reader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		        out.println("add " + filename);
		        out.println("volume 100");
		        out.println("play");
		        out.close();
		    	in.close();
		    	client.close();
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
				Socket client = new Socket(host, port);
		        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
		        Reader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		        out.println("stop");
		        out.close();
		    	in.close();
		    	client.close();
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
				Socket client = new Socket(host, port);
		        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
		        Reader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		        out.println("volume " + Math.round(volumePercent * 100));
		        out.close();
		    	in.close();
		    	client.close();
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
		/* nuthin */
	}
	
	

	
}
