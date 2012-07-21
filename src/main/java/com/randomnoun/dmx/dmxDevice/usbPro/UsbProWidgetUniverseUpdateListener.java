package com.randomnoun.dmx.dmxDevice.usbPro;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.event.DmxUpdateEvent;
import com.randomnoun.dmx.event.UniverseUpdateListener;

/** A class which listens for changes to a universe, and then 
 * passes those changes through to a physical UsbProWidget device
 *  
 * @author knoxg
 */
public class UsbProWidgetUniverseUpdateListener implements UniverseUpdateListener {

	static Logger logger = Logger.getLogger(UsbProWidgetUniverseUpdateListener.class);
	
	private static int threadCount = 0; // show always be 0
	
	private UsbProWidget widget = null;
	private UsbProWidgetTranslator translator = null;
	byte dmxState[];
		
	UsbProUpdaterThread t = null;
	
	// @TODO less obvious thread safety problems
	// @TODO maybe have a single thread for all DmxDevices ?
	public static class UsbProUpdaterThread extends Thread {
		UsbProWidgetUniverseUpdateListener upuul;
		boolean done = false;
		boolean hasChanged = false;
		long startTime = 0;
		
		public UsbProUpdaterThread(UsbProWidgetUniverseUpdateListener upuul) {
			super("UsbProThread-" + threadCount);
			this.upuul = upuul;
			threadCount++;
		}
		
		public void run() {
			startTime = System.currentTimeMillis();
			while (!done) {
				if (hasChanged) {
					// race condition here maybe
					hasChanged = false;
					try {
						logger.debug("Sending DMX data");
						upuul.getTranslator().sendOutputOnlySendDMXPacketRequest((byte) 0, upuul.dmxState);
					} catch (IOException e) {
						done = true;
						upuul.widget.exceptionContainer.addException(e);
						e.printStackTrace();
					} catch (NullPointerException npe) {
						logger.debug("No UsbProWidgetTranslator instance; stopping UsbProUpdaterThread");
						done = true;
					}
				}
				try {
					// sleeping here so that we're not sending a universe DMX update for every individual channel update
					// @TODO sync with whatever update rate the device wants
					Thread.sleep(50);
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
	
	
	public UsbProWidgetUniverseUpdateListener(UsbProWidget widget) {
		this.widget = widget;
		this.translator = widget.getUsbProWidgetTranslator();
		dmxState =  new byte[Universe.MAX_CHANNELS];
	}
	
	public void onEvent(DmxUpdateEvent event) {
		dmxState[event.getDmxChannel()-1] = (byte) event.getValue();
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
	public UsbProWidgetTranslator getTranslator() { return translator; }

}
