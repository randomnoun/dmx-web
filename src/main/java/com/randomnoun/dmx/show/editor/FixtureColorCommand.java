package com.randomnoun.dmx.show.editor;

import java.awt.Color;
import java.util.List;

import com.randomnoun.common.Text;
import com.randomnoun.dmx.fixture.Fixture;

public class FixtureColorCommand extends FixtureCommand {
	Color color;
	public FixtureColorCommand(Frame frame, List<Fixture> fixtures, Color color) {
		super(frame, fixtures);
		this.color = color;
	}
	public void run() {
		for (Fixture f : fixtures) { f.getFixtureController().setColor(color); }
	}
	public String toJava() { 
		return "new FixtureColorCommand(frame, " + getJavaFixtureList() + ", new Color(" + getHexValue(color) + "))";
	}
	
	private String getHexValue(Color c) {
		int argb = c.getRGB();
		int b = (argb)&0xFF;
		int g = (argb>>8)&0xFF;
		int r = (argb>>16)&0xFF;
		int a = (argb>>24)&0xFF;
		return "0x" + 
			twoChars(Integer.toString(r, 16))  +
			twoChars(Integer.toString(g, 16)) +
			twoChars(Integer.toString(b, 16));
	}
	private String twoChars(String s) {
		return s.length()==2 ? s : "0" + s;
	}
	
}