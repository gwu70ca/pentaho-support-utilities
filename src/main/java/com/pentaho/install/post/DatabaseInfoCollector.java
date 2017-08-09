package com.pentaho.install.post;

import com.pentaho.install.*;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.PentahoServerParam.SERVER;
import com.pentaho.install.input.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * Collect database parameters and create database if necessary
 * 
 * @author gwu
 *
 */
public class DatabaseInfoCollector extends InstallAction {
	static Map<DB, Map<String, String>> dbFileMap;
	
	private void init() {
		String hibernate = InstallUtil.getHibernateDatabaseName(serverType);
		String jackrabbit = InstallUtil.getJackrabbitDatabaseName(serverType);
		String quartz = InstallUtil.getQuartzDatabaseName(serverType);
		
		dbFileMap = new HashMap<>();
		Map<String, String> m = new HashMap<>();
		m.put(hibernate, "create_repository_mysql.sql");
		m.put(jackrabbit, "create_jcr_mysql.sql");
		m.put(quartz, "create_quartz_mysql.sql");
		dbFileMap.put(DB.MySQL, m);
		
		m = new HashMap<>();
		m.put(hibernate, "create_repository_postgresql.sql");
		m.put(jackrabbit, "create_jcr_postgresql.sql");
		m.put(quartz, "create_quartz_postgresql.sql");
		dbFileMap.put(DB.PostgreSQL, m);
		

		m = new HashMap<>();
		m.put(hibernate, "create_repository_ora.sql");
		m.put(jackrabbit, "create_jcr_ora.sql");
		m.put(quartz, "create_quartz_ora.sql");
		dbFileMap.put(DB.Oracle, m);
		
		m = new HashMap<>();
		m.put(hibernate, "create_repository_sqlServer.sql");
		m.put(jackrabbit, "create_jcr_sqlServer.sql");
		m.put(quartz, "create_quartz_sqlServer.sql");
		
		dbFileMap.put(DB.MSSQLServer, m);		
	}

	private DB dbType;
	private SERVER serverType;
	private Map<String, DBInstance> dbInstanceMap;
	private boolean manualCreateDb = true;
	
	//Read from CLI
	private Scanner scanner;
	//Read from property file
	private Properties installProp;
	
	public DatabaseInfoCollector(Properties installProp, Scanner scanner) {
		this.installProp = installProp;
		this.scanner = scanner;
	}
	
	public void setDbType(DB dbType) {
		this.dbType = dbType;
	}

	public void setServerType(SERVER serverType) {
		this.serverType = serverType;
		
		dbInstanceMap = DBParam.initDbInstances(serverType);
		init();
	}

	public ActionResult execute() {
		if (PostInstaller.SILENT) {
			readFromFile();
		} else {
			readFromCLI();			
		}
		
		return new DBActionResult(dbInstanceMap, manualCreateDb);
	}
	
	private void readFromFile() {
		DBParam dbParam = new DBParam();
		dbParam.setType(dbType);
		
		//Validate database host, port, password
		String dbHost = installProp.getProperty("db_host");
		if (!InstallUtil.isBlankOrNull(dbHost)) {
			dbParam.setHost(dbHost);
		}
		String dbPort = installProp.getProperty("db_port");
		if (!InstallUtil.isBlankOrNull(dbPort)) {
			try {
				Integer.parseInt(dbPort);
			} catch (Exception ex) {
				System.out.println("Invalid database port:" + dbPort);
				System.exit(0);	
			}
			dbParam.setPort(dbPort);
		}
		String dbAdminUser = installProp.getProperty("db_admin_user").trim();
		if (!InstallUtil.isBlankOrNull(dbAdminUser)) {
			dbParam.setAdminUser(dbAdminUser);
		}
		String dbAdminPassword = installProp.getProperty("db_admin_password").trim();
		if (!InstallUtil.isBlankOrNull(dbAdminPassword)) {
			dbParam.setAdminPassword(dbAdminPassword);
		}
		
		manualCreateDb = "false".equals(installProp.get("installer_create_db"));
		
		String dbNameStr = "db_instance_%s_name";
		String dbUserStr = "db_instance_%s_user";
		String dbPassStr = "db_instance_%s_password";
		
		Map<String, String> fileMap = dbFileMap.get(dbType);
		for (Map.Entry<String, DBInstance> entry : dbInstanceMap.entrySet()) {
			String dbName = entry.getKey();
			Logger.log("\nDatabase: " + dbName);
			
			DBInstance dbInstance = entry.getValue();
			Logger.log("\tinstance name: " + dbInstance.getName() + ", user: " + dbInstance.getUsername() + ", pass: " + dbInstance.getPassword());
			
			String dbFileName = fileMap.get(dbName);
			dbInstance.setDbFileName(dbFileName);
			
			String name = this.installProp.getProperty(String.format(dbNameStr, dbName));
			String user = this.installProp.getProperty(String.format(dbUserStr, dbName));
			String pass = this.installProp.getProperty(String.format(dbPassStr, dbName));
			Logger.log("\tpropery name: " + name + ", user: " + user + ", pass: " + pass);
			
			boolean dbSettingChanged = !(name.equals(dbInstance.getName()) &&
					user.equals(dbInstance.getUsername()) &&
					pass.equals(dbInstance.getPassword()));
			
			if (dbSettingChanged) {
				dbInstance.setName(name);
				dbInstance.setUsername(user);
				dbInstance.setPassword(pass);
				dbInstance.setCustomed(true);	
			}
			
			dbInstance.setType(dbParam.getType());
			dbInstance.setHost(dbParam.getHost());
			dbInstance.setPort(dbParam.getPort());
			dbInstance.setAdminUser(dbParam.getAdminUser());
			dbInstance.setAdminPassword(dbParam.getAdminPassword());
			
			Logger.log(dbInstance.toString());
		}
	}
	
	private String getInstanceDetail() {
		StringBuilder buf = new StringBuilder("\n\n");
		for (Map.Entry<String, DBInstance> entry : dbInstanceMap.entrySet()) {
			buf.append(entry.getValue().toString());
			buf.append(shortBar());
		}
		buf.append("\n\n");
		return buf.toString();
	}
	
	private void readFromCLI() {
		DBParam dbParam = new DBParam();
		dbParam.setType(dbType);
		
		//Ask for db host
		String defaultHost = dbParam.getHost();
		StringInput dbHostInput = new StringInput(String.format("Database hostname [%s]: ", defaultHost));
		dbHostInput.setDefaultValue(defaultHost);
		InstallUtil.ask(scanner, dbHostInput);
		dbParam.setHost(dbHostInput.getValue());
		
		//Ask for db port
		String defaultPort = DBParam.DB_PORT.get(dbType);
		IntegerInput dbPortInput = new IntegerInput("Database port [" + defaultPort + "]: ");
		dbPortInput.setDefaultValue(defaultPort);
		InstallUtil.ask(scanner, dbPortInput);
		dbParam.setPort(dbPortInput.getValue());
		
		InstallUtil.newLine();
		
		String dbNames = DBParam.DB_NAME_HIBERNATE + ", " + DBParam.DB_NAME_JACKRABBIT + ", " + DBParam.DB_NAME_QUARTZ;
		if (serverType.equals(SERVER.DI)) {
			dbNames = DBParam.DB_NAME_HIBERNATE_DI + ", " + DBParam.DB_NAME_JACKRABBIT_DI + ", " + DBParam.DB_NAME_QUARTZ_DI;
		}
		BooleanInput createDbInput = new BooleanInput("Pentaho server requires the following databases to operate: [" + dbNames + "]. Do you want installer to create them for you [y/n]? ");
		InstallUtil.ask(scanner, createDbInput);
		
		if (createDbInput.yes()) {
			try {
				String jdbcDriverClass = InstallUtil.getJdbcDriverClass(dbType);
				Logger.log("Looking for JDBC driver class: " + jdbcDriverClass);
				Class.forName(jdbcDriverClass);
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
				InstallUtil.error("Installer could not locate the JDBC driver");
				System.exit(0);
			}
			Logger.log("\t[ok]");
			
			String defaultAdminUser = DBParam.dbDefaultAdminMap.get(dbParam.getType());
			StringInput adminUserInput = new StringInput("Input database admin username [" + defaultAdminUser + "]: ");
			adminUserInput.setDefaultValue(defaultAdminUser);
			InstallUtil.ask(scanner, adminUserInput);
			dbParam.setAdminUser(adminUserInput.getValue());

			StringInput adminPasswordInput = new StringInput("Input database admin password: ");
			InstallUtil.ask(scanner, adminPasswordInput);
			dbParam.setAdminPassword(adminPasswordInput.getValue());
			
			InstallUtil.newLine();
			System.out.println("Installer is going to create database with admin username [" + dbParam.getAdminUser() + "]");
			InstallUtil.newLine();
			
			manualCreateDb = false;
		} else {
			System.out.println("Please manually create databases and come back to this installer to proceed.");
		}
		
		do {
			InstallUtil.newLine();
			//If the databases were created with customized name/username/password, here is the chance to tell installer
			BooleanInput customDbInput = new BooleanInput("Do you want to customize database name, username or password [y/n]? ");
			InstallUtil.ask(scanner, customDbInput);
			boolean customDb = customDbInput.yes(); 
			
			Map<String, String> fileMap = dbFileMap.get(dbType);
			for (Map.Entry<String, DBInstance> entry : dbInstanceMap.entrySet()) {
				String dbName = entry.getKey();
				Logger.log("\nDatabase: " + dbName);
				
				DBInstance dbInstance = entry.getValue();
				String dbFileName = fileMap.get(dbName);
				dbInstance.setDbFileName(dbFileName);
				
				dbInstance.setType(dbParam.getType());
				dbInstance.setHost(dbParam.getHost());
				dbInstance.setPort(dbParam.getPort());
				dbInstance.setAdminUser(dbParam.getAdminUser());
				dbInstance.setAdminPassword(dbParam.getAdminPassword());
				
				if (customDb) {
					InstallUtil.newLine();
					
					if (!DB.Oracle.equals(dbType)) {
						//Oracle does not need db name
						DBNameInput dbNameInput = new DBNameInput(String.format("Input database name [%s]: ", dbInstance.getName()), dbParam.getType());
						dbNameInput.setDefaultValue(dbInstance.getName());
						InstallUtil.ask(scanner, dbNameInput);
						
						dbName = dbNameInput.getValue();
					} else {
						if (DBParam.DB_NAME_QUARTZ.equals(dbInstance.getName())) {
							dbInstance.setDefaultUsername("quartz");
						} else if (DBParam.DB_NAME_QUARTZ_DI.equals(dbInstance.getName())) {
							dbInstance.setDefaultUsername("di_quartz");
						}
					}
					
					DBUsernameInput dbUsernameInput = new DBUsernameInput(String.format("Input username for [%s]: ", dbName), dbType);
					InstallUtil.ask(scanner, dbUsernameInput);
					
					StringInput dbPasswordInput = new StringInput(String.format("Input password for [%s]: ", dbName));
					InstallUtil.ask(scanner, dbPasswordInput);

					boolean dbSettingChanged = !(dbName.equals(dbInstance.getName()) &&
							dbUsernameInput.getValue().equals(dbInstance.getUsername()) &&
							dbPasswordInput.getValue().equals(dbInstance.getPassword()));
					
					if (dbSettingChanged) {
						dbInstance.setName(dbName);
						dbInstance.setUsername(dbUsernameInput.getValue());
						dbInstance.setPassword(dbPasswordInput.getValue());
						dbInstance.setCustomed(true);	
					}	
				}
				
				Logger.log("");
			}
			
			if (!manualCreateDb) {
				BooleanInput askForProceed = new BooleanInput("Installer is going to create these database(s):" + getInstanceDetail() + "Do you want to continue [y/n]? ");
				InstallUtil.ask(scanner, askForProceed);
				if (askForProceed.yes()) {
					break;
				}
			} else {
				InstallUtil.newLine();
				InstallUtil.output("Database configurations:");
				InstallUtil.output(getInstanceDetail());
				
				break;
			}
			
			//TODO reset parameters
		} while (true);
	}
}
