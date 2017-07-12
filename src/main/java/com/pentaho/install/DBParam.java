package com.pentaho.install;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pentaho.install.PentahoServerParam.SERVER;

public class DBParam {
	public enum DB {MySQL, PostgreSQL, Oracle, MSSQLServer}
	
	public static String DB_NAME_HIBERNATE = "hibernate";
	public static String DB_NAME_JACKRABBIT = "jackrabbit";
	public static String DB_NAME_QUARTZ = "quartz";
	
	public static String DB_NAME_HIBERNATE_DI = "di_hibernate";
	public static String DB_NAME_JACKRABBIT_DI = "di_jackrabbit";
	public static String DB_NAME_QUARTZ_DI = "di_quartz";
	
	public static Map<DB, String> JDBC_PREFIX;
	public static Map<DB, String> DB_PORT;
	public static Map<DB, Integer> DB_NAME_LENGTH;
	public static Map<DB, Integer> DB_USERNAME_LENGTH;
	public static Map<DB, String> dbDefaultAdminMap;

	static {
		JDBC_PREFIX = new HashMap<DB, String>();
		JDBC_PREFIX.put(DB.MySQL, "jdbc:mysql://");
		JDBC_PREFIX.put(DB.PostgreSQL, "jdbc:postgresql://");
		JDBC_PREFIX.put(DB.Oracle, "jdbc:oracle:thin:@");
		JDBC_PREFIX.put(DB.MSSQLServer, "jdbc:sqlserver://");
		
		dbDefaultAdminMap = new HashMap<>();
		dbDefaultAdminMap.put(DB.MySQL, "root");
		dbDefaultAdminMap.put(DB.PostgreSQL, "postgres");
		dbDefaultAdminMap.put(DB.Oracle, "system");
		dbDefaultAdminMap.put(DB.MSSQLServer, "sa");
		
		DB_PORT = new HashMap<DB, String>();
		DB_PORT.put(DB.MySQL, "3306");
		DB_PORT.put(DB.PostgreSQL, "5432");
		DB_PORT.put(DB.Oracle, "1521");
		DB_PORT.put(DB.MSSQLServer, "1433");
		
		DB_NAME_LENGTH = new HashMap<DB, Integer>();
		DB_NAME_LENGTH.put(DB.MSSQLServer, 128);
		DB_NAME_LENGTH.put(DB.MySQL, 64);
		DB_NAME_LENGTH.put(DB.Oracle, 8);
		DB_NAME_LENGTH.put(DB.PostgreSQL, 63);
		
		DB_USERNAME_LENGTH = new HashMap<DB, Integer>();
		DB_USERNAME_LENGTH.put(DB.MSSQLServer, 128);
		DB_USERNAME_LENGTH.put(DB.MySQL, 16);
		DB_USERNAME_LENGTH.put(DB.Oracle, 30);
		DB_USERNAME_LENGTH.put(DB.PostgreSQL, 63);
	}

	public static Map<String, DBInstance> initDbInstances(SERVER serverType) {
		Map<String, DBInstance> dbInstanceMap = new LinkedHashMap<>();
		if (SERVER.BA.equals(serverType)) {
			dbInstanceMap.put(DBParam.DB_NAME_HIBERNATE, new DBInstance(DBParam.DB_NAME_HIBERNATE, "hibuser", "password"));
			dbInstanceMap.put(DBParam.DB_NAME_JACKRABBIT, new DBInstance(DBParam.DB_NAME_JACKRABBIT, "jcr_user", "password"));
			dbInstanceMap.put(DBParam.DB_NAME_QUARTZ, new DBInstance(DBParam.DB_NAME_QUARTZ, "pentaho_user", "password"));
		} else if (SERVER.DI.equals(serverType)) {
			dbInstanceMap.put(DBParam.DB_NAME_HIBERNATE_DI, new DBInstance(DBParam.DB_NAME_HIBERNATE_DI, "hibuser", "password"));
			dbInstanceMap.put(DBParam.DB_NAME_JACKRABBIT_DI, new DBInstance(DBParam.DB_NAME_JACKRABBIT_DI, "jcr_user", "password"));
			dbInstanceMap.put(DBParam.DB_NAME_QUARTZ_DI, new DBInstance(DBParam.DB_NAME_QUARTZ_DI, "pentaho_user", "password"));
		}
		
		return dbInstanceMap;
	}
	
	public static String getValidationQuery(DB dbType) {
		return dbType == DB.Oracle ? "select 1 from dual" : "select 1";
	}
	
	private String adminUser, adminPassword;
	private DB type;
	private String jdbcPrefix;
	private String host = "localhost";
	private String port = "";
	private boolean winAuth;
	
	public DBParam() {
	}
	
	public DB getType() {
		return type;
	}

	public void setType(DB type) {
		this.type = type;
		this.jdbcPrefix = JDBC_PREFIX.get(type);
		this.port = DB_PORT.get(type);
	}

	public String getJdbcPrefix() {
		return jdbcPrefix;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String toString() {
		return "Type: " + this.type + ", host: " + this.host + ", port: " + this.port;
	}

	public boolean isWinAuth() {
		return winAuth;
	}

	public void setWinAuth(boolean winAuth) {
		this.winAuth = winAuth;
	}
}
