import java.awt.Color;

import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.channel.MacroChannelDef.Macro;
import com.randomnoun.dmx.channel.dimmer.BlueDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.GreenDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.MasterDimmerChannelDef;
import com.randomnoun.dmx.channel.dimmer.RedDimmerChannelDef;
import com.randomnoun.dmx.channel.SpeedChannelDef;
import com.randomnoun.dmx.channel.StrobeChannelDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;  

/** Fixture definition for xxxx
 * 
 * @author name
 */
public class DefaultFixtureDef extends FixtureDef {

  
  public DefaultFixtureDef() {
    this.vendor = "Chinese sweatshop workers";
    this.model = "RGB-Spot";
    this.maxWattage = 20;
    this.numDmxChannels=4;
    
    this.addChannelDef(new MasterDimmerChannelDef(0));
    this.addChannelDef(new RedDimmerChannelDef(1));
    this.addChannelDef(new GreenDimmerChannelDef(2));
    this.addChannelDef(new BlueDimmerChannelDef(3));
  }
  
  
  public ChannelMuxer getChannelMuxer(Fixture fixture) {
    return new DefaultChannelMuxer(fixture);
  }
  
  public FixtureController getFixtureController(Fixture fixture) {
    return new DefaultController(fixture);
  }
  
}
