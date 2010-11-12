package com.randomnoun.dmx.fixture.compiled;

import java.awt.Color;

import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.dimmer.BlueDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.GreenDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.MasterDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.RedDimmerChannelDef;
import com.randomnoun.dmx.channel.SpeedChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.channelMuxer.MacroChannelMuxer;
import com.randomnoun.dmx.channelMuxer.filter.MasterDimmerChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.ColorChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.StrobeChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.TimedColorGradientChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.timeSource.DistortedTimeSource;
import com.randomnoun.dmx.timeSource.TimeSource;
import com.randomnoun.dmx.timeSource.UniverseTimeSource;

/** Fixture definition for the (newer) PAR-64 spotlights at Albion 
 * (purchased 2010-10-21)
 * 
 * @author knoxg
 */
public class X0177FixtureDef extends FixtureDef {


	/** The X0177FixtureController can have it's color and strobe controlled
	 * by the default FixtureController. 
	 * 
	 * <p>It adds additional methods for macros, but these could be generalised
	 * to the superclass.
	 * 
	 * @author knoxg
	 */
	public static class X0177FixtureController extends FixtureController {
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

	
	public X0177FixtureDef() {
		this.vendor = "Chinese sweatshop workers";
		this.model = "X0177";
		this.maxWattage = 20;
		
		this.numDmxChannels=7;
		
		this.addChannelDef(new MasterDimmerChannelDef(0));
		this.addChannelDef(new RedDimmerChannelDef(1));
		this.addChannelDef(new GreenDimmerChannelDef(2));
		this.addChannelDef(new BlueDimmerChannelDef(3));
		this.addChannelDef(new StrobeChannelDef(4, 0, 2, 8, 10, 250)); // 2Hz->10Hz strobe
		MacroChannelDef mcd = new MacroChannelDef(5);
		mcd.addMacro(new Macro("rainbow color change, fade transition", 96, 127));
		mcd.addMacro(new Macro("RBG color change, no transition", 128, 159));
		mcd.addMacro(new Macro("RGB color change, flash each color to black 3 times", 160, 191));
		mcd.addMacro(new Macro("rainbow color change, no transition", 192, 255));
		this.addChannelDef(mcd);
		this.addChannelDef(new SpeedChannelDef(6, 0, 255, 1.0, 3.0));
	}
	
	
	// TODO: this may allow fixture/channel definitions to get access to muxers, 
	// which seems like a bad design decision.
	
	// TODO: this bit needs a GUI
	@Override
	public ChannelMuxer getChannelMuxer(Fixture fixture) {
		TimeSource universeTimeSource = new UniverseTimeSource(fixture.getUniverse());
		TimeSource distortedTimeSource = new DistortedTimeSource(fixture, universeTimeSource);
		
		// output is determined by
		//   color DMX values + strobe DMX value
		// which may be overridden by one of the macro DMX values, if set
		// which can be sped up by the macro speed DMX value
		// 
		// and the fixture can then be dimmed by the master dimmer DMX value
		
		ChannelMuxer colorMuxer = new ColorChannelMuxer(fixture);
		StrobeChannelMuxer strobeMuxer = new StrobeChannelMuxer(colorMuxer, universeTimeSource);
		
		// (I'm assuming here we don't strobe the output from macros, 
		// but that we are able to fade them)
		ChannelMuxer macro0Muxer = new TimedColorGradientChannelMuxer(fixture, distortedTimeSource, new TimedColorGradientChannelMuxer.ColorGradientDef[] {
			new TimedColorGradientChannelMuxer.ColorGradientDef(0, Color.BLUE, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.FADE),
			new TimedColorGradientChannelMuxer.ColorGradientDef(1000, Color.GREEN, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.FADE),
			new TimedColorGradientChannelMuxer.ColorGradientDef(2000, Color.CYAN, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.FADE),
			new TimedColorGradientChannelMuxer.ColorGradientDef(3000, Color.RED, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.FADE),
			new TimedColorGradientChannelMuxer.ColorGradientDef(4000, Color.MAGENTA, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.FADE),
		});
		ChannelMuxer macro1Muxer = new TimedColorGradientChannelMuxer(fixture, distortedTimeSource, new TimedColorGradientChannelMuxer.ColorGradientDef[] {
			new TimedColorGradientChannelMuxer.ColorGradientDef(0, Color.RED, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(1000, Color.BLUE, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(2000, Color.GREEN, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
		});
		ChannelMuxer macro2Muxer = new TimedColorGradientChannelMuxer(fixture, distortedTimeSource, new TimedColorGradientChannelMuxer.ColorGradientDef[] {
			new TimedColorGradientChannelMuxer.ColorGradientDef(0, Color.RED, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(500, Color.BLACK, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(1000, Color.RED, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(1500, Color.BLACK, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(2000, Color.RED, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(2500, Color.BLACK, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(3000, Color.GREEN, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(3500, Color.BLACK, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(4000, Color.GREEN, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(4500, Color.BLACK, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(5000, Color.GREEN, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(5500, Color.BLACK, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(6000, Color.BLUE, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(6500, Color.BLACK, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(7000, Color.BLUE, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(7500, Color.BLACK, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(8000, Color.BLUE, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(8500, Color.BLACK, 500, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
		});
		ChannelMuxer macro3Muxer = new TimedColorGradientChannelMuxer(fixture, distortedTimeSource, new TimedColorGradientChannelMuxer.ColorGradientDef[] {
			new TimedColorGradientChannelMuxer.ColorGradientDef(0, Color.BLUE, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(1000, Color.GREEN, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(2000, Color.CYAN, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(3000, Color.RED, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
			new TimedColorGradientChannelMuxer.ColorGradientDef(4000, Color.MAGENTA, 1000, TimedColorGradientChannelMuxer.ColorGradientTransition.SHARP),
		});
		MacroChannelMuxer macroMuxer = new MacroChannelMuxer(strobeMuxer, 
			new ChannelMuxer[] { macro0Muxer, macro1Muxer, macro2Muxer, macro3Muxer });
		
		return new MasterDimmerChannelMuxer(macroMuxer);
	}
	
	public FixtureController getFixtureController(Fixture fixture) {
		return new X0177FixtureController(fixture);
	}
	
}
