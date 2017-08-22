package com.pentaho.install.post;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.Logger;
import com.pentaho.install.db.Dialect;
import com.pentaho.install.input.BooleanInput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class SqlScriptRunner {
    public static boolean DRYRUN = false;

    Scanner scanner;
    File dir;
    Map<String, DBInstance> dbInstanceMap;

    public SqlScriptRunner(Scanner scanner, File dir, Map<String, DBInstance> dbInstanceMap) {
        this.scanner = scanner;
        this.dir = dir;
        this.dbInstanceMap = dbInstanceMap;
    }

    public void execute() {
        //Get first instance
        DBInstance instance = dbInstanceMap.get(dbInstanceMap.keySet().iterator().next());
        Dialect dialect = InstallUtil.createDialect(instance);

        Properties connectionProps = new Properties();
        connectionProps.put("user", instance.getAdminUser());
        connectionProps.put("password", instance.getAdminPassword());

        Connection conn = null;
        Statement stmt = null;
        try {
            System.out.print("Connecting to " + instance.getHost() + "@" + instance.getPort() + " ..... ");

            String url = dialect.getJdbcUrl(instance, true);
            Logger.log(url);

            if (!DRYRUN) {
                conn = DriverManager.getConnection(url, connectionProps);
                InstallUtil.output("\t[connected]\n");
            }

            if (!DRYRUN) {
                stmt = conn.createStatement();
            }
            runScript(stmt, dialect);
        } catch (SQLException sqle) {
            String message = sqle.getMessage();
            if (PostInstaller.DEBUG) {
                InstallUtil.output(message);
            }

            if (message.indexOf("No suitable driver") >= 0) {
                InstallUtil.output("Installer could not find the JDBC driver.");
            } else {
                InstallUtil.output("Could not connect to database.");
            }
            return;
        } finally {
            close(conn);
            close(stmt);
        }
    }

    private void runScript(Statement stmt, Dialect dialect) {
        for (String dbName : dbInstanceMap.keySet()) {
            DBInstance instance = dbInstanceMap.get(dbName);
            String dbFileName = instance.getDbFileName();

            InstallUtil.output("\n--------------------\n");
            InstallUtil.output("Creating database " + instance.getName() + ", " + instance.getUsername());
            InstallUtil.output("Script file: " + dbFileName);
            InstallUtil.output("\n--------------------\n");

            File sqlFile = new File(dir, dbFileName);
            List<String> sqlList = new ArrayList<String>();

            try (BufferedReader in = new BufferedReader(new FileReader(sqlFile))) {
                String line;
                StringBuilder buf = new StringBuilder();
                boolean commentStarted = false;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (commentStarted && line.contains("*/")) {
                        //TODO there might be statement after comment close '*/'
                        commentStarted = false;
                        continue;
                    }

                    if (line.length() == 0 || line.startsWith("--") || line.startsWith("#")) {
                        continue;
                    } else if (line.startsWith("/*")) {
                        commentStarted = true;
                        continue;
                    }

                    String lineLower = line.toLowerCase();
                    if (dialect.isConnect(lineLower)) {
                        //native psql/sqlplus command does not end with ;
                        buf.append(line);
                    } else if (dialect.canBeIgnored(lineLower)) {
                        //anything before this line should be added
                    } else {
                        buf.append(line);
                        if (!dialect.isCompletedSql(line.toLowerCase())) {
                            buf.append("\n");
                            continue;
                        }
                    }

                    if (buf.length() != 0) {
                        String sql = dialect.polish(buf.toString(), instance, dbInstanceMap);
                        sqlList.add(sql);
                        buf.delete(0, buf.length());
                    }
                }

                Connection newConnection = null;
                Statement newStatement = null;
                try {
                    //Run all the SQL statements
                    for (String sql : sqlList) {
                        Logger.log("\tSQL-->" + sql);
                        if (DRYRUN && !Logger.isDebug()) {
                            InstallUtil.output("DRYRUN: " + sql);
                        }

                        try {
                            //psql/sqlplus need special handling,
                            if (newStatement != null) {
                                //new connection to psql/oracle was established
                                if (!DRYRUN) {
                                    newStatement.execute(sql);
                                }
                            } else {
                                if (dialect.isConnect(sql.toLowerCase())) {
                                    //a native psql/sqlplus client command.
                                    Logger.log("A psql/sqlplus native command encountered, create separate JDBC connection");

                                    if (newConnection == null) {
                                        //Initialize connection to new created database
                                        Properties connectionProps = new Properties();
                                        connectionProps.put("user", instance.getUsername());
                                        connectionProps.put("password", instance.getPassword());

                                        try {
                                            String url;
                                            if (DBParam.DB_NAME_PENT_OP_MART.equals(instance.getName())) {
                                                DBInstance hibernate = dbInstanceMap.get(DBParam.DB_NAME_HIBERNATE);
                                                url = dialect.getJdbcUrl(hibernate, false);
                                            } else {
                                                url = dialect.getJdbcUrl(instance, false);
                                            }
                                            Logger.log("Connecting to " + url + ", user: " + instance.getUsername());

                                            if (!DRYRUN) {
                                                newConnection = DriverManager.getConnection(url, connectionProps);
                                                newStatement = newConnection.createStatement();

                                                Logger.log("\t[connected]\n");
                                            }
                                        } catch (SQLException sqle) {
                                            System.out.println("Failed to connect to " + instance.getName() + " with " + instance.getUsername() + " [" + instance.getPassword() + "]");

                                            Logger.log(sqle.toString());
                                        }
                                    }
                                } else {
                                    if (!DRYRUN) {
                                        stmt.execute(sql);
                                    }
                                }
                            }
                        } catch (SQLException sqle) {
                            System.out.println("\n\n==================================================");
                            System.out.println(sql);
                            System.out.println("==================================================");
                            System.out.println(sqle.getMessage());
                            System.out.println("==================================================");
                            System.out.println("Could not execute above line(s)");

                            if (!PostInstaller.SILENT) {
                                BooleanInput askForContinue = new BooleanInput("There is error happened during execution of sql statement, do you want to continue (y/n)? ");
                                InstallUtil.ask(scanner, askForContinue);
                                if (!askForContinue.yes()) {
                                    break;
                                }
                            }
                        }
                    }
                } finally {
                    close(newConnection);
                    close(newStatement);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            InstallUtil.output("\t[created]");
        }
    }

    private void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        /*
        Scanner scanner = new Scanner(System.in);
		File dir = new File("C:\\PENTAHO61\\server\\biserver-ee\\data\\postgresql");
		
		DBInstance hibernate = new DBInstance(DB_NAME_HIBERNATE, "hibuser", "pentaho", "postgres", "postgres", Psql, true);
		hibernate.setName("win_" + DB_NAME_HIBERNATE);
		hibernate.setUsername("win_" + "hibuser");
		hibernate.setPassword("pentaho");
		hibernate.setDbFileName("create_repository_postgresql.sql");
		
		DBInstance jackrabbit = new DBInstance(DB_NAME_JACKRABBIT, "jcr_user", "pentaho", "postgres", "postgres", Psql, true);
		jackrabbit.setName("win_" + DB_NAME_JACKRABBIT);
		jackrabbit.setUsername("win_" + "jcr_user");
		jackrabbit.setPassword("pentaho");
		jackrabbit.setDbFileName("create_jcr_postgresql.sql");
		
		DBInstance quartz = new DBInstance(DB_NAME_QUARTZ, "pentaho_user", "pentaho", "postgres", "postgres", Psql, true);
		quartz.setName("win_" + DB_NAME_QUARTZ);
		quartz.setUsername("win_" + "pentaho_user");
		quartz.setPassword("pentaho");
		quartz.setDbFileName("create_quartz_postgresql.sql");
		
		Map<String, DBInstance> dbInstanceMap = new LinkedHashMap<>();
		dbInstanceMap.put(DB_NAME_HIBERNATE, hibernate);
		dbInstanceMap.put(DB_NAME_JACKRABBIT, jackrabbit);
		dbInstanceMap.put(DB_NAME_QUARTZ, quartz);
		
		BAPostInstaller.DEBUG = true;
		
		SqlScriptRunner ssr = new SqlScriptRunner(scanner, dir, dbInstanceMap);
		ssr.execute();
		*/
    }
}
