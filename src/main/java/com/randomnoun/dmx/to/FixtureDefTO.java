package com.randomnoun.dmx.to;

import java.util.List;

import com.randomnoun.common.Text;

public class FixtureDefTO {

    private long id;
    private String name;
    private String fixtureDefClassName;
    private String fixtureDefScript;
    private String fixtureControllerClassName;
    private String fixtureControllerScript;
    private String channelMuxerClassName;
    private String channelMuxerScript;
    private long dmxChannels; // sourced from fixtureDefScript
    private String htmlImg16; // sourced from fixtureDefScript

    // used in import/export only
    private List<FixtureDefAttachmentTO> fixtureDefAttachments;
    
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
     * @param fixtureDefClassName class name of the fixtureDef defined in the script
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
     * @param fixtureControllerClassName class name of the FixtureController defined in the script
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
    
    /** Returns the class name of the ChannelMuxer defined in the script
     * @return the class name of the ChannelMuxer defined in the script
     */
    public String getChannelMuxerClassName() {
        return channelMuxerClassName;
    }

    /** Set the class name of the ChannelMuxer defined in the script
     * @param channelMuxerClassName class name of the ChannelMuxer defined in the script
     */
    public void setChannelMuxerClassName(String channelMuxerClassName) {
        this.channelMuxerClassName = channelMuxerClassName;
    }
    
    /** Returns the channelMuxerScript
    * @return the channelMuxerScript
    */
   public String getChannelMuxerScript() {
       return channelMuxerScript;
   }

   /** Set the channelMuxerScript
    * @param channelMuxerScript
    */
   public void setChannelMuxerScript(String channelMuxerScript) {
       this.channelMuxerScript = channelMuxerScript;
   }
   
   /** Returns the number of DMX channels
    * @return the number of DMX channels
    */
   public long getDmxChannels() {
       return dmxChannels;
   }

   /** Set the number of DMX channels
    * @param dmxChannels the number of DMX channels
    */
   public void setDmxChannels(long dmxChannels) {
       this.dmxChannels = dmxChannels;
   }

	public String toExportXml() {
		String s = "<fixtureDef>\n" +
			"    <id>" + id + "</id>\n" +
			"    <name>" + Text.escapeHtml(name) + "</name>\n" +
			"    <fixtureDefClassName>" + fixtureDefClassName + "</fixtureDefClassName>\n" +
			// "<fixtureDefScript>" + fixtureDefScript + "</fixtureDefScript>" +
			"    <fixtureControllerClassName>" + fixtureControllerClassName + "</fixtureControllerClassName>\n" +
			// "<fixtureControllerScript>" + fixtureControllerScript + "</fixtureControllerScript>" +
			"    <channelMuxerClassName>" + channelMuxerClassName + "</channelMuxerClassName>\n" +
			// "<channelMuxerScript>" + channelMuxerScript + "</channelMuxerScript>" +
			"    <dmxChannels>" + dmxChannels + "</dmxChannels>\n" +
			(htmlImg16 == null ? "" : "    <htmlImg16>" + htmlImg16 + "</htmlImg16>\n");
		if (fixtureDefAttachments.size() > 0) {
			s += "    <fixtureDefAttachments>\n";
			for (FixtureDefAttachmentTO fda : fixtureDefAttachments) {
				s += Text.indent("        ", fda.toExportXml());
			}
			s += "    </fixtureDefAttachments>\n";
		}
		s += "</fixtureDef>\n";
		return s;
	}

	public List<FixtureDefAttachmentTO> getFixtureDefAttachments() {
		return fixtureDefAttachments;
	}

	public void setFixtureDefAttachments(List<FixtureDefAttachmentTO> fixtureDefAttachments) {
		this.fixtureDefAttachments = fixtureDefAttachments;
	}

	public String getHtmlImg16() {
		return htmlImg16;
	}

	public void setHtmlImg16(String htmlImg16) {
		this.htmlImg16 = htmlImg16;
	}
	
}
