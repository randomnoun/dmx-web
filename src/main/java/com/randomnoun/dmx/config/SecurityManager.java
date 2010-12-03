package com.randomnoun.dmx.config;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.AccessControlContext;
import java.security.Permission;

import org.apache.log4j.Logger;

/** A security manager that restricts the operations allowed by
 * ShowThreads. 
 *
 * XXX: Don't throw additional SecurityExceptions in here; there's only
 * one SecurityManager per VM (set by System.setSecurityManager). 
 * You probably want to look at ProtectionDomains/PolicyManagers instead.
 * 
 * TODO: what about code executed in fixture definitions/controllers via the 
 * controller actions ?
 * 
 * <p>Will defer to a parent security manager if the rules in this class
 * would otherwise permit an action. 
 * 
 * <p>If SecurityManager was an interface, would probably java.lang.Proxy this,
 * but don't feel comfortable cglib'ing an AOP wrapper around a SecurityManager.
 * 
 * @author knoxg
 */
@SuppressWarnings("deprecation")
public class SecurityManager extends java.lang.SecurityManager {

	//Logger logger = Logger.getLogger(SecurityManager.class);

	java.lang.SecurityManager parentManager;
	
	public SecurityManager(java.lang.SecurityManager parentManager) {
		if (parentManager==null) {
			debug("Creating new SecurityManager"); 
		} else {
			debug("Creating new SecurityManager around " + parentManager.getClass().getName() + ": " + parentManager); 
		}
		this.parentManager = parentManager;
	}

	// not using log4j due to circular dependencies logging security 
	// checks within (and whilst loading) log4j classes
	private void debug(String s) {
		String threadId = Thread.currentThread().getName();
		if (!threadId.startsWith("ContainerBackgroundProcessor")) {
			System.out.println("[dmx-web] [" + Thread.currentThread().getName() + "] SecurityManager " + s);
		}
	}
	
	
	// ////////// non-check methods - will just defer to parentManager
	
	@Override
	public boolean getInCheck() {
		// NB: may need to do some more processing here
		if (parentManager!=null) { parentManager.getInCheck(); }
		return false;
	}

	/* subclass protected
	@Override
	protected Class[] getClassContext() {
		return if (parentManager!=null) { parentManager.getClassContext(); }
	}

	@Override
	protected ClassLoader currentClassLoader() {
		return if (parentManager!=null) { parentManager.currentClassLoader(); }
	}

	@Override
	protected Class<?> currentLoadedClass() {
		return if (parentManager!=null) { parentManager.currentLoadedClass(); }
	}

	@Override
	protected int classDepth(String name) {
		return if (parentManager!=null) { parentManager.classDepth(name); }
	}

	@Override
	protected int classLoaderDepth() {
		return if (parentManager!=null) { parentManager.classLoaderDepth(); }
	}

	@Override
	protected boolean inClass(String name) {
		return if (parentManager!=null) { parentManager.inClass(name); }
	}

	@Override
	protected boolean inClassLoader() {
		return if (parentManager!=null) { parentManager.inClassLoader(); }
	}

	@Override
	public Object getSecurityContext() {
		return if (parentManager!=null) { parentManager.getSecurityContext(); }
	}
	*/
	
	@Override
	public ThreadGroup getThreadGroup() {
		if (parentManager!=null) { parentManager.getThreadGroup(); }
		return Thread.currentThread().getThreadGroup();
		
	}
	

	// ////////// check methods
	
	
	@Override
	public void checkPermission(Permission perm) {
		// http threads do a *lot* of these
		if (!perm.getName().equals("setContextClassLoader")) {
			debug("checkPermission([name='" + perm.getName() + "',actions='" + perm.getActions() + "']");
		}
		if (parentManager!=null) { parentManager.checkPermission(perm); }
	}

	@Override
	public void checkPermission(Permission perm, Object context) {
		debug("checkPermission([name='" + perm.getName() + "',actions='" + perm.getActions() + "]," +
			context.getClass().getName() + ")");
		if (parentManager!=null) { parentManager.checkPermission(perm, context); }
	}

	@Override
	public void checkCreateClassLoader() {
		debug("checkCreateClassLoader()");
		if (parentManager!=null) { parentManager.checkCreateClassLoader(); }
	}

	@Override
	public void checkAccess(Thread t) {
		debug("checkAccess(Thread " + t.getId() + ": '" + t.getName() + "'");
		if (parentManager!=null) { parentManager.checkAccess(t); }
	}

	@Override
	public void checkAccess(ThreadGroup g) {
		debug("checkAccess(ThreadGroup '" + g.getName() + "'");
		if (parentManager!=null) { parentManager.checkAccess(g); }
	}

	@Override
	public void checkExit(int status) {
		//throw new SecurityException("exit not permitted");
		debug("checkExit(" + status + ")");
		if (parentManager!=null) { parentManager.checkExit(status); }
	}

	@Override
	public void checkExec(String cmd) {
		//throw new SecurityException("exec not permitted");
		debug("checkExec('" + cmd + "')");
		if (parentManager!=null) { parentManager.checkExec(cmd); }
	}

	@Override
	public void checkLink(String lib) {
		//throw new SecurityException("link not permitted");
		debug("checkLink('" + lib + "')");
		if (parentManager!=null) { parentManager.checkLink(lib); }
	}

	@Override
	public void checkRead(FileDescriptor fd) {
		debug("checkRead(FileDescriptor " + fd.toString() + ")");
		if (parentManager!=null) { parentManager.checkRead(fd); }
	}

	@Override
	public void checkRead(String file) {
		debug("checkRead('" + file + "')");
		if (parentManager!=null) { parentManager.checkRead(file); }
	}

	@Override
	public void checkRead(String file, Object context) {
		debug("checkRead('" + file + "', " + context.getClass().getName() + ")");
		if (parentManager!=null) { parentManager.checkRead(file, context); }
	}

	@Override
	public void checkWrite(FileDescriptor fd) {
		//throw new SecurityException("write not permitted");
		debug("checkWrite(FileDescriptor " + fd.toString() + ")");
		if (parentManager!=null) { parentManager.checkWrite(fd); }
	}

	@Override
	public void checkWrite(String file) {
		//throw new SecurityException("write not permitted");
		debug("checkWrite('" + file + "')");
		if (parentManager!=null) { parentManager.checkWrite(file); }
	}

	@Override
	public void checkDelete(String file) {
		//throw new SecurityException("delete not permitted");
		debug("checkDelete('" + file + "')");
		if (parentManager!=null) { parentManager.checkDelete(file); }
	}

	@Override
	public void checkConnect(String host, int port) {
		//throw new SecurityException("connect not permitted");
		debug("checkConnect('" + host + "', " + port + ")");
		if (parentManager!=null) { parentManager.checkConnect(host, port); }
	}

	@Override
	public void checkConnect(String host, int port, Object context) {
		//throw new SecurityException("connect not permitted");
		debug("checkConnect('" + host + "', " + port + ", " + context.getClass().getName() + ")");
		if (parentManager!=null) { parentManager.checkConnect(host, port, context); }
	}

	@Override
	public void checkListen(int port) {
		//throw new SecurityException("listen not permitted");
		debug("checkListen(" + port + ")");
		if (parentManager!=null) { parentManager.checkListen(port); }
	}

	@Override
	public void checkAccept(String host, int port) {
		//throw new SecurityException("accept not permitted");
		debug("checkAccept('" + host + "', " + port + ")");
		if (parentManager!=null) { parentManager.checkAccept(host, port); }
	}

	@Override
	public void checkMulticast(InetAddress maddr) {
		//throw new SecurityException("multicast not permitted");
		debug("checkMulticast(InetAddress " + maddr.toString() + ")");
		if (parentManager!=null) { parentManager.checkMulticast(maddr); }
	}

	@Override
	public void checkMulticast(InetAddress maddr, byte ttl) {
		//throw new SecurityException("multicast not permitted");
		debug("checkMulticast(InetAddress " + maddr.toString() + ", " + ttl + ")");
		if (parentManager!=null) { parentManager.checkMulticast(maddr, ttl); }
	}

	@Override
	public void checkPropertiesAccess() {
		//throw new SecurityException("propertiesAccess not permitted");
		debug("checkPropertiesAccess()");
		if (parentManager!=null) { parentManager.checkPropertiesAccess(); }
	}

	@Override
	public void checkPropertyAccess(String key) {
		//throw new SecurityException("propertyAccess not permitted");
		debug("checkPropertyAccess('" + key + "')");
		if (parentManager!=null) { parentManager.checkPropertyAccess(key); }
	}

	@Override
	public boolean checkTopLevelWindow(Object window) {
		debug("checkTopLevelWindow(" + window.getClass().getName() + ")");
		if (parentManager!=null) { return parentManager.checkTopLevelWindow(window); }
		return true;
	}

	@Override
	public void checkPrintJobAccess() {
		debug("checkPrintJobAccess()");
		if (parentManager!=null) { parentManager.checkPrintJobAccess(); }
	}

	@Override
	public void checkSystemClipboardAccess() {
		debug("checkSystemClipboardAccess()");
		if (parentManager!=null) { parentManager.checkSystemClipboardAccess(); }
	}

	@Override
	public void checkAwtEventQueueAccess() {
		debug("checkAwtEventQueueAccess()");
		if (parentManager!=null) { parentManager.checkAwtEventQueueAccess(); }
	}

	@Override
	public void checkPackageAccess(String pkg) {
		debug("checkPackageAccess('" + pkg + "')");
		if (parentManager!=null) { parentManager.checkPackageAccess(pkg); }
	}

	@Override
	public void checkPackageDefinition(String pkg) {
		debug("checkPackageDefinition('" + pkg + "')");
		if (parentManager!=null) { parentManager.checkPackageDefinition(pkg); }
	}

	@Override
	public void checkSetFactory() {
		debug("checkSetFactory()");
		if (parentManager!=null) { parentManager.checkSetFactory(); }
	}

	@Override
	public void checkMemberAccess(Class<?> clazz, int which) {
		debug("checkMemberAccess(" + clazz.getName() + ", " + which + ")");
		if (parentManager!=null) { parentManager.checkMemberAccess(clazz, which); }
	}

	@Override
	public void checkSecurityAccess(String target) {
		debug("checkSecurityAccess('" + target + "')");
		if (parentManager!=null) { parentManager.checkSecurityAccess(target); }
	}
	
}
