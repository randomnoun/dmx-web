package com.randomnoun.dmx;

import com.jacob.activeX.ActiveXComponent;
import com.jacobgen.opendmx._OpenDMXCom;

public class TestVBCOM {

	private ActiveXComponent axc;
	private _OpenDMXCom openDmxCom;
	
	public void testCom() {
		axc = new ActiveXComponent("OpenDMX.OpenDMXCom");
		openDmxCom = new _OpenDMXCom(axc);
		
		
		openDmxCom.initialize();
		openDmxCom.init_All(); // automaticly find devices and start threads for them
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i=0; i<4; i++) {
			System.out.println("Error " + i + ": " + openDmxCom.getErrorString(i));
		}
	}
	
	public static void main(String args[]) {
		TestVBCOM testClass = new TestVBCOM();
		testClass.testCom();
		
	}
	
	
}
