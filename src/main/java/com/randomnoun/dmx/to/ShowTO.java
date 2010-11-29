package com.randomnoun.dmx.to;

public class ShowTO {

    private long id;
    private long showDefId;
    private String name;
    private Long onCancelShowId;
    private Long onCompleteShowId;
    private Long showGroup;

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

    /** Returns the showGroup
     * @return the showGroup
     */
    public Long getShowGroup() {
        return showGroup;
    }

    /** Set the showGroup
     * @param showGroup
     */
    public void setShowGroup(Long showGroup) {
        this.showGroup = showGroup;
    }
    
    
}
