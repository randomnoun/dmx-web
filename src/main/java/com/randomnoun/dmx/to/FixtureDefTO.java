package com.randomnoun.dmx.to;

public class FixtureDefTO {

    private long id;
    private String name;
    private String className;
    private String script;

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

}
