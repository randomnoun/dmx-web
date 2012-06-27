package com.randomnoun.dmx.dmxDevice.usbPro.message;

import org.apache.log4j.Logger;

/** The Widget sends this message to the PC in response to the Get Widget Serial Number request
 * 
 */
public class GetWidgetSerialNumberResponse extends ResponseMessage {
	static Logger logger = Logger.getLogger(GetWidgetSerialNumberResponse.class);
	boolean hasSerialNumber;
	int serialNumber;
	public GetWidgetSerialNumberResponse(byte[] data) {
		super(ResponseMessageType.GET_WIDGET_SERIAL_NUMBER_REPLY, data);
		int i;
		if (data.length!=4) {
			setBadMessage();
			logger.warn("Expected 4 bytes in GetWidgetSerialNumberResponse payload; found " + data.length);
		} else {
			// On old Widgets,
			// the serial number was not programmed, and the value would be hex
			// 0FFFFFFFF.
			hasSerialNumber = false;
			for (i=0; i<4; i++) {
				if (data[i]!=0xFF) { hasSerialNumber = true; }
			}
			if (hasSerialNumber) {
				for (i=0; i<4; i++) {
					if ((data[i] & 0x0F) > 10 || ((data[i] & 0xF0)>>4) > 10) { 
						setBadMessage();
						logger.warn("Illegal BCD value " + data[i] + " found in offset " + i + " of GetWidgetSerialNumberResponse"); 
					}
				}
				serialNumber = 
					(data[0] & 0x0F) +
				    ((data[0] & 0xF0) >> 4) * 10 + 
				     (data[1] & 0x0F) * 100 +
				    ((data[1] & 0xF0) >> 4) * 1000 +
				     (data[2] & 0x0F) * 10000 + 
				    ((data[2] & 0xF0) >> 4) * 100000 +
				     (data[3] & 0x0F) * 1000000 +
				    ((data[3] & 0xF0) >> 4) * 10000000;
			}
		}
	}
		
	/** Returns true if the widget reported a serial number, false otherwise
	 * 
	 * @return true if the widget reported a serial number, false otherwise
	 */
	public boolean hasSerialNumber() {
		return hasSerialNumber;
	}
	/** Widget serial number
	 * 
	 * @return Widget serial number
	 */
	public int getSerialNumber() {
		return serialNumber;
	}
}