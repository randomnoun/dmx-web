package com.randomnoun.dmx.protocol.dmxWinamp;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.AudioSource;

public class WinampAudioSource extends AudioSource {

	public static Logger logger = Logger.getLogger(WinampAudioSource.class);
	
	private String host;
	private int port;
    private boolean connected;
    
    private DmxWebWinAmp winamp;

	public WinampAudioSource(Map properties) {
		super(properties);
		this.host = (String) properties.get("host");
		this.port = Integer.parseInt((String) properties.get("port"));
		connected = false;
		// 

	}

	public List<TimestampedException> getExceptions() {
		return winamp.getExceptions();
	}

	public void clearExceptions() {
		winamp.clearExceptions();
	}

	@Override
	public void open() {
		winamp = new DmxWebWinAmp(host, port, 1000, true);
		winamp.connect();
		connected = true;
	}

	@Override
	public void close() {
		winamp.disconnect();
		connected = false;
	}

	@Override
	public boolean getBeat() {
		return winamp.getBeat();
	}

	@Override
	public float[] getBassMidTreble() {
		return winamp.getBassMidTreble();
	}

	@Override
	public float[][] getSpectrum() {
		return winamp.getSpectrum();
	}

}
