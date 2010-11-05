package com.randomnoun.dmx.protocol.dmxUsbPro.message;

import org.apache.log4j.Logger;


/** The Widget sends this message to the PC unsolicited, 
 * whenever the Widget receives a DMX or
 * RDM packet from the DMX port, and the Receive DMX on Change mode is 'Send always'.
 * 
 */
public class ReceivedDMXPacketResponse extends ResponseMessage {
	static Logger logger = Logger.getLogger(ReceivedDMXPacketResponse.class);
	byte[] dmxData;
	public ReceivedDMXPacketResponse(byte[] data) {
		super(ResponseMessageType.RECEIVED_DMX_PACKET, data);
		if (data.length<1 || data.length>514) {
			setBadMessage();
			logger.warn("Expected between 1 and 513 bytes in ReceivedDMXPacketResponse payload; found " + data.length); 
		} else {
			byte receiveStatus = data[0];
			if (receiveStatus == 0) {
				// 0=No error,1=Widget receive queue overflowed
			} else {
				setBadMessage();
				logger.warn("Widget buffer overrun in DMX receive");	
			}
			dmxData = new byte[data.length - 1];
			System.arraycopy(data, 1, dmxData, 0, data.length - 1);
		}
	}
	
	/** Returns received DMX data beginning with the start code
	 * 
	 * @return Received DMX data beginning with the start code
	 */
	public byte[] getDmxData() { return dmxData; }
}