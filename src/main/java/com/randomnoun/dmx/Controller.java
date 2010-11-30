package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;

/** The controller contains all the interfaces a Show needs to
 * update the audio and lighting environment, and to receive
 * information from audio sources (frequency, beats).  
 * 
 * @author knoxg
 */
public class Controller {
	Universe universe;
	List<Fixture> fixtures;
	AudioController audioController;
	AudioSource audioSource;
	
	/** Create a new Controller instance */
	public Controller() {
		this.fixtures = new ArrayList<Fixture>();
	}
	
	// if you wanted to make this fancy, you could have multiple universes here
	/** Sets the DMX Universe that this controller modifies */
	public void setUniverse(Universe universe) {
		this.universe = universe;
	}
	
	/** Retrieves the DMX Universe that this controller modifies */
	public Universe getUniverse() {
		return universe;
	}
	
	/** Sets the AudioController used to manipulate the audio
	 * environment.
	 * 
	 * @param audioController The new AudioController
	 */
	public void setAudioController(AudioController audioController) {
		this.audioController = audioController;
	}
	
	/** Retrieves the AudioController used to manipulate the
	 * audio environment.
	 * 
	 * @return The current AudioController
	 */
	public AudioController getAudioController() {
		return audioController;
	}
	
	/** Adds a Fixture to this Controller. 
	 * 
	 * @param f fixture to add
	 */
	public void addFixture(Fixture f) {
		fixtures.add(f);
	}
	
	/** Removes all fixtures from this controller.
	 * This method is used during reloads 
	 */
	public void removeAllFixtures() {
		
		if (AppConfig.getAppConfig().getAppConfigState()!=AppConfig.AppConfigState.STOPPED) {
			throw new IllegalStateException("You cannot remove fixtures whilst the application is in " + AppConfig.getAppConfig().getAppConfigState() + " state");
		}
		fixtures.clear();
	}

	/** Sets all DMX channels available to this controller to zero. 
	 * 
	 */
	public void blackOut() {
		for (int i=1; i<Universe.MAX_CHANNELS; i++){
			universe.setDmxChannelValue(i, 0);
		}
	}

	/** Sets a DMX channel to a specific value
	 * 
	 * @param dmxChannelNumer the DMX channel (1-255)
	 * @param value the new value (0-255)
	 */
	public void setDmxChannelValue(int dmxChannelNumer, int value) {
		universe.setDmxChannelValue(dmxChannelNumer, value);
	}

	/** Retrieves the list of fixtures */
	public List<Fixture> getFixtures() {
		return fixtures;
	}
	
	/** Retrieves a Fixture by index
	 * 
	 * @param fixtureNumber the fixture index
	 * 
	 * @return the requested fixture
	 * 
	 * @throws IndexOutOfBoundsException if the Fixture does not exist
	 */
	public Fixture getFixture(int fixtureNumber) {
		return fixtures.get(fixtureNumber);
	}

	/** Returns a fixture by name; if a fixture with that name
	 * is not found, this method will return null  
	 * 
	 * @param fixtureName The fixture name
	 * 
	 * @return the requested fixture, or null
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

	/** Returns a fixture by name; if a fixture with that name
	 * is not found, this method will throw an IllegalArgumentException
	 * 
	 * @param fixtureName The fixture name
	 * 
	 * @return the requested fixture
	 * 
	 * @throws IllegalArgumentException if a fixture with that name
	 * is not found. 
	 */
	public Fixture getFixtureByName(String fixtureName) {
		Fixture fixture = getFixtureByNameNoEx(fixtureName);
		if (fixture==null) {
			throw new IllegalArgumentException("No fixture found with name '" + fixtureName + "'");
		}
		return fixture;
	}

	
	/** Returns a fixtureController by fixture name; if a fixture with that name
	 * is not found, this method will return null  
	 * 
	 * @param fixtureName The fixture name
	 * 
	 * @return the requested fixtureController, or null
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

	/** Returns a fixtureController by fixture name; if a fixture with that name
	 * is not found, this method will throw an IllegalArgumentException
	 * 
	 * @param fixtureName The fixture name
	 * 
	 * @return the requested fixtureController
	 * 
	 * @throws IllegalArgumentException if a fixture with that name
	 * is not found. 
	 */
	public FixtureController getFixtureControllerByName(String fixtureName) {
		FixtureController fixture = getFixtureControllerByNameNoEx(fixtureName);
		if (fixture==null) {
			throw new IllegalArgumentException("No fixture found with name '" + fixtureName + "'");
		}
		return fixture;
	}
	
	
	
}
