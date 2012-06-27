package com.randomnoun.dmx.dmxDevice.usbPro.message;

public enum ResponseMessageType {
	UNKNOWN(-1), // place-holder for unknown message type
	PROGRAM_FLASH_PAGE_REPLY(2),
	GET_WIDGET_PARAMETERS_REPLY(3),
	RECEIVED_DMX_PACKET(5),
	RECEIVED_DMX_CHANGE_OF_STATE_PACKET(9),
	GET_WIDGET_SERIAL_NUMBER_REPLY(10);
	
	int label;
	
	private ResponseMessageType(int label) { 
		this.label = label;
	}
	public int getLabel() { return label; }
	
	static public ResponseMessageType valueOf(int label) {
		// hmm...
		switch (label) {
			case 2: return PROGRAM_FLASH_PAGE_REPLY;
			case 3: return GET_WIDGET_PARAMETERS_REPLY;
			case 5: return RECEIVED_DMX_PACKET;
			case 9: return RECEIVED_DMX_CHANGE_OF_STATE_PACKET;
			case 10: return GET_WIDGET_SERIAL_NUMBER_REPLY;
			default:
				throw new IllegalArgumentException("Unknown ResponseMessageType label " + label);
		}
	}
}