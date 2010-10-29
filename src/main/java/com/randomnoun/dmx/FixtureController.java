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
		fixture.setDmxChannel(redDimmerChannelDef.getOffset(), color.getRed());
		fixture.setDmxChannel(greenDimmerChannelDef.getOffset(), color.getGreen());
		fixture.setDmxChannel(blueDimmerChannelDef.getOffset(), color.getBlue());
		
		if (masterDimmerChannelDef!=null) {
			fixture.setDmxChannel(masterDimmerChannelDef.getOffset(), 255);
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
		fixture.setDmxChannel(strobeChannelDef.getOffset(), strobeChannelDef.getMaximumStrobeValue());
	}
	
	public void blackOut() {
		this.setColor(Color.BLACK);
	}
}
