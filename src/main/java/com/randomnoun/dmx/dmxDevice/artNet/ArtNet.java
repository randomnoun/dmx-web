package com.randomnoun.dmx.dmxDevice.artNet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import org.apache.log4j.Logger;

import artnet4j.ArtNetNode;
import artnet4j.ArtNetServer;
import artnet4j.events.ArtNetDiscoveryListener;

import com.randomnoun.common.Text;
import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.PropertyDef;
import com.randomnoun.dmx.ExceptionContainer.TimestampedException;
import com.randomnoun.dmx.audioController.NullAudioController;
import com.randomnoun.dmx.dmxDevice.DmxDevice;
import com.randomnoun.dmx.event.UniverseUpdateListener;

/** Wrapper around a DMX output using the ArtNet protocol.  
 *
 * <p>To use this class, instantiate it with a broadcastAddress, and a
 * UDP receiving and sending port number (defaults to 6454), and
 * the Art-Net subnet and universe IDs that this class will target; e.g.
 * 
 * <pre>
 * Properties initProperties = new Properties();
 * initProperties.put("broadcastAddress", "192.168.0.62");
 * initProperties.put("udpRecvPort", "6454");
 * initProperties.put("udpSendPort", "6454");
 * initProperties.put("artNetSubnetId", "0");
 * initProperties.put("artNetUniverseId", "0");
 * ArtNet artNet = new ArtNet(initProperties);
 * artNet.open();
 * ...
 * artNet.close();
 * </pre>
 * 
 */
public class ArtNet extends DmxDevice implements ArtNetDiscoveryListener {
	
	static Logger logger = Logger.getLogger(ArtNet.class);

	ExceptionContainerImpl exceptionContainer;

	String broadcastAddress = null; // ArtNetServer.DEFAULT_BROADCAST_IP;
	String unicastAddress = null;
	int udpRecvPort = -1; // ArtNetServer.DEFAULT_PORT;
	int udpSendPort = -1; // ArtNetServer.DEFAULT_PORT;
	int artNetSubnetId = 0;
	int artNetUniverseId = 0;
	boolean connected = false;
	
	artnet4j.ArtNet artNet4jObj = null;
	ArtNetNode artNetDestNode = null;
	
	/** Create a new DmxDevice interface to the ArtNet protocol.
	 * 
	 * <p>The properties supplied should include the key 'broadcastAddress',
	 * containing a dotted-quad IP address destination.
	 * 
	 * @param properties set of properties; containing the key 'broadcastAddress'
	 */
	public ArtNet(Map properties) {
		super(properties);
		if (properties==null) { return; } // when called from maintain devices page
		this.unicastAddress = (String) properties.get("unicastAddress");
		this.broadcastAddress = (String) properties.get("broadcastAddress");
		this.udpRecvPort = Integer.parseInt(Text.strDefault((String) properties.get("udpRecvPort"), "6454"));
		this.udpSendPort = Integer.parseInt(Text.strDefault((String) properties.get("udpSendPort"), "6454"));
		this.artNetSubnetId = Integer.parseInt((String) properties.get("artNetSubnetId"));
		this.artNetUniverseId = Integer.parseInt((String) properties.get("artNetUniverseId"));
		exceptionContainer = new ExceptionContainerImpl();
		connected = false;
	}
	
	public String getName() { return "ArtNet"; }
	
	
	// @XXX: do I really want to do discovery here ?
	public void open() {
		try {
			artNet4jObj = new artnet4j.ArtNet();
			// logger.error("XXX: broadcast disabled");
			artNet4jObj.setBroadCastAddress(broadcastAddress); // destination IP
			artNet4jObj.setUdpSendPort(udpSendPort);
			artNet4jObj.setUdpRecvPort(udpRecvPort);
			artNet4jObj.start();
			artNet4jObj.getNodeDiscovery().addListener(this);
			artNet4jObj.startNodeDiscovery();
			int timeout = 0;
			while (artNetDestNode!=null && timeout<50) { // 5 sec. @TODO read spec 
				Thread.sleep(100);
				timeout++;
			}
            if (artNetDestNode==null) {
            	logger.error("Could not find artNet node via broadcastAddress '" + broadcastAddress + "'");
            	exceptionContainer.addException(new IOException("Could not find artNet node via broadcastAddress '" + broadcastAddress + "'"));
            }
			connected = true;
		} catch (Exception e) {
			logger.error("Error finding artNet node at broadcastAddress '" + broadcastAddress + "'", e);
			exceptionContainer.addException(e);
		} finally {
			artNet4jObj.getNodeDiscovery().removeListener(this);
			artNet4jObj.getNodeDiscovery().stop();
		}
	}

	/** Closes any streams/resources held by this class.
	 * 
	 * This method will attempt to close the serial port's inputStream, outputStream,
	 * remove any event listeners assigned to the serial port, and close the serial
	 * port itself. 
	 * 
	 * <p>If an exception occurs, then all these steps will still be 
	 * attempted; any exceptions thrown will be retrievable via the 
	 * ExceptionContainer interface.
	 *  
	 * @throws IOException
	 */ 
	public void close() {
		if (connected) {
			artNet4jObj.getNodeDiscovery().removeListener(this);
		}
		connected = false;
	}

	public List<TimestampedException> getExceptions() {
		return exceptionContainer.getExceptions();
	}

	public void clearExceptions() {
		exceptionContainer.clearExceptions();
	}

	@Override
	public UniverseUpdateListener getUniverseUpdateListener() {
		return new ArtNetUniverseUpdateListener(this,
			unicastAddress, 
			artNetSubnetId,
			artNetUniverseId);
	}
	
    public List getDefaultProperties() {
        List properties = new ArrayList();
        properties.add(new PropertyDef("broadcastAddress", "Broadcast address", "2.255.255.255"));
        properties.add(new PropertyDef("unicastAddress", "Destination address", "2.0.0.10"));
        properties.add(new PropertyDef("artNetSubnetId", "Output subnet ID", "0"));
        properties.add(new PropertyDef("artNetUniverseId", "Ouptut universe ID", "0"));
        properties.add(new PropertyDef("udpRecvPort", "UDP receive port", "6454"));
        properties.add(new PropertyDef("udpSendPort", "UDP send port", "6454"));
        return properties;
  }

    
	public void discoveredNewNode(ArtNetNode node) {
		// TODO Auto-generated method stub
		artNetDestNode = node; // @XXX: there can be only one
	}

	public void discoveredNodeDisconnected(ArtNetNode node) {
		// TODO Auto-generated method stub
	}

	public void discoveryCompleted(List<ArtNetNode> nodes) {
		// TODO Auto-generated method stub
	}

	public void discoveryFailed(Throwable t) {
		// TODO Auto-generated method stub
	}  

	
}
	


