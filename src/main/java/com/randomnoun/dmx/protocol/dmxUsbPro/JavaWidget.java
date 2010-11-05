package com.randomnoun.dmx.protocol.dmxUsbPro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

/** Class to communicate with a DMX USB Pro Widget 
 * 
 * <p>Based on API Specification 1.44: http://www.enttec.com/docs/dmx_usb_pro_api_spec.pdf 
 * 
 * @author knoxg
 */
public class JavaWidget {

	/** Logger instance for this class */
	Logger logger = Logger.getLogger(JavaWidget.class);

	/** Start of message marker for widget */
	private static byte START_OF_MESSAGE = (byte) 0x7E;
	
	/** End of message marker for widget */
	private static byte END_OF_MESSAGE = (byte) 0xE7;
	
	/** Normal DMX firmware. Supports all messages except Send RDM (label=7), Send
	 * RDM Discovery Request(label=11) and receive RDM */
	public static byte FIRMWARE_VERSION_1 = 1;
	
	/** RDM firmware.This enables the Widget to act as an RDM Controller or RDM
	 * responder. Supports all messages except Receive DMX On Change (label=8) and
	 * Change Of State Receive (label=9). */
	public static byte FIRMWARE_VERSION_2 = 2;
	
	/** RDM Sniffer firmware. This is for use with the Enttec RDM packet monitoring
	 * application. */
	public static byte FIRMWARE_VERSION_3 = 3;

	
	/** Data that will be written to the device */
	ByteArrayOutputStream writeBuffer = new ByteArrayOutputStream();
	
	/** Data that has been read from the device */
	ByteArrayInputStream readBuffer;
	
	/** Messages that have been read from the device */
	Queue readMessageQueue = new LinkedList();
	
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
	
	//hmmm... 
	static Map<Integer, ResponseMessageType> constants = new HashMap<Integer, ResponseMessageType>();
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
			constants.put(new Integer(label), this);
		}
		public int getLabel() { return label; }
		static public ResponseMessageType valueOf(int label) {
			ResponseMessageType value = constants.get(new Integer(label));
			if (value==null) { throw new IllegalArgumentException("Unknown ResponseMessageType label " + label); }
			return value;
		}
	}
	
	public abstract static class ResponseMessage {
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
	
	/** The Widget sends this message to the PC on completion of the Program Flash Page request
	 * 
	 */
	public static class ProgramFlashPageResponse extends ResponseMessage {
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
	
	
	/** The Widget sends this message to the PC in response to the Get Widget Parameters request. */
	public static class GetWidgetParametersResponse extends ResponseMessage {
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
		 * @see JavaWidget#sendSetWidgetParametersRequest(int, int, int, byte[])
		 */
		public byte[] getUserData() { return userData; }
		
	}
	
	/** The Widget sends this message to the PC unsolicited, 
	 * whenever the Widget receives a DMX or
	 * RDM packet from the DMX port, and the Receive DMX on Change mode is 'Send always'.
	 * 
	 */
	public static class ReceivedDMXPacketResponse extends ResponseMessage {
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
	
	/** The Widget sends one or more instances of this message to the PC unsolicited, whenever the
	 * Widget receives a changed DMX packet from the DMX port, and the Receive DMX on Change
	 * mode is 'Send on data change only'.
	 *
	 * @TODO add some additional methods to make this easier to use
	 */
	public static class ReceivedDMXChangeOfStatePacketResponse extends ResponseMessage {
		static Logger logger = Logger.getLogger(ReceivedDMXChangeOfStatePacketResponse.class);
		int startChangeByteNumber;
		BitSet changedBytes;
		byte[] newBytes;
		public ReceivedDMXChangeOfStatePacketResponse(byte[] data) {
			super(ResponseMessageType.RECEIVED_DMX_CHANGE_OF_STATE_PACKET, data);
			if (data.length<6 || data.length>46) {
				setBadMessage();
				logger.warn("Expected between 1 and 46 bytes in ReceivedDMXChangeOfStatePacketResponse payload; found " + data.length); 
			} else {
				int checkCount = 0;
				startChangeByteNumber = data[0];
				changedBytes = new BitSet(40);
				// Not sure about the pseudocode in the spec for this;
				// specifically the 'start changed_byte_number * 8' bit, since that
				// means that sets of changes need to start on 8-byte boundaries. 
				// which I guess is OK. 
				/*
				The user program can decode the message into a 513 byte received DMX data array, beginning
				with the start code. The algorithm to do this is shown below:
				On startup, zero out the 513 byte received_dmx_array
				For each Change Of State packet received
				  changed_byte_index = 0
				  For bit_array_index = 0 to 39
				    If changed_bit_array[bit_array_index] is 1 then
				      received_dmx_array[start_changed_byte_number * 8 + bit_array_index] =
				      changed_dmx_data_array[changed_byte_index]
				      Increment changed_byte_index
				    Endif
				  Endfor
				Endfor
				 */
				for (int i=0; i<5; i++) {
				  byte b = data[i+1];
				  if ((b & 1) != 0) { checkCount++; changedBytes.set(i*8); }
				  if ((b & 2) != 0) { checkCount++; changedBytes.set(i*8 + 1); }
				  if ((b & 4) != 0) { checkCount++; changedBytes.set(i*8 + 2); }
				  if ((b & 8) != 0) { checkCount++; changedBytes.set(i*8 + 3); }
				  if ((b & 16) != 0) { checkCount++; changedBytes.set(i*8 + 4); }
				  if ((b & 32) != 0) { checkCount++; changedBytes.set(i*8 + 5); }
				  if ((b & 64) != 0) { checkCount++; changedBytes.set(i*8 + 6); }
				  if ((b & 128) != 0) { checkCount++; changedBytes.set(i*8 + 7); }
				}
				newBytes = new byte[data.length - 6];
				System.arraycopy(data, 6, newBytes, 0, data.length - 6);
				if (checkCount != newBytes.length) {
					setBadMessage();
					logger.warn("Number of bytes in changed DMX array (" + newBytes.length + ") should equal number of bits set in changed bit array (" + checkCount + ")");
				}
			}
		}
			
		/** Returns start changed byte number. 
		 * 
		 * @return start changed byte number
		 */
		public int getStartChangeByteNumber() {
			return startChangeByteNumber * 8;
		}
		
		/** Returns changed bit array, where array bit 0 is bit 0 of first byte and array bit
		 * 39 is bit 7 of last byte.
		 *  
		 * @return changed bit array
		 */
		public BitSet getChangedBytesBitSet() {
			return changedBytes;
		}
		
		/** Returns Changed DMX data byte array. One byte is present for each set bit in
		 * the Changed bit array. 
		 * 
		 * @return changed DMX data byte array
		 */
		public byte[] getChangedBytes() {
			return newBytes;
		}
	}

	/** The Widget sends this message to the PC in response to the Get Widget Serial Number request
	 * 
	 */
	public static class GetWidgetSerialNumberResponse extends ResponseMessage {
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
	
	/** Returns a message as sent to the Widget */
	byte[] getRequestMessage(RequestMessageType messageType, byte[] data) {
		if (data==null) { data = new byte[0]; };
		if (data.length >600) { throw new IllegalArgumentException("message data cannot exceed 600 bytes"); }
		byte[] message = new byte[data.length + 5];
		message[0] = START_OF_MESSAGE;
		message[1] = (byte) messageType.getLabel();
		message[2] = (byte) ((data.length) & 0xFF);
		message[3] = (byte) ((data.length) >> 8);
		System.arraycopy(data, 0, message, 4, data.length);
		message[data.length + 4] = END_OF_MESSAGE;
		return message;
	}
	
	/** This message requests the Widget firmware to run the Widget bootstrap to enable reprogramming of
     * the Widget firmware. 
     */
	byte[] getReprogramFirmwareRequestMessage() {
		return getRequestMessage(RequestMessageType.REPROGRAM_FIRMWARE_REQUEST, null);
	}

	/** This message programs one Flash page of the Widget firmware. The Flash pages must be
     * programmed in order from first to last Flash page, with the contents of the firmware binary file.
     * 
     * @param flashPage  One page of firmware binary file.
     */
	void sendProgramFlashPageRequest(byte[] flashPage) throws IOException {
		if (flashPage.length != 64) { throw new IllegalArgumentException("flash page must be 64 bytes in length"); }
		byte[] message = getRequestMessage(RequestMessageType.PROGRAM_FLASH_PAGE_REQUEST, flashPage);
		writeBuffer.write(message);
	}
	
	/**  This message requests the Widget configuration.
	 * 
	 * @param configSize user configuration size in bytes. Valid range for user
	 * configuration size is 0 to 508.
	 * 
	 * @throws IOException 
	 */
	void sendGetWidgetParametersRequest(int configSize) throws IOException {
		if (configSize < 0 || configSize > 508) {
			throw new IllegalArgumentException("configSize must be in range 0 to 508");
		}
		byte[] message = getRequestMessage(RequestMessageType.GET_WIDGET_PARAMETERS_REQUEST,
			new byte[] { (byte) (configSize >> 8), (byte) (configSize & 0xFF) });
		writeBuffer.write(message);
	}
	
	/** This message sets the Widget configuration. The Widget configuration is preserved when the
	 * Widget loses power.
	 * 
	 * @param dmxOutputBreakTime DMX output break time in 10.67 microsecond units.
	 *   Valid range is 9 to 127.
	 * @param dmxOutputMarkAfterBreakTime DMX output Mark After Break time in 10.67
	 *   microsecond units. Valid range is 1 to 127. DMX output rate in packets per second. Valid range is 1
	 *   to 40, or 0 for fastest rate possible (this will make the
	 *   most difference when the output universe size is smallest).
     * @param dmxOutputPacketsPerSecondRate DMX output rate in packets per second. Valid range is 1
     *   to 40, or 0 for fastest rate possible (this will make the 
     *   most difference when the output universe size is smallest).
     * @param userData User defined configuration data (should be between 0 and 508 bytes in length, inclusive).
	 * 
	 * @throws IOException 
	 * 
	 */
	void sendSetWidgetParametersRequest(int dmxOutputBreakTime, 
		int dmxOutputMarkAfterBreakTime, int dmxOutputPacketsPerSecondRate, 
		byte[] userData ) throws IOException 
	{
		if (dmxOutputBreakTime < 9 || dmxOutputBreakTime > 127) { throw new IllegalArgumentException("dmxOutputBreakTime should be in range 9 to 127"); }
		if (dmxOutputMarkAfterBreakTime < 1 || dmxOutputMarkAfterBreakTime > 127) { throw new IllegalArgumentException("dmxOutputMarkAfterBreakTime should be in range 1 to 127"); }
		if (dmxOutputPacketsPerSecondRate < 1 || dmxOutputPacketsPerSecondRate > 40) { throw new IllegalArgumentException("dmxOutputPacketsPerSecondRate should be in range 1 to 40"); }
		if (userData==null) { userData = new byte[0]; }
		if (userData.length > 508) { throw new IllegalArgumentException("userData cannot be greater than 508 bytes"); }
		byte[] payload = new byte[userData.length + 5];
		payload[0] = (byte) (userData.length >> 8);
		payload[1] = (byte) (userData.length & 0xFF);
		payload[2] = (byte) dmxOutputBreakTime;
		payload[3] = (byte) dmxOutputMarkAfterBreakTime;
		payload[4] = (byte) dmxOutputPacketsPerSecondRate;
		System.arraycopy(userData, 0, payload, 5, userData.length);
		byte[] message = getRequestMessage(RequestMessageType.SET_WIDGET_PARAMETERS_REQUEST,
			payload);
		writeBuffer.write(message);
	}
	
	/** This message requests the Widget to periodically send a DMX packet out of the Widget DMX port
	 * at the configured DMX output rate. This message causes the widget to leave the DMX port direction
	 * as output after each DMX packet is sent, so no DMX packets will be received as a result of this
	 * request.
	 * 
	 * <p>The periodic DMX packet output will stop and the Widget DMX port direction will change to input
	 * when the Widget receives any request message other than the Output Only Send DMX Packet
	 * request, or the Get Widget Parameters request.
     *
     * @param dmxData DMX data to send, beginning with the start code. The overall
     *   message size specifies the size of the DMX data to send, and also
     *   sets the universe size (the number of DMX channels which are
     *   output).
     * 
	 * @throws IOException 
     */ 
	void sendOutputOnlySendDMXPacketRequest(byte[] dmxData) throws IOException {
		if (dmxData.length < 25 || dmxData.length > 513) { throw new IllegalStateException("dmxData must be between 25 and 513 bytes in length"); } 
		byte[] message = getRequestMessage(RequestMessageType.OUTPUT_ONLY_SEND_DMX_PACKET_REQUEST,
			dmxData);
		writeBuffer.write(message);
	}
	
	/** This message requests the Widget to send an RDM packet out of the Widget DMX port, and then
	 * change the DMX port direction to input, so that RDM or DMX packets can be received.
	 *  
	 * @param rdmData RDM data to send, beginning with the start code. The overall
	 *   message size specifies the size of the RDM data to send.
	 *   
	 * @throws IOException 
	 */
	void sendSendRDMPacketRequest(byte[] rdmData) throws IOException {
		if (rdmData.length < 1 || rdmData.length > 513) { throw new IllegalStateException("rdmData must be between 1 and 513 bytes in length"); } 
		byte[] message = getRequestMessage(RequestMessageType.SEND_RDM_PACKET_REQUEST,
			rdmData);
		writeBuffer.write(message);
	}
	
	/** This message requests the Widget send a DMX packet to the PC only when the DMX values change
	 * on the input port.
	 * 
	 * <p>By default the widget will always send, if you want to send on change it must be enabled by sending
	 * this message.
	 * 
	 * <p>This message also reinitializes the DMX receive processing, so that if change of state reception is
	 * selected, the initial received DMX data is cleared to all zeros.
     *
     * @param sendOnDataChange if true, send always; if false, send on data change only 
     */
	void sendReceiveDMXOnChangeRequest(boolean sendOnDataChange) throws IOException {
		byte[] message = getRequestMessage(RequestMessageType.RECEIVE_DMX_ON_CHANGE_REQUEST,
			new byte[] { sendOnDataChange ? (byte) 1 : (byte) 0 });
		writeBuffer.write(message);
	}
	
	/** This message requests the Widget serial number, which should be the same as that printed on the
	 * Widget case
	 * 
	 * @throws IOException
	 */
	void sendGetWidgetSerialNumberRequest() throws IOException {
		byte[] message = getRequestMessage(RequestMessageType.GET_WIDGET_SERIAL_NUMBER_REQUEST, null);
		writeBuffer.write(message);
	}
	
	/** This message requests the Widget to send an RDM Discovery Request packet out of the Widget
	 * DMX port, and then receive an RDM Discovery Response (see Received DMX Packet).
	 * 
	 * @param discUniqueBranchRdmRequest DISC_UNIQUE_BRANCH RDM request packet to send.
	 * 
	 * @throws IOException 
	 */
	void sendRDMDiscoveryRequest(byte[] discUniqueBranchRdmRequest) throws IOException {
		if (discUniqueBranchRdmRequest.length != 38) { throw new IllegalStateException("discUniqueBranchRdmRequest must be 38 bytes"); } 
		byte[] message = getRequestMessage(RequestMessageType.SEND_RDM_DISCOVERY_REQUEST,
			discUniqueBranchRdmRequest);
		writeBuffer.write(message);		
	}
	
    
    /** Parse state: unknown state, start-of-message not yet seen */
	private final static int STATE_INITIAL = 0;
    
	/** Parse state: have receive SOM byte, expecting label byte */
	private final static int STATE_PARSED_SOM = 1;
	
	/** Parse state: have received label byte, expecting data length LSB byte */
	private final static int STATE_PARSED_LABEL = 2;
	
	/** Parse state: have received least significant byte of data length, expecting data length MSB byte */ 
	private final static int STATE_PARSED_DATA_LSB = 3;
	
	/** Parse state: have received most significant byte of data length, expecting data */
	private final static int STATE_PARSED_DATA_MSB = 4;
	
	/** Parse state: have received all bytes of data length, expecting EOM byte */
	private final static int STATE_PARSED_DATA = 5;
	
	/** Current state of Widget inputStream parser. Should be a STATE_* constant */
    private int state;
    
    /** The label of message currently being parsed */
    ResponseMessageType parseLabel;
    
    /** The dataLength  of message currently being parsed */
    int parseDataLength; 
    
    /** The buffer of message currently being parsed */
    byte[] parseBuffer;
    
    /** The size of buffer currently being parsed (should be <= dataLength) */
    int parseReceivedDataBytes;
    
    /** If true, indicates that the currently parsed message does not conform to expected message syntax or structure */
    boolean parseBadMessage = false;
    
    /** Reads all available data from the device, possible adding
     * messages to the readMessageQueue */
    public void readData() throws IOException {
    	// read the comm api and push data onto ByteArrayInputStream
    	
    	while (readBuffer.available() > 0) {
    		int ch = readBuffer.read();
    		if (ch==-1) { throw new IllegalStateException("EOF on non-empty buffer"); }
    		switch(state) {
    			case STATE_INITIAL:
    				if (ch==START_OF_MESSAGE) { 
    					state=STATE_PARSED_SOM;
    					parseReceivedDataBytes=0;
    					parseBadMessage=false; // OK so far...
    				} else {
    					logger.warn("Warning: discarding unrecognised received byte '" + ch + "' from Widget");
    				}
    				break;
    			case STATE_PARSED_SOM:
    				try {
    					parseLabel = ResponseMessageType.valueOf(ch);
    				} catch (IllegalArgumentException iae) {
    					logger.error("Received unknown message type", iae);
    					parseLabel = ResponseMessageType.UNKNOWN;
    					parseBadMessage = true;
    				}
    				state = STATE_PARSED_LABEL;
    				break;
    			case STATE_PARSED_LABEL:
    				parseDataLength = ch;
    				state = STATE_PARSED_DATA_LSB;
    				break;
    			case STATE_PARSED_DATA_LSB:
    				parseDataLength = parseDataLength + (ch*256);
    				state = STATE_PARSED_DATA_MSB;
    				break;
    			case STATE_PARSED_DATA_MSB:
    				parseBuffer[parseReceivedDataBytes] = (byte) ch;
    				parseReceivedDataBytes++;
    				if (parseReceivedDataBytes==parseDataLength) {
    					state = STATE_PARSED_DATA;
    				}
    				break;
    			case STATE_PARSED_DATA:
    				if (ch==END_OF_MESSAGE) {
    					if (!parseBadMessage) {
	    					byte data[] = new byte[parseDataLength];
	    					System.arraycopy(parseBuffer, 0, data, 0, parseDataLength);
	    					ResponseMessage responseMessage = null;
	    					switch (parseLabel) {
		    					case PROGRAM_FLASH_PAGE_REPLY:
		    						responseMessage = new ProgramFlashPageResponse(parseBuffer);
		    						break; 
		    					case GET_WIDGET_PARAMETERS_REPLY:
		    						responseMessage = new GetWidgetParametersResponse(parseBuffer);
		    						break; 
		    					case RECEIVED_DMX_PACKET:
		    						responseMessage = new ReceivedDMXPacketResponse(parseBuffer);
		    						break; 
		    					case RECEIVED_DMX_CHANGE_OF_STATE_PACKET:
		    						responseMessage = new ReceivedDMXChangeOfStatePacketResponse(parseBuffer);
		    						break; 
		    					case GET_WIDGET_SERIAL_NUMBER_REPLY:
		    						responseMessage = new GetWidgetSerialNumberResponse(parseBuffer);
		    						break;
		    					default:
		    						throw new IllegalStateException("Cannot create unknown message type " + parseLabel.toString()); 
	    					}
	    					readMessageQueue.add(responseMessage);
    					}
    					state = STATE_INITIAL;
    				} else {
    					parseBadMessage = true;
    					logger.error("Expected END_OF_MESSAGE, found " + ch + "... discarding message.");
    				}
    				break;
    			
    			default:
    				throw new IllegalStateException("Illegal parse state " + state);
    		}
    	}
    }
    
    
}

