/*
 * This file is part of artnet4j.
 * 
 * Copyright 2009 Karsten Schmidt (PostSpectacular Ltd.)
 * 
 * artnet4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * artnet4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with artnet4j. If not, see <http://www.gnu.org/licenses/>.
 */

package artnet4j.test;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.ArtNetNode;
import artnet4j.events.ArtNetDiscoveryListener;
import artnet4j.packets.ArtDmxPacket;

public class PollTest implements ArtNetDiscoveryListener {

    public static void main(String[] args) {
        // new PollTest().test();
    	PollTest pollTest = new PollTest();
    	//pollTest.setWhite();
    	pollTest.test();
    }

    private ArtNetNode artNetNode;

    private int sequenceID;

    public void discoveredNewNode(ArtNetNode node) {
        if (artNetNode == null) {
            artNetNode = node;
            System.out.println("found net lynx");
        }
    }

    public void discoveredNodeDisconnected(ArtNetNode node) {
        System.out.println("node disconnected: " + node);
        if (node == artNetNode) {
            artNetNode = null;
        }
    }

    public void discoveryCompleted(List<ArtNetNode> nodes) {
        System.out.println(nodes.size() + " nodes found:");
        for (ArtNetNode n : nodes) {
            System.out.println(n);
        }
    }

    public void discoveryFailed(Throwable t) {
        System.out.println("discovery failed");
    }

    /*
    private void setWhite() {
    	ArtNet artnet = new ArtNet();
    	artnet.start();
    	artnet.setBroadCastAddress("192.168.0.62"); // destination IP
    	
    }
    */
    
    
    private void test() {
        ArtNet artnet = new ArtNet();
        try {
            artnet.start();
            artnet.setBroadCastAddress("192.168.0.62"); // destination IP
            artnet.getNodeDiscovery().addListener(this);
            artnet.startNodeDiscovery();
            while (true) {
                if (artNetNode != null) {
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
                    byte[] buffer = new byte[512];
                    for (int i = 0; i < buffer.length; i++) {
                        buffer[i] =
                                (byte) (Math.sin(sequenceID * 0.05 + i * 0.8) * 127 + 128);
                    }
                    dmx.setDMX(buffer, buffer.length);
                    artnet.unicastPacket(dmx, artNetNode.getIPAddress());
                    
                    //dmx.setUniverse(artNetNode.getSubNet(), artNetNode.getDmxOuts()[1]);
                    //artnet.unicastPacket(dmx, artNetNode.getIPAddress());
                    sequenceID++;
                }
                Thread.sleep(30);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (ArtNetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}
