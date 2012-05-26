package com.randomnoun.dmx.show.editor;

import java.util.List;

import com.randomnoun.dmx.fixture.Fixture;

public class FixtureBlackoutCommand extends FixtureCommand {
	public FixtureBlackoutCommand(Frame frame, List<Fixture> fixtures) {
		super(frame, fixtures);
	}
	public void run() {
		for (Fixture f : fixtures) { f.getFixtureController().blackOut(); }
	}
	public String toJava() {
		return "new FixtureBlackoutCommand(frame, " + getJavaFixtureList() + ")";
	}
}