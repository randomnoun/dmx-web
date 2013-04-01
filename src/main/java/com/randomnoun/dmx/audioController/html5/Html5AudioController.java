package com.randomnoun.dmx.audioController.html5;

import java.util.List;
import java.util.Map;

import com.randomnoun.dmx.audioController.AudioController;

/** An audioController which uses a HTML5 &lt;audio/&gt; element embedded on a webpage somewhere
 * to generate audio
 * 
 * I may also want to check out http://html5doctor.com/html5-audio-the-state-of-play/ later
 * 
 * @author knoxg
 */
public class Html5AudioController extends AudioController {

	public Html5AudioController(Map properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<TimestampedException> getExceptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearExceptions() {
		// TODO Auto-generated method stub

	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void playAudioFile(String filename) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopAudio() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVolume(double volumePercent) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
