package com.randomnoun.dmx.show.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.randomnoun.common.Text;
import com.randomnoun.dmx.AudioSource;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.show.Show;

/** Superclass of all recorded shows
 *  
 * @author knoxg
 */
public abstract class RecordedShow extends Show {

	// @TODO get rid of any shows still using Maps.
	
	protected RecordedShow(long id, Controller controller, String name, long length, Map properties) {
		this(id, controller, name, length, (Properties) properties);
	}
	
	protected RecordedShow(long id, Controller controller, String name, long length, Properties properties) {
		super(id, controller, name, length, properties);
	}

	/** This method is used by the web UI to load a recording for playback and modification */ 
	public abstract Recording getRecording();
	
}	
