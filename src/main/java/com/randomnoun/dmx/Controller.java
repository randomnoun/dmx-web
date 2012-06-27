package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.randomnoun.dmx.audioController.AudioController;
import com.randomnoun.dmx.audioSource.AudioSource;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.stage.Stage;

/** The controller contains all the interfaces a Show needs to
 * update the audio and lighting environment, and to receive
 * information from audio sources (frequency, beats).  
 * 
 * @author knoxg
 */
public class Controller {
	
	Stage stage;
	List<Universe> universes;
	List<Fixture> fixtures;
	AudioController audioController;
	AudioSource audioSource;

	Logger logger = Logger.getLogger(Controller.class);
	
	/** Create a new Controller instance */
	public Controller() {
		this.fixtures = new ArrayList<Fixture>();
	}
	
	// if you wanted to make this fancy, you could have multiple universes here
	/** Sets the DMX Universe that this controller modifies */
	public void setUniverses(List<Universe> universes) {
		this.universes = universes;
	}
	
	/** Retrieves the DMX Universe that this controller modifies */
	public Universe getUniverse(int universeIdx) {
		return universes.get(universeIdx);
	}
	public List<Universe> getUniverses() { 
		return universes;
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
	
	// @TODO make these package private, and invoke them from a utility class
	
	/** Adds a Fixture to this Controller. 
	 * 
	 * @param f fixture to add
	 */
	public void addFixture(Fixture f) {
		fixtures.add(f);
	}

	/** Removes a Fixture from this Controller. This should only ever be called internally
	 * if the fixture or fixture controller cannot be instantiated correctly.
	 * 
	 * @param f fixture to add
	 */
	public void removeFixture(Fixture f) {
		fixtures.remove(f);
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

	/** Sets all DMX channels available to this controller to zero,
	 * and blackOut all fixtures. 
	 */
	public void blackOut() {
		for (int u=0; u<universes.size(); u++) {
			Universe universe = universes.get(u);
			for (int i=1; i<Universe.MAX_CHANNELS; i++){
				universe.setDmxChannelValue(i, 0);
			}
		}
		for (Fixture f : fixtures) {
			f.blackOut();
		}
	}

	/** Sets a DMX channel to a specific value
	 * 
	 * @param dmxChannelNumber the DMX channel (1-255)
	 * @param value the new value (0-255)
	 */
	public void setDmxChannelValue(int universeIdx, int dmxChannelNumber, int value) {
		universes.get(universeIdx).setDmxChannelValue(dmxChannelNumber, value);
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

	/** Returns a list of fixtures with the supplied fixture definition name; if a fixture with that name
	 * is not found, this method will return an empty list
	 * 
	 * @param fixtureDefName The name of the fixture definition
	 * 
	 * @return the requested fixture
	 * 
	 * @throws IllegalArgumentException if a fixture with that name
	 * is not found. 
	 */
	public List<Fixture> getFixturesByFixtureDef(String fixtureDefName) {
		List result = new ArrayList<Fixture>();
		
		for (Fixture fixture : fixtures) {
			if (fixture.getFixtureDef().getName().equals(fixtureDefName)) {
				result.add(fixture);
			}
		}
		return result;
	}

	/** Returns a list of fixtureControllers with the supplied fixture definition name; if a fixture with that name
	 * is not found, this method will return an empty list
	 * 
	 * @param fixtureDefName The name of the fixture definition
	 * 
	 * @return the requested fixture
	 * 
	 * @throws IllegalArgumentException if a fixture with that name
	 * is not found. 
	 */
	public List<FixtureController> getFixtureControllersByFixtureDef(String fixtureDefName) {
		List result = new ArrayList<FixtureController>();
		
		for (Fixture fixture : fixtures) {
			if (fixture.getFixtureDef().getName().equals(fixtureDefName)) {
				result.add(fixture.getFixtureController());
			}
		}
		return result;
	}

	
	/** Returns a list of fixtures given an array of names; used by the RecordedShow
	 * class (possibly should go in there ?)
	 * 
	 * <p>Does not throw exceptions on unknown fixtures.
	 * 
	 * @param fixtureName The fixture name
	 * 
	 * @return the requested fixture, or null
	 */
	public List<Fixture> getFixturesByName(String[] names) {
		List<Fixture> result = new ArrayList<Fixture>();
		for (int i=0; i<names.length; i++) {
			String name = names[i];
			for (Fixture fixture : fixtures) {
				if (fixture.getName().equals(name)) {
					result.add(fixture);
					break;
				}
			}
		}
		// logger.info("Returning " + result.size() + " fixtures");
		return result;
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

	/** Returns the stage containing the fixtures and show definitions for this 
	 * controller.
	 * 
	 * @return the active stage
	 */
	public Stage getStage() {
		return stage;
	}

	/** Sets the stage containing the fixtures and show definitions for this 
	 * controller.
	 * 
	 * @param stage the active stage
     */
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	
	
}
