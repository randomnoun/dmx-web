package com.randomnoun.dmx.show.editor;

import java.util.ArrayList;
import java.util.List;


public class Frame {
	Recording recording;
	List<Command> commands; // list of commands to perform in this frame
	public Frame(Recording recording) { 
		this.recording = recording;
		commands = new ArrayList<Command>();
	}
	public void addCommand(Command c) {
		commands.add(c);
	}
	public List<Command> getCommands() { return commands; }
	public String toString() {
		String s="";
		for (int i=0; i<commands.size(); i++) {
			s += "Command " + i + ":" + commands.get(i).toString() + "\n";
		}
		return s;
	}
	public String toJava() {
		String s = "frame = new Frame(recording);\n";
		for (int i=0; i<commands.size(); i++) {
			s += "frame.addCommand(" + commands.get(i).toJava() + ");\n";
		}
		s += "recording.addFrame(frame);\n";
		return s;
	}
}