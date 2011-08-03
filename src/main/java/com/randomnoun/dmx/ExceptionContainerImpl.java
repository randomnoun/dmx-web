package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.List;

import com.randomnoun.common.ExceptionUtils;

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
	public void addException(Throwable t) {
		if (exceptionList.size()>0) {
			TimestampedException le = exceptionList.get(exceptionList.size()-1);
			if (ExceptionUtils.getStackTrace(le.getException()).equals(ExceptionUtils.getStackTrace(t))) {
				le.recur(System.currentTimeMillis());
				return;
			}
		}
		TimestampedException te = new TimestampedException(System.currentTimeMillis(), t);
		exceptionList.add(te);
	}
}
