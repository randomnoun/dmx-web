package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.List;

public class Controller {
	Universe universe;
	List<Fixture> fixtures;
	
	public Controller() {
		this.fixtures = new ArrayList<Fixture>();
	}
	
	// if you wanted to make this fancy, you could have multiple universes here
	public void setUniverse(Universe universe) {
		this.universe = universe;
	}
	
	public void addFixture(Fixture f) {
		fixtures.add(f);
	}

	public void blackOut() {
		for (int i=0; i<Universe.MAX_CHANNELS; i++){
			universe.setDmxChannelValue(i, 0);
		}
	}

	public void setDmxChannelValue(int dmxChannelNumer, int value) {
		universe.setDmxChannelValue(dmxChannelNumer, value);
	}
	
	public List<Fixture> getFixtures() {
		return fixtures;
	}
	
	
	
}
