package com.randomnoun.dmx.fixture;

import java.awt.Color;

/** A utility fixture output class that can have it's output values set
 * directly, rather than being calculated within the class.
 *   
 * @author knoxg
 */
public class KnownFixtureOutput extends FixtureOutput {

	public KnownFixtureOutput(long time, Color color, Double dim,
			Double strobe, Double pan, Double tilt, Double actualPan,
			Double actualTilt) {
		super();
		this.time = time;
		this.color = color;
		this.dim = dim;
		this.strobe = strobe;
		this.pan = pan;
		this.tilt = tilt;
		this.actualPan = actualPan;
		this.actualTilt = actualTilt;
	}
	Color color;
	long time;
	Double pan;
	Double tilt;
	Double actualPan;
	Double actualTilt;
	Double dim;
	Double strobe;
	
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public Double getPan() {
		return pan;
	}
	public void setPan(Double pan) {
		this.pan = pan;
	}
	public Double getTilt() {
		return tilt;
	}
	public void setTilt(Double tilt) {
		this.tilt = tilt;
	}
	public Double getActualPan() {
		return actualPan;
	}
	public void setActualPan(Double actualPan) {
		this.actualPan = actualPan;
	}
	public Double getActualTilt() {
		return actualTilt;
	}
	public void setActualTilt(Double actualTilt) {
		this.actualTilt = actualTilt;
	}
	public Double getDim() {
		return dim;
	}
	public void setDim(Double dim) {
		this.dim = dim;
	}
	public Double getStrobe() {
		return strobe;
	}
	public void setStrobe(Double strobe) {
		this.strobe = strobe;
	}
	
	 
	
}
