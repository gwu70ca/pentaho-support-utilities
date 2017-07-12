package com.pentaho.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.LDAPParam;
import com.pentaho.install.LDAPParam.LDAP;
import com.pentaho.install.input.DBNameInput;
import com.pentaho.install.input.IntegerInput;
import com.pentaho.install.input.SelectInput;
import com.pentaho.install.input.StringInput;

public class Connector {
	static boolean WIN_AUTH = false;
	static boolean DEBUG = false;
	static String EXIT = "0";
	
	Scanner scanner;
	
	protected String NEW_LINE = System.lineSeparator();
	
	String bar() {
		return "==============================";
	}
	String shortBar() {
		return "--------------------";
	}
	
	String exitMenuEntry() {
		return EXIT + ": Exit";
	}
	
	private String menu() {
		StringBuffer buf = new StringBuffer();
		buf.append(NEW_LINE).append(bar()).append(NEW_LINE);
		
		int index = 1;
		buf.append(index++ + ": Test JDBC Connection").append(NEW_LINE);
		buf.append(index++ + ": Test LDAP Connection").append(NEW_LINE);
		buf.append(exitMenuEntry()).append(NEW_LINE);
		
		buf.append(bar()).append(NEW_LINE);
		buf.append("What do you want: ");
		return buf.toString();
	}
	
	public void execute() {
		this.scanner = new Scanner(System.in);
		
		try {
			while (true) {
				InstallUtil.newLine();
				SelectInput rootInput = new SelectInput(menu(), new String[]{"0","1","2"});
				InstallUtil.ask(scanner, rootInput);
				
				String index = rootInput.getValue();
				if (EXIT.equals(index)) {
					break;
				}
				
				switch (index) {
					case "1": testJdbc();
					break;
					case "2": testLdap();
					break;
				}
			}
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			this.scanner.close();
		}
	}
	
	private String[] dbTypePrompt() {
		StringBuffer txt = new StringBuffer();
		StringBuffer opt = new StringBuffer();
		txt.append(NEW_LINE).append(bar()).append(NEW_LINE);
		
		int index = 1;
		for (DB db : DB.values()) {
			opt.append(index).append(",");
			txt.append(index++).append(": ").append(db).append(NEW_LINE);
		}
		
		txt.append(bar()).append(NEW_LINE);
		txt.append("Select the database type: ");
		return new String[]{txt.toString(), opt.substring(0, opt.length()-1)};
	}
	
	private void testJdbc() {
		InstallUtil.newLine();
		String[] prompt = dbTypePrompt();
		SelectInput dbTypeInput = new SelectInput(prompt[0], prompt[1].split(","));
		InstallUtil.ask(scanner, dbTypeInput);
		DB dbType = DB.values()[Integer.parseInt(dbTypeInput.getValue())-1];
		System.out.println(dbType);
		
		String defaultAdminUser = DBParam.dbDefaultAdminMap.get(dbType);
		DBInstance dbParam = new DBInstance("", defaultAdminUser, "");
		dbParam.setType(dbType);

		StringInput dbHostInput = new StringInput(String.format("Database hostname or IP address [%s]: ", dbParam.getHost()));
		dbHostInput.setDefaultValue(dbParam.getHost());
		InstallUtil.ask(scanner, dbHostInput);
		dbParam.setHost(dbHostInput.getValue());
		
		IntegerInput dbPortInput = new IntegerInput("Database port [" + dbParam.getPort() + "]: ");
		dbPortInput.setDefaultValue(dbParam.getPort());
		InstallUtil.ask(scanner, dbPortInput);
		dbParam.setPort(dbPortInput.getValue());
		
		StringInput adminUserInput = new StringInput("Database username [" + defaultAdminUser + "]: ");
		adminUserInput.setDefaultValue(defaultAdminUser);
		InstallUtil.ask(scanner, adminUserInput);
		dbParam.setAdminUser(adminUserInput.getValue());

		StringInput adminPasswordInput = new StringInput("Database password: ");
		InstallUtil.ask(scanner, adminPasswordInput);
		dbParam.setAdminPassword(adminPasswordInput.getValue());
		
		if (!dbType.equals(DB.Oracle)) {
			DBNameInput dbNameInput = new DBNameInput(String.format("Input database name [%s]: ", ""), dbParam.getType());
			dbNameInput.setDefaultValue("");
			InstallUtil.ask(scanner, dbNameInput);
			
			dbParam.setName(dbNameInput.getValue());
		}
		
		Properties connectionProps = new Properties();
	    connectionProps.put("user", dbParam.getAdminUser());
	    connectionProps.put("password", dbParam.getAdminPassword());
	    
	    Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String jdbcUrl = InstallUtil.getJdbcUrl(dbParam, "".equals(dbParam.getName()));
			System.out.println("JDBC Url: " + jdbcUrl);
			
			System.out.print("Connecting to " + dbParam.getHost() + "@" + dbParam.getPort() + " ..... ");
			conn = DriverManager.getConnection(jdbcUrl, connectionProps);
			InstallUtil.output("\t[connected]\n");
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT 1");
			if (rs.next()) {
				System.out.println("Test SQL Result: " + rs.getInt(1));
			}
		} catch (SQLException sqle) {
			String message = sqle.getMessage();
			if (message.indexOf("No suitable driver") >= 0) {
				System.out.println("Installer could not find the JDBC driver.");
			} else {
				System.out.println("Could not connect to database.");
				printError(message);
			}
		} catch (Exception ce) {
			String message = ce.getMessage();
			System.out.println(message);
			ce.printStackTrace();
		} finally {
			close(conn);
			close(stmt);
			close(rs);
		}
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
	
	private void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String[] ldapTypePrompt() {
		StringBuffer txt = new StringBuffer();
		StringBuffer opt = new StringBuffer();
		txt.append(NEW_LINE).append(bar()).append(NEW_LINE);
		
		int index = 1;
		for (LDAP ldap : LDAP.values()) {
			opt.append(index).append(",");
			txt.append(index++).append(": ").append(ldap.getFullname()).append(NEW_LINE);
		}
		
		txt.append(bar()).append(NEW_LINE);
		txt.append("Select the database type: ");
		return new String[]{txt.toString(),opt.substring(0, opt.length()-1)};
	}
	
	private void testLdap() {
		InstallUtil.newLine();
		String[] prompt = ldapTypePrompt();
		SelectInput ldapTypeInput = new SelectInput(prompt[0], prompt[1].split(","));
		InstallUtil.ask(scanner, ldapTypeInput);
		LDAP ldapType = LDAP.values()[Integer.parseInt(ldapTypeInput.getValue())-1];
		System.out.println(ldapType);

		LDAPParam ldapParam = new LDAPParam();
		ldapParam.setType(ldapType);

		StringInput ldapHostInput = new StringInput(String.format("LDAP server hostname or IP address [%s]: ", ldapParam.getHost()));
		ldapHostInput.setDefaultValue(ldapParam.getHost());
		InstallUtil.ask(scanner, ldapHostInput);
		ldapParam.setHost(ldapHostInput.getValue());

		IntegerInput dbPortInput = new IntegerInput("LDAP server port [" + ldapParam.getPort() + "]: ");
		dbPortInput.setDefaultValue(ldapParam.getPort());
		InstallUtil.ask(scanner, dbPortInput);
		ldapParam.setPort(dbPortInput.getValue());

		StringInput adminUserInput = new StringInput("DN or username: ");
		InstallUtil.ask(scanner, adminUserInput);
		ldapParam.setAdminUser(adminUserInput.getValue());

		StringInput adminPasswordInput = new StringInput("Password: ");
		InstallUtil.ask(scanner, adminPasswordInput);
		ldapParam.setAdminPassword(adminPasswordInput.getValue());

		LDAPConnector connector = new LDAPConnector();
		connector.test(ldapParam);
	}

	private void printError(String error) {
		shortBar();
		System.out.println(error);
		shortBar();
	}

	public static void main(String[] args) {
		Connector c = new Connector();
		c.execute();
	}
}
