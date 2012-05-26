package com.randomnoun.dmx.show.editor;

import java.util.List;

import com.randomnoun.common.Text;
import com.randomnoun.dmx.fixture.Fixture;

public abstract class FixtureCommand extends Command {
	List<Fixture> fixtures;
	public FixtureCommand(Frame frame, List<Fixture> fixtures) {
		this.frame = frame;
		this.fixtures = fixtures;
		frame.recording.addFixtures(fixtures); // @TODO omit this for temp recordings
	}
	public String toString() {
		return Text.getLastComponent(this.getClass().getName()) + " on " + fixtures.size() + " fixtures";
	}
	public String getJavaFixtureList() {
		// @TODO: try varargs method signatures. later.
		// alt: getById ? 
		// @TODO: refactor these things when people change fixture names ?
		String s = "controller.getFixturesByName(new String[] {";
		for (int i=0; i<fixtures.size(); i++) {
			s += (i==0 ? "" : ", ") + "\"" + Text.escapeJava(fixtures.get(i).getName()) + "\"";
		}
		s += "})";
		return s;
	}
}