
import java.awt.Color;

import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.channelMuxer.ChannelMuxerWrapper;
import com.randomnoun.dmx.channelMuxer.MacroChannelMuxer;
import com.randomnoun.dmx.channelMuxer.filter.MasterDimmerChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.ColorChannelMuxer;
import com.randomnoun.dmx.fixture.Fixture;

/** The channel muxer allows the application to determine what the fixture is
 * doing based on the current values of it's DMX channels
 */
public class DefaultChannelMuxer extends ChannelMuxerWrapper {
    
  public DefaultChannelMuxer(Fixture fixture) {
    super(fixture);
    ChannelMuxer colorMuxer = new ColorChannelMuxer(fixture);
    setChannelMuxer(new MasterDimmerChannelMuxer(colorMuxer));
  }
    
}
