package com.pentaho.install.post;

import com.pentaho.install.*;
import com.pentaho.install.db.Dialect;

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
		Dialect dialect = InstallUtil.createDialect(installParam.dbType);

		File dbFileDir = new File(installParam.installDir, "server/" + serverDirName + "/data/" + dialect.getScriptDirName());
		SqlScriptRunner runner = new SqlScriptRunner(scanner, dbFileDir, installParam.dbInstanceMap);
		runner.execute();
		
		return new ActionResult(installParam.dbInstanceMap);
	}
}
