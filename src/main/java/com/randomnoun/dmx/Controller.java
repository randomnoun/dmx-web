package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;

public class Controller {
	Universe universe;
	List<Fixture> fixtures;
	AudioController audioController;
	
	public Controller() {
		this.fixtures = new ArrayList<Fixture>();
	}
	
	// if you wanted to make this fancy, you could have multiple universes here
	public void setUniverse(Universe universe) {
		this.universe = universe;
	}
	public Universe getUniverse() {
		return universe;
	}
	
	public void setAudioController(AudioController audioController) {
		this.audioController = audioController;
	}
	public AudioController getAudioController() {
		return audioController;
	}
	
	public void addFixture(Fixture f) {
		fixtures.add(f);
	}
	
	/* To be used during reloads */
	public void removeAllFixtures() {
		
		if (AppConfig.getAppConfig().getAppConfigState()!=AppConfig.AppConfigState.STOPPED) {
			throw new IllegalStateException("You cannot remove fixtures whilst the application is in " + AppConfig.getAppConfig().getAppConfigState() + " state");
		}
		fixtures.clear();
	}

	public void blackOut() {
		for (int i=1; i<Universe.MAX_CHANNELS; i++){
			universe.setDmxChannelValue(i, 0);
		}
	}

	public void setDmxChannelValue(int dmxChannelNumer, int value) {
		universe.setDmxChannelValue(dmxChannelNumer, value);
	}
	
	public List<Fixture> getFixtures() {
		return fixtures;
	}
	
	public Fixture getFixture(int fixtureNumber) {
		return fixtures.get(fixtureNumber);
	}

	/** Returns a fixture by name; 
	 * if missing, will return null
	 * 
	 * @param fixtureName
	 * 
	 * @return
	 */
	public Fixture getFixtureByNameNoEx(String fixtureName) {
		// @TODO hashMap this if people start using it
		for (Fixture fixture : fixtures) {
			if (fixture.getName().equals(fixtureName)) {
				return fixture;
			}
		}
		return null;
	}

	/** Returns a fixture by name; 
	 * if missing, will throw an IllegalArgumentException
	 * 
	 * @param fixtureName
	 * 
	 * @return
	 */
	public Fixture getFixtureByName(String fixtureName) {
		Fixture fixture = getFixtureByNameNoEx(fixtureName);
		if (fixture==null) {
			throw new IllegalArgumentException("No fixture found with name '" + fixtureName + "'");
		}
		return fixture;
	}

	
	/** Returns a fixtureController by name; 
	 * if missing, will return null
	 * 
	 * @param fixtureName
	 * 
	 * @return
	 */
	public FixtureController getFixtureControllerByNameNoEx(String fixtureName) {
		// @TODO hashMap this if people start using it
		for (Fixture fixture : fixtures) {
			if (fixture.getName().equals(fixtureName)) {
				return fixture.getFixtureController();
			}
		}
		return null;
	}

	/** Returns a fixtureController by name; 
	 * if missing, will throw an IllegalArgumentException
	 * 
	 * @param fixtureName
	 * 
	 * @return
	 */
	public FixtureController getFixtureControllerByName(String fixtureName) {
		FixtureController fixture = getFixtureControllerByNameNoEx(fixtureName);
		if (fixture==null) {
			throw new IllegalArgumentException("No fixture found with name '" + fixtureName + "'");
		}
		return fixture;
	}
	
	
	
}
