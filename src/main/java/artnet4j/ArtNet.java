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

package artnet4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import artnet4j.events.ArtNetServerEventAdapter;
import artnet4j.events.ArtNetServerListener;
import artnet4j.packets.ArtNetPacket;
import artnet4j.packets.ArtPollReplyPacket;
import artnet4j.packets.PacketType;

public class ArtNet {

    public static final Logger logger =
            Logger.getLogger(ArtNet.class.getClass().getName());

    protected static final long ARTPOLL_REPLY_TIMEOUT = 3000;

    protected static final String VERSION = "0001-20091119";

    protected ArtNetServer server;
    protected ArtNetNodeDiscovery discovery;

    private String broadcastAddress = null;
    private int udpSendPort = -1;
    private int udpRecvPort = -1; 
    
    public ArtNet() {
        logger.info("Art-Net v" + VERSION);
    }

    public void addServerListener(ArtNetServerListener l) {
        server.addListener(l);
    }

    public void broadcastPacket(ArtNetPacket packet) throws IOException {
        server.broadcastPacket(packet);
    }

    public ArtNetNodeDiscovery getNodeDiscovery() {
        if (discovery == null) {
            discovery = new ArtNetNodeDiscovery(this);
        }
        return discovery;
    }

    public void init() {
    	server = new ArtNetServer();
    	if (udpRecvPort!=-1) { server.setUdpRecvPort(udpRecvPort); }
    	if (udpSendPort!=-1) { server.setUdpSendPort(udpRecvPort); }
        if (broadcastAddress!=null) { server.setBroadcastAddress(broadcastAddress); }
        
        server.addListener(new ArtNetServerEventAdapter() {

            @Override
            public void artNetPacketReceived(ArtNetPacket packet) {
                logger.fine("packet received: " + packet.getType());
                if (discovery != null
                        && packet.getType() == PacketType.ART_POLL_REPLY) {
                    discovery.discoverNode((ArtPollReplyPacket) packet);
                }
            }

            @Override
            public void artNetServerStarted(ArtNetServer artNetServer) {
                logger.fine("server started callback");
            }

            @Override
            public void artNetServerStopped(ArtNetServer artNetServer) {
                logger.info("server stopped");
            }
        });
    }

    public void removeServerListener(ArtNetServerListener l) {
        server.removeListener(l);
    }

    public void setBroadCastAddress(String ip) {
        // server.setBroadcastAddress(ip);
    	this.broadcastAddress = ip;
    	if (server!=null) {
    		server.setBroadcastAddress(ip);
    	}
    }
    
    public void setUdpRecvPort(int udpRecvPort) {
    	this.udpRecvPort = udpRecvPort;
    	if (server!=null) {
    		server.setUdpRecvPort(udpRecvPort);
    	}
    }

    public void setUdpSendPort(int udpSendPort) {
    	this.udpSendPort = udpSendPort;
    	if (server!=null) {
    		server.setUdpSendPort(udpSendPort);
    	}
    }


    public void start() throws SocketException, ArtNetException {
        if (server == null) {
            init();
        }
        server.start();
    }

    public void startNodeDiscovery() throws ArtNetException {
        getNodeDiscovery().start();
    }

    public void stop() {
        if (discovery != null) {
            discovery.stop();
        }
        if (server != null) {
            server.stop();
        }
    }

    /**
     * Sends the given packet to the specified Art-Net node.
     * 
     * @param packet
     * @param node
     * @throws IOException 
     */
    public void unicastPacket(ArtNetPacket packet, ArtNetNode node) throws IOException {
        server.unicastPacket(packet, node.getIPAddress());
    }

    /**
     * Sends the given packet to the specified IP address.
     * 
     * @param packet
     * @param adr
     * @throws IOException 
     */
    public void unicastPacket(ArtNetPacket packet, InetAddress adr) throws IOException {
        server.unicastPacket(packet, adr);
    }

    /**
     * Sends the given packet to the specified IP address.
     * 
     * @param packet
     * @param adr
     * @throws IOException 
     */
    public void unicastPacket(ArtNetPacket packet, String adr) throws IOException {
        InetAddress targetAdress;
        targetAdress = InetAddress.getByName(adr);
        server.unicastPacket(packet, targetAdress);
    }
}