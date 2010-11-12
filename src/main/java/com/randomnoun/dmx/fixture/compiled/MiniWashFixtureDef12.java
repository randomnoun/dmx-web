package com.randomnoun.dmx.fixture.compiled;

import java.awt.Color;

import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.dimmer.BlueDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.DimmerChannelDef.DimmerType;
import com.randomnoun.dmx.channel.dimmer.GreenDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.MasterDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.RedDimmerChannelDef;
import com.randomnoun.dmx.channel.rotation.AngularPanSpeedChannelDef;
import com.randomnoun.dmx.channel.rotation.AngularTiltSpeedChannelDef;
import com.randomnoun.dmx.channel.rotation.AngularTransitionSpeedChannelDef;
import com.randomnoun.dmx.channel.rotation.PanPositionChannelDef;
import com.randomnoun.dmx.channel.rotation.TiltPositionChannelDef;
import com.randomnoun.dmx.channel.SpeedChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.channelMuxer.MacroChannelMuxer;
import com.randomnoun.dmx.channelMuxer.MaskChannelMuxer;
import com.randomnoun.dmx.channelMuxer.filter.MasterDimmerChannelMuxer;
import com.randomnoun.dmx.channelMuxer.filter.NullChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.ColorChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.FixedColorChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.PanPositionChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.TiltPositionChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.StrobeChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.TimedColorGradientChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.fixture.FixtureOutput;
import com.randomnoun.dmx.timeSource.DistortedTimeSource;
import com.randomnoun.dmx.timeSource.TimeSource;
import com.randomnoun.dmx.timeSource.UniverseTimeSource;

/** Fixture definition for the Mini-wash moving heads at Albion 
 * (purchased 2010-10-21), in 12-channel mode.
 * 
 * @author knoxg
 */
public class MiniWashFixtureDef12 extends MiniWashFixtureDefBase {


	/** The MiniWashFixtureController can have it's color and strobe controlled
	 * by the default FixtureController. 
	 * 
	 * <p>It adds additional methods for macros, but these could be generalised
	 * to the superclass.
	 * 
	 * @author knoxg
	 */
	public static class MiniWashFixtureController extends FixtureController {
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

		public void setMovementSpeed(int i) {
			fixture.setDmxChannelValue(4, i);
		}

		public void setColorMacro(int i) {
			throw new UnsupportedOperationException("setColorMacro not implemented");
		}

		public void setMovementMacro(int i) {
			throw new UnsupportedOperationException("setMovementMacro not implemented");		
		}
		
	}

	
		
	public MiniWashFixtureDef12() {
		this.numDmxChannels = 12;
		// perhaps these are absolute pan/tilt values ?
		//this.addChannelDef(new AngularPanSpeedChannelDef(0, 0, 255, 0, 20));  // 0->255 == still->20 degrees/sec
		//this.addChannelDef(new AngularPanSpeedChannelDef(1, 0, 255, 0, 5));  // 0->255 == still->5 degrees/sec
		//this.addChannelDef(new AngularTiltSpeedChannelDef(2, 0, 255, 0, 20));  // 0->255 == still->20 degrees/sec
		//this.addChannelDef(new AngularTiltSpeedChannelDef(3, 0, 255, 0, 5));  // 0->255 == still->5 degrees/sec

		// These are major/minor pan values,  
		// - the major pan position breaks the pan arc into even segments, and 
		// - the minor pan position breaks the major pan position into even segments, and
		//     that the target pan value is the addition of these two values
		// major pan/tilt position (255) is highest rotation
		//   i.e. minor position has no effect when major is set to 255 
		double majorPanStep = this.panRange / 256; double minorPanStep = majorPanStep / 256;
		double majorTiltStep = this.tiltRange / 256; double minorTiltStep = majorTiltStep / 256;
		this.addChannelDef(new PanPositionChannelDef(0, 0, this.panRange /*- majorPanStep*/));  // 0->255 == 0 to 540degrees
		// TODO: multiple pan channels
		//this.addChannelDef(new PanPositionChannelDef(1, 0, majorPanStep));     // 0->255 == 0->540/255 degrees
		this.addChannelDef(new TiltPositionChannelDef(2, 0, this.tiltRange /*- majorTiltStep*/));  // 0->255 == 0 to 180degrees
		// TODO: multiple tilt channels
		//this.addChannelDef(new TiltPositionChannelDef(3, 0, majorTiltStep));  // 0->255 == 0->180/255 degrees
		
		// channel 4 affects speed at which target pan/tilt position is reached
		this.addChannelDef(new AngularTransitionSpeedChannelDef(4, 0, 255, 2, 2, 10, 10));  // 0->255 == slow->fast; (2 degrees/second->10 degrees/second)

		MacroChannelDef dimmerStrobeChannelDef = new MacroChannelDef(5);
		dimmerStrobeChannelDef.addMacro(new Macro("closed", 0, 7));
		dimmerStrobeChannelDef.addMacro(new Macro("100-0%", 8, 134));
		dimmerStrobeChannelDef.addMacro(new Macro("strobe slow-fast", 135, 239));
		dimmerStrobeChannelDef.addMacro(new Macro("open", 240, 255));
		this.addChannelDef(dimmerStrobeChannelDef);
		this.addChannelDef(new RedDimmerChannelDef(6));
		this.addChannelDef(new GreenDimmerChannelDef(7));
		this.addChannelDef(new BlueDimmerChannelDef(8));
		
		// channel 9 is color macros
		MacroChannelDef mcd = new MacroChannelDef(9);
		mcd.addMacro(new Macro("white", 8, 21));
		mcd.addMacro(new Macro("red", 22, 34));  // NB: gap in numbering system
		mcd.addMacro(new Macro("green", 36, 49));
		mcd.addMacro(new Macro("blue", 50, 63));
		mcd.addMacro(new Macro("cyan", 64, 77));
		mcd.addMacro(new Macro("magenta", 78, 91));
		mcd.addMacro(new Macro("yellow", 92, 105));  // NB: possible overlap (105/106)
		mcd.addMacro(new Macro("purple", 106, 110)); // NB: possible overlap (105/106)
		mcd.addMacro(new Macro("orange", 120, 133));
		mcd.addMacro(new Macro("chartreuse", 134, 147));
		mcd.addMacro(new Macro("pink", 148, 161));
		mcd.addMacro(new Macro("brown", 162, 175));
		mcd.addMacro(new Macro("gold", 176, 189));
		mcd.addMacro(new Macro("crimson", 190, 203));
		mcd.addMacro(new Macro("violet", 204, 217));
		mcd.addMacro(new Macro("crepe", 218, 231));
		mcd.addMacro(new Macro("color-changemacro", 232, 255));
		this.addChannelDef(mcd);
		
		// channel 10 is a vectorspeed (color) channel. Hue?
		
		// channel 11 is movement macros
		mcd = new MacroChannelDef(9);
		mcd.addMacro(new Macro("Auto Program 1", 8, 22));
		mcd.addMacro(new Macro("Auto Program 2", 23, 37));
		mcd.addMacro(new Macro("Auto Program 3", 38, 52));
		mcd.addMacro(new Macro("Auto Program 4", 53, 67));
		mcd.addMacro(new Macro("Auto Program 5", 68, 82));
		mcd.addMacro(new Macro("Auto Program 6", 83, 97));
		mcd.addMacro(new Macro("Auto Program 7", 98, 112));
		mcd.addMacro(new Macro("Auto Program 8", 113, 127));
		mcd.addMacro(new Macro("Sound Active 1", 128, 142));
		mcd.addMacro(new Macro("Sound Active 2", 143, 157));
		mcd.addMacro(new Macro("Sound Active 3", 158, 172));
		mcd.addMacro(new Macro("Sound Active 4", 173, 187));
		mcd.addMacro(new Macro("Sound Active 5", 188, 202));
		mcd.addMacro(new Macro("Sound Active 6", 203, 217));
		mcd.addMacro(new Macro("Sound Active 7", 218, 232));
		mcd.addMacro(new Macro("Sound Active 8", 133, 127));
	}
	
	
	// TODO: this may allow fixture/channel definitions to get access to muxers, 
	// which seems like a bad design decision.
	
	// TODO: this bit needs a GUI
	@Override
	public ChannelMuxer getChannelMuxer(Fixture fixture) {
		TimeSource universeTimeSource = new UniverseTimeSource(fixture.getUniverse());
		//TimeSource distortedTimeSource = new DistortedTimeSource(fixture, universeTimeSource);
		
		// output is determined by
		//   color: color DMX values, 
		//      can be overridden by color macro (which can be sped up by colorspeed DMX)
		//   pan: (pan+pan fine), transitions by vector speed DMX values
		//      can be overridden by movement macro
      	//   tilt: (tilt+tilt fine), transitions by vector speed DMX values 
		//      can be overridden by movement macro
		//   
		// and then the whole fixture can be dimmed or strobed by the DimmerStrobe DMX
		
		ChannelMuxer colorMuxer = new ColorChannelMuxer(fixture);
		ChannelMuxer panMuxer = new PanPositionChannelMuxer(fixture);
		ChannelMuxer tiltMuxer = new TiltPositionChannelMuxer(fixture);

		ChannelMuxer blackMuxer = new FixedColorChannelMuxer(fixture, Color.BLACK);
		ChannelMuxer dimmerMuxer = new MasterDimmerChannelMuxer(colorMuxer, 4, 134, 8);
		
		// guessing .5second -> .1second strobe speed
		StrobeChannelDef scd = new StrobeChannelDef(4, 0, 2, 135, 10, 239);
		// TODO: strobeMuxers that can find strobeChannels inside MacroChannelDefs
		//StrobeChannelMuxer strobeMuxer = new StrobeChannelMuxer(colorMuxer, universeTimeSource);
		ChannelMuxer openMuxer = new NullChannelMuxer(colorMuxer);
		MacroChannelMuxer dimmerStrobeMuxer = new MacroChannelMuxer(colorMuxer,
			new ChannelMuxer[] { blackMuxer, dimmerMuxer, /*strobeMuxer*/ dimmerMuxer, openMuxer });
		
		// TODO: color/position macro muxers

		ChannelMuxer maskMuxer = new MaskChannelMuxer(
			new int[] { 
				FixtureOutput.MASK_COLOR, 
				FixtureOutput.MASK_PAN, 
				FixtureOutput.MASK_TILT 
			},
			new ChannelMuxer[] { 
				dimmerStrobeMuxer, 
				panMuxer, 
				tiltMuxer 
			});

		
		return maskMuxer;
	}
	
	public FixtureController getFixtureController(Fixture fixture) {
		return new MiniWashFixtureController(fixture);
	}
	
}
