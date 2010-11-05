package com.randomnoun.dmx.protocol.dmxUsbPro.message;

public enum RequestMessageType {
	REPROGRAM_FIRMWARE_REQUEST (1),
	PROGRAM_FLASH_PAGE_REQUEST (2),
	GET_WIDGET_PARAMETERS_REQUEST (3),
	SET_WIDGET_PARAMETERS_REQUEST (4),
	OUTPUT_ONLY_SEND_DMX_PACKET_REQUEST (6),
	SEND_RDM_PACKET_REQUEST (7),
	RECEIVE_DMX_ON_CHANGE_REQUEST (8),
	GET_WIDGET_SERIAL_NUMBER_REQUEST (10),
	SEND_RDM_DISCOVERY_REQUEST (11);
	int label;
	RequestMessageType(int label) { this.label = label; }
	public int getLabel() { return label; }
}