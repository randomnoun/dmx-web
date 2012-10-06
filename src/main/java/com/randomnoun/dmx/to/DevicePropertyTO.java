package com.randomnoun.dmx.to;

import com.randomnoun.common.Text;

/** @TODO property types */
public class DevicePropertyTO {

    private long id;
    private long deviceId;
    private String key;
    private String value;

    /** Returns the id
     * @return the id
     */
    public long getId() {
        return id;
    }

    /** Set the id
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /** Returns the deviceId
     * @return the deviceId
     */
    public long getDeviceId() {
        return deviceId;
    }

    /** Set the deviceId
     * @param deviceId
     */
    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    /** Returns the key
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /** Set the key
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /** Returns the value
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /** Set the value
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

	public String toExportXml() {
		return "<deviceProperty>\n" +
		    "    <key>" + Text.escapeHtml(key) + "</key>\n" +
		    "    <value>" + Text.escapeHtml(value) + "</value>\n" +
		    "</deviceProperty>\n";
	}

}

