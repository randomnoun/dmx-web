package com.randomnoun.dmx.to;

public class FixtureDefTO {

    private long id;
    private String name;
    private String fixtureDefClassName;
    private String fixtureDefScript;
    private String fixtureControllerClassName;
    private String fixtureControllerScript;

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

    /** Returns the class name of the fixtureDef defined in the script
     * @return the class name of the fixtureDef defined in the script
     */
    public String getFixtureDefClassName() {
        return fixtureDefClassName;
    }

    /** Set the class name of the fixtureDef defined in the script
     * @param class name of the fixtureDef defined in the script
     */
    public void setFixtureDefClassName(String fixtureDefClassName) {
        this.fixtureDefClassName = fixtureDefClassName;
    }

    /** Returns the class name of the FixtureController defined in the script
     * @return the class name of the FixtureController defined in the script
     */
    public String getFixtureControllerClassName() {
        return fixtureControllerClassName;
    }

    /** Set the class name of the FixtureController defined in the script
     * @param class name of the FixtureController defined in the script
     */
    public void setFixtureControllerClassName(String fixtureControllerClassName) {
        this.fixtureControllerClassName = fixtureControllerClassName;
    }


    /** Returns the fixtureDefScript
     * @return the fixtureDefScript
     */
    public String getFixtureDefScript() {
        return fixtureDefScript;
    }

    /** Set the fixtureDefScript
     * @param fixtureDefScript
     */
    public void setFixtureDefScript(String fixtureDefScript) {
        this.fixtureDefScript = fixtureDefScript;
    }

    /** Returns the fixtureControllerScript
     * @return the fixtureControllerScript
     */
    public String getFixtureControllerScript() {
        return fixtureControllerScript;
    }

    /** Set the fixtureControllerScript
     * @param fixtureControllerScript
     */
    public void setFixtureControllerScript(String fixtureControllerScript) {
        this.fixtureControllerScript = fixtureControllerScript;
    }
    
    
}
