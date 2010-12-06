package com.example.dmx.fixture;

import java.awt.Color;

import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.channelMuxer.ChannelMuxerWrapper;
import com.randomnoun.dmx.channelMuxer.MacroChannelMuxer;
import com.randomnoun.dmx.channelMuxer.MaskChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.FixedColorChannelMuxer;
import com.randomnoun.dmx.channelMuxer.primitive.FixedPositionChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.StrobeChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.ColorGradientTransition;
import com.randomnoun.dmx.channelMuxer.timed.TimedMotionChannelMuxer;
import com.randomnoun.dmx.channelMuxer.timed.TimedMotionDef;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.fixture.FixtureOutput;
import com.randomnoun.dmx.timeSource.DistortedTimeSource;
import com.randomnoun.dmx.timeSource.TimeSource;
import com.randomnoun.dmx.timeSource.UniverseTimeSource;

/**
 * The channel muxer allows the application to determine what the fixture is
 * doing based on the current values of it's DMX channels
 */
public class EL5308ChannelMuxer extends ChannelMuxerWrapper {

	public EL5308ChannelMuxer(Fixture fixture) {
		super(fixture);

		// TODO: this bit needs a GUI
		FixtureDef fixtureDef = fixture.getFixtureDef();
		TimeSource universeTimeSource = new UniverseTimeSource(fixture.getUniverse());
		TimeSource distortedTimeSource = new DistortedTimeSource(fixture, universeTimeSource);

		// these would be import static in Java
		ColorGradientTransition FADE = ColorGradientTransition.FADE;
		ColorGradientTransition SHARP = ColorGradientTransition.SHARP;

		// output is determined by
		// fancy 1,
		// then strobe maybe
		// then fancy 2
		// then maybe the head channels
		// then maybe the speed control
		// this documentation is woeful.

		// ChannelMuxer colorMuxer = new ColorChannelMuxer(fixture);
		ChannelMuxer colorMuxer = new FixedColorChannelMuxer(fixture,
			Color.WHITE);

		// this is all completely conjecture
		ChannelMuxer whirlingMuxer = new TimedMotionChannelMuxer(fixture,
			fixtureDef.getChannelDefByOffset(0), new TimedMotionDef[] {
			new TimedMotionDef(0, 1500, 0, 0), // 1.5 sec to get to next pos'n
			new TimedMotionDef(1500, 1500, 0, 360),
			new TimedMotionDef(3000, 1500, 360, 360),
			new TimedMotionDef(4500, 1500, 360, 0) });

		ChannelMuxer fancy1Muxer = new MacroChannelMuxer(
			fixtureDef.getChannelDefByOffset(0), colorMuxer,
			new ChannelMuxer[] {
				new FixedPositionChannelMuxer(fixture, 0, 0),
				whirlingMuxer });

		StrobeChannelMuxer strobeMuxer = new StrobeChannelMuxer(colorMuxer,
			universeTimeSource);

		ChannelMuxer maskMuxer = new MaskChannelMuxer(new int[] {
			FixtureOutput.MASK_COLOR,
			FixtureOutput.MASK_PAN | FixtureOutput.MASK_TILT },
			new ChannelMuxer[] { strobeMuxer, fancy1Muxer });

		setChannelMuxer(maskMuxer);
	}

}