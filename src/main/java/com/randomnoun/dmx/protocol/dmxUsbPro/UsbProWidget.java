package com.randomnoun.dmx.protocol.dmxUsbPro;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import org.apache.log4j.Logger;

/** Wrapper around a COM interface to Enttec USB Pro Widget 
 *
 * <p>To use this class, instantiate it with a COM port, and then use the
 * JavaWidgetTranslator class to send and receive messages; e.g.
 * 
 * <pre>
 * JavaWidget javaWidget = new JavaWidget("COM4");
 * JavaWidgetTranslator translator = javaWidget.openPort();
 * ...
 * translator.sendWhatever();
 * ResponseMessage message = translator.getMessage();
 * ...
 * javaWidget.close();
 * </pre>
 * 
 * Presumably the event handling in javax.comm (which I assume is in it's
 * own thread) will cause messages to magically appear on the translator's 
 * message queue. 
 * 
 */
public class UsbProWidget {
	
	static Logger logger = Logger.getLogger(UsbProWidget.class);
	
	String portName;
	boolean portFound = false;
	SerialPort serialPort = null;
	OutputStream outputStream = null;
	InputStream inputStream = null;
	UsbProWidgetTranslator javaWidgetTranslator = null;
	
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
	public UsbProWidget(String portName) {
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
	public UsbProWidgetTranslator openPort() throws PortInUseException, IOException, TooManyListenersException {
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

				    javaWidgetTranslator = new UsbProWidgetTranslator(inputStream, outputStream);
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
	
	/** Closes any streams/resources held by this class.
	 * 
	 * This method will attempt to close the serial port's inputStream, outputStream,
	 * remove any event listeners assigned to the serial port, and close the serial
	 * port itself. 
	 * 
	 * <p>If an exception occurs, then all these steps will still be 
	 * attempted, but the exception thrown will be the first one encountered.
	 *  
	 * @throws IOException
	 */ 
	public void close() throws IOException {
		Exception e = null;
		if (inputStream!=null) {
			try { inputStream.close(); } catch (Exception e2) { e = (e==null ? e2 : e); }  
			inputStream = null; 
		}
		if (outputStream!=null) { 
			try { outputStream.close(); } catch (Exception e2) { e = (e==null ? e2 : e); } 
			outputStream = null; 
		}
		if (serialPort!=null) {
			try { serialPort.removeEventListener(); } catch (Exception e2) { e = (e==null ? e2 : e); } 
			try { serialPort.close(); } catch (Exception e2) { e = (e==null ? e2 : e); } 
			serialPort = null; 
		}
		if (e!=null) { throw (IOException) new IOException("Exception closing JavaWidget").initCause(e); }
	}
	
	public static class JavaWidgetSerialPortEventListener implements SerialPortEventListener {
		/** Object which will translate received bytes into ResponseMessages */
		UsbProWidgetTranslator javaWidgetTranslator;
		
		public JavaWidgetSerialPortEventListener(UsbProWidgetTranslator javaWidgetTranslator) {
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
	


