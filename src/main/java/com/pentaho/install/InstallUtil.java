package com.pentaho.install;

import com.pentaho.install.action.CopyFileAction;
import com.pentaho.install.db.*;
import com.pentaho.install.input.BooleanInput;
import com.pentaho.install.post.PostInstaller;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static com.pentaho.install.DBParam.DB;
import static com.pentaho.install.PentahoServerParam.SERVER;

public class InstallUtil {
    public static String NEW_LINE = System.lineSeparator();
    public static String EXIT = "0";

    public static String exitMenuEntry() {
        return InstallUtil.EXIT + ": Exit";
    }

    public static String bar() {
        return "================================================================================";
    }

    public static String shortBar() {
        return "\n--------------------\n";
    }

    public static boolean isBlankOrNull(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static void error(String error) {
        System.out.println(error);
    }

    public static void output(String message) {
        System.out.println(message);
    }

    public static void exit() {
        System.exit(0);
    }


    public static String getFilenameFromPath(String path) {
        path = path.replace("\\", "/");
        int lastSlash = path.lastIndexOf("/");
        if (lastSlash == -1) {
            return path;
        }

        return path.substring(lastSlash + 1, path.length());
    }

    public static void ask(Scanner scanner, com.pentaho.install.input.Input input) {
        do {
            System.out.print(input.getPrompt());

            String line = scanner.nextLine().trim();
            Logger.log(line);

            if (line.length() == 0) {
                if (input.getDefaultValue() != null) {
                    input.setValue(input.getDefaultValue());
                    break;
                }
            } else {
                input.setValue(line);
                String error;
                if ((error = input.validate()) == null) {
                    break;
                } else {
                    System.out.println(error);
                }
            }
        } while (true);
    }

    public static void newLine() {
        System.out.println();
    }

    public static boolean isOrcl(DBInstance dbInstance) {
        return DB.Orcl.equals(dbInstance.getType());
    }

    public static boolean isOrcl(DBParam.DB dbType) {
        return DB.Orcl.equals(dbType);
    }

    public static boolean isSqlserver(DBParam.DB dbType) {
        return DB.Sqlserver.equals(dbType);
    }

    public static boolean isPostgresql(DBParam.DB dbType) {
        return DB.Psql.equals(dbType);
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static String getLdapUserSearchFilter(LDAPParam param, String accountName) {
        String searchFilter = LDAPParam.AHACHEDS_USER_SEARCH_FILTER_CN;
        if (LDAPParam.LDAP.MSAD.equals(param.getType())) {
            searchFilter = LDAPParam.MSAD_USER_SEARCH_FILTER;
        }

        if (isBlankOrNull(accountName)) {
            return searchFilter;
        }

        String accountFilter = "(" + (param.isUseSamAccountName() ? "sAMAccountName" : "cn") + "=%s)";
        return "(&" + searchFilter + String.format(accountFilter, accountName) + ")";
    }

    public static String getLdapGroupSearchFilter(LDAPParam param, String accountName) {
        String searchFilter = LDAPParam.AHACHEDS_GROUP_SEARCH_FILTER_CN;
        if (LDAPParam.LDAP.MSAD.equals(param.getType())) {
            searchFilter = LDAPParam.MSAD_GROUP_SEARCH_FILTER;
        }

        if (isBlankOrNull(accountName)) {
            return searchFilter;
        }

        String accountFilter = "(" + (param.isUseSamAccountName() ? "sAMAccountName" : "cn") + "=%s)";
        return "(&" + searchFilter + String.format(accountFilter, accountName) + ")";
    }

    public static boolean isBA(SERVER serverType) {
        return serverType.equals(SERVER.BA);
    }

    public static boolean isDI(SERVER serverType) {
        return serverType.equals(SERVER.DI);
    }

    public static boolean isHYBRID(SERVER serverType) {
        return serverType.equals(SERVER.HYBRID);
    }

    public static String getTomcatContextFilePath(InstallParam installParam) throws Exception {
        String tomcatDir = installParam.installDir + "/server/" + PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType) + "/" + installParam.appServerDir;
        String appName = !InstallUtil.isDI(installParam.pentahoServerType) ? "pentaho" : "pentaho-di";
        String contextDir = tomcatDir + "/webapps/" + appName + "/META-INF";
        contextDir = contextDir.replace('/', File.separatorChar);
        return contextDir + File.separator + "context.xml";
    }

    public static String getTomcatServerConfigFilePath(InstallParam installParam) throws Exception {
        String tomcatDir = installParam.installDir + "/server/" + PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType) + "/" + installParam.appServerDir;
        String contextDir = tomcatDir + "/conf";
        contextDir = contextDir.replace('/', File.separatorChar);
        return contextDir + File.separator + "context.xml";
    }

    public static String getJackrabbitRepositoryFilePath(InstallParam installParam) throws Exception {
        String jackrabbitDir = installParam.installDir + "/server/" + PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType) + "/pentaho-solutions/system/jackrabbit";
        jackrabbitDir = jackrabbitDir.replace('/', File.separatorChar);
        return jackrabbitDir + File.separator + "repository.xml";
    }

    public static String getServerRootDir(InstallParam installParam) throws Exception {
        return installParam.installDir + "/server/" + PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType);
    }

    public static Dialect createDialect(DBParam dbParam) {
        return createDialect(dbParam.getType());
    }

    public static Dialect createDialect(DB dbType) {
        Dialect d;
        switch (dbType) {
            case Mysql:
                d = new Mysql();
                break;
            case Orcl:
                d = new Orcl();
                break;
            case Psql:
                d = new Psql();
                break;
            case Sqlserver:
                d = new Sqlserver();
                break;
            default:
                d = new Psql();
        }
        return d;
    }

    public static PentahoDB createPentahoDB(String dbName, Dialect dialect) {
        PentahoDB pdb = null;
        switch (dbName) {
            case DBParam.DB_NAME_HIBERNATE:
                pdb = new Hibernate(dialect);
                break;
            case DBParam.DB_NAME_JACKRABBIT:
                pdb = new Jackrabbit(dialect);
                break;
            case DBParam.DB_NAME_QUARTZ:
                pdb = new Quartz(dialect);
                break;
            case DBParam.DB_NAME_PENT_OP_MART:
                pdb = new PenOpMart(dialect);
                break;
            default:
                break;
        }

        return pdb;
    }

    public static boolean backup(File original, Scanner scanner) {
        boolean success = false;

        File backup = new File(original.getAbsolutePath() + "." + PostInstaller.TIMESTAMP_SUFFIX);
        CopyFileAction action = new CopyFileAction(original, backup);
        try {
            action.execute();
            success = true;
        } catch (IOException ex) {
            InstallUtil.output("\nFailed to backup the original file: " + original.getAbsolutePath());
        }

        if (!success) {
            BooleanInput askToContinue = new BooleanInput("Do you want to continue [y/n]? ");
            InstallUtil.ask(scanner, askToContinue);
            success = askToContinue.yes();
        }

        return success;
    }
}
