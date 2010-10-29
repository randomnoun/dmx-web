package com.randomnoun.dmx;

import java.awt.Color;

/** Things you might be interested to know that your fixture is doing.
 * 
 * @author knoxg
 */
public abstract class FixtureOutput {

	public abstract Color getColor();
	public abstract long getTime();
	
	// TODO: what direction is it pointing in, what gobos has it got on, etc...
	// could get interesting for fixtures with multiple heads, but we'll get to that later
	
	// TODO: might want to put this into a base class or something rather than 
	// implementing-up the interface like that...
	
	public String toString() {
		Color c = getColor();
		return "r=" + c.getRed()+ ", g=" + c.getGreen() + ", b=" + c.getBlue();
	}
	
}
