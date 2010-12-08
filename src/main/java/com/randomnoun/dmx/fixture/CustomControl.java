package com.randomnoun.dmx.fixture;

/** A custom control allows fixture-specific features (or programmer-created
 * features) to be selected from the fixture panel. 
 * 
 * @author knoxg
 */
public class CustomControl {

	/** The way in which this custom control should be represented to the end-user */
	public enum UIType {
		/** Display this control as a toggle button */
		TOGGLE, 
		
		/** Display this control as a slider */
		SLIDER,
		
		/** Display this control as a grid */
		GRID
	}

	private String label;
	private UIType uiType;
	private int value;
	private CustomControlCallback callback;
	private String top, left;
	
	public CustomControl(String label, UIType uiType, CustomControlCallback callback) {
		this.label = label;
		this.uiType = uiType;
		this.top = null;
		this.left = null;
		this.callback = callback;
	}
	
	public UIType getUIType() { return uiType; }
	public int getValue() { return value; }
	public String getLabel() { return label; }
	public void setTop(String top) { this.top = top; }
	public void setLeft(String left) { this.left = left; }
	public void setTop(long top) { this.top = String.valueOf(top) + "px"; }
	public void setLeft(long left) { this.left = String.valueOf(left) + "px"; }
	public String getTop() { return top; }
	public String getLeft() { return left; }
	
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
