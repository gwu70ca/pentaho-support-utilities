package com.pentaho.install.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.pentaho.install.ActionResult;
import com.pentaho.install.AppServerParam;
import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.InstallAction;
import com.pentaho.install.InstallParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam;
import com.pentaho.install.PentahoServerParam.SERVER;
import com.pentaho.install.action.CopyFileAction;
import com.pentaho.install.input.BooleanInput;
import com.pentaho.install.input.FileInput;
import com.pentaho.install.input.SelectInput;
import com.pentaho.install.input.StringInput;

public class ConfigUpdater extends InstallAction {
	private Scanner scanner;
	
	public static Map<DB, String> quartzMap;
	public static Map<DB, String> hibernateMap;
	public static Map<DB, String> auditMap;
	
	static {
		quartzMap = new HashMap<DB, String>();
		quartzMap.put(DB.MySQL, "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
		quartzMap.put(DB.PostgreSQL, "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
		quartzMap.put(DB.Oracle, "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate");
		quartzMap.put(DB.MSSQLServer, "org.quartz.impl.jdbcjobstore.MSSQLDelegate");
		
		hibernateMap = new HashMap<DB, String>();
		hibernateMap.put(DB.MySQL, "mysql5.hibernate.cfg.xml");
		hibernateMap.put(DB.PostgreSQL, "postgresql.hibernate.cfg.xml");
		hibernateMap.put(DB.Oracle, "oracle10g.hibernate.cfg.xml");
		hibernateMap.put(DB.MSSQLServer, "sqlserver.hibernate.cfg.xml");
		
		auditMap = new HashMap<DB, String>();
		auditMap.put(DB.MySQL, "mysql5");
		auditMap.put(DB.PostgreSQL, "postgresql");
		auditMap.put(DB.Oracle, "oracle10g");
		auditMap.put(DB.MSSQLServer, "sqlserver");
	}

	InstallParam installParam;
	private String serverDirName;
	
	public ConfigUpdater(InstallParam installParam, Scanner scanner) throws Exception {
		this.installParam = installParam;
		serverDirName = PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType);
		
		this.scanner = scanner;
	}

	private String prompt() {
		StringBuffer buf = new StringBuffer();
		buf.append(NEW_LINE).append(bar()).append(NEW_LINE);
		
		buf.append("1: Apache Tomcat\n" );
		buf.append("2: Jboss Application Server\n" );
		
		buf.append(bar()).append(NEW_LINE);
		buf.append("Select the application server type: ");
		return buf.toString();
	}
	
	public ActionResult execute() throws Exception {
		if (!PostInstaller.SILENT) {
			do {
				InstallUtil.newLine();
				BooleanInput askToProceed = new BooleanInput("Installer is going to update various configuration files, do you want to continue [y/n]? ");
				InstallUtil.ask(scanner, askToProceed);
				if (askToProceed.yes()) {
					break;
				}
				
				BooleanInput askToQuit = new BooleanInput("Do you want to quit [y/n]? ");
				InstallUtil.ask(scanner, askToQuit);
				if (askToQuit.yes()) {
					System.exit(0);
				}
			} while (true);
		}
		
		if (installParam.pentahoServerType.equals(PentahoServerParam.SERVER.BA)) {
			updateBAServer();
		} else if (installParam.pentahoServerType.equals(PentahoServerParam.SERVER.DI)) {
			updateDIServer();
		}
		
		InstallUtil.output("All files were updated.");
		return new ActionResult("");
	}
	
	private void updateBAServer() throws Exception {
		//update quartz
		if (!updateQuartz()) {
			if (PostInstaller.SILENT) {
				
			} else {
				BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
				InstallUtil.ask(scanner, askForContinue);
				if (!askForContinue.yes()) {
					System.exit(0);
				}
			}
		}
		InstallUtil.output("\t[done]");
					
		//update hibernate
		if (!updateHibernate()) {
			if (PostInstaller.SILENT) {
				
			} else {
				BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
				InstallUtil.ask(scanner, askForContinue);
				if (!askForContinue.yes()) {
					System.exit(0);
				}
			}
		}
		InstallUtil.output("\t[done]");
				
		//update jackrabbit
		if (!updateJackrabbit()) {
			if (PostInstaller.SILENT) {
				
			} else {
				BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
				InstallUtil.ask(scanner, askForContinue);
				if (!askForContinue.yes()) {
					System.exit(0);
				}
			}
		}
		InstallUtil.output("\t[done]");
		
		//update audit
		if (!updateAudit()) {
			if (PostInstaller.SILENT) {
				
			} else {
				BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
				InstallUtil.ask(scanner, askForContinue);
				if (!askForContinue.yes()) {
					System.exit(0);
				}
			}
		}
		InstallUtil.output("\t[done]");
		
		boolean isTomcat = false, isJboss = false;
		if (PostInstaller.SILENT) {
			AppServerParam.SERVER appServer = installParam.appServerType;
			isTomcat = appServer.equals(AppServerParam.SERVER.TOMCAT);
			isJboss = appServer.equals(AppServerParam.SERVER.JBOSS);
		} else {
			InstallUtil.newLine();
			SelectInput appServerTypeInput = new SelectInput(prompt(), new String[]{"1","2"});
			InstallUtil.ask(scanner, appServerTypeInput);
			isTomcat = "1".equals(appServerTypeInput.getValue());
			isJboss = "2".equals(appServerTypeInput.getValue());
		}
		
		if (isTomcat) {
			//update tomcat
			updateTomcat();
		} else if (isJboss) {
			//update jboss
		}
			
		InstallUtil.newLine();
	}
	
	private void updateTomcat() throws Exception {
		if (PostInstaller.SILENT) {
		} else {
			String tomcatDirName = "tomcat";
			StringInput tomcatDirInput = new StringInput(String.format("Input Tomcat directory name [%s]: ", tomcatDirName));
			tomcatDirInput.setDefaultValue(tomcatDirName);
			InstallUtil.ask(scanner, tomcatDirInput);
			installParam.appServerDir = tomcatDirInput.getValue(); 
		}
		
		TomcatXMLHelper helper = new TomcatXMLHelper(installParam);
		boolean success = helper.updateContextXml();
		
		if (!success) {
			if (PostInstaller.SILENT) {
				
			} else {
				BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
				InstallUtil.ask(scanner, askForContinue);
				if (!askForContinue.yes()) {
					System.exit(0);
				}
			}
		}
		InstallUtil.output("\t[done]");

		if (PostInstaller.SILENT) {
			
		} else {
			InstallUtil.newLine();
			BooleanInput askForContinue = new BooleanInput("Do you want installer to copy the JDBC driver jar file (y/n)? ");
			InstallUtil.ask(scanner, askForContinue);
			if (askForContinue.yes()) {
				do {
					FileInput jdbcDriverJar = new FileInput("Input location of the JDBC driver jar file: ");
					InstallUtil.ask(scanner, jdbcDriverJar);
					String jarPath = jdbcDriverJar.getValue();
					String jarFilename = InstallUtil.getFilenameFromPath(jarPath);
					
					File source = new File(jarPath);
					File target = new File(installParam.installDir + "/server/" + serverDirName + "/" + installParam.appServerDir + "/lib", jarFilename);
					CopyFileAction action = new CopyFileAction(source, target);
					try {
						action.execute();
						InstallUtil.output("\t[done]");
						break;
					} catch (IOException ioe) {
						askForContinue = new BooleanInput("There is error happened, do you want to try again (y/n)? ");
						InstallUtil.ask(scanner, askForContinue);
						if (askForContinue.yes()) {
							continue;
						}
						break;
					}
				} while (true);
			}
		}
	}
	
	private void updateDIServer() throws Exception {
		//update quartz
		if (!updateQuartz()) {
			if (PostInstaller.SILENT) {
				
			} else {
				BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
				InstallUtil.ask(scanner, askForContinue);
				if (!askForContinue.yes()) {
					System.exit(0);
				}
			}
		}
		InstallUtil.output("\t[done]");
		
		//update hibernate
		if (!updateHibernate()) {
			if (PostInstaller.SILENT) {
				
			} else {
				BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
				InstallUtil.ask(scanner, askForContinue);
				if (!askForContinue.yes()) {
					System.exit(0);
				}
			}
		}
		InstallUtil.output("\t[done]");
		
		//update audit
		if (!updateAudit()) {
			if (PostInstaller.SILENT) {
				
			} else {
				BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
				InstallUtil.ask(scanner, askForContinue);
				if (!askForContinue.yes()) {
					System.exit(0);
				}
			}
		}
		InstallUtil.output("\t[done]");
		
		//update jackrabbit
		if (!updateJackrabbit()) {
			if (PostInstaller.SILENT) {
				
			} else {
				BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
				InstallUtil.ask(scanner, askForContinue);
				if (!askForContinue.yes()) {
					System.exit(0);
				}
			}
		}
		InstallUtil.output("\t[done]");
		
		updateTomcat();
	}
	
	private boolean updateQuartz() {
		boolean success = false;
		
		String quartzDir = installParam.installDir + "/server/" + serverDirName + "/pentaho-solutions/system/quartz";
		quartzDir = quartzDir.replace('/', File.separatorChar);
		File file = new File(quartzDir + File.separator + "quartz.properties");
		InstallUtil.output("Updating quartz configuration file " + file.getAbsolutePath());
		
		try {
			StringBuilder builder = new StringBuilder();
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.startsWith("#") || line.trim().length() == 0) {
					builder.append(line).append(NEW_LINE);
					continue;
				}
				if (line.startsWith("org.quartz.jobStore.driverDelegateClass")) {
					line = "org.quartz.jobStore.driverDelegateClass = " + quartzMap.get(installParam.dbType);
				} else if (line.startsWith("org.quartz.dataSource.myDS.jndiURL")) {
					line = "org.quartz.dataSource.myDS.jndiURL = Quartz";
				}
				builder.append(line).append(NEW_LINE);
			}
			sc.close();
			
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			writer.write(builder.toString());
			writer.close();
			
			success = true;
		} catch (FileNotFoundException e) {
			InstallUtil.output("\nCould not find the file " + file.getAbsolutePath());
		} catch (IOException ioe) {
			InstallUtil.output("\nCould not write to the file " + file.getAbsolutePath());
		}
		return success;
	}
	
	private boolean updateHibernate() {
		boolean success = false;
		
		String hibernateDir = installParam.installDir + "/server/" + serverDirName + "/pentaho-solutions/system/hibernate";
		hibernateDir = hibernateDir.replace('/', File.separatorChar);
		String hibernateCfgFile = hibernateMap.get(installParam.dbType);
		File file = new File(hibernateDir + File.separator + "hibernate-settings.xml");
		InstallUtil.output("\nUpdating hibernate configuration file " + file.getAbsolutePath());
		
		try {
			String STR = "<config-file>system/hibernate/";
			StringBuilder builder = new StringBuilder();
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.trim().startsWith(STR)) {
					line = line.substring(0, line.indexOf("<config-file>")) + "<config-file>system/hibernate/" + hibernateCfgFile + "</config-file>";
				}
				builder.append(line).append(NEW_LINE);
			}
			sc.close();
			
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			writer.write(builder.toString());
			writer.close();
			
			success = true;
		} catch (FileNotFoundException e) {
			InstallUtil.output("\nCould not find the file " + file.getAbsolutePath());
		} catch (IOException ioe) {
			InstallUtil.output("\nCould not write to the file " + file.getAbsolutePath());
		}
		
		if (!success) {
			return success;
		}
		
		success = false;
		file = new File(hibernateDir + File.separator + hibernateCfgFile);
		InstallUtil.output("Updating hibernate configuration file " + file.getAbsolutePath());
		
		try {
			String hibernate = installParam.pentahoServerType.equals(SERVER.BA) ? DBParam.DB_NAME_HIBERNATE : DBParam.DB_NAME_HIBERNATE_DI;
			DBInstance hibernateDbInstance = installParam.dbInstanceMap.get(hibernate);

			String JDBC_STR = "<property name=\"connection.url\">";
			String USERNAME_STR = "<property name=\"connection.username\">";
			String PASSWORD_STR = "<property name=\"connection.password\">";
			StringBuilder builder = new StringBuilder();
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.trim().startsWith(JDBC_STR)) {
					line = line.substring(0, line.indexOf(JDBC_STR) + JDBC_STR.length()) +
							InstallUtil.getJdbcUrl(hibernateDbInstance, false) + "</property>";
				} else if (line.trim().startsWith(USERNAME_STR)) {
					line = line.substring(0, line.indexOf(USERNAME_STR) + USERNAME_STR.length()) + hibernateDbInstance.getUsername() + "</property>";
				} else if (line.trim().startsWith(PASSWORD_STR)) {
					line = line.substring(0, line.indexOf(PASSWORD_STR) + PASSWORD_STR.length()) + hibernateDbInstance.getPassword() + "</property>";
				}
				builder.append(line).append(NEW_LINE);
			}
			sc.close();
			
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			writer.write(builder.toString());
			writer.close();
			
			success = true;
		} catch (FileNotFoundException e) {
			InstallUtil.output("\nCould not find the file " + file.getAbsolutePath());
		} catch (IOException ioe) {
			InstallUtil.output("\nCould not write to the file " + file.getAbsolutePath());
		}
		
		return success;
	}

	private boolean updateJackrabbit() {
		boolean success = false;
		
		String jrDir = installParam.installDir + "/server/" + serverDirName + "/pentaho-solutions/system/jackrabbit";
		jrDir = jrDir.replace('/', File.separatorChar);
		File repoFile = new File(jrDir + File.separator + "repository.xml");
		InstallUtil.output("\nUpdating jackrabbit configuration file " + repoFile.getAbsolutePath());
		
		String jackrabbit = installParam.pentahoServerType.equals(SERVER.BA) ? DBParam.DB_NAME_JACKRABBIT : DBParam.DB_NAME_JACKRABBIT_DI;
		JackRabbitRepoXMLHelper helper = new JackRabbitRepoXMLHelper();
		helper.setJackrabbitInstance(installParam.dbInstanceMap.get(jackrabbit));
		helper.setServerType(installParam.pentahoServerType);
		success = helper.updateRepositoryXml(repoFile);
		
		return success;
	}
	
	private boolean updateAudit() {
		boolean success = false;

		String auditDir = installParam.installDir + "/server/" + serverDirName + "/pentaho-solutions/system/dialects";
		auditDir = auditDir.replace('/', File.separatorChar);
		InstallUtil.output("\nCopying audit configuration file");
		
		File source = new File(auditDir + File.separator + auditMap.get(installParam.dbType) + File.separator + "audit_sql.xml");
		File target = new File(installParam.installDir + "/server/" + serverDirName + "/pentaho-solutions/system/audit_sql.xml");
		CopyFileAction action = new CopyFileAction(source, target);

		try {
			action.execute();
			success = true;
		} catch (IOException ex) {
			InstallUtil.output("\nFailed to copy from \n\t" + source.getAbsolutePath() + "\n\tto" + target.getAbsolutePath());
		}			
		
		return success;
	}
}
