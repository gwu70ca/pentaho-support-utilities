package com.pentaho.install;

import com.pentaho.install.DBParam.DB;
import com.pentaho.install.input.BooleanInput;
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
            if (installParam.dbType == null) {
                InstallUtil.output("Invalid database type:" + dbTypeStr);
                InstallUtil.exit();
            }
        } else {
            DatabaseChooser dbc = new DatabaseChooser(scanner);
            ActionResult result = dbc.execute();
            installParam.dbType = (DBParam.DB) result.getReturnedValue();
            InstallUtil.newLine();
        }
        InstallUtil.output("Database type: " + installParam.dbType);
        InstallUtil.output("\n");

        //Need database type to verify the "data" directory
        boolean verified = false;
        try {
            if (!verifyDirectoryStructure(installParam)) {
                BooleanInput askToContinue = new BooleanInput("Some of the directories are not valid, do you want to continue [y/n]? ");
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

    private boolean verifyDirectoryStructure(InstallParam installParam) throws Exception {
        boolean verified = false;
        String serverRootDir = InstallUtil.getServerRootDir(installParam);
        if (!canReadAndWrite(new File(serverRootDir)) || !canReadAndWrite(new File(serverRootDir + "/data/" + DBParam.dbDirMap.get(installParam.dbType)))) {
            InstallUtil.output("Directory [" + serverRootDir + "] is invalid. Make sure the directory exists and current user has read/write permission.");
        } else {
            if (InstallUtil.isBA(installParam)) {
                File analyzerDir = new File(serverRootDir + "/system/analyzer");
                boolean analyzerDirValid = isValidDir(analyzerDir);
                if (!analyzerDirValid) {
                    InstallUtil.output("Analyzer directory [" + analyzerDir + "] is invalid.");
                }

                File dashboardsDir = new File(serverRootDir + "/system/dashboards");
                boolean dashboardsDirValid = isValidDir(dashboardsDir);
                if (!dashboardsDirValid) {
                    InstallUtil.output("Dashboards directory [" + dashboardsDir + "] is invalid.");
                }

                File pirDir = new File(serverRootDir + "/system/pentaho-interactive-reporting");
                boolean pirDirValid = isValidDir(pirDir);
                if (!pirDirValid) {
                    InstallUtil.output("Pentaho Interactive Report directory [" + pirDir + "] is invalid.");
                }

                File pmpDir = new File(serverRootDir + "/system/pentaho-mobile-plugin");
                boolean pmpDirValid = isValidDir(pmpDir);
                if (!pmpDirValid) {
                    InstallUtil.output("Pentaho Mobile Plugin directory [" + pmpDir + "] is invalid.");
                }

                verified = true;
            } else if (InstallUtil.isDI(installParam)) {

                verified = true;
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
