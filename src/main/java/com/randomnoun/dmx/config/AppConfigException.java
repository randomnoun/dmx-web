package com.randomnoun.dmx.config;

public class AppConfigException extends Exception {

	public AppConfigException(String message, Exception cause) {
		super(message, cause);
	}

	public AppConfigException(String message) {
		super(message);
	}
	
}
