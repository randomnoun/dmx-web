package com.randomnoun.dmx;

import java.util.List;

/** An object which implements this method can return a list of 
 * exceptions that have occurred within it 
 */
public interface ExceptionContainer {

	public static class TimestampedException {
		long firstTimestamp;
		long lastTimestamp;
		long count;
		Exception exception;
		public TimestampedException(long timestamp, Exception exception) {
			this.lastTimestamp = timestamp;
			this.firstTimestamp = timestamp;
			this.exception = exception;
			this.count = 1;
		}
		public long getTimestamp() { return lastTimestamp; }
		public long getFirstTimestamp() { return firstTimestamp; }
		public long getCount() { return count ;}
		public Exception getException() { return exception; }
		public void recur(long timestamp) {
			this.lastTimestamp = timestamp;
			this.count++;
		}
	}
	
	public List<TimestampedException> getExceptions();
	public void clearExceptions();
}
