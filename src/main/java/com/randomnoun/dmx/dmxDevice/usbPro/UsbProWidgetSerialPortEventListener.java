package com.randomnoun.dmx.dmxDevice.usbPro;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;

/** Class responsible for handling incoming traffic on the COM port on which a USB Pro widget is configured.
 * This class runs on a thread managed by the RXTX framework.
 * 
 * <p>The class uses a {@link UsbProWidgetTranslator} to read and queue data that is received on the COM port.
 * 
 * */ 
public class UsbProWidgetSerialPortEventListener implements SerialPortEventListener {
	/** Object which will translate received bytes into ResponseMessages */
	UsbProWidgetTranslator usbProWidgetTranslator;
	/** Used to remove the event listener if the widget is removed */
	UsbProWidget usbProWidget;
	
	public UsbProWidgetSerialPortEventListener(UsbProWidget usbProWidget, UsbProWidgetTranslator usbProWidgetTranslator) {
		this.usbProWidget = usbProWidget;
		this.usbProWidgetTranslator = usbProWidgetTranslator;
	}
	public void serialEvent(SerialPortEvent spEvent) {
		switch (spEvent.getEventType()) {
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
					usbProWidgetTranslator.readData();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if (e.getMessage().equals("No error in nativeavailable")) {
						// this occurs if widget is unplugged, RXTX stderr also dumps:
						// Error 0x5 at /home/bob/foo/rxtx-devel/build/../src/termios.c(482): Access is denied.
                        // Error 0x5 at /home/bob/foo/rxtx-devel/build/../src/termios.c(2714): Access is denied.
						// stop listening for messages
						usbProWidget.exceptionContainer.addException(new IOException("Error reading from device '" + usbProWidget.getName() + "'", e));
						usbProWidget.notifyRemoved();
						
					}
					e.printStackTrace();
				}
			    
		}
	}
}