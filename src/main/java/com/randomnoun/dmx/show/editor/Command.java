package com.randomnoun.dmx.show.editor;

import com.randomnoun.common.Text;

public abstract class Command {
	Frame frame;
	public abstract void run();      // run this command
	public abstract String toJava(); // this command, as represented in a Show definition class
	public String toString() {
		return Text.getLastComponent(this.getClass().getName());
	}
}