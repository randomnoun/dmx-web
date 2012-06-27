package com.randomnoun.dmx.dmxDevice.usbPro.message;

import com.randomnoun.dmx.dmxDevice.usbPro.UsbProWidgetTranslator;

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
