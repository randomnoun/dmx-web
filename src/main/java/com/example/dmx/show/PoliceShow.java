package com.example.dmx.show;

import java.awt.Color;
import java.util.Properties;

import com.example.dmx.fixture.MiniWashFixtureController;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.show.Show;

/** Sets the washes to red and blue, then pan them around, swapping
 * colours occasionally.  
 * 
 * @author knoxg
 */
public class PoliceShow extends Show {

	MiniWashFixtureController leftWash;
	MiniWashFixtureController rightWash;
	
	public PoliceShow(long id, Controller controller, Properties properties) {
		super(id, controller, "Police", 5000L, properties);
		leftWash = (MiniWashFixtureController) controller.getFixture(0).getFixtureController();
		rightWash = (MiniWashFixtureController) controller.getFixture(1).getFixtureController();
		
	}
	
	public void pause() {}
	public void stop() {}
	
	protected void reset() {
		super.reset();
		leftWash.blackOut();
		rightWash.blackOut();
		// @TODO wait until muxers say that the fixtures
		// are in the right position
	}
	
	public void play() {
		reset();
		leftWash.setColor(Color.RED); 
		rightWash.setColor(Color.BLUE);
		leftWash.setMovementSpeed(0);
		rightWash.setMovementSpeed(0);
		leftWash.panTo(leftWash.getFixtureDef().getPanRange()); // ==540
		rightWash.panTo(rightWash.getFixtureDef().getPanRange()); // ==540
		waitUntil(2500); // 2.5 seconds into show
		if (isCancelled()) { return; }
		leftWash.setColor(Color.BLUE);
		rightWash.setColor(Color.RED);
		leftWash.panTo(0);
		rightWash.panTo(0);
		waitUntil(5000); // 5 seconds into show
		if (isCancelled()) { return; }
		leftWash.setColor(Color.BLACK);
		rightWash.setColor(Color.BLACK);
		
	}
	
	
}	
