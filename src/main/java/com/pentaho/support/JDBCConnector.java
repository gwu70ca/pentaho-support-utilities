package com.pentaho.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallUtil;

public class JDBCConnector {
	static boolean WIN_AUTH = false;
	static boolean DEBUG = false;

	public static void main(String[] args) {
		String dbHost = null, dbPort = null, dbName = null, dbUser = null, dbPass = null, dbType = null;
		
		if (args.length > 0) {
			try {
				for (int i=0;i<args.length;i++) {
					if (args[i].equals("-H") || args[i].equals("--host")) {
						dbHost = args[++i];
					} else if (args[i].equals("-P") || args[i].equals("--port")) {
						dbPort = args[++i];
					} else if (args[i].equals("-N") || args[i].equals("--name")) {
						dbName = args[++i];						
					} else if (args[i].equals("-U") || args[i].equals("--user")) {
						dbUser = args[++i];
					} else if (args[i].equals("-W") || args[i].equals("--pass")) {
						dbPass = args[++i];						
					} else if (args[i].equals("-T") || args[i].equals("--type")) {
						dbType = args[++i];
					} else if (args[i].equals("-I") || args[i].equals("--windows")) {
						WIN_AUTH = true;						
					} else if (args[i].equals("-D") || args[i].equals("--debug")) {
						DEBUG = true;
					}
				}	
			} catch (Exception ex) {
				InstallUtil.output("Invalid CLI parameters");
				InstallUtil.exit();
			}
		}
		
		DBInstance dbInstance = new DBInstance(dbName, dbUser, dbPass);
		dbInstance.setHost(dbHost);
		dbInstance.setPort(dbPort);
		
		DBParam.DB databaseType;
		if (dbType == null || (databaseType=guessDbType(dbType)) == null) {
			System.out.println("Unknown database type: " + dbType);
			System.exit(0);
		} else {
			dbInstance.setType(databaseType);
		}
		
		if (dbPass == null && !WIN_AUTH) {
			System.out.println("Dude, we need password");
			System.exit(0);
		}

		testJdbcConnection(dbInstance);
	}
	
	private static DBParam.DB guessDbType(String typeString) {
		String str = typeString.toLowerCase(); 
		if (str.contains("mssql") || str.contains("sqlserver") || str.contains("mssqlserver") || str.contains("microsoft")) {
			return DBParam.DB.MSSQLServer;
		} else if (str.contains("postgre") || str.contains("psql")) {
			return DBParam.DB.PostgreSQL;
		} else if (str.contains("orcl") || str.contains("oracle")) {
			return DBParam.DB.Oracle;
		} else if (str.contains("mysql")) {
			return DBParam.DB.MySQL;
		}
		return DBParam.DB.valueOf(str);
	}

	public static void testJdbcConnection(DBInstance dbInstance) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String url = InstallUtil.getJdbcUrl(dbInstance, dbInstance.getName()==null);
			Properties connectionProps = new Properties();
			if (WIN_AUTH && DBParam.DB.MSSQLServer.equals(dbInstance.getType())) {
			    url += ";integratedSecurity=true";
			} else {
				connectionProps.put("user", dbInstance.getUsername());
			    connectionProps.put("password", dbInstance.getPassword());
			}
			System.out.println(url);
							
			System.out.print("Connecting to " + dbInstance.getHost() + "@" + dbInstance.getPort() + " ..... ");
			if (!WIN_AUTH) {
				conn = DriverManager.getConnection(url, connectionProps);
			} else {
				conn = DriverManager.getConnection(url);
			}
			System.out.println("\t[connected]\n");
			
			stmt = conn.createStatement();
			String validateSql = DBParam.getValidationQuery(dbInstance.getType());
			System.out.print("Running validation query: " + validateSql + " ..... ");
			rs = stmt.executeQuery(validateSql);

			boolean success = false;
			if (rs.next()) {
				success = (rs.getInt(1) != 0);
			}

			System.out.println(success ? "[successed]" : "[failed]");
		} catch (SQLException sqle) {
			System.err.println(sqle);
		} finally {
			if (conn != null) {
				try {conn.close();}catch (Exception ex) {}
			}
			if (stmt != null) {
				try {stmt.close();}catch (Exception ex) {}
			}
			if (rs != null) {
				try {rs.close();}catch (Exception ex) {}
			}
		}
	}
}
