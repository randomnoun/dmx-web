package com.example.dmx.fixture;

import com.randomnoun.dmx.channel.LabelChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.SpeedChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
  
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
  
  
  public ChannelMuxer getChannelMuxer(Fixture fixture) {
	  return new EL5308ChannelMuxer(fixture);
  }
  
  public FixtureController getFixtureController(Fixture fixture) {
    return new EL5308FixtureController(fixture);
  }
  
}
