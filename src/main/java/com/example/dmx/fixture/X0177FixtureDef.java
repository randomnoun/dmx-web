package com.example.dmx.fixture;

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
import com.randomnoun.dmx.channelMuxer.timed.ColorGradientTransition;
import com.randomnoun.dmx.channelMuxer.timed.ColorGradientDef;
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


  public X0177FixtureDef() {
    this.vendor = "Chinese sweatshop workers";
    this.model = "X0177";
    this.maxWattage = 20;
    
    this.numDmxChannels=7;
    
    this.addChannelDef(new MasterDimmerChannelDef(0));
    this.addChannelDef(new RedDimmerChannelDef(1));
    this.addChannelDef(new GreenDimmerChannelDef(2));
    this.addChannelDef(new BlueDimmerChannelDef(3));
    // 0.55Hz->4Hz strobe
    this.addChannelDef(new StrobeChannelDef(4, 0, 0.55, 8, 4.0, 250)); 
    MacroChannelDef mcd = new MacroChannelDef(5);
    mcd.addMacro(new Macro("rainbow color change, fade transition", 96, 127));
    mcd.addMacro(new Macro("RBG color change, no transition", 128, 159));
    mcd.addMacro(new Macro("RGB color change, flash each color to black 3 times", 160, 191));
    mcd.addMacro(new Macro("rainbow color change, no transition", 192, 255));
    this.addChannelDef(mcd);
    this.addChannelDef(new SpeedChannelDef(6, 0, 255, 1.0, 3.0));
  }
  
  
  // TODO: this bit needs a GUI
  public ChannelMuxer getChannelMuxer(Fixture fixture) {
    TimeSource universeTimeSource = new UniverseTimeSource(fixture.getUniverse());
    TimeSource distortedTimeSource = new DistortedTimeSource(fixture, universeTimeSource);

    // these would be import static in Java  
    ColorGradientTransition FADE = ColorGradientTransition.FADE;  
    ColorGradientTransition SHARP = ColorGradientTransition.SHARP;
  
    // output is determined by
    //   color DMX values + strobe DMX value
    // which may be overridden by one of the macro DMX values, if set
    // which can be sped up by the macro speed DMX value
    // 
    // and the fixture can then be dimmed by the master dimmer DMX value
    
    ChannelMuxer colorMuxer = new ColorChannelMuxer(fixture);
    StrobeChannelMuxer strobeMuxer = 
      new StrobeChannelMuxer(colorMuxer, universeTimeSource);
    
    // (I'm assuming here we don't strobe the output from macros, 
    // but that we are able to fade them)
    ChannelMuxer macro0Muxer = 
      new TimedColorGradientChannelMuxer(fixture, distortedTimeSource, 
      new ColorGradientDef[] {
      new ColorGradientDef(0, Color.BLUE, 1000,FADE),
      new ColorGradientDef(1000, Color.GREEN, 1000, FADE),
      new ColorGradientDef(2000, Color.CYAN, 1000, FADE),
      new ColorGradientDef(3000, Color.RED, 1000, FADE),
      new ColorGradientDef(4000, Color.MAGENTA, 1000, FADE),
    });
    ChannelMuxer macro1Muxer = 
      new TimedColorGradientChannelMuxer(fixture, distortedTimeSource, 
      new ColorGradientDef[] {
      new ColorGradientDef(0, Color.RED, 1000, SHARP),
      new ColorGradientDef(1000, Color.BLUE, 1000, SHARP),
      new ColorGradientDef(2000, Color.GREEN, 1000, SHARP),
    });
    ChannelMuxer macro2Muxer = 
      new TimedColorGradientChannelMuxer(fixture, distortedTimeSource, 
      new ColorGradientDef[] {
      new ColorGradientDef(0, Color.RED, 500, SHARP),
      new ColorGradientDef(500, Color.BLACK, 500, SHARP),
      new ColorGradientDef(1000, Color.RED, 500, SHARP),
      new ColorGradientDef(1500, Color.BLACK, 500, SHARP),
      new ColorGradientDef(2000, Color.RED, 500, SHARP),
      new ColorGradientDef(2500, Color.BLACK, 500, SHARP),
      new ColorGradientDef(3000, Color.GREEN, 500, SHARP),
      new ColorGradientDef(3500, Color.BLACK, 500, SHARP),
      new ColorGradientDef(4000, Color.GREEN, 500, SHARP),
      new ColorGradientDef(4500, Color.BLACK, 500, SHARP),
      new ColorGradientDef(5000, Color.GREEN, 500, SHARP),
      new ColorGradientDef(5500, Color.BLACK, 500, SHARP),
      new ColorGradientDef(6000, Color.BLUE, 500, SHARP),
      new ColorGradientDef(6500, Color.BLACK, 500, SHARP),
      new ColorGradientDef(7000, Color.BLUE, 500, SHARP),
      new ColorGradientDef(7500, Color.BLACK, 500, SHARP),
      new ColorGradientDef(8000, Color.BLUE, 500, SHARP),
      new ColorGradientDef(8500, Color.BLACK, 500, SHARP),
    });
    ChannelMuxer macro3Muxer = 
      new TimedColorGradientChannelMuxer(fixture, distortedTimeSource, 
      new ColorGradientDef[] {
      new ColorGradientDef(0, Color.BLUE, 1000, SHARP),
      new ColorGradientDef(1000, Color.GREEN, 1000, SHARP),
      new ColorGradientDef(2000, Color.CYAN, 1000, SHARP),
      new ColorGradientDef(3000, Color.RED, 1000, SHARP),
      new ColorGradientDef(4000, Color.MAGENTA, 1000, SHARP),
    });
    MacroChannelMuxer macroMuxer = new MacroChannelMuxer(strobeMuxer, 
      new ChannelMuxer[] { macro0Muxer, macro1Muxer, macro2Muxer, macro3Muxer });
    
    return new MasterDimmerChannelMuxer(macroMuxer);
  }
  
  public FixtureController getFixtureController(Fixture fixture) {
    return new X0177FixtureController(fixture);
  }
  
}
