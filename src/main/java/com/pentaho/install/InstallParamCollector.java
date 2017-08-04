package com.pentaho.install;

import com.pentaho.install.DBParam.DB;
import com.pentaho.install.post.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class InstallParamCollector {
	String installCfgFile;
	Scanner scanner;
	
	public InstallParamCollector(String cfgFile, Scanner scanner) throws Exception {
		this.installCfgFile = cfgFile;
		this.scanner = scanner;
	}
	
	public InstallParam collect() {
		Properties installProp = new Properties();
		
		if (PostInstaller.SILENT) {
			InstallUtil.output("Reading install property file: " + installCfgFile);
			
			try {
				installProp.load(new FileInputStream(new File(installCfgFile)));
			} catch (Exception ex) {
				InstallUtil.output("Invalid install properties file");
				InstallUtil.exit();
			}
		}
		
		InstallParam installParam = new InstallParam();
		
		//Get Pentaho server type
		if (PostInstaller.SILENT) {
			installParam.pentahoServerType = PentahoServerParam.SERVER.valueOf(installProp.getProperty("server_type"));
		} else {
			ServerChooser sc = new ServerChooser(scanner);
			ActionResult result = sc.execute();
			installParam.pentahoServerType = (PentahoServerParam.SERVER)result.getReturnedValue();
		}
		InstallUtil.output("Pentaho server type: " + installParam.pentahoServerType);
		InstallUtil.newLine();
		
		//Get install directory
		if (PostInstaller.SILENT) {
			installParam.installDir = installProp.getProperty("install_dir");
		} else {
			LocationChooser loc = new LocationChooser(scanner);
			loc.setServerType(installParam.pentahoServerType);
			ActionResult result = loc.execute();
			installParam.installDir = (String)result.getReturnedValue();
		}
		InstallUtil.output("Install directory: " + installParam.installDir);
		InstallUtil.output("\n");

		//Get database type
		if (PostInstaller.SILENT) {
			String dbTypeStr = installProp.getProperty("db_type");
			installParam.dbType = DB.valueOf(dbTypeStr);
			if (installParam.dbType == null) {
				InstallUtil.output("Invalid database type:" + dbTypeStr);
				InstallUtil.exit();
			}
		} else {
			DatabaseChooser dbc = new DatabaseChooser(scanner);
			ActionResult result = dbc.execute();
			installParam.dbType = (DBParam.DB)result.getReturnedValue();
			InstallUtil.newLine();
		}
		InstallUtil.output("Database type: " + installParam.dbType);
		InstallUtil.output("\n");
		
		DatabaseInfoCollector dbInfoCollector = new DatabaseInfoCollector(installProp, scanner);
		dbInfoCollector.setServerType(installParam.pentahoServerType);
		dbInfoCollector.setDbType(installParam.dbType);
		DBActionResult dbResult = (DBActionResult)dbInfoCollector.execute();
		Map<String, DBInstance> dbInstanceMap = (Map<String, DBInstance>)dbResult.getReturnedValue();
		boolean manualCreateDb = dbResult.isManual();
		installParam.dbInstanceMap = dbInstanceMap;
		installParam.manualCreateDb = manualCreateDb;
		InstallUtil.output("\n");
		
		if (PostInstaller.SILENT) {
			String appServer = installProp.getProperty("app_server");
			installParam.appServerType = AppServerParam.SERVER.valueOf(appServer.toUpperCase());
			installParam.appServerDir = installProp.getProperty("app_server_dir");
		}
		return installParam;
	}
}
