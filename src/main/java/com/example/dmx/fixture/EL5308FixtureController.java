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

/** The EL5308 is a strange thing indeed. Good thing it's not documented.
 * 
 * 
 * @author knoxg
 */
public class EL5308FixtureController extends FixtureController {

  public EL5308FixtureController(Fixture fixture) {
    super(fixture);
	  List controls = new ArrayList();
	  controls.add(getSliderCustomControl("Fancy 1", 0));
	  controls.add(getSliderCustomControl("Fancy 2", 2));
	  controls.add(getSliderCustomControl("Head 1", 3));
	  controls.add(getSliderCustomControl("Head 2", 4));
	  controls.add(getSliderCustomControl("Head 3", 5));
	  controls.add(getSliderCustomControl("Head 4", 6));
	  controls.add(getSliderCustomControl("Head 5", 7));
	  controls.add(getSliderCustomControl("Head 6", 8));
	  controls.add(getSliderCustomControl("Dunno", 9));
	  controls.add(getSliderCustomControl("Speed", 10));
	  setCustomControls(controls);
  }
  
  public CustomControl getSliderCustomControl(String label, int channel) {
  	final int finalChannel = channel;
    return new CustomControl(label, UIType.SLIDER, 
    new CustomControlCallback() {
      public void setValue(int value) { 
        fixture.setDmxChannelValue(finalChannel, value);
      }
    });
  }
  
  /** Unsure how colors are set in this fixture 
   * 
   * @param color The color to set the light to.
   */
  public void setColor(Color color) {
	  return;
  }

  /** Unsure whether this fixture has a dimmer */
  public void setMasterDimmer(int value) {
	  return;
  }

  public void setStrobe(int value) {
    fixture.setDmxChannelValue(1, value);
  }
  
  public void unsetStrobe() {
    fixture.setDmxChannelValue(1, 0);
  }

  
  public void setMovementSpeed(int i) {
    fixture.setDmxChannelValue(10, i);
  }

  /* maybe useful later
  public void setColorMacro(int i) {
    MacroChannelDef mcd = (MacroChannelDef) fixture.getFixtureDef().getChannelDefByOffset(9);
    fixture.setDmxChannelValue(9, mcd.getMacros().get(i).getLowValue());
  }

  public void setMovementMacro(int i) {
    MacroChannelDef mcd = (MacroChannelDef) fixture.getFixtureDef().getChannelDefByOffset(11);
    fixture.setDmxChannelValue(11, mcd.getMacros().get(i).getLowValue());
  }
  */
  
}