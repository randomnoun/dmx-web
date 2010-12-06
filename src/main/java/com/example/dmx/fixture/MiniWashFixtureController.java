package com.example.dmx.fixture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.randomnoun.dmx.channel.MacroChannelDef;
import com.randomnoun.dmx.fixture.CustomControl;
import com.randomnoun.dmx.fixture.CustomControlCallback;
import com.randomnoun.dmx.fixture.CustomControl.UIType;
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
    controls.add(getSliderCustomControl("Color speed", 10, 0, 20));
    controls.add(getMotionMacroCustomControl("Motion Macro 1", 1, 60, 20));
    controls.add(getMotionMacroCustomControl("Motion Macro 2", 2, 150, 20));
    controls.add(getMotionMacroCustomControl("Motion Macro 3", 3, 240, 20));
    controls.add(getMotionMacroCustomControl("Motion Macro 4", 4, 330, 20));
    controls.add(getMotionMacroCustomControl("Motion Macro 5", 5, 420, 20));
    controls.add(getMotionMacroCustomControl("Motion Macro 6", 6, 510, 20));
    controls.add(getMotionMacroCustomControl("Motion Macro 7", 7, 600, 20));
    controls.add(getMotionMacroCustomControl("Motion Macro 8", 8, 60, 100));
    CustomControl colorCycle=new CustomControl("Color Cycle", UIType.TOGGLE,
      new CustomControlCallback() {
        public void setValue(int value) {
          setColorMacro(value==1 ? 17: 0);
        }
      } );
    colorCycle.setLeft(150); colorCycle.setTop(100);
    controls.add(colorCycle);
    setCustomControls(controls);
  }
    
   public CustomControl getMotionMacroCustomControl(String label, int macro, long x, long y) {
        final int finalMacro = macro;
        CustomControl control = new CustomControl(label, UIType.TOGGLE, 
        new CustomControlCallback() {
          public void setValue(int value) { 
            setMovementMacro(value==1 ? finalMacro : 0);
            for (int i=1; i<=8; i++) { 
              if (i!=finalMacro) { getCustomControls().get(i).setValue(0); }
            }
          }
        });
          control.setLeft(x); control.setTop(y);
          return control;
      }
        
  public CustomControl getSliderCustomControl(String label, int channel, long x, long y) {
    final int finalChannel = channel;
    CustomControl control = new CustomControl(label, UIType.SLIDER, 
    new CustomControlCallback() {
      public void setValue(int value) { 
        fixture.setDmxChannelValue(finalChannel, value);
      }
    });
    control.setLeft(x); control.setTop(y);
    return control;
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