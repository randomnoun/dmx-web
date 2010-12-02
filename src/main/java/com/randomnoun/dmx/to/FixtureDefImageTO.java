package com.randomnoun.dmx.to;

public class FixtureDefImageTO {

    private long id;
    private long fixtureDefId;
    private String name;
    private long size;
    private String contentType;
    private String fileLocation;

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

    /** Returns the fixtureDefId
     * @return the fixtureDefId
     */
    public long getFixtureDefId() {
        return fixtureDefId;
    }

    /** Set the fixtureDefId
     * @param fixtureDefId
     */
    public void setFixtureDefId(long fixtureDefId) {
        this.fixtureDefId = fixtureDefId;
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

}

