package com.randomnoun.dmx.to;

import com.randomnoun.common.Text;

public class ShowPropertyTO {

    private long id;
    private long showId;
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

    /** Returns the showId
     * @return the showId
     */
    public long getShowId() {
        return showId;
    }

    /** Set the showId
     * @param showId
     */
    public void setShowId(long showId) {
        this.showId = showId;
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
		return "<showProperty>\n" +
	        "    <key>" + Text.escapeHtml(key) + "</key>\n" +
	        "    <value>" + Text.escapeHtml(value) + "</value>\n" +
	        "</showProperty>\n";
	}

}
