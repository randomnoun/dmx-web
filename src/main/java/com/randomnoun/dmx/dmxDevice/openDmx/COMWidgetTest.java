package com.randomnoun.dmx.dmxDevice.openDmx;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;
import com.jacobgen.dmx._OpenDMXCom;

// should be using enttec usb pro interface, not opendmx
// so that's interesting. 

public class COMWidgetTest {


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
	
	public static void main(String args[]) {
		COMWidgetTest testClass = new COMWidgetTest();
		testClass.testOpenDmxCom();
	}
	
	
}
