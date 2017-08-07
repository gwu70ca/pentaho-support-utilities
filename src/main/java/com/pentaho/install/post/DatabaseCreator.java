package com.pentaho.install.post;

import com.pentaho.install.*;

import java.io.File;
import java.util.Scanner;

public class DatabaseCreator {
	InstallParam installParam;
	Scanner scanner;
	
	public DatabaseCreator(InstallParam installParam, Scanner scanner) {
		this.installParam = installParam;
		this.scanner = scanner;
	}

	public ActionResult execute() throws Exception {
		String serverDirName = PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType);
		Logger.log("Pentaho server: " + installParam.pentahoServerType + ", " + serverDirName);
		
		File dbFileDir = new File(installParam.installDir, "server/" + serverDirName + "/data/" + DBParam.dbDirMap.get(installParam.dbType));
		
		SqlScriptRunner runner = new SqlScriptRunner(scanner, dbFileDir, installParam.dbInstanceMap);
		runner.execute();
		
		return new ActionResult(installParam.dbInstanceMap);
	}
}
