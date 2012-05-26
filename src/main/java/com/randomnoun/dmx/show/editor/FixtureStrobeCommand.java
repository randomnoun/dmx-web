package com.randomnoun.dmx.show.editor;

import java.util.List;

import com.randomnoun.dmx.fixture.Fixture;

public class FixtureStrobeCommand extends FixtureCommand {
	int value;
	public FixtureStrobeCommand(Frame frame, List<Fixture> fixtures, int value) {
		super(frame, fixtures);
		this.value = value;
	}
	public void run() {
		for (Fixture f : fixtures) { f.getFixtureController().setStrobe(value); }
	}
	public String toJava() {
		return "new FixtureStrobeCommand(frame, " + getJavaFixtureList() + ", " + value + ")";
	}
}