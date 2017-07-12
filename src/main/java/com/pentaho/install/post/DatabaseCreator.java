package com.pentaho.install.post;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.pentaho.install.ActionResult;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.InstallParam;
import com.pentaho.install.Logger;
import com.pentaho.install.PentahoServerParam;

public class DatabaseCreator {
	static Map<DB, String> dbDirMap;
	
	static {
		dbDirMap = new HashMap<>();
		dbDirMap.put(DB.MySQL, "mysql5");
		dbDirMap.put(DB.PostgreSQL, "postgresql");
		dbDirMap.put(DB.Oracle, "oracle10g");
		dbDirMap.put(DB.MSSQLServer, "sqlserver");
	}
	
	InstallParam installParam;
	Scanner scanner;
	
	public DatabaseCreator(InstallParam installParam, Scanner scanner) {
		this.installParam = installParam;
		this.scanner = scanner;
	}

	public ActionResult execute() throws Exception {
		String serverDirName = PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType);
		Logger.log("Pentaho server: " + installParam.pentahoServerType + ", " + serverDirName);
		
		File dbFileDir = new File(installParam.installDir, "server/" + serverDirName + "/data/" + dbDirMap.get(installParam.dbType));		
		
		SqlScriptRunner runner = new SqlScriptRunner(scanner, dbFileDir, installParam.dbInstanceMap);
		runner.execute();
		
		return new ActionResult(installParam.dbInstanceMap);
	}
}
