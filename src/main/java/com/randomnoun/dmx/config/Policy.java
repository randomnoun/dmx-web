package com.randomnoun.dmx.config;

import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.PropertyPermission;

public class Policy extends java.security.Policy {

	//PermissionCollection allPermissions; // for my code
	PermissionCollection limitedPermissions; // for scripted code;
	URL beanshellCodeSource;
	java.security.Policy oldPolicy;
	
	public Policy(java.security.Policy oldPolicy) {
		//allPermissions = new Permissions();
		//allPermissions.add(new AllPermission());
		this.oldPolicy = oldPolicy;
		
		limitedPermissions = new Permissions();
		limitedPermissions.add(new RuntimePermission("accessDeclaredMembers"));
		limitedPermissions.add(new ReflectPermission("suppressAccessChecks"));
		limitedPermissions.add(new RuntimePermission("defineClassInPackage.java.lang")); // hmm
		limitedPermissions.add(new RuntimePermission("defineClassInPackage.java.io")); // hmm
		limitedPermissions.add(new RuntimePermission("defineClassInPackage.java.util")); // hmm
		limitedPermissions.add(new RuntimePermission("defineClassInPackage.java.net")); // hmm
		limitedPermissions.add(new RuntimePermission("defineClassInPackage.java.awt")); // hmm
		limitedPermissions.add(new RuntimePermission("defineClassInPackage.java.awt.event")); // hmm
		limitedPermissions.add(new RuntimePermission("createClassLoader")); // hmm
		limitedPermissions.add(new SocketPermission("beanshell", "connect,accept,resolve")); // hmm
		limitedPermissions.add(new PropertyPermission("debugClasses", "read")); // hmm
		
		
		try {
			beanshellCodeSource = new URL("http://beanshell");
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Cannot create beanshell CodeSource URL");
		}
	}
	
	@Override
	public PermissionCollection getPermissions(CodeSource codesource) {
		System.out.println("getPermissions(" + codesource.getLocation() + ")");
		if (codesource.getLocation().getHost().equals("beanshell")) {
			System.out.println("** returning limitedPermissions");
			return limitedPermissions;
		} else {
			return oldPolicy.getPermissions(codesource);
		}
	}
	
	@Override
	public PermissionCollection getPermissions(ProtectionDomain domain) {
		System.out.println("getPermissions(dom " + domain.getCodeSource().getLocation() + ")");
		if (domain.getCodeSource().getLocation().getHost().equals("beanshell")) {
			System.out.println("** returning limitedPermissions; domain contains:");
			System.out.println(domain.getPermissions());
			return limitedPermissions;
		} else {
			return oldPolicy.getPermissions(domain);
		}
	}
	
	@Override
	public boolean implies(ProtectionDomain domain, Permission permission) {
		if (domain.getCodeSource().getLocation().getHost().equals("beanshell")) {
			if (limitedPermissions.implies(permission)) {
				return true;
			}
			//System.out.println("Tentatively allowing permission " + permission.getClass().getName() + " [name='" + permission.getName() + 
			//		"',actions='" + permission.getActions() + "] to beanshell");
			//return true;
			if (permission instanceof SocketPermission) {
				return ((SocketPermission) permission).getName().equals("beanshell");
			}
			if (permission instanceof FilePermission) {
				FilePermission filePermission = (FilePermission) permission;
				AppConfig appConfig = AppConfig.getAppConfigNoInit();
				if (appConfig != null) {
					String audioDir = appConfig.getProperty("audioController.defaultPath");
					String log4jDir = appConfig.getProperty("log4j.logDirectory");
					if (filePermission.getName().startsWith(audioDir) || 
						filePermission.getName().startsWith(log4jDir)) {
						return true;
					}
				}
			}
			System.out.println("Denying permission " + permission.getClass().getName() + " [name='" + permission.getName() + 
				"',actions='" + permission.getActions() + "] to beanshell");
			return false;
		} else {
			return oldPolicy.implies(domain, permission);
		}
	}
	
	@Override
	public void refresh() {
		oldPolicy.refresh();
	}

}
