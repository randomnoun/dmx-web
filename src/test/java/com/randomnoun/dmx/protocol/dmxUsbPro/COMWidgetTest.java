package com.randomnoun.dmx.protocol.dmxUsbPro;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;
import com.jacobgen.dmx._USBDMXProCom;

// should be using enttec usb pro interface, not opendmx
// so that's interesting. 

public class COMWidgetTest {


	/*
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
			
			System.out.println("device 4: " + usbDMXPro.getErrorString(4));
			enabledDevice = 4;
			
			byte[] universe = new byte[512];
			if (enabledDevice!=-1) { 
				System.out.println("Setting DMX values to 0");
				for (int i=0; i<512; i++) { universe[i] = 0; }
				SafeArray safeArray = new SafeArray(Variant.VariantByte, 512);
				safeArray.fromByteArray(universe);
				usbDMXPro.setDMXValues(enabledDevice, safeArray);
				usbDMXPro.send(enabledDevice);
				System.out.println("device 4: " + usbDMXPro.getErrorString(4));
				
				try { Thread.sleep(1000); } catch (InterruptedException e) { }
				System.out.println("Setting DMX channel 20 to 255");
				universe[20] = (byte) 255;
				safeArray.fromByteArray(universe);
				usbDMXPro.setDMXValues(enabledDevice, safeArray);
				usbDMXPro.send(enabledDevice);
				System.out.println("device 4: " + usbDMXPro.getErrorString(4));
				
				try { Thread.sleep(1000); } catch (InterruptedException e) { }
				System.out.println("Setting DMX channel 20 to 0");
				universe[20] = (byte) 0;
				safeArray.fromByteArray(universe);
				usbDMXPro.setDMXValues(enabledDevice, safeArray);
				usbDMXPro.send(enabledDevice);
				System.out.println("device 4: " + usbDMXPro.getErrorString(4));

				
			}
			
			for (int i=4; i<=4; i++) {
				String errorString = usbDMXPro.getErrorString(i);
				System.out.println("Error " + i + ": " + errorString);
				// if (errorString.equals("SENDING DMX")) { enabledDevice = i; }
				enabledDevice = 4;
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
		COMWidgetTest testClass = new COMWidgetTest();
		testClass.testUSBDMXProCom();
		
	}
	*/
	
	
}
