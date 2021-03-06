package com.randomnoun.dmx.to;

import com.randomnoun.common.Text;

public class FixtureTO {

    private long id;
    private long stageId;
    private long fixtureDefId;
    private String name;
    private long universeNumber;
    private long dmxOffset;
    private Long x;
    private Long y;
    private Long z;
    private Long lookingAtX;
    private Long lookingAtY;
    private Long lookingAtZ;
    private Long upX;
    private Long upY;
    private Long upZ;
    private Long sortOrder;
    private String fixPanelType;
    private Long fixPanelX;
    private Long fixPanelY;

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

    /** Returns the x
     * @return the x
     */
    public Long getX() {
        return x;
    }

    /** Set the x
     * @param x
     */
    public void setX(Long x) {
        this.x = x;
    }

    /** Returns the y
     * @return the y
     */
    public Long getY() {
        return y;
    }

    /** Set the y
     * @param y
     */
    public void setY(Long y) {
        this.y = y;
    }

    /** Returns the z
     * @return the z
     */
    public Long getZ() {
        return z;
    }

    /** Set the z
     * @param z
     */
    public void setZ(Long z) {
        this.z = z;
    }

    /** Returns the lookingAtX
     * @return the lookingAtX
     */
    public Long getLookingAtX() {
        return lookingAtX;
    }

    /** Set the lookingAtX
     * @param lookingAtX
     */
    public void setLookingAtX(Long lookingAtX) {
        this.lookingAtX = lookingAtX;
    }

    /** Returns the lookingAtY
     * @return the lookingAtY
     */
    public Long getLookingAtY() {
        return lookingAtY;
    }

    /** Set the lookingAtY
     * @param lookingAtY
     */
    public void setLookingAtY(Long lookingAtY) {
        this.lookingAtY = lookingAtY;
    }

    /** Returns the lookingAtZ
     * @return the lookingAtZ
     */
    public Long getLookingAtZ() {
        return lookingAtZ;
    }

    /** Set the lookingAtZ
     * @param lookingAtZ
     */
    public void setLookingAtZ(Long lookingAtZ) {
        this.lookingAtZ = lookingAtZ;
    }

    /** Returns the upX
     * @return the upX
     */
    public Long getUpX() {
        return upX;
    }

    /** Set the upX
     * @param upX
     */
    public void setUpX(Long upX) {
        this.upX = upX;
    }

    /** Returns the upY
     * @return the upY
     */
    public Long getUpY() {
        return upY;
    }

    /** Set the upY
     * @param upY
     */
    public void setUpY(Long upY) {
        this.upY = upY;
    }

    /** Returns the upZ
     * @return the upZ
     */
    public Long getUpZ() {
        return upZ;
    }

    /** Set the upZ
     * @param upZ
     */
    public void setUpZ(Long upZ) {
        this.upZ = upZ;
    }

    /** Returns the sortOrder
     * @return the sortOrder
     */
    public Long getSortOrder() {
        return sortOrder;
    }

    /** Set the sortOrder
     * @param sortOrder
     */
    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }

	/** 
	 * @return the stageId
	 */
	public long getStageId() {
		return stageId;
	}

	/**
	 * @param stageId the stageId to set
	 */
	public void setStageId(long stageId) {
		this.stageId = stageId;
	}

	/**
	 * @return the fixPanelType
	 */
	public String getFixPanelType() {
		return fixPanelType;
	}

	/**
	 * @param fixPanelType the fixPanelType to set
	 */
	public void setFixPanelType(String fixPanelType) {
		this.fixPanelType = fixPanelType;
	}

	/**
	 * @return the fixPanelX
	 */
	public Long getFixPanelX() {
		return fixPanelX;
	}

	/**
	 * @param fixPanelX the fixPanelX to set
	 */
	public void setFixPanelX(Long fixPanelX) {
		this.fixPanelX = fixPanelX;
	}

	/**
	 * @return the fixPanelY
	 */
	public Long getFixPanelY() {
		return fixPanelY;
	}

	/**
	 * @param fixPanelY the fixPanelY to set
	 */
	public void setFixPanelY(Long fixPanelY) {
		this.fixPanelY = fixPanelY;
	}

	public long getUniverseNumber() {
		return universeNumber;
	}

	public void setUniverseNumber(long universeNumber) {
		this.universeNumber = universeNumber;
	}
	
	public String toExportXml() {
		return "<fixture>\n" +
	        "    <id>" + id + "</id>\n" +
			"    <stageId>" + stageId + "</stageId>\n" +
			"    <fixtureDefId>" + fixtureDefId + "</fixtureDefId>\n" +
			"    <name>" + Text.escapeHtml(name) + "</name>\n" +
			"    <universeNumber>" + universeNumber + "</universeNumber>\n" +
			"    <dmxOffset>" + dmxOffset + "</dmxOffset>\n" +
			(x==null ? "" : "    <x>" + x + "</x>\n") +
			(y==null ? "" : "    <y>" + y + "</y>\n") +
			(z==null ? "" : "    <z>" + z + "</z>\n") +
			(lookingAtX==null ? "" : "    <lookingAtX>" + lookingAtX + "</lookingAtX>\n") +
			(lookingAtY==null ? "" : "    <lookingAtY>" + lookingAtY + "</lookingAtY>\n") +
			(lookingAtZ==null ? "" : "    <lookingAtZ>" + lookingAtZ + "</lookingAtZ>\n") +
			(upX==null ? "" : "    <upX>" + upX + "</upX>\n") +
			(upY==null ? "" : "    <upY>" + upY + "</upY>\n") +
			(upZ==null ? "" : "    <upZ>" + upZ + "</upZ>\n") +
			(sortOrder==null ? "" : "    <sortOrder>" + sortOrder + "</sortOrder>\n") +
			"    <fixPanelType>" + fixPanelType + "</fixPanelType>\n" +
			(fixPanelX==null ? "" : "    <fixPanelX>" + fixPanelX + "</fixPanelX>\n") +
			(fixPanelY==null ? "" : "    <fixPanelY>" + fixPanelY + "</fixPanelY>\n") +
			"</fixture>\n";
	}
	
	

}

