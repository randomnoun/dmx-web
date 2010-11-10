package com.randomnoun.dmx.show;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.config.AppConfig;

public class ShowThread extends Thread {

	static Logger logger = Logger.getLogger(ShowThread.class);
	
	private Show show;
	
	public ShowThread(Show show) {
		this.show = show;
	}
	
	// @TODO do something with exceptions that occur in shows here
	public void run() {
		logger.debug("Playing show '" + show.getName() + "'");
		try {
			show.play();
			if (show.isCancelled()) { 
				logger.debug("Show '" + show.getName() + "' was cancelled");
			} else {
				logger.debug("Show '" + show.getName() + "' completed");
			}
		} catch (Exception e) {
			logger.debug("Show '" + show.getName() + "' threw an exception", e);
			AppConfig.getAppConfig().addShowException(show, e);
		}
	}
	
	public void cancel() {
		logger.debug("Cancelling show '" + show.getName() + "'");
		show.cancel();
	}
	
}
