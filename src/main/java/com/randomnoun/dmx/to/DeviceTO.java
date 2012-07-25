package com.randomnoun.dmx.to;

import java.util.List;

import com.randomnoun.common.Text;

public class DeviceTO {

    private long id;
    private String name;
    private String className;
    private String type;
    private String active;
    private Long universeNumber;
    private long devicePropertyCount;
    
    // used in import/export only
    private List<DevicePropertyTO> deviceProperties;

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

    /** Returns the active
     * @return the active
     */
    public String getActive() {
        return active;
    }

    /** Set the active
     * @param active
     */
    public void setActive(String ynActive) {
        this.active = ynActive;
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

	public String toExportXml() {
		String s = "<device>\n" +
			// "    <id>" + id + "</id>\n" +                                                   
			"    <name>" + Text.escapeHtml(name) + "</name>\n" +                                             
			"    <className>" + className + "</className>\n" +                              
			"    <type>" + type + "</type>\n" +                                             
			"    <active>" + active + "</active>\n" +                                       
			"    <universeNumber>" + universeNumber + "</universeNumber>\n";
		if (deviceProperties.size() > 0) {
			s += "    <deviceProperties>\n";
			for (DevicePropertyTO dp : deviceProperties) {
				s += Text.indent("        ", dp.toExportXml());
			}
			s += "    </deviceProperties>\n";
		}
		s += "</device>\n";
		return s;
	}

	public List<DevicePropertyTO> getDeviceProperties() {
		return deviceProperties;
	}

	public void setDeviceProperties(List<DevicePropertyTO> deviceProperties) {
		this.deviceProperties = deviceProperties;
	}


}

