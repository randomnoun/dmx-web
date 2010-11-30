package com.randomnoun.dmx.fixture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.channel.BitResolution;
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
		PanPositionChannelDef pcdHigh=null, pcdLow=null;
		for (ChannelDef cd : fixtureDef.getChannelDefs()) {
			if (cd instanceof PanPositionChannelDef) {
				BitResolution br = ((PanPositionChannelDef)cd).getBitResolution(); 
				if (br==BitResolution.BYTE || br==BitResolution.WORDHIGH) {
					pcdHigh = (PanPositionChannelDef)cd;
				} else if (br==BitResolution.WORDLOW){
					pcdLow = (PanPositionChannelDef)cd;
				}
				if (pcdHigh!=null && pcdLow!=null) { break; }
			}
		}
		// @TODO sanity checks
		if (pcdHigh==null) {
			throw new UnsupportedOperationException("Default setPan implementation requires pan position channel definition for this fixture");
		}
		if (panPosition < pcdHigh.getMinAngle()) { throw new IllegalArgumentException("panPosition must be equal or greater than " + pcdHigh.getMinAngle()); }
		if (panPosition > pcdHigh.getMaxAngle()) { throw new IllegalArgumentException("panPosition must be equal or less than " + pcdHigh.getMaxAngle()); }
		if (pcdLow!=null) {
			int value = (int) ((panPosition - pcdHigh.getMinAngle()) *
					(65535) / (pcdHigh.getMaxAngle() - pcdHigh.getMinAngle()));
			fixture.setDmxChannelValue(pcdHigh.getOffset(), value >> 8);
			fixture.setDmxChannelValue(pcdLow.getOffset(), value & 255);
		} else {
			fixture.setDmxChannelValue(pcdHigh.getOffset(),
				pcdHigh.getLowDmxValue() + 
				(int) ((panPosition - pcdHigh.getMinAngle()) *
				(pcdHigh.getHighDmxValue() - pcdHigh.getLowDmxValue()) /
				(pcdHigh.getMaxAngle() - pcdHigh.getMinAngle())));
		}
	}
	

	
	/** Sets the tilt position of this fixture.
	 * 
	 * By default, this sets the major tilt position to the 
	 * closest setting to the desired angle. 
	 * 
	 * @param tiltPosition The tilt position, in degrees.
	 */
	public void tiltTo(double tiltPosition) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		TiltPositionChannelDef pcdHigh=null, pcdLow=null;
		for (ChannelDef cd : fixtureDef.getChannelDefs()) {
			if (cd instanceof TiltPositionChannelDef) {
				BitResolution br = ((TiltPositionChannelDef)cd).getBitResolution(); 
				if (br==BitResolution.BYTE || br==BitResolution.WORDHIGH) {
					pcdHigh = (TiltPositionChannelDef)cd;
				} else if (br==BitResolution.WORDLOW){
					pcdLow = (TiltPositionChannelDef)cd;
				}
				if (pcdHigh!=null && pcdLow!=null) { break; }
			}
		}
		// @TODO sanity checks
		if (pcdHigh==null) {
			throw new UnsupportedOperationException("Default setTilt implementation requires tilt position channel definition for this fixture");
		}
		if (tiltPosition < pcdHigh.getMinAngle()) { throw new IllegalArgumentException("tiltPosition must be equal or greater than " + pcdHigh.getMinAngle()); }
		if (tiltPosition > pcdHigh.getMaxAngle()) { throw new IllegalArgumentException("tiltPosition must be equal or less than " + pcdHigh.getMaxAngle()); }
		if (pcdLow!=null) {
			int value = (int) ((tiltPosition - pcdHigh.getMinAngle()) *
					(65535) / (pcdHigh.getMaxAngle() - pcdHigh.getMinAngle()));
			fixture.setDmxChannelValue(pcdHigh.getOffset(), value >> 8);
			fixture.setDmxChannelValue(pcdLow.getOffset(), value & 255);
		} else {
			fixture.setDmxChannelValue(pcdHigh.getOffset(),
				pcdHigh.getLowDmxValue() + 
				(int) ((tiltPosition - pcdHigh.getMinAngle()) *
				(pcdHigh.getHighDmxValue() - pcdHigh.getLowDmxValue()) /
				(pcdHigh.getMaxAngle() - pcdHigh.getMinAngle())));
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
	
	/** Enables this fixture's strobe setting, where value is normalised from
	 * 0 (slowest strobe setting) to 255 (highest strobe setting).
	 * 
	 * <p>Use unsetStrobe() to disable strobe.
	 * 
	 * @param value
	 */
	public void setStrobe(int value) {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		StrobeChannelDef strobeChannelDef = (StrobeChannelDef) fixtureDef.getChannelDefByClass(StrobeChannelDef.class);
		if (strobeChannelDef==null) {
			throw new UnsupportedOperationException("Default setStrobe implementation requires a strobe channel for this fixture");
		}
		fixture.setDmxChannelValue(strobeChannelDef.getOffset(),
			strobeChannelDef.getLowDmxValue() + 
			(strobeChannelDef.getHighDmxValue()-strobeChannelDef.getLowDmxValue()) * value / 255); 	
	}
	
	/** Disable's this fixture's strobe setting */
	public void unsetStrobe() {
		FixtureDef fixtureDef = fixture.getFixtureDef();
		StrobeChannelDef strobeChannelDef = (StrobeChannelDef) fixtureDef.getChannelDefByClass(StrobeChannelDef.class);
		if (strobeChannelDef==null) {
			throw new UnsupportedOperationException("Default setStrobe implementation requires a strobe channel for this fixture");
		}
		fixture.setDmxChannelValue(strobeChannelDef.getOffset(), strobeChannelDef.getDisableStrobeValue());
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
