package com.randomnoun.dmx.show.editor;


public class DmxValueCommand extends Command {
	int universeIdx, channel, value;
	public DmxValueCommand(Frame frame, int universeIdx, int channel, int value) {
		this.frame = frame;
		this.universeIdx = universeIdx;
		this.channel = channel;
		this.value = value;
		frame.recording.addDmxChannel(channel); // @TODO omit this for temp recordings
	}
	// @XXX backwards compatibility only
	public DmxValueCommand(Frame frame, int channel, int value) {
		this.frame = frame;
		this.universeIdx = 0;
		this.channel = channel;
		this.value = value;
		frame.recording.addDmxChannel(channel); // @TODO omit this for temp recordings
	}
	public void run() {
		frame.recording.controller.getUniverse(universeIdx).setDmxChannelValue(channel, value);
		// TODO Auto-generated method stub
	}
	public String toString() {
		return "DmxValueCommand on universe " + universeIdx + ", channel " + channel + " to " + value;
	}
	@Override
	public String toJava() {
		// TODO Auto-generated method stub
		return "new DmxValueCommand(frame, " + universeIdx + ", " + channel + ", " + value + ")";
	}
}