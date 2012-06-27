package com.randomnoun.dmx.dmxDevice.usbPro.message;

import org.apache.log4j.Logger;

/** The Widget sends this message to the PC on completion of the Program Flash Page request
 * 
 */
public class ProgramFlashPageResponse extends ResponseMessage {
	static Logger logger = Logger.getLogger(ProgramFlashPageResponse.class);
	boolean success = false;
	public ProgramFlashPageResponse(byte[] data) {
		super(ResponseMessageType.PROGRAM_FLASH_PAGE_REPLY, data);
		if (data.length!=4) {
			setBadMessage();
			logger.warn("Expected 4 bytes in ProgramFlashPageResponse payload; found " + data.length); 
		} else {
			if (data[0]=='T' && data[1]=='R' && data[2]=='U' && data[3]=='E') { 
				success = true; 
			} else if (data[0]=='F' && data[1]=='A' && data[2]=='L' && data[3]=='S') { 
				success = false; 
			} else { 
				setBadMessage();
				logger.warn("Expected TRUE or FALS response in ProgramFlashPageResponse; found [" + data[0] + ", " + data[1] + ", " + data[2] + ", " + data[3] + "]"); 
			}
		}
	}
	
	/** Success indicator, set to true if firmware page was
	 *  programmed successfully, set to false if firmware page programming
	 *  failed.
	 *  
	 * @return success indicator  
	 */
	public boolean getSuccess() { return success; }
}