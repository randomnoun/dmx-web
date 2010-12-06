package com.example.dmx.fixture;

import java.awt.Color;

import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.channelMuxer.ChannelMuxerWrapper;
import com.randomnoun.dmx.channelMuxer.MacroChannelMuxer;
import com.randomnoun.dmx.channelMuxer.filter.MasterDimmerChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.ColorChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.StrobeChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.TimedColorGradientChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.ColorGradientTransition;
import com.randomnoun.dmx.channelMuxer.timed.ColorGradientDef;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.timeSource.DistortedTimeSource;
import com.randomnoun.dmx.timeSource.TimeSource;
import com.randomnoun.dmx.timeSource.UniverseTimeSource;

/** The channel muxer allows the application to determine what the fixture is
 * doing based on the current values of it's DMX channels
 */
public class X0177ChannelMuxer extends ChannelMuxerWrapper {
    
  public X0177ChannelMuxer(Fixture fixture) {
    super(fixture);
    
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
    
    setChannelMuxer(new MasterDimmerChannelMuxer(macroMuxer));
  }
    
}