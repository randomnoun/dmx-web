package com.example.dmx.fixture;

import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;

/** The X0177FixtureController can have it's color and strobe controlled
 * by the default FixtureController. 
 * 
 * <p>It adds additional methods for macros, but these could be generalised
 * to the superclass.
 * 
 * @author knoxg
 */
public class X0177FixtureController extends FixtureController {
	public X0177FixtureController(Fixture fixture) {
		super(fixture);
	}
	
	public void setMacro(int index) {
		// we know this is on channel 5 for this fixture
		MacroChannelDef mcd = (MacroChannelDef) fixture.getFixtureDef().getChannelDefByOffset(5);
		Macro m = mcd.getMacros().get(index);
		fixture.setDmxChannelValue(5, (m.getLowValue() + m.getHighValue()) / 2);
	}
}