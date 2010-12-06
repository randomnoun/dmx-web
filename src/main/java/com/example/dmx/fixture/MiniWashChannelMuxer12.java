package com.example.dmx.fixture;

import java.awt.Color;

import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.channelMuxer.ChannelMuxerWrapper;
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
import com.randomnoun.dmx.channelMuxer.timed.ColorGradientTransition;
import com.randomnoun.dmx.channelMuxer.timed.ColorGradientDef;
import com.randomnoun.dmx.channelMuxer.timed.TimedMotionChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.TimedMotionDef;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureOutput;
import com.randomnoun.dmx.timeSource.DistortedTimeSource;
import com.randomnoun.dmx.timeSource.TimeSource;
import com.randomnoun.dmx.timeSource.UniverseTimeSource;

/** The channel muxer allows the application to determine what the fixture is
 * doing based on the current values of it's DMX channels
 */
public class MiniWashChannelMuxer12 extends ChannelMuxerWrapper {
    
  public MiniWashChannelMuxer12(Fixture fixture) {
    super(fixture);

    // TODO: this bit needs a GUI
    MiniWashFixtureDef12 fixtureDef = (MiniWashFixtureDef12) fixture.getFixtureDef();
    TimeSource universeTimeSource = new UniverseTimeSource(fixture.getUniverse());
    TimeSource distortedTimeSource = new DistortedTimeSource(fixture, universeTimeSource);
    
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

    // these would be import static in Java  
    ColorGradientTransition FADE = ColorGradientTransition.FADE;  
    ColorGradientTransition SHARP = ColorGradientTransition.SHARP;
    
    // TODO: color/position macro muxers
    // channel 9 is color macros. The colours here aren't particularly accurate.
    ChannelMuxer openMuxer2 = new NullChannelMuxer(colorMuxer);
    ChannelMuxer colorGradientMuxer =  
        new TimedColorGradientChannelMuxer(fixture, distortedTimeSource, 
        new ColorGradientDef[] {
        new ColorGradientDef(0, Color.BLUE, 1000,FADE),
        new ColorGradientDef(1000, Color.GREEN, 1000, FADE),
        new ColorGradientDef(2000, Color.CYAN, 1000, FADE),
        new ColorGradientDef(3000, Color.RED, 1000, FADE),
        new ColorGradientDef(4000, Color.MAGENTA, 1000, FADE),
      });
    MacroChannelMuxer colorMacroMuxer = 
    	new MacroChannelMuxer(fixtureDef.getChannelDefByOffset(9), colorMuxer,
    	new ChannelMuxer[] {
    	  openMuxer2,
    	  new FixedColorChannelMuxer(fixture, Color.WHITE),
    	  new FixedColorChannelMuxer(fixture, Color.RED),
    	  new FixedColorChannelMuxer(fixture, Color.GREEN),
    	  new FixedColorChannelMuxer(fixture, Color.BLUE),
    	  new FixedColorChannelMuxer(fixture, Color.CYAN),
    	  new FixedColorChannelMuxer(fixture, Color.MAGENTA),
    	  new FixedColorChannelMuxer(fixture, Color.YELLOW),
    	  new FixedColorChannelMuxer(fixture, Color.MAGENTA),  // 'PURPLE'
    	  new FixedColorChannelMuxer(fixture, Color.ORANGE),
    	  new FixedColorChannelMuxer(fixture, Color.PINK),     // 'CHARTREUSE'
    	  new FixedColorChannelMuxer(fixture, Color.PINK),
    	  new FixedColorChannelMuxer(fixture, Color.ORANGE),   // 'BROWN'
    	  new FixedColorChannelMuxer(fixture, Color.YELLOW),   // 'GOLD'
    	  new FixedColorChannelMuxer(fixture, Color.MAGENTA),  // 'CRIMSON'
    	  new FixedColorChannelMuxer(fixture, Color.MAGENTA),  // 'VIOLET'
    	  new FixedColorChannelMuxer(fixture, Color.GRAY),     // 'CREPE'
    	  colorGradientMuxer
    	});
    colorMacroMuxer.setName("colorMacroMuxer"); // debugging

    ChannelMuxer openMuxer = new NullChannelMuxer(colorMacroMuxer);
    ChannelMuxer blackMuxer = new FixedColorChannelMuxer(fixture, Color.BLACK);
    ChannelMuxer dimmerMuxer = new MasterDimmerChannelMuxer(colorMacroMuxer, 5, 134, 8);

    // the strobe for this fixture is embedded within a macro channel
    StrobeChannelMuxer strobeMuxer = 
      new StrobeChannelMuxer(colorMacroMuxer, universeTimeSource,
        fixtureDef.fakeStrobeChannelDef);
  
    MacroChannelMuxer dimmerStrobeMuxer = 
      new MacroChannelMuxer(fixtureDef.getChannelDefByOffset(5), colorMacroMuxer,
      new ChannelMuxer[] { openMuxer /* is documented as black */, 
        dimmerMuxer, strobeMuxer, openMuxer });
    
    ChannelMuxer panTiltMuxer = new MaskChannelMuxer(
      new int[] { FixtureOutput.MASK_PAN, FixtureOutput.MASK_TILT },
      new ChannelMuxer[] { panMuxer, tiltMuxer });

    ChannelMuxer autoProgram1Muxer =  
        new TimedMotionChannelMuxer(fixture, fixtureDef.getChannelDefByOffset(11), 
        	new TimedMotionDef[] {
        	new TimedMotionDef(    0, 1500, 0, 0), // 1.5 sec to get to next pos'n
        	new TimedMotionDef( 1500, 1500, 0, 180),
        	new TimedMotionDef( 3000, 1500, 0, 0),
        	new TimedMotionDef( 4500, 1500, 0, 180),
        	new TimedMotionDef( 6000, 1500, 0, 0),
        	new TimedMotionDef( 7500, 1500, 0, 180),
        	new TimedMotionDef( 9000, 1500, 270, 0),
        	new TimedMotionDef(10500, 1500, 270, 180),
        	new TimedMotionDef(12000, 1500, 270, 0),
        	new TimedMotionDef(13500, 1500, 270, 180),
        	// new TimedMotionDef(15000, 1500, 0, 0),
        });
    ChannelMuxer autoProgram3Muxer =  
        new TimedMotionChannelMuxer(fixture, fixtureDef.getChannelDefByOffset(11), 
        	new TimedMotionDef[] {
        	new TimedMotionDef(    0, 1500, 0, 0), // 1.5 sec to get to next pos'n
        	new TimedMotionDef( 1500, 1500, 0, 180),
        	new TimedMotionDef( 3000, 1500, 0, 0),
        	new TimedMotionDef( 4500, 1500, 0, 180),
        	new TimedMotionDef( 6000, 1500, 0, 0),
        	new TimedMotionDef( 7500, 1500, 0, 180),
        	new TimedMotionDef( 9000, 1500, 0, 0),
        	new TimedMotionDef(10500, 1500, 0, 180),
        	new TimedMotionDef(12000, 1500, 0, 0),
        	new TimedMotionDef(13500, 2000, 0, 180),
        	new TimedMotionDef(15500, 2000, 450, 0),
        	//new TimedMotionDef(17500, 1500, 0, 0),
        });

    MacroChannelMuxer motionMacroMuxer =
        new MacroChannelMuxer(fixtureDef.getChannelDefByOffset(11), panTiltMuxer,
        new ChannelMuxer[] {
        	panTiltMuxer,
        	autoProgram1Muxer,
        	autoProgram1Muxer,  // autoProgram2 appears to be same as autoProgram1
        	autoProgram3Muxer,
        	autoProgram3Muxer,
        	autoProgram3Muxer,
        	autoProgram3Muxer,
        	autoProgram3Muxer,
        	autoProgram3Muxer,
        	autoProgram1Muxer,  // is documented as Sound Active 1
        	autoProgram1Muxer,  // is documented as Sound Active 2
        	autoProgram3Muxer,  // is documented as Sound Active 3
        	autoProgram3Muxer,  // is documented as Sound Active 4
        	autoProgram3Muxer,  // is documented as Sound Active 5
        	autoProgram3Muxer,  // is documented as Sound Active 6
        	autoProgram3Muxer,  // is documented as Sound Active 7
        	autoProgram3Muxer,  // is documented as Sound Active 8
        });
    motionMacroMuxer.setName("motionMacroMuxer"); // debugging    		
    
    
    ChannelMuxer maskMuxer = new MaskChannelMuxer(
      new int[] {
        FixtureOutput.MASK_COLOR,
        FixtureOutput.MASK_PAN | FixtureOutput.MASK_TILT
      },
      new ChannelMuxer[] {
        dimmerStrobeMuxer,
        motionMacroMuxer
      });
    
    setChannelMuxer(maskMuxer);
  }
    
}