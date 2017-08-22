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
        System.out.println("  -H, --host          Database server hostname or IP address");
        System.out.println("  -P, --port          Database server port number");
        System.out.println("  -U, --user          Database server username");
        System.out.println("  -W, --pass          Database server password");
        System.out.println("  -N, --name          Database name");
        System.out.println("  -T, --type          Database vendor [mysql|postgresql|sqlserver|oracle]");
        System.out.println("  -I, --windows       Use Microsoft Windows Integration Authentication");
        System.out.println("  -J, --jtds          Use jDTS driver for Microsoft SQL server");
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
        boolean winAuth = false, jtds = false;

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
                    } else if (args[i].equals("-J") || args[i].equals("--jtds")) {
                        jtds = true;
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
        dbInstance.setWinAuth(winAuth);
        dbInstance.setJtds(jtds);

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

    public static boolean test(DBInstance dbInstance) {
        boolean success = false;

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Dialect dialect = InstallUtil.createDialect(dbInstance.getType());
            conn = getConnection(dbInstance);

            stmt = conn.createStatement();
            String validateSql = dialect.getValidationQuery();
            System.out.println("Running validation query: " + validateSql + " ..... ");
            rs = stmt.executeQuery(validateSql);

            if (rs.next()) {
                success = (rs.getInt(1) != 0);
            }

            System.out.println("\t" + (success ? "[succeeded]" : "[failed]"));
            System.out.println();
            if (success) {

                try {
                    System.out.println("==================================================");
                    DatabaseMetaData metadata = conn.getMetaData();
                    System.out.println("JDBC Driver And Database Information");
                    System.out.println("Driver Name: " + metadata.getDriverName());
                    System.out.println("Driver Version: " + metadata.getDriverVersion());
                    System.out.println("JDBC Version: " + metadata.getJDBCMajorVersion() + "." + metadata.getJDBCMinorVersion());
                    System.out.println("Database Product Name: " + metadata.getDatabaseProductName());
                    System.out.println("Database Product Version: " + metadata.getDatabaseProductVersion());
                    System.out.println("==================================================");
                    System.out.println();
                } catch (Exception e) {
                    System.err.println("Failed to obtain Database Metadata");
                }
            }
        } catch (SQLException sqle) {
            String message = sqle.getMessage();
            if (message.indexOf("No suitable driver") >= 0) {
                System.out.println("Installer could not find the JDBC driver.");
            } else {
                System.err.println(message);
            }
        } finally {
            close(conn, stmt, rs);
        }

        return success;
    }

    public static boolean executeSql(DBInstance dbInstance, String sql) {
        boolean success = false;

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sqlLower = sql.toLowerCase();
            conn = getConnection(dbInstance);
            stmt = conn.createStatement();

            if (sqlLower.startsWith("select")) {
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Number of records returned: " + count);
                    success = true;
                }
            } else {
                success = stmt.execute(sql);
            }

            System.out.println("\t" + (success ? "[succeeded]" : "[failed]"));
            System.out.println();
        } catch (SQLException sqle) {
            String message = sqle.getMessage();
            if (message.indexOf("No suitable driver") >= 0) {
                System.out.println("Could not find the JDBC driver in classpath.");
            } else {
                System.err.println(message);
            }
        } finally {
            close(conn, stmt, rs);
        }

        return success;
    }

    private static Connection getConnection(DBInstance dbInstance) throws SQLException {
        Connection conn;

        Dialect dialect = InstallUtil.createDialect(dbInstance);
        String url = dialect.getJdbcUrl(dbInstance, dbInstance.getName() == null || dbInstance.getName().length() == 0);
        System.out.println("jdbc url: " + url);

        boolean requiredUsernameAndPassword = !dbInstance.isWinAuth() || dbInstance.isJtds() && !InstallUtil.isWindows();

        Properties connectionProps = new Properties();
        if (requiredUsernameAndPassword) {
            connectionProps.put("user", dbInstance.getUsername());
            connectionProps.put("password", dbInstance.getPassword());
        }

        System.out.println("Connecting to " + dbInstance.getHost() + "@" + dbInstance.getPort() + " ..... ");
        if (requiredUsernameAndPassword) {
            conn = DriverManager.getConnection(url, connectionProps);
        } else {
            conn = DriverManager.getConnection(url);
        }
        System.out.println("\t[connected]\n");

        return conn;
    }

    private static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
