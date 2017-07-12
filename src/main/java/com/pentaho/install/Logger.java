package com.pentaho.install;

import com.pentaho.install.post.PostInstaller;

public class Logger {
	public static void log(String message) {
		if (PostInstaller.DEBUG) {
			System.out.println(message);
		}
	}
	
	public static boolean isDebug() {
		return PostInstaller.DEBUG; 
	}
}
