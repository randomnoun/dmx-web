package com.randomnoun.dmx.protocol.dmxUsbPro;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.event.DmxUpdateEvent;
import com.randomnoun.dmx.event.DmxValueDumper;
import com.randomnoun.dmx.event.UniverseUpdateListener;
import com.randomnoun.dmx.event.DmxValueDumper.DmxValueDumperThread;

/** A class which listens for changes to a universe, and then 
 * passes those changes through to a physical UsbProWidget device
 *  
 * @TODO thread this so that it doesn't send out changes after every channel change 
 *  
 * @author knoxg
 */
public class UsbProWidgetUniverseUpdateListener implements UniverseUpdateListener {

	static Logger logger = Logger.getLogger(UsbProWidgetUniverseUpdateListener.class);
	
	UsbProWidgetTranslator translator = null;
	byte dmxState[];
		
	UsbProUpdaterThread t = null;
	
	// @TODO obvious thread safety problems
	public static class UsbProUpdaterThread extends Thread {
		UsbProWidgetUniverseUpdateListener upuul;
		boolean done = false;
		boolean hasChanged = false;
		long startTime = 0;
		
		public UsbProUpdaterThread(UsbProWidgetUniverseUpdateListener upuul) {
			this.upuul = upuul;
		}
		
		public void run() {
			startTime = System.currentTimeMillis();
			while (!done) {
				if (hasChanged) {
					hasChanged = false;  // @TODO race condition
					try {
						logger.debug("Sending DMX data");
						upuul.translator.sendOutputOnlySendDMXPacketRequest((byte) 0, upuul.dmxState);
					} catch (IOException e) {
						done = true;
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
		}
		public void done() {
			done = true;
		}
		public void notifyChange() {
			hasChanged = true;
		}
	}
	
	
	public UsbProWidgetUniverseUpdateListener(UsbProWidgetTranslator widget) {
		this.translator = widget;
		dmxState =  new byte[Universe.MAX_CHANNELS];
	}
	
	public void onEvent(DmxUpdateEvent event) {
		dmxState[event.getDmxChannel()] = (byte) event.getValue();
		t.notifyChange();
	}
	
	public void startThread() {
		if (t!=null) { throw new IllegalStateException("Thread already started"); }
		t = new UsbProUpdaterThread(this);
		t.start();
	}
	
	public void stopThread() {
		if (t!=null) { t.done(); }
	}

}
