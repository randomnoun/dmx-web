package com.randomnoun.dmx.fixture;

/** A custom control allows fixture-specific features (or programmer-created
 * features) to be selected from the fixture panel. 
 * 
 * @author knoxg
 */
public class CustomControl {

	public enum UIType { TOGGLE, SLIDER }

	private String label;
	private UIType uiType;
	private int value;
	private CustomControlCallback callback;
	
	public CustomControl(String label, UIType uiType, CustomControlCallback callback) {
		this.label = label;
		this.uiType = uiType;
		this.callback = callback;
	}
	
	public UIType getUIType() { return uiType; }
	public int getValue() { return value; }
	public String getLabel() { return label; }
	
	// sets the value of this control, non-user initiated
	// (e.g. to clear a set of mutually-exclusive toggles)
	// should only be called from within CustomControlCallback methods. 
	public void setValue(int value) { 
		this.value = value; 
	}
	
	// sets the value of this control, as if the user initiated it
	// will call the supplied callback, which should then perform the
	// requested action
	public void setValueWithCallback(int value) {
		this.value = value;
		callback.setValue(value);
	}
	
}
