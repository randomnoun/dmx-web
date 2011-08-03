package com.randomnoun.dmx;

import java.util.List;

/** An object which implements this interface can return a list of 
 * exceptions that have occurred within it. 
 */
public interface ExceptionContainer {

	/** TimestampedExceptions are used within ExceptionContainers
	 * and container information on when an exception first and
	 * last occured, and the number of times it has occurred.
	 * 
	 * @author knoxg
	 */
	public static class TimestampedException {
		long firstTimestamp;
		long lastTimestamp;
		long count;
		Throwable exception;
		
		/** Creates a new TimestampedException.
		 * 
		 * @param timestamp The time the exception occurred (msec since the epoch)
		 * @param exception The exception
		 */
		public TimestampedException(long timestamp, Throwable exception) {
			this.lastTimestamp = timestamp;
			this.firstTimestamp = timestamp;
			this.exception = exception;
			this.count = 1;
		}
		
		public long getTimestamp() { return lastTimestamp; }
		public long getFirstTimestamp() { return firstTimestamp; }
		public long getCount() { return count ;}
		public Throwable getException() { return exception; }
		public void recur(long timestamp) {
			this.lastTimestamp = timestamp;
			this.count++;
		}
	}

	/** Returns the list of exceptions that have occurred within this object. */
	public List<TimestampedException> getExceptions();
	
	/** Clears the list of exceptions that have occurred within this object. */
	public void clearExceptions();
}
