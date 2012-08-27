package com.randomnoun.dmx.fixture;


public class FixtureControllerMatrix {
	private int maxX=0, maxY=0;
	private int minX=Integer.MAX_VALUE, minY=Integer.MAX_VALUE;
	private FixtureController[][] fixtureControllers;
	
	public int getMaxX() {
		return maxX;
	}
	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}
	public int getMaxY() {
		return maxY;
	}
	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}
	public int getMinX() {
		return minX;
	}
	public void setMinX(int minX) {
		this.minX = minX;
	}
	public int getMinY() {
		return minY;
	}
	public void setMinY(int minY) {
		this.minY = minY;
	}
	public FixtureController[][] getFixtureControllers() {
		return fixtureControllers;
	}
	public void setFixtureControllers(FixtureController[][] fixtureControllers) {
		this.fixtureControllers = fixtureControllers;
	}
	
}