package com.example.dmx.fixture;

import java.awt.Color;

import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;

/** The MiniWashFixtureController can have it's color and strobe controlled
 * by the default FixtureController. 
 * 
 * <p>It adds additional methods for macros, but these could be generalised
 * to the superclass.
 * 
 * @author knoxg
 */
public class MiniWashFixtureController extends FixtureController {

	public MiniWashFixtureController(Fixture fixture) {
		super(fixture);
	}
	
	public void setMacro(int index) {
		// we know this is on channel 5 for this fixture
		MacroChannelDef mcd = (MacroChannelDef) fixture.getFixtureDef().getChannelDefByOffset(5);
		Macro m = mcd.getMacros().get(index);
		fixture.setDmxChannelValue(5, (m.getLowValue() + m.getHighValue()) / 2);
	}
	
	/** Sets the color of this fixture.
	 * 
	 * This sets the red, green and blue dimmers to the values for this color,
	 * and sets the dimmer/strobe macro channel to open. 
	 * 
	 * @param color The color to set the light to.
	 */
	public void setColor(Color color) {
		fixture.setDmxChannelValue(6, color.getRed());
		fixture.setDmxChannelValue(7, color.getGreen());
		fixture.setDmxChannelValue(8, color.getBlue());
		fixture.setDmxChannelValue(5, 255);
	}
	
	public void setMasterDimmer(int value) {
		//super.setMasterDimmer(value);
		fixture.setDmxChannelValue(5, 8 + ((255-value)*126)/255);
	}
	

	public void setMovementSpeed(int i) {
		fixture.setDmxChannelValue(4, i);
	}

	public void setColorMacro(int i) {
		MacroChannelDef mcd = (MacroChannelDef) fixture.getFixtureDef().getChannelDefByOffset(9);
		fixture.setDmxChannelValue(9, mcd.getMacros().get(9).getLowValue());
	}

	public void setMovementMacro(int i) {
		MacroChannelDef mcd = (MacroChannelDef) fixture.getFixtureDef().getChannelDefByOffset(11);
		fixture.setDmxChannelValue(11, mcd.getMacros().get(11).getLowValue());
	}
	
}