package com.randomnoun.dmx.protocol.dmxUsbPro;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

import org.apache.log4j.Logger;

/** Wrapper around a COM interface to Enttec USB Pro Widget 
 *
 * <p>To use this class, instantiate it with a COM port, and then use the
 * JavaWidgetTranslator class to send and receive messages; e.g.
 * 
 * <pre>
 * JavaWidget javaWidget = new JavaWidget("COM4");
 * JavaWidgetTranslator translator = javaWidget.openPort();
 * translator.whatever();
 * ...
 * javaWidget.close();
 * </pre>
 * 
 * Presumably the event handling in javax.comm (which I assume is in it's
 * own thread) will cause messages to magically appear on the translator's queue 
 * 
 */
public class JavaWidget {
	
	static Logger logger = Logger.getLogger(JavaWidget.class);
	
	String portName;
	boolean portFound = false;
	SerialPort serialPort = null;
	OutputStream outputStream = null;
	InputStream inputStream = null;
	JavaWidgetTranslator javaWidgetTranslator = null;
	
	/** Create a new low-level interface to a Enttec USB Pro Widget.
	 * 
	 * <p>On Unix, this will encapsulate a COM port, where port names
	 * should appear similar to /dev/term/a, /dev/term/b, etc...
	 * 
	 * <p>On Windows, this will encapsulate a COM port, where port names
	 * should appear similar to COM1, COM2, etc...
	 * 
	 * @param portName communications port name
	 */
	public JavaWidget(String portName) {
		this.portName = portName;
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
	public JavaWidgetTranslator openPort() throws PortInUseException, IOException, TooManyListenersException {
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

				    javaWidgetTranslator = new JavaWidgetTranslator(inputStream, outputStream);
				    serialPort.addEventListener(new JavaWidgetSerialPortEventListener(javaWidgetTranslator));
				    serialPort.notifyOnDataAvailable(true);
				    serialPort.notifyOnOutputEmpty(true);
			    }
		    }
		} 
		if (!portFound) {
			throw new IllegalArgumentException("Port '" + portName + "' not found");
		}
		return javaWidgetTranslator;
	}
	
	/** Closes any streams/resources held by this class
	 *  
	 * @throws IOException
	 */ 
	public void close() throws IOException {
		
		if (inputStream!=null) { 
			inputStream.close(); 
			inputStream = null; 
		}
		if (outputStream!=null) { 
			outputStream.close(); 
			outputStream = null; 
		}
		if (serialPort!=null) { 
			serialPort.removeEventListener(); 
			serialPort.close(); 
			serialPort = null; 
		}
	}
	
	public static class JavaWidgetSerialPortEventListener implements SerialPortEventListener {
		/** Object which will translate received bytes into ResponseMessages */
		JavaWidgetTranslator javaWidgetTranslator;
		
		public JavaWidgetSerialPortEventListener(JavaWidgetTranslator javaWidgetTranslator) {
			this.javaWidgetTranslator = javaWidgetTranslator;
		}
		public void serialEvent(SerialPortEvent arg0) {
			switch (arg0.getEventType()) {
				case SerialPortEvent.BI:
				case SerialPortEvent.OE:
				case SerialPortEvent.FE:
				case SerialPortEvent.PE:
				case SerialPortEvent.CD:
				case SerialPortEvent.CTS:
				case SerialPortEvent.DSR:
				case SerialPortEvent.RI:
				case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				    break;
		
				case SerialPortEvent.DATA_AVAILABLE:
					try {
						javaWidgetTranslator.readData();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
			}
		}
	}
	
	
}
	


