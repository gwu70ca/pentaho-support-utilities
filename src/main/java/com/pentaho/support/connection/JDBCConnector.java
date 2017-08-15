package com.pentaho.support.connection;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.db.Dialect;

import java.sql.*;
import java.util.Properties;

public class JDBCConnector {
    static boolean DEBUG = false;

    public static void usage() {
        System.out.println("=================================================================================");
        System.out.println("options:");
        System.out.println("  -H, --host          Database hostname or IP address");
        System.out.println("  -P, --port          Database port number");
        System.out.println("  -U, --user          Database username");
        System.out.println("  -W, --pass          Database password");
        System.out.println("  -N, --name          Database name");
        System.out.println("  -T, --type          Database vendor [mysql|postgresql|sqlserver|oracle]");
        System.out.println("  -I, --windows       Use Microsoft Windows Integration Authentication");
        System.out.println("");
        System.out.println("examples:");
        System.out.println("  1. Test connection to PostgreSQL database quartz on localhost");
        System.out.println("     -H localhost -P 5432 -U postgres -W postgres -T postgresql -N quartz");
        System.out.println("");
        System.out.println("  2. Test connection to MySQL database on localhost");
        System.out.println("     -H localhost -P 3306 -U root -W password -T mysql");
        System.out.println("");
        System.out.println("  3. Test connection to Microsoft SQL Server database with Windows authentication");
        System.out.println("     --host 10.0.0.1 --port 1433 --user sa --type sqlserver --windows");
        System.out.println("=================================================================================");
    }

    public static void main(String[] args) {
        String dbHost = null, dbPort = null, dbName = null, dbUser = null, dbPass = null, dbType = null;
        boolean winAuth = false;

        if (args.length == 0) {
            usage();
            System.exit(0);
        } else {
            try {
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-H") || args[i].equals("--host")) {
                        dbHost = args[++i];
                    } else if (args[i].equals("-P") || args[i].equals("--port")) {
                        dbPort = args[++i];
                    } else if (args[i].equals("-N") || args[i].equals("--name")) {
                        dbName = args[++i];
                    } else if (args[i].equals("-U") || args[i].equals("--user")) {
                        dbUser = args[++i];
                    } else if (args[i].equals("-W") || args[i].equals("--pass")) {
                        dbPass = args[++i];
                    } else if (args[i].equals("-T") || args[i].equals("--type")) {
                        dbType = args[++i];
                    } else if (args[i].equals("-I") || args[i].equals("--windows")) {
                        winAuth = true;
                    } else if (args[i].equals("-D") || args[i].equals("--debug")) {
                        DEBUG = true;
                    }
                }
            } catch (Exception ex) {
                InstallUtil.output("Invalid CLI parameters");
                InstallUtil.exit();
            }
        }

        DBInstance dbInstance = new DBInstance(dbName, dbUser, dbPass);
        dbInstance.setHost(dbHost);
        dbInstance.setPort(dbPort);

        DBParam.DB databaseType;
        if (dbType == null || (databaseType = guessDbType(dbType)) == null) {
            System.out.println("Unknown database type: " + dbType);
            System.exit(0);
        } else {
            dbInstance.setType(databaseType);
        }

        if (dbPass == null && !winAuth) {
            System.out.println("Dude, we need password");
            System.exit(0);
        }

        if (DEBUG) {
            System.out.println(dbInstance);
        }

        test(dbInstance);
    }

    private static DBParam.DB guessDbType(String typeString) {
        String str = typeString.toLowerCase();
        if (str.contains("mssql") || str.contains("sqlserver") || str.contains("mssqlserver") || str.contains("microsoft")) {
            return DBParam.DB.Sqlserver;
        } else if (str.contains("postgresql") || str.contains("psql")) {
            return DBParam.DB.Psql;
        } else if (str.contains("orcl") || str.contains("oracle")) {
            return DBParam.DB.Orcl;
        } else if (str.contains("mysql")) {
            return DBParam.DB.Mysql;
        }
        return DBParam.DB.valueOf(str);
    }

    public static void test(DBInstance dbInstance) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Dialect dialect = InstallUtil.createDialect(dbInstance);
            String url = dialect.getJdbcUrl(dbInstance, dbInstance.getName() == null || dbInstance.getName().length() == 0);
            Properties connectionProps = new Properties();
            if (dbInstance.isWinAuth() && DBParam.DB.Sqlserver.equals(dbInstance.getType())) {
                url += ";integratedSecurity=true";
            } else {
                connectionProps.put("user", dbInstance.getUsername());
                connectionProps.put("password", dbInstance.getPassword());
            }
            System.out.println(url);

            System.out.println("Connecting to " + dbInstance.getHost() + "@" + dbInstance.getPort() + " ..... ");
            if (!dbInstance.isWinAuth()) {
                conn = DriverManager.getConnection(url, connectionProps);
            } else {
                conn = DriverManager.getConnection(url);
            }
            System.out.println("\t[connected]\n");

            stmt = conn.createStatement();
            String validateSql = DBParam.getValidationQuery(dbInstance.getType());
            System.out.println("Running validation query: " + validateSql + " ..... ");
            rs = stmt.executeQuery(validateSql);

            boolean success = false;
            if (rs.next()) {
                success = (rs.getInt(1) != 0);
            }

            System.out.println("\t" + (success ? "[succeeded]" : "[failed]"));
        } catch (SQLException sqle) {
            String message = sqle.getMessage();
            if (message.indexOf("No suitable driver") >= 0) {
                System.out.println("Installer could not find the JDBC driver.");
            } else {
                System.out.println("Could not connect to database.");
                System.err.println(message);
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ex) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                }
            }
        }
    }
}
