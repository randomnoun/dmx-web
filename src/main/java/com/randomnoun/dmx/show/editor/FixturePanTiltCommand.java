package com.randomnoun.dmx.show.editor;

import java.util.List;

import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureDef;

public class FixturePanTiltCommand extends FixtureCommand {
	double panPosition, tiltPosition;
	public FixturePanTiltCommand(Frame frame, List<Fixture> fixtures, double panPosition, double tiltPosition) {
		super(frame, fixtures);
		this.panPosition = panPosition;
		this.tiltPosition = tiltPosition;
	}
	public void run() {
		for (Fixture f : fixtures) { 
			// @TODO as %age of pan/tilt range; see below
			FixtureDef fd = f.getFixtureDef();
			f.getFixtureController().panTo(fd.getPanRange() * panPosition / 100); 
			f.getFixtureController().tiltTo(fd.getTiltRange() * tiltPosition / 100);
		}
	}
	public String toJava() {
		return "new FixturePanTiltCommand(frame, " + getJavaFixtureList() + ", " + panPosition + ", " + tiltPosition + ")";
	}
}