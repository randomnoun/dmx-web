package com.randomnoun.dmx.fixture;

import java.awt.Color;

/** Things you might be interested to know that your fixture is doing.
 * 
 * @author knoxg
 */
public abstract class FixtureOutput {

	/** A color mask (includes color, dim and strobe attributes) */ 
	public static int MASK_COLOR = 1;
	
	/** A time mask */
	public static int MASK_TIME = 2;
	
	/** A pan mask */
	public static int MASK_PAN = 4;
	
	/** A tilt mask */
	public static int MASK_TILT = 8;
	
	/** A mask containing no fixture attributes */
	public static int MASK_NONE = 0;
	
	/** A mask containing all fixture attributes (color, dim, strobe, time, pan and tilt) */
	public static int MASK_ALL=MASK_COLOR | MASK_TIME | MASK_PAN | MASK_TILT;
	
	public abstract Color getColor();  /* post-dim color */
	public abstract long getTime();
	public abstract Double getPan();   /* pan value, in degrees */
	public abstract Double getTilt();  /* pan value, in degrees */
	public abstract Double getDim();   /* master dimmer value (0.0-1.0 : closed-open)*/
	public abstract Double getStrobe(); /* strobe value, in Hertz (null==no strobe) */
	
	// TODO: what direction is it pointing in, what gobos has it got on, etc...
	// could get interesting for fixtures with multiple heads, but we'll get to that later
	
	// TODO: might want to put this into a base class or something rather than 
	// implementing-up the interface like that...
	
	public String toString() {
		Color c = getColor();
		return "r=" + c.getRed()+ ", g=" + c.getGreen() + ", b=" + c.getBlue() + ", d=" + getDim();
	}
	
}
