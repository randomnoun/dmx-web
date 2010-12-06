package com.example.dmx.fixture;

import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.dimmer.BlueDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.GreenDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.MasterDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.RedDimmerChannelDef;
import com.randomnoun.dmx.channel.SpeedChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
  
/** Fixture definition for the (newer) PAR-64 spotlights at Albion 
 * (purchased 2010-10-21) t2
 * 
 * @author knoxg
 */
public class X0177FixtureDef extends FixtureDef {

  public X0177FixtureDef() {
    this.vendor = "Chinese sweatshop workers";
    this.model = "X0177";
    this.htmlLabel = "X0177 PAR 64";
    this.htmlImg16 = "par_16x16.png";
    this.htmlImg32 = "par_32x32.png";
    this.maxWattage = 20;
    
    this.minimumStrobeHertz = 0.55;
    this.maximumStrobeHertz = 4.0;
    
    this.numDmxChannels=7;
    
    this.addChannelDef(new MasterDimmerChannelDef(0));
    this.addChannelDef(new RedDimmerChannelDef(1));
    this.addChannelDef(new GreenDimmerChannelDef(2));
    this.addChannelDef(new BlueDimmerChannelDef(3));
    // 0.55Hz->4Hz strobe
    this.addChannelDef(new StrobeChannelDef(4, 0, 
      minimumStrobeHertz, 8, maximumStrobeHertz, 250)); 
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
    return new X0177ChannelMuxer(fixture);
  }
  
  public FixtureController getFixtureController(Fixture fixture) {
    return new X0177FixtureController(fixture);
  }
  
}
