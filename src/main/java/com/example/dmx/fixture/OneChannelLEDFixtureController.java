package com.example.dmx.fixture;

import java.awt.Color;

import com.randomnoun.dmx.channel.ChannelDef;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.show.ShowUtils;

public class OneChannelLEDFixtureController extends FixtureController {
	public OneChannelLEDFixtureController(Fixture fixture) {
		super(fixture);
	}

	/**
	 * Sets the color of this fixture.
	 * 
	 * For black or white, set to fixed DMX value, for others, find 
	 * the closest hue/intensity value and set to that.
	 * 
	 * @param color The color to set the light to.
	 */
	public void setColor(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		FixtureDef fixtureDef = fixture.getFixtureDef();
		ChannelDef channelDef = (ChannelDef) fixtureDef.getChannelDefByOffset(0);
		if (r<=16 && g<=16 && b<=16) { 
			fixture.setDmxChannelValue(0, 0);
		} else if (r>=239 && g>=239 && b>=239) {
			fixture.setDmxChannelValue(0, 1);
		} else {
			int[] hsv = new int[3];
			ShowUtils.rgb2hsv(r, g, b, hsv);
			if (hsv[2]<=7) { 
				fixture.setDmxChannelValue(0, 0);
			} else {
				// ignore saturation, rescale hue and intensity between 0 & 15
				// low bits 0-4 are hue
				// high bits 5-8 are intensity
				int hue = Math.max(((int)(hsv[0]/22.5)), 15);
				int intensity = Math.max(((int)(hsv[2]/6.25)), 15);
				fixture.setDmxChannelValue(0, (intensity<<4) | hue);
			}
			
				
		}
	}

}