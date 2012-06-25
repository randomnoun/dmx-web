package com.randomnoun.dmx.to;

public class StageTO {

    private long id;
    private String name;
    private String filename;
    private String active;
    private String fixPanelBackgroundImage;

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

    /** Returns the filename
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /** Set the filename
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
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
    public void setActive(String active) {
        this.active = active;
    }

	public String getFixPanelBackgroundImage() {
		return fixPanelBackgroundImage;
	}

	public void setFixPanelBackgroundImage(String fixPanelBackgroundImage) {
		this.fixPanelBackgroundImage = fixPanelBackgroundImage;
	}

	public String toExportXml() {
		return "<stage>\n" +
			//"    <id>" + id + "</id>\n" +
			"    <name>" + name + "</name>\n" +
			"    <filename>" + filename + "</filename>\n" +
			"    <active>" + active + "</active>\n" +
			"    <fixPanelBackgroundImage>" + fixPanelBackgroundImage + "</fixPanelBackgroundImage>\n" +
			"</stage>\n";
	}

}

