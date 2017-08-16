package com.pentaho.install.post;

import com.pentaho.install.InstallParam;
import com.pentaho.install.InstallParamCollector;
import com.pentaho.install.InstallUtil;

import java.io.File;
import java.util.Scanner;

/**
 * This installer does everything after you run the IZPack for archive install mode
 * 
 * Silent install:
 * 		java -cp postinstall.jar;YOUR_JDBC_DRIVER.jar com.pentaho.install.post.PostInstaller --file install.ba.properties
 * 
 * 
 * @author gwu
 *
 */
public class PostInstaller {
	public static boolean DEBUG = false;
	public static boolean SILENT = false;
	
	String installCfgFile;
	Scanner scanner;

	public PostInstaller(Scanner scanner, String installCfgFile) {
		this.scanner = scanner;
		this.installCfgFile = installCfgFile;
	}

	public void install() throws Exception {
		if (SILENT) {
			File installConfigFile = new File(installCfgFile);
			if (!installConfigFile.exists() ||
					installConfigFile.isDirectory() ||
					!installConfigFile.canRead()) {
				InstallUtil.output("Invalid installation configuration file: " + installCfgFile);
				throw new Exception("Invalid installation configuration file: " + installCfgFile);
			}
		}

		InstallParamCollector paramCollector = new InstallParamCollector(this.installCfgFile,scanner);
		InstallParam installParam = paramCollector.execute();
		
		if (!installParam.manualCreateDb) {
			//User may choose to manually create database
			DatabaseCreator dbCreator = new DatabaseCreator(installParam, scanner);
			dbCreator.execute();
		}
		
		//A lot XMLs, really
		ConfigUpdater cfgUpdator = new ConfigUpdater(installParam, scanner);
		cfgUpdator.installParam = installParam;
		cfgUpdator.execute();
		
		InstallUtil.newLine();
	}

    public static void main(String[] args) throws Exception {
        InstallUtil.output("This program performs Pentaho Server Post Installation with Archive mode (Version 7.x, Version 6.1 BA/DI)");

        String cfgFile = null;
        if (args.length > 0) {
            try {
                for (int i=0;i<args.length;i++) {
                    if (args[i].equals("-F") || args[i].equals("--file")) {
                        cfgFile = args[++i];
                        SILENT = true;
                    } else if (args[i].equals("-D") || args[i].equals("--debug")) {
                        DEBUG = true;
                    } else if (args[i].equals("-R") || args[i].equals("--dryrun")) {
                        //dryrun will still connect to database, just don't execute any sql statement
                        SqlScriptRunner.DRYRUN = true;
                    }
                }
            } catch (Exception ex) {
                InstallUtil.output("Invalid CLI parameters");
                InstallUtil.exit();
            }
        }

        Scanner scanner = null;
        if (!SILENT) {
            scanner = new Scanner(System.in);
        }
        PostInstaller installer = new PostInstaller(scanner, cfgFile);
        installer.install();

        if (!SILENT) {
            scanner.close();
        }
    }
}
