package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.List;

/** An object which implements this method can return a list of 
 * exceptions that have occurred within it 
 */
public class ExceptionContainerImpl implements ExceptionContainer {

	List<TimestampedException> exceptionList;
	
	public ExceptionContainerImpl() {
		exceptionList = new ArrayList<TimestampedException>();
	}
	
	public List<TimestampedException> getExceptions() {
		return exceptionList;
	}
	public void clearExceptions() {
		exceptionList.clear();
	}
	public void addException(Exception e) {
		TimestampedException te = new TimestampedException(System.currentTimeMillis(), e);
		exceptionList.add(te);
	}
}
