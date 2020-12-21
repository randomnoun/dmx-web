package com.randomnoun.dmx;

import java.util.ArrayList;
import java.util.List;

import com.randomnoun.common.ExceptionUtil;

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
		for (int i=exceptionList.size()-1; i>=0; i--) {
			TimestampedException le = exceptionList.get(i);
			if (ExceptionUtil.getStackTrace(le.getException()).equals(ExceptionUtil.getStackTrace(t))) {
				le.recur(System.currentTimeMillis());
				return;
			}
		}
		/*if (exceptionList.size()>0) {
			TimestampedException le = exceptionList.get(exceptionList.size()-1);
			if (ExceptionUtil.getStackTrace(le.getException()).equals(ExceptionUtil.getStackTrace(t))) {
				le.recur(System.currentTimeMillis());
				return;
			}
		}*/
		
		TimestampedException te = new TimestampedException(System.currentTimeMillis(), t);
		exceptionList.add(te);
	}
}
