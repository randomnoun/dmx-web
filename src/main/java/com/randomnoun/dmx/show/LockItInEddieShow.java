package com.randomnoun.dmx.show;

import com.randomnoun.dmx.Controller;

public class LockItInEddieShow extends Show {

	long length;
	Controller controller;
	
	public LockItInEddieShow(Controller controller) {
		super(controller, "Lock it in, Eddie", 5000);
		this.controller = controller;
	}
	
	public long getLength() { return length; }
	public void play() {}
	public void pause() {};
	public void stop() {}

}	
