package com.randomnoun.dmx.protocol.dmxUsbPro.message;

import java.util.BitSet;

import org.apache.log4j.Logger;

/** The Widget sends one or more instances of this message to the PC unsolicited, whenever the
 * Widget receives a changed DMX packet from the DMX port, and the Receive DMX on Change
 * mode is 'Send on data change only'.
 *
 * @TODO add some additional methods to make this easier to use
 */
public class ReceivedDMXChangeOfStatePacketResponse extends ResponseMessage {
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