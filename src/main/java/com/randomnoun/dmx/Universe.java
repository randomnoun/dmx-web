package com.randomnoun.dmx;

/** This object contains all the dmx values of all fixtures on a 
 * single DMX universe (bus).
 *  
 */
public class Universe {

	int dmxValues[];
	long time;
	
	/** Returns the DMX value of one channel in this universe
	 *  
	 * @param offset
	 * 
	 * @return
	 */
	public int getChannelValue(int dmxChannelNumber) {
		return dmxValues[dmxChannelNumber];
	}
	
	/** Sets the DMX value of a channel in this universe
	 * 
	 * @param dmxChannelNumber
	 * @param value
	 * 
	 * @return
	 */
	public void setChannelValue(int dmxChannelNumber, int value) {
		dmxValues[dmxChannelNumber] = value;
		// @TODO fire some events off to any listeners
		// (for display, sending to the outside world, etc)
	}
	
	public long getTime() {
		return time;
	}
	
	
}
