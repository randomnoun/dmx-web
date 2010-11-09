package com.randomnoun.dmx.protocol.openDmxJni;

/** A JNI wrapper around the OpenDMX C interface to the OpenDMX VB
 * module/class thing.
 * 
 * @author knoxg
 *
 */
public class OpenDmx {

	public native void initialise();
	
	public native int init(int deviceNumber);
	
	public native int initAll();
	
	public native int getId(int deviceNumber);
	
	public native int getNumberOfOpenDevices();
	
	// should be a 512-element byte array
	public native void setDmx(int deviceNumber, byte[] dmxArray);
	
	public native int getDmx(int deviceNumber, int dmxChannel);
	
	// send from buffer to interface n
	public native void send(int deviceNumber);
	
	public native void stopThreads();
	
	public native void killThread(int deviceNumber);
	
	// close a device
	public native void done(int deviceNumber);
	
	public native void doneAll();
	
	static {
		try {
			System.loadLibrary("OpenDMX");
		} catch (Exception e) {
			System.err.println("Could not load OpenDMX library");
			e.printStackTrace();
		}
	}
	
}
