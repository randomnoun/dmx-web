package com.randomnoun.dmx.to;

public class DeviceTO {

    private long id;
    private String name;
    private String className;
    private String type;
    private String ynActive;
    private Long universeNumber;
    private long devicePropertyCount;
    

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

    /** Returns the className
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /** Set the className
     * @param className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /** Returns the type
     * @return the type
     */
    public String getType() {
        return type;
    }

    /** Set the type
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /** Returns the ynActive
     * @return the ynActive
     */
    public String getYnActive() {
        return ynActive;
    }

    /** Set the ynActive
     * @param ynActive
     */
    public void setYnActive(String ynActive) {
        this.ynActive = ynActive;
    }

    /** Returns the universeNumber
     * @return the universeNumber
     */
    public Long getUniverseNumber() {
        return universeNumber;
    }

    /** Set the universeNumber
     * @param universeNumber
     */
    public void setUniverseNumber(Long universeNumber) {
        this.universeNumber = universeNumber;
    }

	public long getDevicePropertyCount() {
		return devicePropertyCount;
	}

	public void setDevicePropertyCount(long devicePropertyCount) {
		this.devicePropertyCount = devicePropertyCount;
	}


}
