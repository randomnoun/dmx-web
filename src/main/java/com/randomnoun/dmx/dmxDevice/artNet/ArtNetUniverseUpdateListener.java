package com.randomnoun.dmx.dmxDevice.artNet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import artnet4j.packets.ArtDmxPacket;

import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.dmxDevice.usbPro.UsbProWidgetUniverseUpdateListener;
import com.randomnoun.dmx.dmxDevice.usbPro.UsbProWidgetUniverseUpdateListener.UsbProUpdaterThread;
import com.randomnoun.dmx.event.DmxUpdateEvent;
import com.randomnoun.dmx.event.UniverseUpdateListener;

public class ArtNetUniverseUpdateListener implements UniverseUpdateListener {

	static Logger logger = Logger.getLogger(UsbProWidgetUniverseUpdateListener.class);
	
	private static int threadCount = 0; // show always be 0
	artnet4j.ArtNet artNet4jObj = null;
	
	private InetAddress artNetUnicastAddress = null;
	int artNetSubnetId = -1; // subnet to send to
	int artNetUniverseId = -1;  // universe to send to 
	
	byte dmxState[];
	ArtNetUpdaterThread t = null;
	
	public ArtNetUniverseUpdateListener(artnet4j.ArtNet artNet4jObj,
		String unicastAddress,
		int artNetSubnetId,
		int artNetUniverseId) 
	{
		this.artNet4jObj = artNet4jObj;
		try {
			this.artNetUnicastAddress = InetAddress.getByName(unicastAddress);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Invalid unicastAddress '" + unicastAddress + "'", e);
		} 
		this.artNetSubnetId = artNetSubnetId;
		this.artNetUniverseId = artNetUniverseId;
		dmxState =  new byte[Universe.MAX_CHANNELS];
	}
	
	public artnet4j.ArtNet getArtNet4jObj() { return artNet4jObj; } 

	public void onEvent(DmxUpdateEvent event) {
		dmxState[event.getDmxChannel()-1] = (byte) event.getValue();
		t.notifyChange();
	}
	
	public void startThread() {
		if (t!=null) { throw new IllegalStateException("Thread already started"); }
		t = new ArtNetUpdaterThread(this);
		t.start();
	}
	
	public void stopThread() {
		if (t!=null) { t.done(); }
	}
	
	public static class ArtNetUpdaterThread extends Thread {

		ArtNetUniverseUpdateListener anuul;
		boolean done = false;
		boolean hasChanged = false;
		long startTime = 0;
		
		public ArtNetUpdaterThread(ArtNetUniverseUpdateListener anuul) {
			super("ArtNetThread-" + threadCount);
			this.anuul = anuul;
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
						ArtDmxPacket dmx = new ArtDmxPacket();
						dmx.setUniverse(anuul.artNetSubnetId, anuul.artNetUniverseId);
						dmx.setDMX(anuul.dmxState, anuul.dmxState.length);
									
						anuul.getArtNet4jObj().unicastPacket(dmx, anuul.artNetUnicastAddress);
					} catch (NullPointerException npe) {
						logger.debug("No ArtNetUpdaterThread instance; stopping ArtNetUpdaterThread");
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
		
		/*
		from TestPoll.java: 
		  
		public void run() {
			ArtDmxPacket dmx = new ArtDmxPacket();
            System.out.println("subnet:" + artNetNode.getSubNet());
            System.out.println("universeId:" + artNetNode.getDmxOuts()[0]);
            System.out.println("nodeStatus:" + artNetNode.getNodeStatus());
            System.out.println("oemCode:" + artNetNode.getOemCode());
            System.out.println("shortName:" + artNetNode.getShortName());
            System.out.println("longName:" + artNetNode.getLongName());
            System.out.println("numPorts:" + artNetNode.getNumPorts());
            System.out.println("reportCode:" + artNetNode.getReportCode());
            System.out.println("nodeStyle:" + artNetNode.getNodeStyle());
            System.out.println("ipAddress:" + artNetNode.getIPAddress());
            dmx.setUniverse(artNetNode.getSubNet(),
                    artNetNode.getDmxOuts()[0]);
            //dmx.setSequenceID(sequenceID % 255); // broken: if in use, sequenceId!=0
            dmx.setSequenceID(0);
            byte[] buffer = new byte[510];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] =
                        (byte) (Math.sin(sequenceID * 0.05 + i * 0.8) * 127 + 128);
            }
            dmx.setDMX(buffer, buffer.length);
            artnet.unicastPacket(dmx, artNetNode.getIPAddress());			
		}
		*/
	}
	
}
