package com.randomnoun.dmx.show.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.fixture.Fixture;

public class Recording {
	Controller controller;  // for playback
	Set<Fixture> fixtures; // fixtures affected by this recording
	Set<Integer> dmxChannels; // dmx channels affected by this recording
	
	List<Frame> frames;  // list of frames in this recording
	int currentFrame;   // current frame number
	public Recording(Controller controller) {
		this.controller = controller;
		frames = new ArrayList<Frame>();
		fixtures = new HashSet<Fixture>();
		dmxChannels = new HashSet<Integer>();
		currentFrame = -1;
	}
	// invoked by FancyControllerAction to assign a real controller
	// after object construction (we delay assign the real controller
	// to prevent any show actions taking place during object initialisation)
	
	public void setController(Controller controller) {
		this.controller = controller;
	}
	public List<Fixture> getModifiedFixtures() { return new ArrayList(fixtures); }
	public List<Integer> getModifiedDmxChannels() { return new ArrayList(dmxChannels); }
	public void addFixtures(List<Fixture> fixtures) {
		this.fixtures.addAll(fixtures); 
	}
	public void addDmxChannel(int dmxChannel) { 
		dmxChannels.add(dmxChannel);  // @TODO HashSet this
	}
	/** Insert this frame after current frame position */
	public void addFrame(Frame f) {
		frames.add(currentFrame + 1, f);
		currentFrame = currentFrame + 1;
	}
	public List<Frame> getFrames() { return frames; }
	/** Add a command to the current frame */
	public void addCommand(Command c) {
		getCurrentFrame().addCommand(c);
	}
	public Frame getCurrentFrame() { return frames.get(currentFrame); }
	public int getCurrentFrameIndex() { return currentFrame; }
	public void setCurrentFrameIndex(int currentFrame) { this.currentFrame = currentFrame; }
	public String toJava() {
		String s = "Recording recording = new Recording(controller);\n";
		for (int i=0; i<frames.size(); i++) {
			s += frames.get(i).toJava() + "\n";
		}
		s += "return recording;\n";
		return s;
	}
}