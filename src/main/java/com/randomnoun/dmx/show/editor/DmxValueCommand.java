package com.randomnoun.dmx.show.editor;


public class DmxValueCommand extends Command {
	int channel, value;
	public DmxValueCommand(Frame frame, int channel, int value) {
		this.frame = frame;
		this.channel = channel;
		this.value = value;
		frame.recording.addDmxChannel(channel); // @TODO omit this for temp recordings
	}
	public void run() {
		frame.recording.controller.getUniverse().setDmxChannelValue(channel, value);
		// TODO Auto-generated method stub
	}
	public String toString() {
		return "DmxValueCommand on channel " + channel + " to " + value;
	}
	@Override
	public String toJava() {
		// TODO Auto-generated method stub
		return "new DmxValueCommand(frame, " + channel + ", " + value + ")";
	}
}