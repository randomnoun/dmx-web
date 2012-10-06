package com.randomnoun.dmx.audioController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.PropertyDef;

/** A null audio controller
 * 
 * @author knoxg
 */
public class NullAudioController extends AudioController 
	implements ExceptionContainer 
{

	Logger logger = Logger.getLogger(NullAudioController.class);
	ExceptionContainerImpl exceptionContainer;
	
	public NullAudioController(Map properties) throws IOException {
		super(properties);
		exceptionContainer = new ExceptionContainerImpl();
	}
	
    public String getName() { return "Null controller"; }
	
	/** The NullAudioController will do nothing when requested
	 * to play audio.
	 */
	@Override
	public void playAudioFile(String filename) {
	}

	/** The NullAudioController will do nothing when requested
	 * to stop audio
	 */
	@Override
	public void stopAudio() {
	}

	/** The NullAudioController will do nothing when requested
	 * to set the volume
	 */
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
