package com.randomnoun.dmx.dmxDevice.usbPro;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.PropertyDef;
import com.randomnoun.dmx.ExceptionContainer.TimestampedException;
import com.randomnoun.dmx.audioController.NullAudioController;
import com.randomnoun.dmx.dmxDevice.DmxDevice;
import com.randomnoun.dmx.event.UniverseUpdateListener;

/** Wrapper around an RXTX interface to an Enttec USB Pro Widget. 
 *
 * <p>To use this class, instantiate it with a serial COM port, and then use the
 * UsbProWidgetTranslator class to send and receive messages; e.g.
 * 
 * <pre>
 * Properties initProperties = new Properties();
 * initProperties.put("portName", "COM4");
 * UsbProWidget usbProWidget = new UsbProWidget(initProperties);
 * UsbProWidgetTranslator translator = usbProWidget.openPort();
 * ...
 * translator.sendWhatever();
 * ResponseMessage message = translator.getMessage();
 * ...
 * usbProWidget.close();
 * </pre>
 * 
 * Presumably the event handling in javax.comm (which I assume is in it's
 * own thread) will cause messages to magically appear on the translator's 
 * message queue. 
 * 
 */
public class UsbProWidget extends DmxDevice {
	
	static Logger logger = Logger.getLogger(UsbProWidget.class);

	ExceptionContainerImpl exceptionContainer;

	String portName;
	boolean portFound = false;
	boolean connected = false;
	SerialPort serialPort = null;
	OutputStream outputStream = null;
	InputStream inputStream = null;
	UsbProWidgetTranslator usbProTranslator = null;
	UsbProWidgetUniverseUpdateListener upwuul = null;
	
	/** Create a new low-level interface to a Enttec USB Pro Widget.
	 * 
	 * <p>The properties supplied should include the key 'portName'.
	 * 
	 * <p>On Unix, this will encapsulate a COM port, where port names
	 * should appear similar to /dev/term/a, /dev/term/b, etc...
	 * 
	 * <p>On Windows, this will encapsulate a COM port, where port names
	 * should appear similar to COM1, COM2, etc...
	 * 
	 * @param properties set of properties; containing the key 'portName'
	 */
	public UsbProWidget(Map properties) {
		super(properties);
		if (properties==null) { return; } // when called from maintain devices page
		this.portName = (String) properties.get("portName");
		exceptionContainer = new ExceptionContainerImpl();
		connected = false;
	}
	
	public String getName() { return "Enttec USB Pro"; }
	
	public List getDefaultProperties() {
        List properties = new ArrayList();
        properties.add(new PropertyDef("portName", "COM port", "COM1"));
        return properties;
    }  
	
	public void open() {
		try {
			usbProTranslator = openPort();
			connected = true;
		} catch (java.lang.UnsatisfiedLinkError ule) {
			IOException ioe = new IOException("Could not open device '" + getName() + "'", ule);
			logger.error("Could not open device '" + getName() + "', port '" + portName + "; java.library.path=" + System.getProperty("java.library.path"), ule);
			exceptionContainer.addException(ioe);
		} catch (Exception e) {
			IOException ioe = new IOException("Could not open device '" + getName() + "'", e);
			logger.error("Could not open device '" + getName () + "', port '" + portName + "'", e);
			exceptionContainer.addException(ioe);
		}
	}

	/** Attempts to open the port, and if successful, creates a JavaWidgetTranslator
	 * object and assigns the input from this port to it.
	 * 
	 * If successful, returns the JavaWidgetTranslator interface, otherwise
	 * throws an exception.
	 * 
	 * @throws PortInUseException
	 * @throws IOException
	 * @throws TooManyListenersException
	 */
	private UsbProWidgetTranslator openPort() throws PortInUseException, IOException, TooManyListenersException {
		logger.info("UsbProWidgetTranslater.openPort() with portName '" + portName + "'");
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
		    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(portName)) {
				    logger.debug("Found port: " + portName);
				    portFound = true;
				    serialPort = (SerialPort) portId.open(this.getClass().getName(), 2000);
				    inputStream = serialPort.getInputStream();
				    outputStream = serialPort.getOutputStream();
				    // unnecessary since this is a virtual COM port
					//serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, 
					//	       SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				    usbProTranslator = new UsbProWidgetTranslator(inputStream, outputStream);
				    serialPort.addEventListener(new UsbProWidgetSerialPortEventListener(this, usbProTranslator));
				    serialPort.notifyOnDataAvailable(true);
				    serialPort.notifyOnOutputEmpty(true);
			    }
		    }
		} 
		if (!portFound) {
			throw new IllegalArgumentException("Port '" + portName + "' not found");
		}
		return usbProTranslator;
	}
	
	/** Returns an object which can send/receive
	 * FTDI messages to this device 
	 */
	public UsbProWidgetTranslator getUsbProWidgetTranslator() {
		return usbProTranslator;
	}

	public void notifyRemoved() {
		logger.info("USB Widget removed; closing universe updater and serial port event listener");
		upwuul.stopThread();
	    close();
		
	}
	
	/** Closes any streams/resources held by this class.
	 * 
	 * This method will attempt to close the serial port's inputStream, outputStream,
	 * remove any event listeners assigned to the serial port, and close the serial
	 * port itself. 
	 * 
	 * <p>If an exception occurs, then all these steps will still be 
	 * attempted; any exceptions thrown will be retrievable via the 
	 * ExceptionContainer interface.
	 *  
	 * @throws IOException
	 */ 
	public void close() {
		if (inputStream!=null) {
			try { inputStream.close(); } catch (Exception e2) { logger.error(e2); exceptionContainer.addException(e2); }  
			inputStream = null; 
		}
		if (outputStream!=null) { 
			try { outputStream.close(); } catch (Exception e2) { logger.error(e2); exceptionContainer.addException(e2); } 
			outputStream = null; 
		}
		if (serialPort!=null) {
			try { serialPort.removeEventListener(); } 
			catch (NullPointerException npe) { /* already removed */ } 
			catch (Exception e2) { logger.error(e2); exceptionContainer.addException(e2); } 
			try { serialPort.close(); } catch (Exception e2) { logger.error(e2); exceptionContainer.addException(e2); } 
			serialPort = null; 
		}
		connected = false;
	}
	
	public List<TimestampedException> getExceptions() {
		return exceptionContainer.getExceptions();
	}

	public void clearExceptions() {
		exceptionContainer.clearExceptions();
	}

	@Override
	public synchronized UniverseUpdateListener getUniverseUpdateListener() {
		if (upwuul==null) {
			upwuul=new UsbProWidgetUniverseUpdateListener(this);
		}
		return upwuul;
		// return new UsbProWidgetUniverseUpdateListener(this);
	}
	
    

	
}
	


