package com.example.dmx.fixture;

import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.dimmer.BlueDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.GreenDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.RedDimmerChannelDef;
import com.randomnoun.dmx.channel.rotation.AngularSpeedChannelDef;
import com.randomnoun.dmx.channel.BitResolution;
import com.randomnoun.dmx.channel.rotation.PanPositionChannelDef;
import com.randomnoun.dmx.channel.rotation.TiltPositionChannelDef;
import com.randomnoun.dmx.channel.SpeedChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;

/** Fixture definition for the Mini-wash moving heads at Albion
 * (purchased 2010-10-21), in 12-channel mode.
 *
 * @author knoxg
 */
public class MiniWashFixtureDef12 extends FixtureDef {

  // is contained within the MacroChannelDef on offset 5
  StrobeChannelDef fakeStrobeChannelDef;  
  
  public MiniWashFixtureDef12() {

    this.vendor = "Chinese sweatshop workers";
    this.model = "Miniwash";
    this.length = 300;
    this.width = 240;
    this.height= 240;
    this.weight = 3800;
    this.maxAmbientTemperature = 40;
    this.maxWattage = 68;

    this.panRange = 540;
    this.tiltRange = 180;
    this.minimumStrobeHertz = 0.6;
    this.maximumStrobeHertz = 4.0;

    this.numDmxChannels = 12;
    
    BitResolution WORDHIGH = BitResolution.WORDHIGH;
    BitResolution WORDLOW = BitResolution.WORDLOW; 
  
    // 16-bit 0->540deg pan
    this.addChannelDef(new PanPositionChannelDef(0, WORDHIGH, 0, this.panRange));  
    this.addChannelDef(new PanPositionChannelDef(1, WORDLOW, 0, this.panRange));  
    // 16-bit 0->180deg tilt
    this.addChannelDef(new TiltPositionChannelDef(2, WORDHIGH, 0, this.tiltRange)); 
    this.addChannelDef(new TiltPositionChannelDef(3, WORDLOW, 0, this.tiltRange));  
  
    // channel 4 affects speed at which target pan/tilt position is reached
    // pan speeds:
    // DMX values:           0    64   128   192   224   255   
    // Time to pan 360deg:   2100 2500 3500  5600  9500  27200
    // 
    // tilt speeds:
    // DMX values:           0    64   128   192   224   255
    // Time to tilt 180deg:  1700 1700 1700  2200  3200  29500
  
    this.addChannelDef(new AngularSpeedChannelDef(4, 
      new int[] { 0, 64, 128, 192, 224, 255 }, 
      new double[] { 360 / (double) 2100, 
        360 / (double) 2500, 
        360 / (double) 3500,  
        360 / (double) 5600, 
        360 / (double) 9500, 
        360 / (double) 27200 },
      new double[] { 180 / (double) 1700,
        180 / (double) 1700,
        180 / (double) 1700,
        180 / (double) 2200,
        180 / (double) 3200,
        180 / (double) 29500 }));

    MacroChannelDef dimmerStrobeChannelDef = new MacroChannelDef(5);
    dimmerStrobeChannelDef.addMacro(new Macro("open", 0, 7));  // is documented as closed
    dimmerStrobeChannelDef.addMacro(new Macro("100-0%", 8, 134));
    dimmerStrobeChannelDef.addMacro(new Macro("strobe slow-fast", 135, 239));
    dimmerStrobeChannelDef.addMacro(new Macro("open", 240, 255));
    dimmerStrobeChannelDef.setHtmlLabel("Dimmer/Strobe");
    this.addChannelDef(dimmerStrobeChannelDef);
  
    // this is used by the muxer to perform strobes (TODO: combine into Macros above)
    fakeStrobeChannelDef = new StrobeChannelDef(5, 0, 
    	minimumStrobeHertz, 135, maximumStrobeHertz, 239); // 0.6Hz->4Hz strobe
  
    this.addChannelDef(new RedDimmerChannelDef(6));
    this.addChannelDef(new GreenDimmerChannelDef(7));
    this.addChannelDef(new BlueDimmerChannelDef(8));
    
    // channel 9 is color macros
    MacroChannelDef mcd = new MacroChannelDef(9);
    mcd.addMacro(new Macro("No Function", 0, 7));
    mcd.addMacro(new Macro("white", 8, 21));
    mcd.addMacro(new Macro("red", 22, 35));  // NB: different to spec
    mcd.addMacro(new Macro("green", 36, 49));
    mcd.addMacro(new Macro("blue", 50, 63));
    mcd.addMacro(new Macro("cyan", 64, 77));
    mcd.addMacro(new Macro("magenta", 78, 91));
    mcd.addMacro(new Macro("yellow", 92, 105));  
    mcd.addMacro(new Macro("purple", 106, 110)); 
    mcd.addMacro(new Macro("orange", 120, 133));
    mcd.addMacro(new Macro("chartreuse", 134, 147));
    mcd.addMacro(new Macro("pink", 148, 161));
    mcd.addMacro(new Macro("brown", 162, 175));
    mcd.addMacro(new Macro("gold", 176, 189));
    mcd.addMacro(new Macro("crimson", 190, 203));
    mcd.addMacro(new Macro("violet", 204, 217));
    mcd.addMacro(new Macro("crepe", 218, 231));
    mcd.addMacro(new Macro("color-changemacro", 232, 255));
    mcd.setHtmlLabel("Color macro");
    this.addChannelDef(mcd);
    
    // channel 10 alters the speed of the color-changemacro above
    this.addChannelDef(new SpeedChannelDef(10, 0, 255, 1.0, 3.0));
  
    // channel 11 is movement macros
    mcd = new MacroChannelDef(11);
    mcd.addMacro(new Macro("No Function", 0, 7));
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
    mcd.setHtmlLabel("Movement macro");
    this.addChannelDef(mcd);
  }
  
  
  public ChannelMuxer getChannelMuxer(Fixture fixture) {
	  return new MiniWashChannelMuxer12(fixture);
  }
  
  public FixtureController getFixtureController(Fixture fixture) {
    return new MiniWashFixtureController(fixture);
  }
  
}
