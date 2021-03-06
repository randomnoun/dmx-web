package com.randomnoun.dmx.audioController.winampNg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.log4j.Logger;

/** An interface to the ngwinamp remote control for winamp */
public class WinampNG {

	Logger logger = Logger.getLogger(WinampNG.class);
	
	String host;
	int port;
	int timeout;
	
	private boolean retryEnabled = false;
	private String retryPassword;

	// message currently being read
	long curcode = 0;
	long curparam1 = 0;
	long curparam2 = 0;
	double curparam3 = 0;
	long cursize = 0;
	byte[] curdata;
	
	// last message written (used for retries)
	byte[] outputBuffer;
	
	Socket socket;
	LEDataInputStream inputStream;
	LEDataOutputStream outputStream;
	
	// generic constants
	public static long NGWINAMP_NONE = 0;
	public static long NGWINAMP_ALL = -1;

	// auth results
	 // authorization successfull
	public static long NGWINAMP_AUTH_SUCCESS = 0x0;
	 // authorization failure (wrong passord or not authorized)
	public static long NGWINAMP_AUTH_FAILURE = 0x1;
	 // authorization not done (a command has been sent without authorization)
	public static long NGWINAMP_AUTH_NOTDONE = 0x2;
	 // authorization failed (too many connection at a given time, or invalid source address)
	public static long NGWINAMP_AUTH_TOOMANYCON = 0x3;

	// requests
	public static long NGWINAMP_REQ_NONE = 0;
	 // auth
	public static long NGWINAMP_REQ_AUTH = 1;
	 // sound
	public static long NGWINAMP_REQ_PREV = 11;
	public static long NGWINAMP_REQ_PLAY = 12;
	public static long NGWINAMP_REQ_PAUSE = 13;
	public static long NGWINAMP_REQ_STOP = 14;
	public static long NGWINAMP_REQ_NEXT = 15;
	public static long NGWINAMP_REQ_GETVOLUME = 21;
	public static long NGWINAMP_REQ_SETVOLUME = 22;
	public static long NGWINAMP_REQ_GETPAN = 23;
	public static long NGWINAMP_REQ_SETPAN = 24;
	public static long NGWINAMP_REQ_GETPOS = 25;
	public static long NGWINAMP_REQ_SETPOS = 26;
	 // playlist
	public static long NGWINAMP_REQ_PLCLEAR = 100;
	public static long NGWINAMP_REQ_PLGETNAMES = 101;
	public static long NGWINAMP_REQ_PLGETFILES = 103;
	public static long NGWINAMP_REQ_PLSETFILES = 104;
	public static long NGWINAMP_REQ_PLDELFILES = 105;
	public static long NGWINAMP_REQ_PLADDFILES = 106;
	public static long NGWINAMP_REQ_PLMOVEFILES = 107;
	public static long NGWINAMP_REQ_PLGETPOS = 111;
	public static long NGWINAMP_REQ_PLSETPOS = 112;
	public static long NGWINAMP_REQ_PLGETSHUFFLE = 121;
	public static long NGWINAMP_REQ_PLSETSHUFFLE = 122;
	public static long NGWINAMP_REQ_PLGETREPEAT = 123;
	public static long NGWINAMP_REQ_PLSETREPEAT = 124;
	public static long NGWINAMP_REQ_PLRANDOMIZE = 130;
	public static long NGWINAMP_REQ_PLSORTBYNAME = 131;
	public static long NGWINAMP_REQ_PLSORTBYPATH = 132;
	public static long NGWINAMP_REQ_PLDELDEADFILES = 133;
	 // browsing
	public static long NGWINAMP_REQ_BWGETROOTS = 200;
	public static long NGWINAMP_REQ_BWGETLIST = 201;

	// answers
	public static long NGWINAMP_ANS_NONE = 0;
	 // auth
	public static long NGWINAMP_ANS_AUTH = 1;
	 // sound
	public static long NGWINAMP_ANS_VOLUME = 21;
	public static long NGWINAMP_ANS_PAN = 23;
	public static long NGWINAMP_ANS_POS = 25;
	 // playlist
	public static long NGWINAMP_ANS_PLNAMES = 101;
	public static long NGWINAMP_ANS_PLFILES = 103;
	public static long NGWINAMP_ANS_PLBROWSE = 107;
	public static long NGWINAMP_ANS_PLPOS = 111;
	public static long NGWINAMP_ANS_PLSHUFFLE = 121;
	public static long NGWINAMP_ANS_PLREPEAT = 123;
	 // browsing
	public static long NGWINAMP_ANS_BWROOTS = 200;
	public static long NGWINAMP_ANS_BWLIST = 201;

	

	
	public WinampNG(String host, int port, int timeout, boolean retryEnabled) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.retryEnabled = retryEnabled;
	}
	
	public void connect() throws IOException {
		InetAddress inetAddress = InetAddress.getByName(host);
		SocketAddress socketAddress = new InetSocketAddress(host, port);
		socket = new Socket();
		socket.setSoTimeout(timeout);
		socket.connect(socketAddress);
		logger.debug("Socket " + (socket.isConnected() ? "" : "dis") + "connected; recv buffer size=" + socket.getReceiveBufferSize() + ", send buffer size=" + socket.getSendBufferSize());
		inputStream = new LEDataInputStream(socket.getInputStream());
		outputStream = new LEDataOutputStream(socket.getOutputStream());
	}
	
	public void disconnect() throws IOException {
		socket.close();
	}
	
	public long authenticate(String password) throws IOException {
		if (retryEnabled) { retryPassword = password; }
		ngsend(NGWINAMP_REQ_AUTH, 0, 0, 0.0, password.length(), password.getBytes());
		waitForMessage(NGWINAMP_ANS_AUTH);
		// return NGWINAMP_AUTH_FAILURE;
		// return authentification code (see top of this file)
		return curparam1;
	}
	
	// Go to previous song
	public void sendPrev() throws IOException {
		ngsend(NGWINAMP_REQ_PREV);
	}
	// Play the current song
	public void sendPlay() throws IOException {
		ngsend(NGWINAMP_REQ_PLAY);
	}
	// Pause/resume playing current song
	public void sendPause() throws IOException {
		ngsend(NGWINAMP_REQ_PAUSE);
	}
	// Stop playing current song
	public void sendStop() throws IOException {
		ngsend(NGWINAMP_REQ_STOP);
	}
	// Go to next song
	public void sendNext() throws IOException {
		ngsend(NGWINAMP_REQ_NEXT);
	}
	// Retrieve current volume (return from 0.0 to 100.0, in percent)
	public double sendGetVolume() throws IOException {
		ngsend(NGWINAMP_REQ_GETVOLUME);
		waitForMessage(NGWINAMP_ANS_VOLUME);
		return curparam3 * 100.0;
	}
	// Set current volume (input from 0.0 to 100.0, in percent)
	public void sendSetVolume(double volume) throws IOException {
		if (volume < 0) { throw new IllegalArgumentException("Volume must be greater than or equal to 0.0"); }
		if (volume > 100) { throw new IllegalArgumentException("Volume must be less than or equal to 100.0"); }
		ngsend(NGWINAMP_REQ_SETVOLUME, 0, 0, (volume / 100), 0, null);
	}
	
	// Retrieve current panning (return from -100.0 to 100.0, in percent ; a negative value mean left and a postive one mean right)
	public double sendGetPan() throws IOException {
		ngsend(NGWINAMP_REQ_GETPAN);
		waitForMessage(NGWINAMP_ANS_PAN);
		return curparam3 * 100.0;
	}
	// // Set current panning (input from -100.0 to 100.0, in percent ; a negative value mean left and a postive one mean right)
	public void sendSetPan(double pan) throws IOException {
		if (pan < -100) { throw new IllegalArgumentException("Volume must be greater than or equal to -100.0"); }
		if (pan > 100) { throw new IllegalArgumentException("Volume must be less than or equal to 100.0"); }
		ngsend(NGWINAMP_REQ_SETPAN, 0, 0, (pan / 100), 0, null);
	}
	
	public static class SongPosition {
		double percentPosition;
		long position;
	    long length;
	    public SongPosition(double percentPosition, long position, long length) {
	    	this.percentPosition = percentPosition;
	    	this.position = position;
	    	this.length = length;
	    }
	    /** Get position in percent */
	    public double getPercentPosition() { return percentPosition; }
	    /** Get position in milliseconds */
	    public long getPosition() { return position; }
	    /** Get length in milliseconds */
	    public long getLength() { return length; }
	}
	
	// Retrieve current song postion and length. (return an array with 'pos'      -> position in percent,
	// 																   'posms'    -> position in milliseconds,
	//																   'lengthms' -> length in milliseconds)
	public SongPosition sendGetSongPosition() throws IOException {
		ngsend(NGWINAMP_REQ_GETPOS);
		waitForMessage(NGWINAMP_ANS_POS);
		return new SongPosition(curparam3 * 100.0, curparam2, curparam1);
	}
	
	// Set current song postion. (input from 0.0 to 100.0, in percent)
	public void sendSetPosition(double position) throws IOException {
		if (position < 0) { throw new IllegalArgumentException("Position must be greater than or equal to 0.0"); }
		if (position > 100) { throw new IllegalArgumentException("Position must be less than or equal to 100.0"); }
		ngsend(NGWINAMP_REQ_SETPOS, 0, 0, (position / 100), 0, null);
	}
	
	// Clear the current playlist
	public void sendClearPlaylist() throws IOException {
		ngsend(NGWINAMP_REQ_PLCLEAR);
	}
	
	// Retrieve one/all song's title in playlist (return an array of string)
	// default playlistIndex to NGWINAMP_ALL
	public String[] sendGetNames(long playlistIndex) throws IOException {
		ngsend(NGWINAMP_REQ_PLGETNAMES, playlistIndex, 0, 0.0, 0, null);
		waitForMessage(NGWINAMP_ANS_PLNAMES);
		ByteArrayInputStream bais = new ByteArrayInputStream(curdata);
		LEDataInputStream dataInput = new LEDataInputStream(bais);
		String[] names = new String[(int) curparam1]; 
		for (int i=0; i<curparam1; i++) {
			long index = dataInput.readInt();
			short length = dataInput.readShort();
			byte[] name = new byte[length];
			int bytesRead = dataInput.read(name, 0, name.length);
			names[i] = new String(name, "ASCII");
		}
		return names;
	}
	
	// Retrieve one/all song's title in playlist (return an array of string)
	// default playlistIndex to NGWINAMP_ALL
	public String[] sendGetFiles(long playlistIndex) throws IOException {
		ngsend(NGWINAMP_REQ_PLGETFILES, playlistIndex, 0, 0.0, 0, null);
		waitForMessage(NGWINAMP_ANS_PLFILES);
		ByteArrayInputStream bais = new ByteArrayInputStream(curdata);
		LEDataInputStream dataInput = new LEDataInputStream(bais);
		String[] names = new String[(int) curparam1]; 
		for (int i=0; i<curparam1; i++) {
			long index = dataInput.readInt(); // TODO validate against 'i'
			short length = dataInput.readShort();
			byte[] name = new byte[length];
			int bytesRead = dataInput.read(name, 0, name.length);
			names[i] = new String(name, "ASCII");
		}
		return names;
	}
	
	/** Set playlist items (input is an array of filename)
	 * 
	 * @param files
	 * 
	 * @throws IOException
	 */
	public void sendSetFiles(String[] files) throws IOException {
		if (files.length==0) { throw new IllegalArgumentException("Cannot use setFiles with length zero"); }
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LEDataOutputStream dataOutput = new LEDataOutputStream(baos);
		for (int i=0; i<files.length; i++) {
			dataOutput.writeInt(i);
			dataOutput.writeShort(files[i].length());
			dataOutput.writeBytes(files[i]);
		}
		byte[] buffer = baos.toByteArray();
		ngsend(NGWINAMP_REQ_PLSETFILES, files.length, 0, 0.0, buffer.length, buffer);
	}


	/** Add playlist items (input is an array of filename)
	 * 
	 * @param files
	 * 
	 * @throws IOException
	 */
	public void sendAddFiles(String[] files) throws IOException {
		if (files.length==0) { throw new IllegalArgumentException("Cannot use setFiles with length zero"); }
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LEDataOutputStream dataOutput = new LEDataOutputStream(baos);
		for (int i=0; i<files.length; i++) {
			dataOutput.writeInt(i);
			dataOutput.writeShort(files[i].length());
			dataOutput.writeBytes(files[i]);
		}
		byte[] buffer = baos.toByteArray();
		ngsend(NGWINAMP_REQ_PLADDFILES, files.length, 0, 0.0, buffer.length, buffer);
	}
	
	/** Delete playlist items (input is an array of index ; between 0 and playlist's length)
	 * 
	 * @param playlistIds the list of ids to remove from the playlist
	 *
	 * @throws IOException 
	 */
	public void sendDeletePlaylist(int[] playlistIds) throws IOException {
		if (playlistIds.length==0) { throw new IllegalArgumentException("Cannot use sendDeletePlaylist with length zero"); }
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LEDataOutputStream dataOutput = new LEDataOutputStream(baos);
		for (int i=0; i<playlistIds.length; i++) {
			dataOutput.writeInt(playlistIds[i]);
		}
		byte[] buffer = baos.toByteArray();
		ngsend(NGWINAMP_REQ_PLDELFILES, playlistIds.length, 0, 0.0, 4 * playlistIds.length, buffer);		
	}

	// Swap two playlist item (input is the two indexes to swap)
	public void sendSwap(int playlistId1, int playlistId2) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LEDataOutputStream dataOutput = new LEDataOutputStream(baos);
		dataOutput.writeInt(playlistId1);
		dataOutput.writeInt(playlistId2);
		byte[] buffer = baos.toByteArray();
		ngsend(NGWINAMP_REQ_PLMOVEFILES, 1, 0, 0.0, 2 * 4, buffer);		
	}
	
	// Retrieve current song index and playlist length (return an array with 'index'  -> current song index,
	//																		 'length' -> playlist length)
	public static class PlaylistPosition {
		long index;
	    long length;
	    public PlaylistPosition(long index, long length) {
	    	this.index = index;
	    	this.length = length;
	    }
	    /** Get current song index */
	    public long getIndex() { return index; }
	    /** Get playlist length */
	    public long getLength() { return length; }
	}
	
	// Retrieve current song postion and length. (return an array with 'pos'      -> position in percent,
	// 																   'posms'    -> position in milliseconds,
	//																   'lengthms' -> length in milliseconds)
	public PlaylistPosition sendGetPlaylistPosition() throws IOException {
		ngsend(NGWINAMP_REQ_PLGETPOS);
		waitForMessage(NGWINAMP_ANS_PLPOS);
		return new PlaylistPosition(curparam1, curparam2);
	}
	
	// Set current song (input is a zero based index in playlist)
	public void sendSetPlaylistPosition(long playlistId) throws IOException {
		ngsend(NGWINAMP_REQ_PLSETPOS, playlistId, 0, 0.0, 0, null);
	}

	// Retrieve current shuffle mode (return is a boolean value : enabled or disabled)
	public boolean sendGetShuffle() throws IOException {
		ngsend(NGWINAMP_REQ_PLGETSHUFFLE);
		waitForMessage(NGWINAMP_ANS_PLSHUFFLE);
		return curparam1 != NGWINAMP_NONE;
	}

	// Change current shuffle mode (input is a boolean value : enable or disable)
	public void sendSetShuffle(boolean enable) throws IOException {
		if (enable) {
			ngsend(NGWINAMP_REQ_PLSETSHUFFLE, NGWINAMP_ALL, 0, 0.0, 0, null);
		} else {
			ngsend(NGWINAMP_REQ_PLSETSHUFFLE, NGWINAMP_NONE, 0, 0.0, 0, null);
		}
	}
	
	// Retrieve current repeat mode (return is a boolean value : enabled or disabled)
	public boolean sendGetRepeat() throws IOException {
		ngsend(NGWINAMP_REQ_PLGETREPEAT);
		waitForMessage(NGWINAMP_ANS_PLREPEAT);
		return curparam1 != NGWINAMP_NONE;
	}

	// Change current repeat mode (input is a boolean value : enable or disable)
	public void sendSetRepeat(boolean enable) throws IOException {
		if (enable) {
			ngsend(NGWINAMP_REQ_PLSETREPEAT, NGWINAMP_ALL, 0, 0.0, 0, null);
		} else {
			ngsend(NGWINAMP_REQ_PLSETREPEAT, NGWINAMP_NONE, 0, 0.0, 0, null);
		}
	}
	
	// Randomize playlist
	public void sendSetRandomize() throws IOException {
		ngsend(NGWINAMP_REQ_PLRANDOMIZE);
	}
	// Sort playlist by path and filename
	public void sendSortByPath() throws IOException {
		ngsend(NGWINAMP_REQ_PLSORTBYPATH);
	}
	// Sort playlist by title
	public void sendSetSortByName() throws IOException {
		ngsend(NGWINAMP_REQ_PLSORTBYNAME);
	}

	// Remove dead songs from playlist
	public void sendRemoveDead() throws IOException {
		ngsend(NGWINAMP_REQ_PLDELDEADFILES);
	}

	// Retrieve authorized file roots (return an array of string)
	public String[] sendGetRoots() throws IOException {
		ngsend(NGWINAMP_REQ_BWGETROOTS);
		waitForMessage(NGWINAMP_ANS_BWROOTS);
		ByteArrayInputStream bais = new ByteArrayInputStream(curdata);
		LEDataInputStream dataInput = new LEDataInputStream(bais);
		String[] names = new String[(int) curparam1]; 
		for (int i=0; i<curparam1; i++) {
			short length = dataInput.readShort();
			byte[] name = new byte[length];
			int bytesRead = dataInput.read(name, 0, name.length);
			names[i] = new String(name, "ASCII");
		}
		return names;
	}

	
	public static class ListResult {
		String[] directories;
		String[] files;
		
		public ListResult(String[] directories, String[] files) {
			this.directories = directories;
			this.files = files;
		}
		public String[] getDirectories() { return directories; }
		public String[] getFiles() { return files; }
	}
	
	//  Retrieve content list from a path (return an array with 'directories' -> an array of string,
	//														   'files' -> an array of string)
	public ListResult sendGetContentList(String path) throws IOException {
		ngsend(NGWINAMP_REQ_BWGETLIST, 0, 0, 0.0, path.length(), path.getBytes());
		waitForMessage(NGWINAMP_ANS_BWLIST);
		ByteArrayInputStream bais = new ByteArrayInputStream(curdata);
		LEDataInputStream dataInput = new LEDataInputStream(bais);
		String[] directories = new String[(int) curparam1]; 
		String[] files = new String[(int) curparam2];
		for (int i=0; i<curparam1; i++) {
			short length = dataInput.readShort();
			byte[] name = new byte[length];
			int bytesRead = dataInput.read(name, 0, name.length);
			directories[i] = new String(name, "ASCII");
		}
		for (int i=0; i<curparam2; i++) {
			short length = dataInput.readShort();
			byte[] name = new byte[length];
			int bytesRead = dataInput.read(name, 0, name.length);
			files[i] = new String(name, "ASCII");
		}
		return new ListResult(directories, files);
	}


	
	private void waitForMessage(long code) throws IOException {
		do {
			ngrecv();
		} while (curcode != code);
	}
	
	/** Receive answer from NGWinamp server (internal usage) 
	 * @throws IOException */
	private void ngrecv() throws IOException {
		
		// read header
		logger.debug("receiving...");
		//LEDataInputStream dataInput = inputStream;
		
		//logger.debug("presleep");
		//try { Thread.sleep(1000); } catch (InterruptedException ie) { } ;
		//logger.debug("postsleep");
		
		// buffer size should be size + 32

		// allow one retry
		try {
			curcode = inputStream.readInt();   // 4 bytes. Why doesn't this block ??
		} catch (java.net.SocketException se) {
			// java.net.SocketException: Software caused connection abort: recv failed
			logger.info("Socket exception reading NGwinamp data - retrying");
			this.disconnect();
			this.connect();
			this.authenticate(retryPassword);
			ngresendLastMessage();
			curcode = inputStream.readInt();
			// throw new IOException("Socket exception occurred on NGwinamp read; reconnect attempted");
		} catch (EOFException eof) {
			logger.info("EOF exception reading NGwinamp data - reconnecting");
			this.disconnect();
			this.connect();
			this.authenticate(retryPassword);
			ngresendLastMessage();
			curcode = inputStream.readInt();
			// throw new IOException("EOF exception occurred on NGwinamp read; reconnect attempted");
		}

		curparam1 = inputStream.readInt(); // 4 bytes
		curparam2 = inputStream.readInt(); // 4 bytes
		long zero = inputStream.readInt(); // 4 ignored bytes
		curparam3 = inputStream.readDouble(); // 8 bytes
		long size = inputStream.readInt(); // 4 bytes
		zero = inputStream.readInt(); // another 4 ignored bytes
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i=0; i<size; i++) { baos.write(inputStream.readByte()); } // @TODO handle -1 (EOF)
		curdata = baos.toByteArray();
		
		logger.debug("recv msg type " + curcode + ", param1=" + curparam1 + ", param2=" + curparam2 + ", param3=" + curparam3 + ", data size=" + size);
	}
	
	private void ngsend(long code) throws IOException {
		ngsend(code, 0, 0, 0, 0, null);
	}
	
	/** Send request to NGWinamp server (internal usage) 
	 * @throws IOException */
	private void ngsend(long code, long param1, long param2, double param3, int size, byte[] data) throws IOException {
		if (data==null && size!=0) { throw new IllegalArgumentException("data cannot be null if size > 0"); }
		if (data!=null && size!=data.length) { throw new IllegalArgumentException("data length (" + data.length + ") must be equal to size (" + size + ")"); }
		
		// send header + data
		//ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		//oos.write
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		LEDataOutputStream dataOutput = new LEDataOutputStream(baos);
		// buffer size should be size + 32
		dataOutput.writeInt((int) code);  // 'dwords'
		dataOutput.writeInt((int) param1);
		dataOutput.writeInt((int) param2); 
		dataOutput.writeInt(0);
		dataOutput.writeDouble(param3);
		dataOutput.writeInt(size);
		dataOutput.writeInt(0);
		if (data!=null) {
			dataOutput.write(data);
		}
		
		// this is written in 32 byte chunks, it appears
		outputBuffer = baos.toByteArray();
		if (outputBuffer.length != size + 32) {
			throw new IllegalStateException("buffer length (" + outputBuffer.length + ") should be header size (32) + data length (" + data.length + ")"); 
		}
		logger.debug("Sending msg type " + code + ", data size=" + size + ", buffer size: " + outputBuffer.length);
		try {
			outputStream.write(outputBuffer);
		} catch (java.net.SocketException se) {
			// allow one retry
			// java.net.SocketException: Software caused connection abort: socket write error
			logger.info("Socket exception writing NGwinamp data - retrying");
			this.disconnect();
			this.connect();
			this.authenticate(retryPassword);
			outputStream.write(outputBuffer);
		}
		outputStream.flush();
		try { Thread.sleep(100); } catch (InterruptedException ie) { }
	}

	/** Send request to NGWinamp server (internal usage) 
	 * @throws IOException */
	private void ngresendLastMessage() throws IOException {
		logger.debug("Resending last msg, buffer size: " + outputBuffer.length);
		try { Thread.sleep(100); } catch (InterruptedException ie) { }
		outputStream.write(outputBuffer);
		outputStream.flush();
		try { Thread.sleep(100); } catch (InterruptedException ie) { }
	}
	
	
	private boolean ngcheck() {
		return ngcheck(0);
	}
	
	/** Check if the connection is still alive */
	private boolean ngcheck(int minsize) {
		// TODO: if #unred bytes < minsize, return false
		// TODO: if socket timed out, return false (handled by exceptions I would imagine)
		// TODO: if eof, return false
		return true;
		// if (inputStream.available()
		
	}
	
	/** Clear answer storage (internal usage) */
	private void ngclear() {
		curcode = 0;
		curparam1 = 0;
		curparam2 = 0;
		curparam3 = 0;
		cursize = 0;
		curdata = null;
	}
	
	
}
