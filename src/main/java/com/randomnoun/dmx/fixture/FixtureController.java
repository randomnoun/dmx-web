package com.randomnoun.dmx.fixture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channel.dimmer.BlueDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.GreenDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.MasterDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.RedDimmerChannelDef;
import com.randomnoun.dmx.channel.rotation.AngularTransitionSpeedChannelDef;
import com.randomnoun.dmx.channel.rotation.PanPositionChannelDef;
import com.randomnoun.dmx.channel.rotation.TiltPositionChannelDef;

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
		
		if (masterDimmerChannelDef==null) {
			// look for a master dimmer in a macro channel definition
			// hmm... going to put this into a FixtureController subclass rather than 
			// typing the Macros within a MacroChannelDef
			/*
			for (ChannelDef channelDef : fixtureDef.getChannelDefs()) {
				if (channelDef instanceof MacroChannelDef) {
					MacroChannelDef mcd = (MacroChannelDef) channelDef;
					for (Macro macro : mcd.getMacros()) {
						if (macro.)
					}
				}
			}
			*/
		} else {
			fixture.setDmxChannelValue(masterDimmerChannelDef.getOffset(), 255);
		}
	}

	/** Sets the pan position of this fixture.
	 * 
	 * By default, this sets the major pan position to the 
	 * closest setting to the desired angle. 
	 * 
	 * @param pan The pan position, in degrees.
	 */
	public void panTo(double panPosition) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		PanPositionChannelDef ppcd = (PanPositionChannelDef) fixtureDef.getChannelDefByClass(PanPositionChannelDef.class);
		
		if (ppcd==null) {
			throw new UnsupportedOperationException("Default setPan implementation requires pan position channel definition for this fixture");
		}
		if (panPosition < ppcd.getMinAngle()) { throw new IllegalArgumentException("panPosition must be equal or greater than " + ppcd.getMinAngle()); }
		if (panPosition > ppcd.getMaxAngle()) { throw new IllegalArgumentException("panPosition must be equal or less than " + ppcd.getMaxAngle()); }
		fixture.setDmxChannelValue(ppcd.getOffset(),
			ppcd.getLowDmxValue() + 
			(int) ((panPosition - ppcd.getMinAngle()) *
			(ppcd.getHighDmxValue() - ppcd.getLowDmxValue()) /
			(ppcd.getMaxAngle() - ppcd.getMinAngle())));
	}
	

	
	/** Sets the pan position of this fixture.
	 * 
	 * By default, this sets the major pan position to the 
	 * closest setting to the desired angle. 
	 * 
	 * @param pan The pan position, in degrees.
	 */
	public void tiltTo(double tiltPosition) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		TiltPositionChannelDef tpcd = (TiltPositionChannelDef) fixtureDef.getChannelDefByClass(TiltPositionChannelDef.class);
		
		if (tpcd==null) {
			throw new UnsupportedOperationException("Default setPan implementation requires pan position channel definition for this fixture");
		}
		if (tiltPosition < tpcd.getMinAngle()) { throw new IllegalArgumentException("tiltPosition must be equal or greater than " + tpcd.getMinAngle()); }
		if (tiltPosition > tpcd.getMaxAngle()) { throw new IllegalArgumentException("tiltPosition must be equal or less than " + tpcd.getMaxAngle()); }
		fixture.setDmxChannelValue(tpcd.getOffset(),
			tpcd.getLowDmxValue() + 
			(int) ((tiltPosition - tpcd.getMinAngle()) *
			(tpcd.getHighDmxValue() - tpcd.getLowDmxValue()) /
			(tpcd.getMaxAngle() - tpcd.getMinAngle()) ));
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
		FixtureDef fixtureDef = fixture.getFixtureDef();
		for (int i = 0; i < fixtureDef.getNumDmxChannels(); i++){
			fixture.setDmxChannelValue(i, 0);
		}
	}
	
	public Fixture getFixture() {
		return fixture;
	}
	
	public FixtureDef getFixtureDef() {
		return fixture.getFixtureDef();
	}
	

}
