package com.pentaho.install;

public class PentahoServerParam {
	public enum SERVER {BA, DI, HYBRID}
	
	public static String BA_SERVER_DIR = "biserver-ee";
	public static String DI_SERVER_DIR = "data-integration-server";
	
	public static String getServerDirectoryName(SERVER serverType) throws Exception {
		if (serverType.equals(SERVER.BA)) {
			return BA_SERVER_DIR;
		} else if (serverType.equals(SERVER.DI)) {
			return DI_SERVER_DIR;
		} else {
			throw new Exception("Unknown Pentaho server type");
		}
	}
}
