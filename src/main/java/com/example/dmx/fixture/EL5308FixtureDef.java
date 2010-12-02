package com.example.dmx.fixture;

import java.awt.Color;

import com.randomnoun.dmx.channel.LabelChannelDef;
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
import com.randomnoun.dmx.channelMuxer.MaskChannelMuxer;
import com.randomnoun.dmx.channelMuxer.filter.MasterDimmerChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.ColorChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.FixedColorChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.FixedPositionChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.StrobeChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.TimedColorGradientChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.ColorGradientTransition;
import com.randomnoun.dmx.channelMuxer.timed.ColorGradientDef;
import com.randomnoun.dmx.channelMuxer.timed.TimedMotionChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.TimedMotionDef;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.fixture.FixtureOutput;
import com.randomnoun.dmx.timeSource.DistortedTimeSource;
import com.randomnoun.dmx.timeSource.TimeSource;
import com.randomnoun.dmx.timeSource.UniverseTimeSource;
  
/** Fixture definition for the EL5308 7-headed hydra at Albion 
 * (purchased 2010-10-21)
 * 
 * @author knoxg
 */
public class EL5308FixtureDef extends FixtureDef {

	
  private MacroChannelDef getHeadChannelDef(int dmxChannel, String htmlLabel) {
	MacroChannelDef mcd = new MacroChannelDef(dmxChannel);
	mcd.addMacro(new Macro("undocumented macros", 0, 10));
	mcd.addMacro(new Macro("some of which change colors", 11, 20));
	mcd.addMacro(new Macro("and some of which auto-run", 21, 30));
	mcd.addMacro(new Macro("and some of which full light", 31, 255));
	mcd.setHtmlLabel(htmlLabel);
	return mcd;
  }

  public EL5308FixtureDef() {
    this.vendor = "Chinese sweatshop workers";
    this.model = "EL5308";
    this.maxWattage = 30;
    this.panRange = 360;
    this.tiltRange = 360;
    
    this.numDmxChannels=11;
    
    MacroChannelDef fancyChannelDef = new MacroChannelDef(0);
	fancyChannelDef.addMacro(new Macro("stillness", 0, 10));
	fancyChannelDef.addMacro(new Macro("whirling", 11, 255));
	fancyChannelDef.setHtmlLabel("Fancy 1");
    this.addChannelDef(fancyChannelDef);
    
    // 0.55Hz->4Hz strobe
    this.addChannelDef(new StrobeChannelDef(1, 0, 0.55, 8, 4.0, 250));
    this.addChannelDef(new LabelChannelDef(2, null, "Fancy 2"));
    
    this.addChannelDef(getHeadChannelDef(3, "Head 1 Macro"));
    this.addChannelDef(getHeadChannelDef(4, "Head 2 Macro"));
    this.addChannelDef(getHeadChannelDef(5, "Head 3 Macro"));
    this.addChannelDef(getHeadChannelDef(6, "Head 4 Macro"));
    this.addChannelDef(getHeadChannelDef(7, "Head 5 Macro"));
    this.addChannelDef(getHeadChannelDef(8, "Head 6 Macro"));
    
    // the following is documented as "VII of the first adjustment"
    this.addChannelDef(getHeadChannelDef(9, "Head 7 Macro"));
    
    this.addChannelDef(new SpeedChannelDef(10, 0, 255, 1.0, 3.0));
  }
  
  
  // TODO: this bit needs a GUI
  public ChannelMuxer getChannelMuxer(Fixture fixture) {
	FixtureDef fixtureDef = fixture.getFixtureDef();
    TimeSource universeTimeSource = new UniverseTimeSource(fixture.getUniverse());
    TimeSource distortedTimeSource = new DistortedTimeSource(fixture, universeTimeSource);

    // these would be import static in Java  
    ColorGradientTransition FADE = ColorGradientTransition.FADE;  
    ColorGradientTransition SHARP = ColorGradientTransition.SHARP;
  
    // output is determined by
    //   fancy 1, 
    //   then strobe maybe
    //   then fancy 2
    //   then maybe the head channels
    //   then maybe the speed control
    // this documentation is woeful.
    
    //ChannelMuxer colorMuxer = new ColorChannelMuxer(fixture);
    ChannelMuxer colorMuxer = new FixedColorChannelMuxer(fixture, Color.WHITE);

    // this is all completely conjecture
    ChannelMuxer whirlingMuxer =  
        new TimedMotionChannelMuxer(fixture, fixtureDef.getChannelDefByOffset(0), 
        	new TimedMotionDef[] {
        	new TimedMotionDef(    0, 1500, 0, 0), // 1.5 sec to get to next pos'n
        	new TimedMotionDef( 1500, 1500, 0, 360),
        	new TimedMotionDef( 3000, 1500, 360, 360),
        	new TimedMotionDef( 4500, 1500, 360, 0) });
    
    ChannelMuxer fancy1Muxer = 
        new MacroChannelMuxer(fixtureDef.getChannelDefByOffset(0), colorMuxer,
        	new ChannelMuxer[] {
        	new FixedPositionChannelMuxer(fixture, 0, 0),
        	whirlingMuxer
        } );


    StrobeChannelMuxer strobeMuxer = 
        new StrobeChannelMuxer(colorMuxer, universeTimeSource);
    
    ChannelMuxer maskMuxer = new MaskChannelMuxer(
    	      new int[] {
    	        FixtureOutput.MASK_COLOR,
    	        FixtureOutput.MASK_PAN | FixtureOutput.MASK_TILT
    	      },
    	      new ChannelMuxer[] {
    	        strobeMuxer,
    	        fancy1Muxer
    	      });
    	    
    return maskMuxer;

  }
  
  public FixtureController getFixtureController(Fixture fixture) {
    return new EL5308FixtureController(fixture);
  }
  
}
