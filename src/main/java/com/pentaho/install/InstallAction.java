package com.pentaho.install;

public abstract class InstallAction {
	protected String NEW_LINE = System.lineSeparator();
	
	protected String bar() {
		return "==================================================";
	}
	
	protected String shortBar() {
		return "\n--------------------\n";
	}
}
