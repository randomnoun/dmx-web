package com.randomnoun.dmx.show;

import org.apache.log4j.Logger;

public class ShowThread extends Thread {

	static Logger logger = Logger.getLogger(ShowThread.class);
	
	private Show show;
	
	public ShowThread(Show show) {
		this.show = show;
	}
	
	public void run() {
		logger.debug("Playing show '" + show.getName() + "'");
		show.play();
		if (show.isCancelled()) { 
			logger.debug("Show '" + show.getName() + "' was cancelled");
		} else {
			logger.debug("Show '" + show.getName() + "' completed");
		}
	}
	
	public void cancel() {
		logger.debug("Cancelling show '" + show.getName() + "'");
		show.cancel();
	}
	
}
