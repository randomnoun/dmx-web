package com.randomnoun.dmx;

import java.util.List;

/** An object which implements this method can return a list of 
 * exceptions that have occurred within it 
 */
public interface ExceptionContainer {

	public static class TimestampedException {
		long timestamp;
		Exception exception;
		public TimestampedException(long timestamp, Exception exception) {
			this.timestamp = timestamp;
			this.exception = exception;
		}
		public long getTimestamp() { return timestamp; }
		public Exception getException() { return exception; }
	}
	
	public List<TimestampedException> getExceptions();
	public void clearExceptions();
}
