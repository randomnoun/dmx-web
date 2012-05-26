package com.randomnoun.dmx.show.editor;

import java.util.List;

import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;

public class FixtureCustomControlCommand extends FixtureCommand {
	int controlId, value;
	public FixtureCustomControlCommand(Frame frame, List<Fixture> fixtures, int controlId, int value) {
		super(frame, fixtures);
		this.controlId = controlId;
		this.value = value;
	}
	public void run() {
		for (Fixture f : fixtures) { 
			FixtureController fc = f.getFixtureController();
			fc.getCustomControls().get(controlId).setValueWithCallback(value);
		}
	}
	public String toJava() {
		return "new FixtureCustomControlCommand(frame, " + getJavaFixtureList() + ", " + controlId + ", " + value + ")";
	}
}