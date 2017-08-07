package com.pentaho.install;

import com.pentaho.install.DBParam.DB;

import java.io.File;
import java.util.Scanner;

public class InstallUtil {
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

	public static String bar() {
		return "==============================";
	}
	
	public static String getFilenameFromPath(String path) {
		path = path.replace("\\", "/");
		int lastSlash = path.lastIndexOf("/");
		if (lastSlash == -1) {
			return path;
		}
		
		return path.substring(lastSlash+1, path.length());
	}
	
	public static String getJdbcDriverClass(DB dbType) {
		String driver = "com.mysql.jdbc.Driver";
		if (dbType == DB.MSSQLServer) {
			driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if (dbType == DB.Oracle) {
			driver = "oracle.jdbc.OracleDriver";
		} else if (dbType == DB.PostgreSQL) {
			driver = "org.postgresql.Driver";
		}
		
		return driver;
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
				String error = null;
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

	public static boolean isOracle(DBInstance dbInstance) {
	    return dbInstance.getType().equals(DB.Oracle);
    }
	
	public static String getJdbcUrl(DBInstance dbInstance) {
		return getJdbcUrl(dbInstance, false);
	}

	public static String getJdbcUrl(DBInstance dbInstance, boolean isAdmin) {
		String url = dbInstance.getJdbcPrefix() + dbInstance.getHost() + ":" + dbInstance.getPort() + "/"
				+ (isAdmin ? "" : dbInstance.getName());
		DB dbType = dbInstance.getType();

		if (dbInstance.getType() == DB.MySQL) {
			//
		} else if (dbType == DB.MSSQLServer) {
			url = dbInstance.getJdbcPrefix() + dbInstance.getHost() + ":" + dbInstance.getPort()
					+ (isAdmin ? "" : ";DatabaseName=" + dbInstance.getName());
		} else if (dbType == DB.Oracle) {
			url = dbInstance.getJdbcPrefix() + dbInstance.getHost() + ":" + dbInstance.getPort() + "/XE";
		} else if (dbType == DB.PostgreSQL) {
			url = dbInstance.getJdbcPrefix() + dbInstance.getHost() + ":" + dbInstance.getPort() + "/"
					+ (isAdmin ? "" : dbInstance.getName());
		}
		return url;
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

	public static boolean isBA(InstallParam param) {
	    return param.pentahoServerType.equals(PentahoServerParam.SERVER.BA);
    }

    public static boolean isDI(InstallParam param) {
        return param.pentahoServerType.equals(PentahoServerParam.SERVER.DI);
    }

    public static boolean isHYBRID(InstallParam param) {
        return param.pentahoServerType.equals(PentahoServerParam.SERVER.HYBRID);
    }

    public static String getTomcatContextFilePath(InstallParam installParam) throws Exception {
        String tomcatDir = installParam.installDir + "/server/" + PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType) + "/" + installParam.appServerDir;
        String appName = InstallUtil.isBA(installParam) ? "pentaho" : "pentaho-di";
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
        String jackrabbitDir = installParam.installDir + "/server/" + PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType) + "/pentaho-solutions/system/jackrabbit" ;
        jackrabbitDir = jackrabbitDir.replace('/', File.separatorChar);
        return jackrabbitDir + File.separator + "repository.xml";
    }

    public static String getServerRootDir(InstallParam installParam) throws Exception {
	    return installParam.installDir + "/server/" + PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType);
    }
}
