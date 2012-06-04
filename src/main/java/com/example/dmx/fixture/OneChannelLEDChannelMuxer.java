package com.example.dmx.fixture;

import java.awt.Color;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.channelMuxer.ChannelMuxerWrapper;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureOutput;
import com.randomnoun.dmx.fixture.KnownFixtureOutput;
import com.randomnoun.dmx.show.ShowUtils; // @TODO rename this

/** The channel muxer allows the application to determine what the fixture is
 * doing based on the current values of it's DMX channels
 */
public class OneChannelLEDChannelMuxer extends ChannelMuxerWrapper {
     
	Logger logger = Logger.getLogger(OneChannelLEDChannelMuxer.class);
	KnownFixtureOutput output;
	  
	public OneChannelLEDChannelMuxer(Fixture fixture) {
	    super(fixture);
	    output = new KnownFixtureOutput(0, Color.BLACK, 1.0, 0.0, null, null, null, null);
	}
  
    public FixtureOutput getOutput() {
	  
	    // @TODO could modify this so that output only set when 
	    // dmx values change (for non-timed output)
        int value = fixture.getDmxChannelValue(0);
        // logger.debug("fixture=" + fixture.getName() + ", value=" + value);
	    switch (value) {
			case 0: output.setColor(Color.BLACK); break;
			case 1: output.setColor(Color.WHITE); break;
			// 2-15 fancy macros
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				long t = fixture.getUniverse().getTime();
				output.setTime(t);
				float cycle = ((float) (t%5000))/5000;
				output.setColor(new Color(0, cycle, 0));
				break;
				
			default:
				// going to HSV the Color from whatever bits we have left
				// low bits 0-4 are intensity
				// high bits 5-8 are hue
					float intensity = (float) (value & 15);
					float hue = (float) (value >> 4);    			
					int[] rgb = new int[4];
					ShowUtils.hsv2rgb(hue/16, 1, intensity/16, rgb);
					output.setColor(new Color(rgb[0], rgb[1], rgb[2]));
					
	    }
	    return output;
    }
    
}	

	    