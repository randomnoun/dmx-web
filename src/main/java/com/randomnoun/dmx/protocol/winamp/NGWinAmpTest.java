package com.randomnoun.dmx.protocol.winamp;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.randomnoun.common.log4j.Log4jCliConfiguration;

public class NGWinAmpTest {

	Logger logger = Logger.getLogger(NGWinAmpTest.class);
	
	public void testThings() throws IOException {
		NGWinAmp winAmp = new NGWinAmp("localhost", 18443, 10000);
		try {
			winAmp.connect();
			logger.info("authenticate: " + winAmp.authenticate("abc123"));
			String[] files = winAmp.sendGetRoots(); // NGWinAmp.NGWINAMP_ALL
			logger.info("Received " + files.length + " file roots");
			for (int i=0; i<files.length; i++) {
				logger.info("Root " + i + ": " + files[i]);
			}
			files = winAmp.sendGetFiles(NGWinAmp.NGWINAMP_ALL); 
			logger.info("Received " + files.length + " files");
			for (int i=0; i<files.length; i++) {
				logger.info("File " + i + ": " + files[i]);
			}
		
		} finally {
			winAmp.disconnect();
		}
	}
	
	public static void main(String args[]) throws IOException {
		Log4jCliConfiguration lcc = new Log4jCliConfiguration();
		Properties props = new Properties();
		props.put("logger.com.randomnoun.dmx", "DEBUG");
		lcc.init("[NGWinAmpTest] ", props);
		NGWinAmpTest ngWinAmpTest = new NGWinAmpTest();
		ngWinAmpTest.testThings();
	}
	
}
