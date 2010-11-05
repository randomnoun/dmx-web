package com.randomnoun.dmx.protocol.dmxUsbPro.message;

import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidgetTranslator;

public abstract class ResponseMessage {
	ResponseMessageType responseMessageType;
	byte[] data;
	boolean badMessage = false;
	
	public ResponseMessage(ResponseMessageType responseMessageType, byte[] data) {
		this.responseMessageType = responseMessageType;
		this.data = data;
		this.badMessage = false;
	}
	public void setBadMessage() { 
		this.badMessage = true;
	}
	public boolean isBadMessage() { 
		return badMessage;
	}
	
}
