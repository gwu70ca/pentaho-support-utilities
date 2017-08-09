package com.pentaho.install;

public class PentahoServerParam {
	public enum SERVER {BA, DI, HYBRID}

	private static String BA_SERVER_DIR = "biserver-ee";
	private static String DI_SERVER_DIR = "data-integration-server";
	private static String PENTAHO_SERVER_DIR = "pentaho-server";
	
	public static String getServerDirectoryName(SERVER serverType) throws Exception {
		if (serverType.equals(SERVER.BA)) {
			return BA_SERVER_DIR;
		} else if (serverType.equals(SERVER.DI)) {
			return DI_SERVER_DIR;
		} else if (serverType.equals(SERVER.HYBRID)) {
			return PENTAHO_SERVER_DIR;
		} else {
			throw new Exception("Unknown Pentaho server type");
		}
	}
}
