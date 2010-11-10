package com.randomnoun.dmx.to;

public class ShowTO {

    private long id;
    private long showTypeId;
    private Long onCancelShowId;
    private Long onCompleteShowId;

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

    /** Returns the showTypeId
     * @return the showTypeId
     */
    public long getShowTypeId() {
        return showTypeId;
    }

    /** Set the showTypeId
     * @param showTypeId
     */
    public void setShowTypeId(long showTypeId) {
        this.showTypeId = showTypeId;
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

}
