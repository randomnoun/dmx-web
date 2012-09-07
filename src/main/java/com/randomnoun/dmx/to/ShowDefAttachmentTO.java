package com.randomnoun.dmx.to;

import com.randomnoun.common.Text;

public class ShowDefAttachmentTO {

    private long id;
    private long showDefId;
    private String name;
    private long size;
    private String contentType;
    private String fileLocation;
    private String description;

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

    /** Returns the showDefId
     * @return the showDefId
     */
    public long getShowDefId() {
        return showDefId;
    }

    /** Set the showDefId
     * @param showDefId
     */
    public void setShowDefId(long showDefId) {
        this.showDefId = showDefId;
    }

    /** Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /** Set the name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Returns the size
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /** Set the size
     * @param size
     */
    public void setSize(long size) {
        this.size = size;
    }
    
    /** Returns the size
     * @return the size
     */
    public String getSizeInUnits() {
    	if (size < 1024) { return size + " bytes"; }
    	else if (size < 1024 * 1024) { return (size/1024) + "KB"; }
    	else if (size < 1024 * 1024 * 1024) { return (size/1024/1024) + "MB"; }
    	else { return (size/1024/1024/1024) + "GB"; }
    }

    
    /** Returns the contentType
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /** Set the contentType
     * @param contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /** Returns the fileLocation
     * @return the fileLocation
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /** Set the fileLocation
     * @param fileLocation
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /** Returns the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /** Set the description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    
    public String toExportXml() {
    	return "<showDefAttachment>\n" + 
    		"    <showDefId>" + showDefId + "</showDefId>\n" +
    		"    <name>" + Text.escapeHtml(name) + "</name>\n" +
    		"    <description>" + description + "</description>\n" +
    		"    <size>" + size + "</size>\n" +
    		"    <contentType>" + Text.escapeHtml(contentType) + "</contentType>\n" +
    		"</showDefAttachment>\n";
    }
    
}

