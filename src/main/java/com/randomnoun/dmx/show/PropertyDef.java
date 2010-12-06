package com.randomnoun.dmx.show;

/** A property definition */
public class PropertyDef {

	String key;
	String description;
	String defaultValue;
	
	public PropertyDef(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}
	
	public String getKey() { return key; }
	public String getDescription() { return description; }
	public String getDefaultValue() { return defaultValue; }

}
