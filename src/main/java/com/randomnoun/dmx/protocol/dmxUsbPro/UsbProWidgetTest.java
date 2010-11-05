package com.randomnoun.dmx.protocol.dmxUsbPro;

import java.io.IOException;
import java.util.TooManyListenersException;

import gnu.io.PortInUseException;

/** Holy shit it works */
public class UsbProWidgetTest {

	public static void main(String args[]) throws PortInUseException, IOException, TooManyListenersException {
		//JavaWidgetTranslator widget = new JavaWidgetTranslator(4);
		
		UsbProWidget widget = new UsbProWidget("COM4");
		UsbProWidgetTranslator translator = widget.openPort();
		byte[] dmxData = new byte[512];
		
		System.out.println("Setting dmx channels to 0");
		for (int i=0; i<512; i++) { dmxData[i] = 0; }
		translator.sendOutputOnlySendDMXPacketRequest((byte) 0, dmxData);
		
		try { Thread.sleep(1000); } catch (InterruptedException ie) { }
		
		System.out.println("Setting dmx channel 21 to 255");
		dmxData[20] = (byte) 255;
		translator.sendOutputOnlySendDMXPacketRequest((byte) 0, dmxData);
		try { Thread.sleep(1000); } catch (InterruptedException ie) { }
		
		System.out.println("Setting dmx channel 21 to 0");
		dmxData[20] = (byte) 0;
		translator.sendOutputOnlySendDMXPacketRequest((byte) 0, dmxData);
		try { Thread.sleep(1000); } catch (InterruptedException ie) { }
		
		System.out.println("Finishing up...");
		widget.close();
		
		
	}
	
}
