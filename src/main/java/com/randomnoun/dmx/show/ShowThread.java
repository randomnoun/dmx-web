package com.randomnoun.dmx.show;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.config.AppConfig.AppConfigState;
import com.randomnoun.dmx.web.action.FancyControllerAction.RecordingPlaybackShow;

public class ShowThread extends Thread {

	static Logger logger = Logger.getLogger(ShowThread.class);
	
	private Show show;
	private ShowAudioSource showAudioSource;
	
	public ShowThread(Show show, ShowAudioSource showAudioSource) {
		this.show = show;
		this.showAudioSource = showAudioSource;
	}
	

	public void run() {
		show.audioSource = showAudioSource;
		show.state = Show.State.SHOW_RUNNING;
		AppConfig appConfig = AppConfig.getAppConfig();
		long onCancelShowId = show.getOnCancelShowId();
		long onCompleteShowId = show.getOnCompleteShowId();
		boolean onWithTheShow = true;
		while (onWithTheShow) {
			try {
				showAudioSource.open(); // listen for the beat
				show.state = Show.State.SHOW_RUNNING;
				logger.debug("Playing show '" + show.getName() + "'");
				show.internalReset();
				show.play();
				show.state = Show.State.SHOW_STOPPED; // although the thread is still running, really...
				onWithTheShow=false;
				if (show.isCancelled()) { 
					logger.debug("Show '" + show.getName() + "' was cancelled");
					// @TODO if this is the same show (*really bad idea*), then just loop this method
					if (onCancelShowId!=-1 && appConfig.getAppConfigState()==AppConfigState.RUNNING) {
						logger.debug("Show '" + show.getName() + "' requesting onCancelShowId " + onCancelShowId + " to start");
						AppConfig.getAppConfig().startShow(onCancelShowId);
					}
				} else {
					logger.debug("Show '" + show.getName() + "' completed");
					if (onCompleteShowId!=-1 && appConfig.getAppConfigState()==AppConfigState.RUNNING) {
						if (onCompleteShowId==show.getId()) {
							logger.debug("Show '" + show.getName() + "' restarting");
							onWithTheShow=true;
						} else {
							logger.debug("Show " + show.getId() + " '" + show.getName() + "' requesting onCompletedShowId " + onCompleteShowId + " to start");
							AppConfig.getAppConfig().startShow(onCompleteShowId);
						}
					}
				}
			} catch (Exception e) {
				onWithTheShow = false;
				logger.debug("Show '" + show.getName() + "' threw an exception", e);
				show.state = Show.State.SHOW_STOPPED_WITH_EXCEPTION;
				show.setLastException(e);
				AppConfig.getAppConfig().addShowException(show, e);
				if (onCancelShowId!=-1 && appConfig.getAppConfigState()==AppConfigState.RUNNING) {
					logger.debug("Show '" + show.getName() + "' requesting onCancelShowId " + onCancelShowId + " to start due to exception");
					AppConfig.getAppConfig().startShow(onCancelShowId);
				}
			} finally {
				showAudioSource.close();
			}
		}
		if (show instanceof RecordingPlaybackShow) {
			RecordingPlaybackShow rps = (RecordingPlaybackShow) show;
			appConfig.stopRecordingPlaybackShowCallback(rps);
		}
	}
	
	public void cancel() {
		logger.debug("Cancelling show '" + show.getName() + "'");
		show.cancel();
		
		// block if this is a recorded show (possibly make this a param?)
    	if (show instanceof RecordingPlaybackShow) {
    		// this method is always called by a different thread. isn't it.
    		if (isAlive()) {
	    		logger.info("... recordingPlaybackShow still running (1)");
	    		try { Thread.sleep(100); } catch (InterruptedException ie) { }
	    		if (isAlive()) {
	    			logger.info("... recordingPlaybackShow still running (2)");
	    			stop();
	    			try { Thread.sleep(100); } catch (InterruptedException ie) { }
	    			logger.info("... recordingPlaybackShow: running: " + isAlive());
	    		}
    		}
    	}

	}
	
}
