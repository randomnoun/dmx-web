package com.randomnoun.dmx.event;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureDef;
import com.randomnoun.dmx.FixtureOutput;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.event.MuxValueDumper.MuxValueDumperThread;

/** This listener will send DMX change events to VLC
 *
 * start vlc with these options, with a webcam pointing at the fixture
 * you want to  record:
 *
 * .vlc dshow:// "--dshow-vdev=Creative WebCam Notebook" 
 *   --sub-filter "marq{marquee=@my_marq,color=16776960}:marq{marquee=%H:%M:%S,position=6}"
 *   --extraintf rc --rc-host="localhost:9999"
 * 
 * 
 * 
 * vlc --extraintf rc --rc-host="localhost:9999" --sub-filter=marq@my_marq .....

and in a shell script

    #!/bin/bash

    nc localhost 9999 <<EOF
    @my_marq marq-marquee $1
    logout
    EOF
    
    @author knoxg
 */
public class VlcUniverseUpdateListener implements UniverseUpdateListener {
	
	VlcUpdateThread t = null;
	
	// probably need to replicate another Universe object here, but ... 
	//int dmxState[];
	
	Fixture fixture;
	int dmxState[];
	String vlcHost;
	int vlcPort;
	InetAddress address;

	public VlcUniverseUpdateListener(String host, String port) throws UnknownHostException {
		this.vlcHost = host;
		this.vlcPort = Integer.parseInt(port);
		address = InetAddress.getByName(vlcHost);
		dmxState = new int[Universe.MAX_CHANNELS+1];
	}
	
	
	// @TODO obvious thread safety problems
	public static class VlcUpdateThread extends Thread {
		VlcUniverseUpdateListener mvd;
		boolean done = false;
		boolean hasChanges = false;
		long startTime = 0;
		Fixture fixture;
		FixtureDef fixtureDef;
		
		
		
		public VlcUpdateThread(VlcUniverseUpdateListener vuul) {
			this.mvd = vuul;
			fixture = vuul.fixture;
			fixtureDef = vuul.fixture.getFixtureDef();
		}
		
		public void run() {
			startTime = System.currentTimeMillis();
			while (!done) {
				if (hasChanges) {
					hasChanges = false;
					
					String dmxOutput="[";
					for (int i=0; i<fixtureDef.getNumDmxChannels(); i++) {
						dmxOutput += mvd.dmxState[fixture.getStartDmxChannel() + i] + ", ";
					}
					dmxOutput += "]";
					
					String fixOutput="";
					ChannelMuxer mux = fixture.getChannelMuxer();
					FixtureOutput muxOutput = mux.getOutput();
					Color c = muxOutput.getColor();
					fixOutput += "[r=" + c.getRed()+ ", g=" + c.getGreen() + ", b=" + c.getBlue() + "][p=" + muxOutput.getPan() + "][t=" + muxOutput.getTilt() + "]";
					
					
					
					try {
						Socket vlcSocket = new Socket();
						vlcSocket.connect(new InetSocketAddress(mvd.address, mvd.vlcPort));
						OutputStream os = vlcSocket.getOutputStream();
						// see syntax at http://forum.videolan.org/viewtopic.php?f=7&t=57094
						PrintWriter pw = new PrintWriter(os);
						pw.println("@my_marq marq-marquee " + dmxOutput + "\n" +
                          "logout\n");
						pw.flush();
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//System.out.println((System.currentTimeMillis()-startTime) + ": " + output);
				try {
					Thread.sleep(20); // plenty fast enough
				} catch (InterruptedException e) {
				}
				
			}
		}
		public void done() {
			done = true;
		}
	}
	
	public void setFixture(Fixture fixture) {
		fixture = fixture;
	}
	
	public void onEvent(DmxUpdateEvent event) {
		dmxState[event.getDmxChannel()] = event.getValue();
		t.hasChanges = true;
	}
	
	public void startThread() {
		if (t!=null) { throw new IllegalStateException("Thread already started"); }
		t = new VlcUpdateThread(this);
		t.start();
	}
	
	public void stopThread() {
		if (t!=null) { t.done(); }
	}
	

}
