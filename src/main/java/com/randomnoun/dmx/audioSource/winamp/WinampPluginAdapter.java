package com.randomnoun.dmx.audioSource.winamp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import com.randomnoun.dmx.ExceptionContainer.TimestampedException;
import com.randomnoun.dmx.ExceptionContainerImpl;
import com.randomnoun.dmx.web.action.FancyControllerAction;

/** An interface to the dmx-web winamp plugin */

public class WinampPluginAdapter {
	
	public Logger logger = Logger.getLogger(WinampPluginAdapter.class);
	
	private PollingThread pollingThread;
	private HttpClient httpClient;
	private WinampAudioSource audioSource;
	private ExceptionContainerImpl exceptionContainer;
	
	// received data
	private boolean isBeat = false;
	private float avg[] = new float[3];
	private float spectrum[][] = new float[2][256];
	
	public static class PollingThread extends Thread {
		
		private WinampPluginAdapter winamp;
		private boolean retryEnabled = false;
		private boolean done = false;
		private boolean exceptionOnLastConnection = false; // to suppress hundreds of exceptions
		private boolean enableSpectrum;
		private String url;
		
		public Logger logger = Logger.getLogger(PollingThread.class);
		
		public PollingThread(WinampPluginAdapter winamp, String url, boolean retryEnabled, boolean enableSpectrum) {
			this.setName("dmxWebPollingThread-" + this.getId());
			this.winamp = winamp;
			this.done = false;
			this.url = url;
			this.retryEnabled = retryEnabled;
			this.exceptionOnLastConnection = false;
		}
		
		public void run() {
			GetMethod method = new GetMethod(url);
			while (!done) {
				try {
					logger.debug("httpClient loop");
					winamp.httpClient.executeMethod(method);
					int status = method.getStatusCode();
					if (status==200) {
						InputStream is = method.getResponseBodyAsStream();
						Properties props = new Properties();
						props.load(is);
						winamp.setBeat("1".equals(props.get("m_is_beat")));
						for (int i=0; i<3; i++) {
							winamp.avg[i] = Float.parseFloat(props.getProperty("avg[" + i + "]"));
						}
						if (enableSpectrum) {
							for (int i=0; i<256; i++) {
								winamp.spectrum[0][i] = Float.parseFloat(props.getProperty("f[0][" + i + "]"));
								winamp.spectrum[1][i] = Float.parseFloat(props.getProperty("f[1][" + i + "]"));
							}
						}
					}
					exceptionOnLastConnection = false;
				} catch (HttpException e) {
					if (!exceptionOnLastConnection) { e.printStackTrace(); }
					exceptionOnLastConnection = true;
					winamp.exceptionContainer.addException(e);
					if (!retryEnabled) { done = true; }
				} catch (IOException e) {
					if (!exceptionOnLastConnection) { e.printStackTrace(); }
					exceptionOnLastConnection = true;
					winamp.exceptionContainer.addException(e);
					if (!retryEnabled) { done = true; }
				} catch (Exception e) {
					if (!exceptionOnLastConnection) { e.printStackTrace(); }
					exceptionOnLastConnection = true;
					winamp.exceptionContainer.addException(e);
					if (!retryEnabled) { done = true; }
				}
			}
		}
		
		public void done() {
			done = true;
		}
	}
	
	public WinampPluginAdapter(WinampAudioSource audioSource, 
		String host, int port, int timeout, boolean retryEnabled, boolean enableSpectrum) {

		this.audioSource = audioSource;
		exceptionContainer = new ExceptionContainerImpl();
		
		Properties props = new Properties();
	    InputStream is = FancyControllerAction.class.getClassLoader().getResourceAsStream("/build.properties");
    	Properties version = new Properties();
    	if (is==null) {
    		version.put("error", "Missing build.properties");
    	} else {
	    	try {
				version.load(is);
				is.close();
			} catch (IOException e) {
				// ignore
			}
    	}
		
		httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
		httpClient.getParams().setParameter("http.useragent", "User-Agent: dmx-web/" + version.get("maven.pom.version") + " (" + version.get("bamboo.buildNumber") + ")");
		httpClient.getParams().setParameter("http.protocol.single-cookie-header", new Boolean(true));
		httpClient.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
		String url = "http://" + host + ":" + port + "/";
		if (enableSpectrum) { url += "s"; }
		this.pollingThread = new PollingThread(this, url, retryEnabled, enableSpectrum);
	}

	public void connect() {
		pollingThread.start();
	}
	
	public void disconnect() {
		logger.debug("Disconnecting from DmxWebWinamp plugin");
		pollingThread.done();
		// TODO: should actually wait for the polling thread to die.
		//  This is causing socket leaks in the plugin as the thread can't
		//  close the connection without a log4j LogManager, which isn't around
		//  when the servletContext is reloaded. 
		//  Probably only an issue during development though.
		
		// @TODO use shutdown() in newer HttpClients
		httpClient.getHttpConnectionManager().closeIdleConnections(0); 
		
	}

	public boolean getBeat() {
		return isBeat;  
	}
	
	private void setBeat(boolean beat) {
		this.isBeat = beat;
		if (beat) {
			logger.info("beat");
			audioSource.setBeat();
		}
	}

	public float[] getBassMidTreble() {
		return avg;
	}

	public float[][] getSpectrum() {
		return spectrum;
	}

	public List<TimestampedException> getExceptions() {
		return exceptionContainer.getExceptions();
	}

	public void clearExceptions() {
		exceptionContainer.clearExceptions();
	}
	
	

}
