package com.randomnoun.dmx.show.editor;

import java.util.List;

import com.randomnoun.dmx.fixture.Fixture;

public class FixtureDimCommand extends FixtureCommand {
	int value;
	public FixtureDimCommand(Frame frame, List<Fixture> fixtures, int value) {
		super(frame, fixtures);
		this.value = value;
	}
	public void run() {
		for (Fixture f : fixtures) { f.getFixtureController().setMasterDimmer(value); }
	}
	
	// at this point I could:
	// (a) reconstruct the Recording object, which would make this *much* easier to edit later#
	// (b) execute the commands directly, which might be easier to read, if anyone did that.
	//
	// also: probably collapse multiple commands on the same fixture into one, but that,
	// as they say, can wait until another day.
	public String toJava() {
		return "new FixtureDimCommand(frame, " + getJavaFixtureList() + ", " + value + ")";
	}
}