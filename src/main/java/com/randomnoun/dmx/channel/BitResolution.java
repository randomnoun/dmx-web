package com.randomnoun.dmx.channel;

/** 8 and 16-bit value encodings, used when creating channel definitions spanning 
 * more than one DMX offset. 
 * 
 * <p>Each DMX offset is assigned a BitResolution value, which
 * for single-byte values will be {@link BitResolution#BYTE}, and for two-byte values
 * will be one of either {@link BitResolution#WORDHIGH} or {@link BitResolution#WORDLOW}
 * 
 * @author knoxg
 */
public enum BitResolution { 

	/** Constant to define a one-byte (8-bit) value */
	BYTE, 
	
	/** Constant to define the high (larger) byte of a two-byte (16-bit) value.
	 */
	WORDHIGH, 

	/** Constant to define the low (smaller) byte of a two-byte (16-bit) value.
	 */
	WORDLOW 
}