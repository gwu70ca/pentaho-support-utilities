package com.pentaho.install;

import com.pentaho.install.DBParam.DB;
import com.pentaho.install.db.Dialect;
import com.pentaho.install.input.BooleanInput;
import com.pentaho.install.post.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class InstallParamCollector {
    private String installCfgFile;
    private Scanner scanner;

    public InstallParamCollector(String cfgFile, Scanner scanner) throws Exception {
        this.installCfgFile = cfgFile;
        this.scanner = scanner;
    }

    public InstallParam execute() {
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
            installParam.pentahoServerType = (PentahoServerParam.SERVER) result.getReturnedValue();
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
            installParam.installDir = (String) result.getReturnedValue();
        }
        InstallUtil.output("Install directory: " + installParam.installDir);
        InstallUtil.output("\n");

        //Get database type
        if (PostInstaller.SILENT) {
            String dbTypeStr = installProp.getProperty("db_type");
            installParam.dbType = DB.valueOf(dbTypeStr);
        } else {
            DatabaseChooser dbc = new DatabaseChooser(scanner);
            ActionResult result = dbc.execute();
            installParam.dbType = (DBParam.DB) result.getReturnedValue();
            InstallUtil.newLine();
        }
        InstallUtil.output("Database type: " + installParam.dbType);
        InstallUtil.output("\n");

        Dialect dialect = InstallUtil.createDialect(installParam.dbType);

        //Need database type to verify the "data" directory
        try {
            if (!verifyDirectoryStructure(installParam, dialect)) {
                BooleanInput askToContinue = new BooleanInput("\nSome of the directories are not valid, do you want to continue [y/n]? ");
                InstallUtil.ask(scanner, askToContinue);
                if (!askToContinue.yes()) {
                    System.exit(0);
                }
            }
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }

        DatabaseInfoCollector dbInfoCollector = new DatabaseInfoCollector(installProp, scanner);
        dbInfoCollector.setServerType(installParam.pentahoServerType);
        dbInfoCollector.setDbType(installParam.dbType);
        DBActionResult dbResult = (DBActionResult) dbInfoCollector.execute();
        Map<String, DBInstance> dbInstanceMap = (Map<String, DBInstance>) dbResult.getReturnedValue();
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

    private boolean verifyDirectoryStructure(InstallParam installParam, Dialect dialect) throws Exception {
        boolean verified = true;
        String serverRootDir = InstallUtil.getServerRootDir(installParam);
        if (!canReadAndWrite(new File(serverRootDir)) || !canReadAndWrite(new File(serverRootDir + "/data/" + dialect.getScriptDirName()))) {
            InstallUtil.output("Directory [" + serverRootDir + "] is invalid. Make sure the directory exists and current user has read/write permission.");
        } else {
            if (InstallUtil.isBA(installParam.pentahoServerType)) {
                File analyzerDir = new File(serverRootDir + "/pentaho-solutions/system/analyzer");
                if (!isValidDir(analyzerDir)) {
                    InstallUtil.output("Analyzer directory [" + analyzerDir + "] is invalid.");
                    verified = false;
                }

                File dashboardsDir = new File(serverRootDir + "/pentaho-solutions/system/dashboards");
                if (!isValidDir(dashboardsDir)) {
                    InstallUtil.output("Dashboards directory [" + dashboardsDir + "] is invalid.");
                    verified = false;
                }

                File pirDir = new File(serverRootDir + "/pentaho-solutions/system/pentaho-interactive-reporting");
                if (!isValidDir(pirDir)) {
                    InstallUtil.output("Pentaho Interactive Report directory [" + pirDir + "] is invalid.");
                    verified = false;
                }

                File pmpDir = new File(serverRootDir + "/pentaho-solutions/system/pentaho-mobile-plugin");
                if (!isValidDir(pmpDir)) {
                    InstallUtil.output("Pentaho Mobile Plugin directory [" + pmpDir + "] is invalid.");
                    verified = false;
                }
            } else if (InstallUtil.isDI(installParam.pentahoServerType) || InstallUtil.isHYBRID(installParam.pentahoServerType)) {
            }
        }
        return verified;
    }

    private boolean canReadAndWrite(File dir) {
        return dir.exists() && dir.isDirectory() && dir.canRead() && dir.canWrite();
    }

    private boolean isValidDir(File dir) {
        return dir.exists() && dir.isDirectory();
    }
}
