package com.pentaho.install.post;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.Logger;
import com.pentaho.install.input.BooleanInput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class SqlScriptRunner {
	public static boolean DRYRUN = false;
	
	public static String POSTGRESQL_CONNECT_COMMAND_L = "\\connect";
	public static String POSTGRESQL_CONNECT_COMMAND_S = "\\c";
	public static String ORACLE_CONNECT_COMMAND = "conn";
	
	Scanner scanner;
	File dir;
	Map<String, DBInstance> dbInstanceMap;
	
	private boolean enableMySQLRemoteAccess = true;
	
	public SqlScriptRunner(Scanner scanner, File dir, Map<String, DBInstance> dbInstanceMap) {
		this.scanner = scanner;
		this.dir = dir;
		this.dbInstanceMap = dbInstanceMap;
	}
	
	public void execute() {
		//Get first instance
		DBInstance instance = dbInstanceMap.get(dbInstanceMap.keySet().iterator().next());
		
		Properties connectionProps = new Properties();
	    connectionProps.put("user", instance.getAdminUser());
	    connectionProps.put("password", instance.getAdminPassword());
	    
		Connection conn = null;
		Statement stmt = null;
		try {
			System.out.print("Connecting to " + instance.getHost() + "@" + instance.getPort() + " ..... ");
			
			String url = InstallUtil.getJdbcUrl(instance, true);
			Logger.log(url);
			
			if (!DRYRUN) {
				conn = DriverManager.getConnection(url, connectionProps);
				InstallUtil.output("\t[connected]\n");
			}
			
			if (!DRYRUN) {
				stmt = conn.createStatement();
			}
			runScript(stmt);
		} catch (SQLException sqle) {
			String message = sqle.getMessage();
			if (PostInstaller.DEBUG) {
				System.out.println(message);
			}
			
			if (message.indexOf("No suitable driver") >= 0) {
				System.out.println("Installer could not find the JDBC driver.");
			} else {
				System.out.println("Could not connect to database.");
			}
			return;
		} finally {
			close(conn);
			close(stmt);
		}
	}
	
	private boolean isConnect(String sql, DB type) {
		return DB.Oracle.equals(type) && sql.startsWith(ORACLE_CONNECT_COMMAND) 
				|| DB.PostgreSQL.equals(type) && sql.startsWith(POSTGRESQL_CONNECT_COMMAND_L);
	}
	
	private boolean canBeIgnored(String sql, DB type) {
		return DB.Oracle.equals(type) && sql.startsWith("set") 
				|| DB.MSSQLServer.equals(type) && sql.equalsIgnoreCase("go");
	}
	
	public boolean isCompletedSql(String sql, DB type) {
		return sql.endsWith(";") || 
				DB.MSSQLServer.equals(type) && (sql.startsWith("create database") || sql.startsWith("drop database"));
	}
	
	private void runScript(Statement stmt) {
		for (String dbName : dbInstanceMap.keySet()) {
			DBInstance instance = dbInstanceMap.get(dbName);
			String dbFileName = instance.getDbFileName();
			
			InstallUtil.output("Creating database " + instance.getName() + ", " + instance.getDefaultUsername() + ", " + instance.getUsername());
			InstallUtil.output("Script file: " + dbFileName);
			InstallUtil.output("\n--------------------\n");
			
			File sqlFile = new File(dir, dbFileName);
			List<String> sqlList = new ArrayList<String>();
			
			try (BufferedReader in = new BufferedReader(new FileReader(sqlFile))) {
				String line;
				StringBuilder buf = new StringBuilder();
				boolean commentStarted = false;
				while ((line = in.readLine()) != null) {
					line = line.trim();
					if (commentStarted && line.contains("*/")) {
						//TODO there might be statement after comment close '*/'
						commentStarted = false;
						continue;
					}
					
					if (line.length() == 0 || line.startsWith("--") || line.startsWith("#")) {
						continue;
					} else if (line.startsWith("/*")) {
						commentStarted = true;
						continue;
					}

					String lineLower = line.toLowerCase();
					if (isConnect(lineLower, instance.getType())) {
						//native psql/sqlplus command does not end with ;
						buf.append(line);
					} else if (canBeIgnored(lineLower, instance.getType())) {
						//anything before this line should be added
					} else {
						buf.append(line);
						if (!isCompletedSql(line.toLowerCase(), instance.getType())) {
							buf.append("\n");
							continue;
						}
					}
					
					if (buf.length() != 0) {
						String sql = polish(buf.toString(), instance);
						sqlList.add(sql);
						buf.delete(0, buf.length());
					}
				}
				
				Connection newConnection = null;
				Statement newStatement = null;
				try {
					//Run all the SQL statements
					for (String sql : sqlList) {
						Logger.log("\tSQL-->" + sql);
						if (DRYRUN && !Logger.isDebug()) {
							System.out.println("DRYRUN: " + sql);
						}
						
						try {
							//psql/sqlplus need special handling, 
							if (newStatement != null) {
								//new connection to psql/oracle was established
								if (!DRYRUN) {
									newStatement.execute(sql);
								}
							} else {
								String sqlLower = sql.toLowerCase();
								if (isConnect(sqlLower, instance.getType())) {
									//a native psql/sqlplus client command.
									Logger.log("A psql/sqlplus native command encountered, create separate JDBC connection");

									if (newConnection == null) {
										//Initialize connection to new created database
										Properties connectionProps = new Properties();
									    connectionProps.put("user", instance.getUsername());
									    connectionProps.put("password", instance.getPassword());
									    
									    try {
									    	String url = InstallUtil.getJdbcUrl(instance, false);
									    	Logger.log("Connecting to " + url + ", " + instance.getUsername());

									    	if (!DRYRUN) {
									    		newConnection = DriverManager.getConnection(url, connectionProps);
										    	newStatement = newConnection.createStatement();
										    	
										    	Logger.log("\tconnected");
									    	}
									    } catch (SQLException sqle) {
									    	System.out.println("Failed to connect to " + instance.getName() + " with " + instance.getUsername() + " [" + instance.getPassword() + "]");
									    	
									    	Logger.log(sqle.toString());
									    }
									}
								} else {
									if (!DRYRUN) {
										stmt.execute(sql);
									}
								}	
							}
						} catch (SQLException sqle) {
							System.out.println("\n\n==================================================");
							System.out.println(sql);
							System.out.println("==================================================");
							System.out.println(sqle.getMessage());
							System.out.println("==================================================");							
							System.out.println("Could not execute above line(s)");

							if (!PostInstaller.SILENT) {
								BooleanInput askForContinue = new BooleanInput("There is error happened during execution of sql statement, do you want to continue (y/n)? ");
								InstallUtil.ask(scanner, askForContinue);
								if (!askForContinue.yes()) {
									break;
								}
							}
						}
					}
				} finally {
					close(newConnection);
					close(newStatement);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			InstallUtil.output("\t[created]");
		}
	}
	
	//TODO ask if enable remote access for mysql
	private String polish(String sql, DBInstance instance) {
		if (PostInstaller.DEBUG) {

		}
		if (DB.MySQL.equals(instance.getType())) {
			if (instance.isCustomed()) {
				sql = sql.replace(instance.getDefaultName(), instance.getName())
						.replace(instance.getDefaultUsername(), instance.getUsername())
						.replace(instance.getDefaultPassword(), instance.getPassword());
			}
			
			sql = sql.replace("'localhost'", "'" + mySQLHostToGrant() + "'");
		} else if (DB.PostgreSQL.equals(instance.getType())) {
			if (instance.isCustomed()) {
				sql = sql.replace(instance.getDefaultName(), instance.getName())
						.replace(instance.getDefaultUsername(), instance.getUsername())
						.replace(instance.getDefaultPassword(), instance.getPassword());
			}
		} else if (DB.MSSQLServer.equals(instance.getType())) {
			if (instance.isCustomed()) {
				sql = sql.replace(instance.getDefaultName(), instance.getName())
						.replace(instance.getDefaultUsername(), instance.getUsername())
						.replace(instance.getDefaultPassword(), instance.getPassword());
			}
		} else if (DB.Oracle.equals(instance.getType())) {
			if (instance.isCustomed()) {
				//Oracle does not need to change db name
				sql = sql
						.replace(instance.getDefaultUsername(), instance.getUsername())
						.replace(instance.getDefaultPassword(), instance.getPassword());
			}
			if (sql.endsWith(";")) {
				sql = sql.substring(0, sql.length()-1);
			}
		}
		
		return sql;
	}
	
	private String mySQLHostToGrant() {
		return enableMySQLRemoteAccess ? "%" : "localhost";
	}
	
	private void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void close(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		/*
		Scanner scanner = new Scanner(System.in);
		File dir = new File("C:\\PENTAHO61\\server\\biserver-ee\\data\\postgresql");
		
		DBInstance hibernate = new DBInstance(DB_NAME_HIBERNATE, "hibuser", "pentaho", "postgres", "postgres", PostgreSQL, true);
		hibernate.setName("win_" + DB_NAME_HIBERNATE);
		hibernate.setUsername("win_" + "hibuser");
		hibernate.setPassword("pentaho");
		hibernate.setDbFileName("create_repository_postgresql.sql");
		
		DBInstance jackrabbit = new DBInstance(DB_NAME_JACKRABBIT, "jcr_user", "pentaho", "postgres", "postgres", PostgreSQL, true);
		jackrabbit.setName("win_" + DB_NAME_JACKRABBIT);
		jackrabbit.setUsername("win_" + "jcr_user");
		jackrabbit.setPassword("pentaho");
		jackrabbit.setDbFileName("create_jcr_postgresql.sql");
		
		DBInstance quartz = new DBInstance(DB_NAME_QUARTZ, "pentaho_user", "pentaho", "postgres", "postgres", PostgreSQL, true); 
		quartz.setName("win_" + DB_NAME_QUARTZ);
		quartz.setUsername("win_" + "pentaho_user");
		quartz.setPassword("pentaho");
		quartz.setDbFileName("create_quartz_postgresql.sql");
		
		Map<String, DBInstance> dbInstanceMap = new LinkedHashMap<>();
		dbInstanceMap.put(DB_NAME_HIBERNATE, hibernate);
		dbInstanceMap.put(DB_NAME_JACKRABBIT, jackrabbit);
		dbInstanceMap.put(DB_NAME_QUARTZ, quartz);
		
		BAPostInstaller.DEBUG = true;
		
		SqlScriptRunner ssr = new SqlScriptRunner(scanner, dir, dbInstanceMap);
		ssr.execute();
		*/
	}
}
