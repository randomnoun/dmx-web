package com.example.dmx.fixture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.fixture.CustomControl;
import com.randomnoun.dmx.fixture.CustomControl.UIType;
import com.randomnoun.dmx.fixture.CustomControlCallback;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;

/** The MiniWashFixtureController can have it's color and strobe controlled
 * by the default FixtureController. 
 * 
 * <p>It adds additional methods for macros, but these could be generalised
 * to the superclass.
 * 
 * @author knoxg
 */
public class MiniWashFixtureController extends FixtureController {

  public MiniWashFixtureController(Fixture fixture) {
    super(fixture);
	  List controls = new ArrayList();
	  controls.add(
	    new CustomControl("Movement Macro 1", UIType.TOGGLE, 
	    new CustomControlCallback() {
	    	public void setValue(int value) { 
	    		setMovementMacro(value==1 ? 1 : 0);
	    		getCustomControls().get(1).setValue(0);
	    	}; }) );
	  controls.add(
	    new CustomControl("Movement Macro 2", UIType.TOGGLE, 
	    new CustomControlCallback() {
	    	public void setValue(int value) { 
	    		setMovementMacro(value==1 ? 2 : 0);
	    		getCustomControls().get(0).setValue(0);
	    	}; }) );
	  setCustomControls(controls);
    
  }
  
  /** Sets the color of this fixture.
   * 
   * This sets the red, green and blue dimmers to the values for this color,
   * and sets the dimmer/strobe macro channel to open. 
   * 
   * @param color The color to set the light to.
   */
  public void setColor(Color color) {
    fixture.setDmxChannelValue(6, color.getRed());
    fixture.setDmxChannelValue(7, color.getGreen());
    fixture.setDmxChannelValue(8, color.getBlue());
    int dimmerStrobeValue = fixture.getDmxChannelValue(5);
    if (dimmerStrobeValue >= 135 && dimmerStrobeValue <= 239) {
      // currently strobing, don't set intensity (this will disable strobe)
    } else {
      fixture.setDmxChannelValue(5, 255);
    }
  }
  
  public void setMasterDimmer(int value) {
    //super.setMasterDimmer(value);
    fixture.setDmxChannelValue(5, 8 + ((255-value)*126)/255);
  }

  public void setStrobe(int value) {
    fixture.setDmxChannelValue(5, 135 + (value*104/255));
  }
  
  public void unsetStrobe() {
    fixture.setDmxChannelValue(5, 0);
  }


  public void setMovementSpeed(int i) {
    fixture.setDmxChannelValue(4, i);
  }

  public void setColorMacro(int i) {
    MacroChannelDef mcd = (MacroChannelDef) fixture.getFixtureDef().getChannelDefByOffset(9);
    fixture.setDmxChannelValue(9, mcd.getMacros().get(i).getLowValue());
  }

  public void setMovementMacro(int i) {
    MacroChannelDef mcd = (MacroChannelDef) fixture.getFixtureDef().getChannelDefByOffset(11);
    fixture.setDmxChannelValue(11, mcd.getMacros().get(i).getLowValue());
  }
  
}