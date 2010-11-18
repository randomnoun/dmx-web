package com.randomnoun.dmx.protocol.nullDevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.DmxDevice;
import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.ExceptionContainer.TimestampedException;
import com.randomnoun.dmx.event.DmxUpdateEvent;
import com.randomnoun.dmx.event.UniverseUpdateListener;
import com.randomnoun.dmx.protocol.nullDevice.NullAudioController;

/** Wrapper around an interface to a null DMX Device 
 *
 * 
 */
public class NullDmxDevice extends DmxDevice {
	
	static Logger logger = Logger.getLogger(NullDmxDevice.class);

	ExceptionContainerImpl exceptionContainer;

	public NullDmxDevice(Map properties) {
		super(properties);
		exceptionContainer = new ExceptionContainerImpl();
	}

	@Override
	public void open() {
	}
	
	/** Closes any streams/resources held by this class.
	 * 
	 * @throws IOException
	 */ 
	public void close() {
	}
	

	public List<TimestampedException> getExceptions() {
		return exceptionContainer.getExceptions();
	}

	public void clearExceptions() {
		exceptionContainer.clearExceptions();
	}

	@Override
	public UniverseUpdateListener getUniverseUpdateListener() {
		return new UniverseUpdateListener() {
			public void onEvent(DmxUpdateEvent event) {
			}
			public void startThread() {
			}
			public void stopThread() {
			}
		};
	}

	
}
	


