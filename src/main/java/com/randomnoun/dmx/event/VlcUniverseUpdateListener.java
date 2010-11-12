package com.randomnoun.dmx.event;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.event.MuxValueDumper.MuxValueDumperThread;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.fixture.FixtureOutput;

/** This listener will send DMX change events to VLC
 *
 * start vlc with these options, with a webcam pointing at the fixture
 * you want to  record (all on one line, no space after "size=20}:" )
 *
 * ./vlc dshow:// "--dshow-vdev=Creative WebCam Notebook" 
 *   --sub-filter "marq@topleft{marquee=topleft,size=20}:
 *     marq@bottomleft{marquee=bottomleft,position=9,size=20}:
 *     marq@bottomright{marquee=bottomright,position=10,size=20}:
 *     marq{marquee=%H:%M:%S,position=6}" 
 *   --video-filter "adjust{gamma=2.0}"
 *
 * 
 *
 * Start should be something like

    vlc.exe -I rc --sub-filter=marq@something{marquee=$t ($P%%),color=16776960}:marq@something2{marquee=%H:%m%s,position=6} somevideo.avi

then you can change the first text with command
    @something marq-marquee new_text

and the second with
    @something2 marq-marquee new_text2

and in a shell script

    #!/bin/bash

    nc localhost 9999 <<EOF
    @my_marq marq-marquee $1
    logout
    EOF

    now using oldtelnet interface
    
    @author knoxg
 */
public class VlcUniverseUpdateListener implements UniverseUpdateListener {

	static Logger logger = Logger.getLogger(VlcUniverseUpdateListener.class);
	
	VlcUpdateThread t = null;
	
	// probably need to replicate another Universe object here, but ... 
	//int dmxState[];
	
	Fixture fixture;
	FixtureDef fixtureDef;

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
		VlcUniverseUpdateListener vuul;
		boolean done = false;
		boolean hasChanges = false;
		private boolean updateText = false;
		long startTime = 0;
		SimpleDateFormat sdf;
		DecimalFormat df;
		
		Socket vlcSocket;
		OutputStream os;
		InputStream is;
		PrintWriter pw;
		long lastConnectTime = -1;
		
		public VlcUpdateThread(VlcUniverseUpdateListener vuul) {
			this.vuul = vuul;
			this.setName(getName() + "-VlcUniverseUpdateListener");
			this.sdf=new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss.SSS");
			this.df=new DecimalFormat("##0.000");
			this.vlcSocket = null;
		}
		
		public void reconnect() {
			try {
				if (vlcSocket!=null && !vlcSocket.isClosed()) { 
					try { vlcSocket.close(); } catch (Exception e2) { }
					vlcSocket = null;
				}
				if (System.currentTimeMillis()-lastConnectTime > 5000) {
					lastConnectTime = System.currentTimeMillis();	
					vlcSocket = new Socket();
					vlcSocket.connect(new InetSocketAddress(vuul.address, vuul.vlcPort));
					os = vlcSocket.getOutputStream();
					is = vlcSocket.getInputStream();
					// see syntax at http://forum.videolan.org/viewtopic.php?f=7&t=57094
					pw = new PrintWriter(os);
					pw.println();  // initial newline to get past password
				}
			} catch (IOException e) {
				if (vlcSocket!=null && !vlcSocket.isClosed()) { 
					try { vlcSocket.close(); } catch (Exception e2) { } 
				}
				vlcSocket = null;
				logger.debug("No connection to VLC: " + e.getMessage());
			}
		}
			
		
		public void run() {
			String dmxOutput = "", muxOutput = "";
			startTime = System.currentTimeMillis();
			while (!done) {
				updateText = false;
				if (vuul.fixture!=null) {
					updateText = true;
					dmxOutput="[";
					for (int i=0; i<vuul.fixtureDef.getNumDmxChannels(); i++) {
						dmxOutput += vuul.dmxState[vuul.fixture.getStartDmxChannel() + i] + ", ";
					}
					dmxOutput += "]";
					
					muxOutput="";
					ChannelMuxer mux = vuul.fixture.getChannelMuxer();
					FixtureOutput output = mux.getOutput();
					Color c = output.getColor();
					muxOutput += "[r=" + c.getRed()+ ", g=" + c.getGreen() + ", b=" + c.getBlue() + "][p=" + df.format(output.getPan()) + "][t=" + df.format(output.getTilt()) + "]";
				}
				if (vlcSocket!=null) {
					
					// @TODO could keep socket open
					logger.debug("Updating VLC marquee to '" + dmxOutput + "'");
					pw.println("@topleft marq-marquee " + dmxOutput);
					pw.println("@bottomleft marq-marquee " + muxOutput);
					pw.println("@bottomright marq-marquee " + sdf.format(new Date()));
					if (pw.checkError()) {
						logger.debug("Error in VLC printWriter");
						reconnect();
					}
				} else {
					reconnect();
				}
				//System.out.println((System.currentTimeMillis()-startTime) + ": " + output);
				try {
					Thread.sleep(20); // plenty fast enough
				} catch (InterruptedException e) {
				}
			}
			
			// shut down socket before completion
			if (vlcSocket!=null) {
				try {
		            pw.println("logout\n");
					pw.flush();
					os.close();
					vlcSocket.close();
				} catch (IOException ioe) {
					logger.error("Error closing socket to VLC", ioe);
				}
			}

			
		}
		public void done() {
			done = true;
		}
	}
	
	public void setFixture(Fixture fixture) {
		this.fixtureDef = fixture.getFixtureDef();
		this.fixture = fixture;
	}
	
	public void onEvent(DmxUpdateEvent event) {
		dmxState[event.getDmxChannel()] = event.getValue();
		if (t!=null) { t.hasChanges = true; }
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
