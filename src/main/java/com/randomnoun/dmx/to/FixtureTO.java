package com.randomnoun.dmx.to;

public class FixtureTO {

    private long id;
    private long fixtureDefId;
    private String name;
    private long dmxOffset;

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

    /** Returns the dmxOffset
     * @return the dmxOffset
     */
    public long getDmxOffset() {
        return dmxOffset;
    }

    /** Set the dmxOffset
     * @param dmxOffset
     */
    public void setDmxOffset(long dmxOffset) {
        this.dmxOffset = dmxOffset;
    }

}
