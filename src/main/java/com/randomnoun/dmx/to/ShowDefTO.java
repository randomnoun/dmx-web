package com.randomnoun.dmx.to;

import java.util.List;

import com.randomnoun.common.Text;

public class ShowDefTO {

    private long id;
    private String name;
    private String className;
    private String script;
    private String javadoc;
    private boolean isRecorded;

    // used in import/export only
    private List<ShowDefAttachmentTO> showDefAttachments;

    
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

    /** Returns the script
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /** Set the script
     * @param script
     */
    public void setScript(String script) {
        this.script = script;
    }

    /** Returns the javadoc
     * @return the javadoc
     */
    public String getJavadoc() {
        return javadoc;
    }

    /** Set the javadoc
     * @param javadoc
     */
    public void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
    }

	public boolean isRecorded() {
		return isRecorded;
	}

	public void setRecorded(boolean isRecorded) {
		this.isRecorded = isRecorded;
	}
	
	public String toExportXml() {
		return "<showDef>\n" +
			"    <id>" + id + "</id>\n" +
			"    <name>" + Text.escapeHtml(name) + "</name>\n" +
			"    <className>" + className + "</className>\n" +
			//"<script>" + script + "</script>\n" +
			"    <javadoc>" + Text.escapeHtml(javadoc) + "</javadoc>\n" +
			"    <isRecorded>" + isRecorded + "</isRecorded>\n" +
			"</showDef>\n";
	}
    
    
}

