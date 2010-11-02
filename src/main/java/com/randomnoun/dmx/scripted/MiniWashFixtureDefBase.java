package com.randomnoun.dmx.scripted;

import com.randomnoun.dmx.Fixture;
import com.randomnoun.dmx.FixtureController;
import com.randomnoun.dmx.FixtureDef;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.lightSource.HexagonLedGridDef;

public abstract class MiniWashFixtureDefBase extends FixtureDef {

	public MiniWashFixtureDefBase() {
		this.vendor = "Chinese sweatshop workers";
		this.model = "Miniwash";
		this.length = 300;
		this.width = 240;
		this.height= 240;
		this.weight = 3800;
		this.panRange = 540;
		this.tiltRange = 180;
		this.maxAmbientTemperature = 40;
		this.maxWattage = 68;
		this.lightSourceDef = new HexagonLedGridDef(3,
		  "  B R G " +
		  " R B G R " +
		  "G G x B B" +
		  " R B G R" +
		  "  B R G ");
	}

}
