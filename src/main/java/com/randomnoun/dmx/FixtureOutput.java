package com.randomnoun.dmx;

import java.awt.Color;

/** Things you might be interested to know that your fixture is doing.
 * 
 * @author knoxg
 */
public interface FixtureOutput {

	public Color getColor();
	public long getTime();
	
	// what direction is it pointing in, what gobos has it got on, etc...
	// could get interesting for fixtures with multiple heads, but we'll get to that later
	
}
