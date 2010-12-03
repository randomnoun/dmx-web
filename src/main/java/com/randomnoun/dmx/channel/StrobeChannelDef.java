package com.randomnoun.dmx.channel;

/** Strobe channel definition. Different settings on this channel will cause the
 * fixture to go to black and return to it's current setting at a
 * predetermined rate.
 *  
 * <p>This class currently assumes a linear change in strobe speed
 * by DMX value. The StrobeChannelMuxer currently turns off strobe for
 * values outside those specified by this class.  
 *  
 * @author knoxg
 */
public class StrobeChannelDef extends ChannelDef {

	/** A DMX value which will disable strobe */ 
	int disableStrobeValue;
	
	/** The slowest strobe speed in hertz (cycles per second) */
	double minimumStrobeHertz;
	
	/** The DMX value which will give the slowest strobe speed */
	int minimumStrobeValue;

	/** The fastest strobe speed in hertz (cycles per second) */
	double maximumStrobeHertz;
	
	/** The DMX value which will give the fastest strobe speed */
	int maximumStrobeValue;

	public StrobeChannelDef(int offset, int disableStrobeValue, double minimumStrobeHertz,
		int minimumStrobeValue, double maximumStrobeHertz, int maximumStrobeValue) 
	{
		super(offset, minimumStrobeValue, maximumStrobeValue);
		this.disableStrobeValue = disableStrobeValue;
		this.minimumStrobeHertz = minimumStrobeHertz;
		this.minimumStrobeValue = minimumStrobeValue;
		this.maximumStrobeHertz = maximumStrobeHertz;
		this.maximumStrobeValue = maximumStrobeValue;
		setHtmlImg("image/channel/strobe_16x16.png");
		setHtmlLabel("Strobe");
	}
	
	public int getDisableStrobeValue() { return disableStrobeValue; }
	public double getMinimumStrobeHertz() { return minimumStrobeHertz; }
	public int getMinimumStrobeValue() { return minimumStrobeValue; }
	public double getMaximumStrobeHertz() { return maximumStrobeHertz; }
	public int getMaximumStrobeValue() { return maximumStrobeValue; }
	
}
