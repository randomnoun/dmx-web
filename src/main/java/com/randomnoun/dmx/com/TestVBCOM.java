package com.randomnoun.dmx.com;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;
import com.jacobgen.dmx._OpenDMXCom;
import com.jacobgen.dmx._USBDMXProCom;

// should be using enttec usb pro interface, not opendmx
// so that's interesting. 

public class TestVBCOM {


	/*
	public static byte toByte(int i) {
		return (byte) i & 0xff;
	}
	*/
	
	public void testOpenDmxCom() {
		ActiveXComponent axc;
		_OpenDMXCom openDmxCom;

		axc = new ActiveXComponent("Randomnoun.DMX.OpenDMXCom");
		openDmxCom = new _OpenDMXCom(axc);
		
		try {
			openDmxCom.initialize();
			openDmxCom.init_All(); // automatically find devices and start threads for them
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int enabledDevice = -1;
			
			for (int i=0; i<4; i++) {
				String errorString = openDmxCom.getErrorString(i);
				System.out.println("Error " + i + ": " + errorString);
				if (errorString.equals("SENDING DMX")) { enabledDevice = i; }
			}
			
			byte[] universe = new byte[512];
			if (enabledDevice!=-1) { 
				System.out.println("Setting DMX values to 0");
				for (int i=0; i<512; i++) { universe[i] = 0; }
				SafeArray safeArray = new SafeArray(Variant.VariantByte, 512);
				safeArray.fromByteArray(universe);
				openDmxCom.setDMXValues(enabledDevice, safeArray);
				
				try { Thread.sleep(1000); } catch (InterruptedException e) { }
				System.out.println("Setting DMX channel 11 and 12 to 255");
				universe[11] = (byte) 255;
				universe[12] = (byte) 255;
				safeArray.fromByteArray(universe);
				openDmxCom.setDMXValues(enabledDevice, safeArray);
				
				try { Thread.sleep(1000); } catch (InterruptedException e) { }
				System.out.println("Setting DMX channel 11 and 12 to 0");
				universe[11] = (byte) 0;
				universe[12] = (byte) 0;
				safeArray.fromByteArray(universe);
				openDmxCom.setDMXValues(enabledDevice, safeArray);
				
				
			}
			
			
		} finally {
			if (openDmxCom != null) {
				System.out.println("Closing COM object");

				// openDmxCom.stop_Threads();
				openDmxCom.done_All();
			}
		}
	}
	

	public void testUSBDMXProCom() {
		ActiveXComponent axc;
		_USBDMXProCom usbDMXPro;

		axc = new ActiveXComponent("Randomnoun.DMX.USBDMXProCom");
		usbDMXPro = new _USBDMXProCom(axc);
		
		try {
			System.out.println("DLL version: " + usbDMXPro.getDllVersion());
			usbDMXPro.searchPorts();
			for (int i=1; i<=16; i++) {
				String errorString = usbDMXPro.getErrorString(i);
				System.out.println("Error " + i + ": " + errorString);
				// if (errorString.equals("SENDING DMX")) { enabledDevice = i; }
			}
			
			usbDMXPro.init(4);
			// usbDMXPro.init_All(); // automatically find devices and start threads for them
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int enabledDevice = -1;
			
			for (int i=4; i<=4; i++) {
				String errorString = usbDMXPro.getErrorString(i);
				System.out.println("Error " + i + ": " + errorString);
				// if (errorString.equals("SENDING DMX")) { enabledDevice = i; }
				enabledDevice = 4;
			}
			
			byte[] universe = new byte[512];
			if (enabledDevice!=-1) { 
				System.out.println("Setting DMX values to 0");
				for (int i=0; i<512; i++) { universe[i] = 0; }
				SafeArray safeArray = new SafeArray(Variant.VariantByte, 512);
				safeArray.fromByteArray(universe);
				usbDMXPro.setDMXValues(enabledDevice, safeArray);
				usbDMXPro.send(enabledDevice);
				
				try { Thread.sleep(1000); } catch (InterruptedException e) { }
				System.out.println("Setting DMX channel 21 to 255");
				universe[21] = (byte) 255;
				safeArray.fromByteArray(universe);
				usbDMXPro.setDMXValues(enabledDevice, safeArray);
				usbDMXPro.send(enabledDevice);
				
				try { Thread.sleep(1000); } catch (InterruptedException e) { }
				System.out.println("Setting DMX channel 21 to 0");
				universe[21] = (byte) 0;
				safeArray.fromByteArray(universe);
				usbDMXPro.setDMXValues(enabledDevice, safeArray);
				usbDMXPro.send(enabledDevice);
				
			}
			
		} finally {
			if (usbDMXPro != null) {
				System.out.println("Closing COM object");

				// openDmxCom.stop_Threads();
				usbDMXPro.close(4);
			}
		}
	}
	
	
	public static void main(String args[]) {
		TestVBCOM testClass = new TestVBCOM();
		// testClass.testOpenDmxCom();
		testClass.testUSBDMXProCom();
		
	}
	
	
}
