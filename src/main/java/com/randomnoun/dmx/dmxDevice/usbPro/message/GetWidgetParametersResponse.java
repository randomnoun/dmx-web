package com.randomnoun.dmx.dmxDevice.usbPro.message;

import org.apache.log4j.Logger;

/** The Widget sends this message to the PC in response to the Get Widget Parameters request. */
public class GetWidgetParametersResponse extends ResponseMessage {
	static Logger logger = Logger.getLogger(GetWidgetParametersResponse.class);
	int firmwareVersion = 0;
	int dmxOutputBreakTime = 0;
	int dmxOutputMarkAfterBreakTime = 0;
	int dmxOutputPacketsPerSecondRate = 0;
	byte[] userData;
	//if (dmxOutputBreakTime < 9 || dmxOutputBreakTime > 127) { throw new IllegalArgumentException("dmxOutputBreakTime should be in range 9 to 127"); }
	//if (dmxOutputMarkAfterBreakTime < 1 || dmxOutputMarkAfterBreakTime > 127) { throw new IllegalArgumentException("dmxOutputMarkAfterBreakTime should be in range 1 to 127"); }
	//if (dmxOutputPacketsPerSecondRate < 1 || dmxOutputPacketsPerSecondRate > 40) { throw new IllegalArgumentException("dmxOutputPacketsPerSecondRate should be in range 1 to 40"); }
	
	public GetWidgetParametersResponse(byte[] data) {
		super(ResponseMessageType.GET_WIDGET_PARAMETERS_REPLY, data);
		if (data.length<5) { 
			setBadMessage();
			logger.warn("Expected at least 5 bytes in GetWidgetParametersResponse payload; found " + data.length);
		} else {
			firmwareVersion = data[0] + data[1] * 256;
			dmxOutputBreakTime = data[2];
			dmxOutputMarkAfterBreakTime = data[3];
			dmxOutputPacketsPerSecondRate = data[4];
			userData = new byte[data.length - 5];
			System.arraycopy(data, 5, userData, 0, data.length - 5);
			if (dmxOutputBreakTime < 9 || dmxOutputBreakTime > 127 ) {
				setBadMessage();
				logger.warn("Received dmxOutputBreakTime should be between 9 and 127; found " + dmxOutputBreakTime); 
			}
			if (dmxOutputMarkAfterBreakTime < 1 || dmxOutputMarkAfterBreakTime > 127 ) {
				setBadMessage();
				logger.warn("Received dmxOutputMarkAfterBreakTime should be between 1 and 127; found " + dmxOutputMarkAfterBreakTime); 
			}
			if (dmxOutputPacketsPerSecondRate < 1 || dmxOutputPacketsPerSecondRate > 40 ) {
				setBadMessage();
				logger.warn("Received dmxOutputPacketsPerSecondRate should be between 1 and 40; found " + dmxOutputPacketsPerSecondRate); 
			}
		}
	}
	/** Returns the Firmware version
	 * 
	 * @return Firmware version
	 */
	public int getFirmwareVersion() { return firmwareVersion; }
	
	/** Returns the DMX output break time in 10.67 microsecond units.
	 * Valid range is 9 to 127.
     *
     * @return the DMX output break time 
     */
	
	public int getDmxOutputBreakTime() { return dmxOutputBreakTime; }
	
	/** Returns the DMX output Mark After Break time in 10.67
	 * microsecond units. Valid range is 1 to 127.
	 * 
	 * @return DMX output Mark After Break time in 10.67 microsecond units. 
	 */
	public int getDmxOutputMarkAfterBreakTime() { return dmxOutputMarkAfterBreakTime; }
	
	/** Returns the DMX output rate in packets per second. Valid range is
	 * 1 to 40.
	 * 
	 * @return the DMX output rate in packets per second
	 */
	public int getDmxOutputPacketsPerSecondRate() { return dmxOutputPacketsPerSecondRate; }
	
	/** User defined configuration data. See Set Widget
	 * Parameters request.
	 * 
	 * @return User defined configuration data. 
	 * 
	 * @see com.randomnoun.dmx.dmxDevice.usbPro.UsbProWidgetTranslator#sendSetWidgetParametersRequest(int, int, int, byte[])
	 */
	public byte[] getUserData() { return userData; }
	
}