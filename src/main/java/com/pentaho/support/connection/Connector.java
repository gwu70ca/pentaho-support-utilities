package com.pentaho.support.connection;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.LDAPParam;
import com.pentaho.install.LDAPParam.LDAP;
import com.pentaho.install.input.*;

import java.util.Scanner;

public class Connector {
	static String EXIT = "0";
	
	Scanner scanner;
	
	String NEW_LINE = System.lineSeparator();

	String exitMenuEntry() {
		return EXIT + ": Exit";
	}
	
	private String[] menuPrompt() {
		StringBuffer txt = new StringBuffer();
		txt.append(NEW_LINE).append(InstallUtil.bar()).append(NEW_LINE);
		
		int index = 1;
		txt.append(index++ + ": Test JDBC Connection").append(NEW_LINE);
		txt.append(index++ + ": Test LDAP Connection").append(NEW_LINE);
		txt.append(exitMenuEntry()).append(NEW_LINE);

		txt.append(InstallUtil.bar()).append(NEW_LINE);
		txt.append("What do you want: ");
		return new String[]{txt.toString(),"0,1,2"};
	}
	
	public void execute() {
		this.scanner = new Scanner(System.in);
		
		try {
			while (true) {
				InstallUtil.newLine();
				String[] menu = menuPrompt();
				SelectInput rootInput = new SelectInput(menu[0], menu[1].split(","));
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
		txt.append(NEW_LINE).append(InstallUtil.bar()).append(NEW_LINE);
		
		int index = 1;
		for (DB db : DB.values()) {
			opt.append(index).append(",");
			txt.append(index++).append(": ").append(db).append(NEW_LINE);
		}
		
		txt.append(InstallUtil.bar()).append(NEW_LINE);
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

		boolean winAuth = false;
		if (DB.Sqlserver.equals(dbType)) {
			BooleanInput wiaInput = new BooleanInput("Do you want to use Microsoft Windows Integration Authentication [y/n]? ");
			InstallUtil.ask(scanner, wiaInput);
			winAuth = wiaInput.yes();
			dbParam.setWinAuth(winAuth);
		}

		StringInput dbHostInput = new StringInput(String.format("Database hostname or IP address [%s]: ", dbParam.getHost()));
		dbHostInput.setDefaultValue(dbParam.getHost());
		InstallUtil.ask(scanner, dbHostInput);
		dbParam.setHost(dbHostInput.getValue());
		
		IntegerInput dbPortInput = new IntegerInput("Database port [" + dbParam.getPort() + "]: ");
		dbPortInput.setDefaultValue(dbParam.getPort());
		InstallUtil.ask(scanner, dbPortInput);
		dbParam.setPort(dbPortInput.getValue());

		if (!winAuth) {
			StringInput userInput = new StringInput("Database username [" + defaultAdminUser + "]: ");
			userInput.setDefaultValue(defaultAdminUser);
			InstallUtil.ask(scanner, userInput);
			dbParam.setUsername(userInput.getValue());

			StringInput passwordInput = new StringInput("Database password: ");
			InstallUtil.ask(scanner, passwordInput);
			dbParam.setPassword(passwordInput.getValue());
		}

		if (!dbType.equals(DB.Orcl)) {
			DBNameInput dbNameInput = new DBNameInput(String.format("Input database name [%s]: ", ""), dbParam.getType());
			dbNameInput.setDefaultValue("");
			InstallUtil.ask(scanner, dbNameInput);
			
			dbParam.setName(dbNameInput.getValue());
		}

		System.out.println(dbParam);

		JDBCConnector connector = new JDBCConnector();
		connector.test(dbParam);
	}

	private String[] ldapTypePrompt() {
		StringBuffer txt = new StringBuffer();
		StringBuffer opt = new StringBuffer();
		txt.append(NEW_LINE).append(InstallUtil.bar()).append(NEW_LINE);
		
		int index = 1;
		for (LDAP ldap : LDAP.values()) {
			opt.append(index).append(",");
			txt.append(index++).append(": ").append(ldap.getFullname()).append(NEW_LINE);
		}
		
		txt.append(InstallUtil.bar()).append(NEW_LINE);
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
		boolean connected = connector.test(ldapParam);

		if (connected) {
			BooleanInput searchUserInput = new BooleanInput("\nDo you want to search user [y/n]? ");
			InstallUtil.ask(scanner, searchUserInput);
			if (searchUserInput.yes()) {
				if (LDAP.MSAD.equals(ldapType)) {
					BooleanInput useSamInput = new BooleanInput("Do you want to use SAM Account Name to perform the search [y/n]? ");
					InstallUtil.ask(scanner,useSamInput);
					ldapParam.setUseSamAccountName(useSamInput.yes());
				}

				String accountType = !ldapParam.isUseSamAccountName() ? "Common Name: " : "Sam Account Name: ";
				StringInput ldapUserInput = new StringInput(accountType);
				//Allow empty name to return all users
				ldapUserInput.setDefaultValue("");
				InstallUtil.ask(scanner, ldapUserInput);
				ldapParam.setUserSearchFilter(InstallUtil.getLdapUserSearchFilter(ldapParam, ldapUserInput.getValue()));

				StringInput ldapSearchBaseInput = new StringInput("LDAP search base: ");
				InstallUtil.ask(scanner, ldapSearchBaseInput);
				ldapParam.setUserSearchBase(ldapSearchBaseInput.getValue());

				connector.searchUser(ldapParam);
			}

			ldapParam.setUseSamAccountName(false);
			InstallUtil.newLine();

			BooleanInput searchGroupInput = new BooleanInput("\nDo you want to search group [y/n]? ");
			InstallUtil.ask(scanner, searchGroupInput);
			if (searchGroupInput.yes()) {
				if (LDAP.MSAD.equals(ldapType)) {
					BooleanInput useSamInput = new BooleanInput("Do you want to use SAM Account Name to perform the search [y/n]? ");
					InstallUtil.ask(scanner,useSamInput);
					ldapParam.setUseSamAccountName(useSamInput.yes());
				}

				String accountType = !ldapParam.isUseSamAccountName() ? "Common Name: " : "Sam Account Name: ";
				StringInput ldapGroupInput = new StringInput(accountType);
				//Allow empty name to return all groups
				ldapGroupInput.setDefaultValue("");
				InstallUtil.ask(scanner, ldapGroupInput);
				ldapParam.setGroupSearchFilter(InstallUtil.getLdapGroupSearchFilter(ldapParam, ldapGroupInput.getValue()));

				StringInput ldapSearchBaseInput = new StringInput("LDAP search base: ");
				InstallUtil.ask(scanner, ldapSearchBaseInput);
				ldapParam.setGroupSearchBase(ldapSearchBaseInput.getValue());

				connector.searchGroup(ldapParam);
			}
		}
	}

	public static void main(String[] args) {
		Connector c = new Connector();
		c.execute();
	}
}
