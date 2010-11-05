package com.randomnoun.dmx.protocol.dmxUsbPro;

import java.io.IOException;
import java.util.TooManyListenersException;

import gnu.io.PortInUseException;

/** Holy shit it works */
public class JavaWidgetTest {

	public static void main(String args[]) throws PortInUseException, IOException, TooManyListenersException {
		//JavaWidgetTranslator widget = new JavaWidgetTranslator(4);
		
		JavaWidget widget = new JavaWidget("COM4");
		JavaWidgetTranslator translator = widget.openPort();
		byte[] dmxData = new byte[513]; // includes startcode
		
		System.out.println("Setting dmx channels to 0");
		for (int i=0; i<512; i++) { dmxData[i] = 0; }
		translator.sendOutputOnlySendDMXPacketRequest(dmxData);
		
		try { Thread.sleep(1000); } catch (InterruptedException ie) { }
		
		System.out.println("Setting dmx channel 21 to 255");
		dmxData[21] = (byte) 255;
		translator.sendOutputOnlySendDMXPacketRequest(dmxData);
		try { Thread.sleep(1000); } catch (InterruptedException ie) { }
		
		System.out.println("Setting dmx channel 21 to 0");
		dmxData[21] = (byte) 0;
		translator.sendOutputOnlySendDMXPacketRequest(dmxData);
		try { Thread.sleep(1000); } catch (InterruptedException ie) { }
		
		System.out.println("Finishing up...");
		widget.close();
		
		
	}
	
}
