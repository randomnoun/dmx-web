package com.randomnoun.dmx.audioSource.winamp;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.audioSource.AudioSource;

public class WinampAudioSource extends AudioSource {

	public static Logger logger = Logger.getLogger(WinampAudioSource.class);
	
	private String host;
	private int port;
    private boolean connected;
    private boolean enableSpectrum;
    
    private WinampPluginAdapter winamp;

	public WinampAudioSource(Map properties) {
		super(properties);
		this.host = (String) properties.get("host");
		this.port = Integer.parseInt((String) properties.get("port"));
		this.enableSpectrum = "true".equals(properties.get("enableSpectrum"));
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
		winamp = new WinampPluginAdapter(this, host, port, 1000, true, enableSpectrum);
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
