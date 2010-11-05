package com.randomnoun.dmx.protocol.dmxUsbPro;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.protocol.dmxUsbPro.message.GetWidgetParametersResponse;
import com.randomnoun.dmx.protocol.dmxUsbPro.message.GetWidgetSerialNumberResponse;
import com.randomnoun.dmx.protocol.dmxUsbPro.message.ProgramFlashPageResponse;
import com.randomnoun.dmx.protocol.dmxUsbPro.message.ReceivedDMXChangeOfStatePacketResponse;
import com.randomnoun.dmx.protocol.dmxUsbPro.message.ReceivedDMXPacketResponse;
import com.randomnoun.dmx.protocol.dmxUsbPro.message.RequestMessageType;
import com.randomnoun.dmx.protocol.dmxUsbPro.message.ResponseMessage;
import com.randomnoun.dmx.protocol.dmxUsbPro.message.ResponseMessageType;

/** Class to communicate with a DMX USB Pro Widget 
 * 
 * <p>Based on API Specification 1.44: http://www.enttec.com/docs/dmx_usb_pro_api_spec.pdf 
 * 
 * <p>I think if terminators aren't added to the DMX lines then you might get echos
 * of messages sent across the DMX bus (getting response messages with label 6 in tests)
 * 
 * @author knoxg
 */
public class UsbProWidgetTranslator {

	/** Logger instance for this class */
	Logger logger = Logger.getLogger(UsbProWidgetTranslator.class);

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
	private OutputStream writeBuffer;
	
	/** Data that has been read from the device */
	private InputStream readBuffer;
	
	/** Messages that have been read from the device */
	private Queue<ResponseMessage> readMessageQueue = new LinkedList<ResponseMessage>();

	
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
	
	
    public UsbProWidgetTranslator(InputStream inputStream, OutputStream outputStream) {
    	this.readBuffer = inputStream;
    	this.writeBuffer = outputStream;
    }
    
    /** Returns the next message if one is  available, null otherwise */
    public ResponseMessage getMessage() {
    	return readMessageQueue.poll();
    }
    
    /** Returns the number of messages currently available in this translator's 
     * message queue
     * 
     * @return the number of messages currently available 
     */
    public int getMessagesAvailable() {
    	return readMessageQueue.size();
    }

	
	/** Wraps a request message in the format required by the FTDI driver */
	private byte[] getRequestMessage(RequestMessageType messageType, byte[] data) {
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
	 * @throws IOException 
     */
	public void sendReprogramFirmwareRequestMessage() throws IOException {
		byte[] message = getRequestMessage(RequestMessageType.REPROGRAM_FIRMWARE_REQUEST, null);
		writeBuffer.write(message);
	}

	/** This message programs one Flash page of the Widget firmware. The Flash pages must be
     * programmed in order from first to last Flash page, with the contents of the firmware binary file.
     * 
     * @param flashPage  One page of firmware binary file.
     */
	public void sendProgramFlashPageRequest(byte[] flashPage) throws IOException {
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
	public void sendGetWidgetParametersRequest(int configSize) throws IOException {
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
	public void sendSetWidgetParametersRequest(int dmxOutputBreakTime, 
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
     * @param the DMX startCode, typically 0. See 
     *   <a href="http://en.wikipedia.org/wiki/DMX512-A#Protocol">http://en.wikipedia.org/wiki/DMX512-A#Protocol</a>
     * @param dmxData DMX data to send, beginning with the start code. The overall
     *   message size specifies the size of the DMX data to send, and also
     *   sets the universe size (the number of DMX channels which are
     *   output).
     *   <p>A few notes on the DMXKing USB dongle:
     *   <p>A start code of 0 seems to indicate that dmxData[0] is channel 1. 
     *   <p>A start code of 1 seems to indicate that dmxData[0] is channel 0.
     *   <p>So that's interesting.
     *   
     * 
	 * @throws IOException 
     */ 
	public void sendOutputOnlySendDMXPacketRequest(byte startCode, byte[] dmxData) throws IOException {
		if (dmxData.length < 24 || dmxData.length > 512) { throw new IllegalStateException("dmxData must be between 24 and 512 bytes in length"); }
		byte[] payload = new byte[dmxData.length + 1];
		payload[0] = startCode;
		System.arraycopy(dmxData, 0, payload, 1, dmxData.length);
		byte[] message = getRequestMessage(RequestMessageType.OUTPUT_ONLY_SEND_DMX_PACKET_REQUEST,
			payload);
		writeBuffer.write(message);
	}
	
	/** This message requests the Widget to send an RDM packet out of the Widget DMX port, and then
	 * change the DMX port direction to input, so that RDM or DMX packets can be received.
	 * 
	 * @param the RDM startCode; typically zero 
	 * @param rdmData RDM data to send, beginning with the start code. The overall
	 *   message size specifies the size of the RDM data to send.
	 *   
	 * @throws IOException 
	 */
	public void sendSendRDMPacketRequest(byte startCode, byte[] rdmData) throws IOException {
		if (rdmData.length < 1 || rdmData.length > 513) { throw new IllegalStateException("rdmData must be between 1 and 513 bytes in length"); }
		byte[] payload = new byte[rdmData.length + 1];
		payload[0] = startCode;
		System.arraycopy(rdmData, 0, payload, 1, rdmData.length);
		byte[] message = getRequestMessage(RequestMessageType.SEND_RDM_PACKET_REQUEST,
			payload);
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
	public void sendReceiveDMXOnChangeRequest(boolean sendOnDataChange) throws IOException {
		byte[] message = getRequestMessage(RequestMessageType.RECEIVE_DMX_ON_CHANGE_REQUEST,
			new byte[] { sendOnDataChange ? (byte) 1 : (byte) 0 });
		writeBuffer.write(message);
	}
	
	/** This message requests the Widget serial number, which should be the same as that printed on the
	 * Widget case
	 * 
	 * @throws IOException
	 */
	public void sendGetWidgetSerialNumberRequest() throws IOException {
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
	public void sendRDMDiscoveryRequest(byte[] discUniqueBranchRdmRequest) throws IOException {
		if (discUniqueBranchRdmRequest.length != 38) { throw new IllegalStateException("discUniqueBranchRdmRequest must be 38 bytes"); } 
		byte[] message = getRequestMessage(RequestMessageType.SEND_RDM_DISCOVERY_REQUEST,
			discUniqueBranchRdmRequest);
		writeBuffer.write(message);		
	}
	

    /** Reads all available data from the device, possible adding
     * messages to the readMessageQueue */
    void readData() throws IOException {
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

