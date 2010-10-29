package com.randomnoun.dmx;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channel.dimmer.BlueDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.GreenDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.MasterDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.RedDimmerChannelDef;

/** An object which can be used to control a fixture using
 * relatively simple commands
 * 
 * @return
 */
public abstract class FixtureController {

	protected Fixture fixture;
	
	public FixtureController(Fixture fixture) {
		this.fixture = fixture;
	}
	
	/** Sets the color of this fixture.
	 * 
	 * By default, this sets the red, green and blue dimmers to the values for this color,
	 * and if a master dimmer is available for the fixture, it is set to 100%. 
	 * 
	 * @param color The color to set the light to.
	 */
	public void setColor(Color color) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		DimmerChannelDef redDimmerChannelDef = (DimmerChannelDef) fixtureDef.getChannelDefByClass(RedDimmerChannelDef.class);
		DimmerChannelDef greenDimmerChannelDef = (DimmerChannelDef) fixtureDef.getChannelDefByClass(GreenDimmerChannelDef.class);
		DimmerChannelDef blueDimmerChannelDef = (DimmerChannelDef) fixtureDef.getChannelDefByClass(BlueDimmerChannelDef.class);
		DimmerChannelDef masterDimmerChannelDef = (DimmerChannelDef) fixtureDef.getChannelDefByClass(MasterDimmerChannelDef.class);
		
		if (redDimmerChannelDef==null || 
			greenDimmerChannelDef==null ||
			blueDimmerChannelDef==null) 
		{
			throw new UnsupportedOperationException("Default setColor implementation requires red, green and blue dimmers for this fixture");
		}
		fixture.setDmxChannelValue(redDimmerChannelDef.getOffset(), color.getRed());
		fixture.setDmxChannelValue(greenDimmerChannelDef.getOffset(), color.getGreen());
		fixture.setDmxChannelValue(blueDimmerChannelDef.getOffset(), color.getBlue());
		
		if (masterDimmerChannelDef!=null) {
			fixture.setDmxChannelValue(masterDimmerChannelDef.getOffset(), 255);
		}
	}
	
	/** Sets the strobe for this fixture to the fastest setting available.
	 * If the fixture does not support strobe, an UnsupportedOperationException is thrown. 
	 */
	public void strobe() {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		StrobeChannelDef strobeChannelDef = (StrobeChannelDef) fixtureDef.getChannelDefByClass(StrobeChannelDef.class);
		if (strobeChannelDef==null) {
			throw new UnsupportedOperationException("Default strobe implementation requires strobe channel for this fixture");
		}
		fixture.setDmxChannelValue(strobeChannelDef.getOffset(), strobeChannelDef.getMaximumStrobeValue());
	}
	
	/** Sets the master dimmer value of this fixture to the specified value
	 * 
	 * @param value
	 */
	public void setMasterDimmer(int value) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		DimmerChannelDef masterDimmerChannelDef = (DimmerChannelDef) fixtureDef.getChannelDefByClass(MasterDimmerChannelDef.class);		
		// 
		if (masterDimmerChannelDef==null) {
			throw new UnsupportedOperationException("Default setMasterDimmer implementation requires a master dimmer channel for this fixture");
		}
		fixture.setDmxChannelValue(masterDimmerChannelDef.getOffset(), value);
	}
	
	/** Sets all channels for this fixture to 0 */
	public void blackOut() {
		// which is different to setting the color to black
		// this.setColor(Color.BLACK);
		
		for (int i=0; i<fixture.getFixtureDef().getNumDmxChannels(); i++){
			fixture.setDmxChannelValue(i, 0);
		}
	}
}
