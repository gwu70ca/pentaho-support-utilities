package com.pentaho.install;

import com.pentaho.install.PentahoServerParam.SERVER;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DBParam {
	public enum DB {Mysql, Psql, Orcl, Sqlserver}

    public static String POSTGRESQL_CONNECT_COMMAND_L = "\\connect";
    public static String POSTGRESQL_CONNECT_COMMAND_S = "\\c";
    public static String ORACLE_CONNECT_COMMAND = "conn";

	public static String RESOURCE_NAME_HIBERNATE = "jdbc/Hibernate";
	public static String RESOURCE_NAME_AUDIT = "jdbc/Audit";
	public static String RESOURCE_NAME_QUARTZ = "jdbc/Quartz";
	public static String RESOURCE_NAME_PENTAHO_OPERATIONS_MART = "jdbc/pentaho_operations_mart";
	public static String RESOURCE_NAME_PDI_OPERATIONS_MART = "jdbc/PDI_Operations_Mart";

	public static String DB_NAME_HIBERNATE = "hibernate";
	public static String DB_NAME_JACKRABBIT = "jackrabbit";
	public static String DB_NAME_QUARTZ = "quartz";
	public static String DB_NAME_PENT_OP_MART = "pentaho_operations_mart";

    public static String DB_NAME_HIBERNATE_DI = "di_hibernate";
    public static String DB_NAME_JACKRABBIT_DI = "di_jackrabbit";
    public static String DB_NAME_QUARTZ_DI = "di_quartz";

	public static Map<DB, String> JDBC_PREFIX;
	public static Map<DB, String> DB_PORT;
	public static Map<DB, Integer> DB_NAME_LENGTH;
	public static Map<DB, Integer> DB_USERNAME_LENGTH;
	public static Map<DB, String> dbDefaultAdminMap;

    public static Map<DB, String> dbDirMap;

    static {
        dbDirMap = new HashMap<>();
        dbDirMap.put(DB.Mysql, "mysql5");
        dbDirMap.put(DB.Psql, "postgresql");
        dbDirMap.put(DB.Orcl, "oracle10g");
        dbDirMap.put(DB.Sqlserver, "sqlserver");

		JDBC_PREFIX = new HashMap<>();
		JDBC_PREFIX.put(DB.Mysql, "jdbc:mysql://");
		JDBC_PREFIX.put(DB.Psql, "jdbc:postgresql://");
		JDBC_PREFIX.put(DB.Orcl, "jdbc:oracle:thin:@");
		JDBC_PREFIX.put(DB.Sqlserver, "jdbc:sqlserver://");
		
		dbDefaultAdminMap = new HashMap<>();
		dbDefaultAdminMap.put(DB.Mysql, "root");
		dbDefaultAdminMap.put(DB.Psql, "postgres");
		dbDefaultAdminMap.put(DB.Orcl, "system");
		dbDefaultAdminMap.put(DB.Sqlserver, "sa");
		
		DB_PORT = new HashMap<>();
		DB_PORT.put(DB.Mysql, "3306");
		DB_PORT.put(DB.Psql, "5432");
		DB_PORT.put(DB.Orcl, "1521");
		DB_PORT.put(DB.Sqlserver, "1433");
		
		DB_NAME_LENGTH = new HashMap<>();
		DB_NAME_LENGTH.put(DB.Sqlserver, 128);
		DB_NAME_LENGTH.put(DB.Mysql, 64);
		DB_NAME_LENGTH.put(DB.Orcl, 8);
		DB_NAME_LENGTH.put(DB.Psql, 63);
		
		DB_USERNAME_LENGTH = new HashMap<>();
		DB_USERNAME_LENGTH.put(DB.Sqlserver, 128);
		DB_USERNAME_LENGTH.put(DB.Mysql, 16);
		DB_USERNAME_LENGTH.put(DB.Orcl, 30);
		DB_USERNAME_LENGTH.put(DB.Psql, 63);
	}

	public static Map<String, DBInstance> initDbInstances(SERVER serverType, DB dbType) {
		Map<String, DBInstance> dbInstanceMap = new LinkedHashMap<>();
        dbInstanceMap.put(DBParam.DB_NAME_HIBERNATE, new DBInstance(DBParam.DB_NAME_HIBERNATE, "hibuser", "password", dbType, DBParam.RESOURCE_NAME_HIBERNATE));
        dbInstanceMap.put(DBParam.DB_NAME_JACKRABBIT, new DBInstance(DBParam.DB_NAME_JACKRABBIT, "jcr_user", "password", dbType, DBParam.RESOURCE_NAME_AUDIT));
        dbInstanceMap.put(DBParam.DB_NAME_QUARTZ, new DBInstance(DBParam.DB_NAME_QUARTZ, "pentaho_user", "password", dbType, DBParam.RESOURCE_NAME_QUARTZ));
        dbInstanceMap.put(DBParam.DB_NAME_PENT_OP_MART, new DBInstance(DBParam.DB_NAME_PENT_OP_MART, "hibuser", "password", dbType, DBParam.RESOURCE_NAME_PENTAHO_OPERATIONS_MART));
		
		return dbInstanceMap;
	}
	
	public static String getValidationQuery(DB dbType) {
		return dbType == DB.Orcl ? "select 1 from dual" : "select 1";
	}
	
	protected String adminUser, adminPassword;
	protected DB type = DBParam.DB.Psql;
	protected String jdbcPrefix;
	protected String host = "localhost";
	protected String port = "";
	protected boolean winAuth;
	
	public DBParam() {
	}
	
	public DB getType() {
		return type;
	}

	public void setType(DB type) {
		this.type = type;

		initDbProperty();
	}

	protected void initDbProperty() {
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

	public boolean isWinAuth() {
		return winAuth;
	}

	public void setWinAuth(boolean winAuth) {
		this.winAuth = winAuth;
	}

	public String toString() {
		return "Type: " + this.type + ", host: " + this.host + ", port: " + this.port;
	}
}
