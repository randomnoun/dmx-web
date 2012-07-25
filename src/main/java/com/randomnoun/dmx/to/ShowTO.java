package com.randomnoun.dmx.to;

import java.util.List;

import com.randomnoun.common.Text;

public class ShowTO {

    private long id;
    private long showDefId;
    private String name;
    private Long onCancelShowId;
    private Long onCompleteShowId;
    private Long showGroupId;
    private long showPropertyCount;
    private long stageId;

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

    /** Returns the onCancelShowId
     * @return the onCancelShowId
     */
    public Long getOnCancelShowId() {
        return onCancelShowId;
    }

    /** Set the onCancelShowId
     * @param onCancelShowId
     */
    public void setOnCancelShowId(Long onCancelShowId) {
        this.onCancelShowId = onCancelShowId;
    }

    /** Returns the onCompleteShowId
     * @return the onCompleteShowId
     */
    public Long getOnCompleteShowId() {
        return onCompleteShowId;
    }

    /** Set the onCompleteShowId
     * @param onCompleteShowId
     */
    public void setOnCompleteShowId(Long onCompleteShowId) {
        this.onCompleteShowId = onCompleteShowId;
    }

    /** Returns the showGroupId
     * @return the showGroupId
     */
    public Long getShowGroupId() {
        return showGroupId;
    }

    /** Set the showGroup
     * @param showGroupId the showGroup
     */
    public void setShowGroupId(Long showGroupId) {
        this.showGroupId = showGroupId;
    }

    /** Returns the showGroshowPropertyCountupId
     * @return the showPropertyCount
     */
    public long getShowPropertyCount() {
        return showPropertyCount;
    }

    /** Set the showPropertyCount
     * @param showPropertyCount
     */
    public void setShowPropertyCount(long showPropertyCount) {
        this.showPropertyCount = showPropertyCount;
    }

	/**
	 * @return the stageid
	 */
	public long getStageId() {
		return stageId;
	}

	/**
	 * @param stageid the stageid to set
	 */
	public void setStageId(long stageid) {
		this.stageId = stageid;
	}
	
	// @TODO put comments in here with the stage, showDef names etc 
	public String toExportXml(List<ShowPropertyTO> showProperties) {
		String s = "<show>\n" +
		    "    <id>" + id + "</id>\n" +
			"    <stageId>" + stageId + "</stageId>\n" +		
			"    <showDefId>" + showDefId + "</showDefId>\n" +
			"    <name>" + Text.escapeHtml(name) + "</name>\n" +
			(onCancelShowId==null ? "" : "    <onCancelShowId>" + onCancelShowId + "</onCancelShowId>\n") +
			(onCompleteShowId==null ? "" : "    <onCompleteShowId>" + onCompleteShowId + "</onCompleteShowId>\n") +
			"    <showGroupId>" + showGroupId + "</showGroupId>\n";
		// "    <showPropertyCount>" + showPropertyCount + "</showPropertyCount>\n" +
		if (showProperties.size() > 0) {
			s += "    <showProperties>\n";
			for (ShowPropertyTO sp : showProperties) {
				s += Text.indent("        ", sp.toExportXml());
			}
			s += "    </showProperties>\n";
		}
		s += "</show>\n";
		return s;
	}

    
}
