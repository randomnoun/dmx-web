package com.example.dmx.fixture;

import java.awt.Color;

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
import com.randomnoun.dmx.channel.LabelChannelDef;
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
import com.randomnoun.dmx.timeSource.TimeSource;
import com.randomnoun.dmx.timeSource.UniverseTimeSource;

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
    this.panRange = 540;
    this.tiltRange = 180;
    this.maxAmbientTemperature = 40;
    this.maxWattage = 68;

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
    this.addChannelDef(dimmerStrobeChannelDef);
  
    // this is used by the muxer to perform strobes (TODO: combine into Macros above)
    fakeStrobeChannelDef = new StrobeChannelDef(5, 0, 0.6, 135, 4, 239); // 0.6Hz->4Hz strobe
  
    this.addChannelDef(new RedDimmerChannelDef(6));
    this.addChannelDef(new GreenDimmerChannelDef(7));
    this.addChannelDef(new BlueDimmerChannelDef(8));
    
    // channel 9 is color macros
    MacroChannelDef mcd = new MacroChannelDef(9);
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
    this.addChannelDef(mcd);
    
    // channel 10 alters the speed of the color-changemacro above
    this.addChannelDef(new LabelChannelDef(10, null, "VectorSpeed (Color)"));
  
    // channel 11 is movement macros
    mcd = new MacroChannelDef(11);
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
    this.addChannelDef(mcd);
  }
  
  
  // TODO: this may allow fixture/channel definitions to get access to muxers,
  // which seems like a bad design decision.
  
  // TODO: this bit needs a GUI
  public ChannelMuxer getChannelMuxer(Fixture fixture) {
    MiniWashFixtureDef12 fixtureDef = (MiniWashFixtureDef12) fixture.getFixtureDef();
    TimeSource universeTimeSource = new UniverseTimeSource(fixture.getUniverse());
    
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

    ChannelMuxer openMuxer = new NullChannelMuxer(colorMuxer);
    ChannelMuxer blackMuxer = new FixedColorChannelMuxer(fixture, Color.BLACK);
    ChannelMuxer dimmerMuxer = new MasterDimmerChannelMuxer(colorMuxer, 5, 134, 8);
    
    // the strobe for this fixture is embedded within a macro channel
    StrobeChannelMuxer strobeMuxer = 
      new StrobeChannelMuxer(colorMuxer, universeTimeSource,
        fixtureDef.fakeStrobeChannelDef);
  
    MacroChannelMuxer dimmerStrobeMuxer = 
      new MacroChannelMuxer(fixtureDef.getChannelDefByOffset(5), colorMuxer,
      new ChannelMuxer[] { openMuxer /* is documented as black */, 
        dimmerMuxer, strobeMuxer, openMuxer });
    
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
