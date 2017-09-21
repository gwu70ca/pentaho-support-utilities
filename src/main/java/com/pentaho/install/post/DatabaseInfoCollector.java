package com.pentaho.install.post;

import com.pentaho.install.*;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.PentahoServerParam.SERVER;
import com.pentaho.install.db.Dialect;
import com.pentaho.install.input.BooleanInput;
import com.pentaho.install.input.IntegerInput;
import com.pentaho.install.input.StringInput;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * Collect database parameters and create database if necessary
 *
 * @author gwu
 */
public class DatabaseInfoCollector extends InstallAction {
    static Map<DB, Map<String, String>> dbFileMap;

    private void initSqlScriptFiles() {
        dbFileMap = new HashMap<>();
        Map<String, String> m = new HashMap<>();
        m.put(DBParam.DB_NAME_HIBERNATE, "create_repository_mysql.sql");
        m.put(DBParam.DB_NAME_JACKRABBIT, "create_jcr_mysql.sql");
        m.put(DBParam.DB_NAME_QUARTZ, "create_quartz_mysql.sql");
        m.put(DBParam.DB_NAME_PENT_OP_MART, "pentaho_mart_mysql.sql");
        dbFileMap.put(DB.Mysql, m);

        m = new HashMap<>();
        m.put(DBParam.DB_NAME_HIBERNATE, "create_repository_postgresql.sql");
        m.put(DBParam.DB_NAME_JACKRABBIT, "create_jcr_postgresql.sql");
        m.put(DBParam.DB_NAME_QUARTZ, "create_quartz_postgresql.sql");
        m.put(DBParam.DB_NAME_PENT_OP_MART, "pentaho_mart_postgresql.sql");
        dbFileMap.put(DB.Psql, m);

        m = new HashMap<>();
        m.put(DBParam.DB_NAME_HIBERNATE, "create_repository_ora.sql");
        m.put(DBParam.DB_NAME_JACKRABBIT, "create_jcr_ora.sql");
        m.put(DBParam.DB_NAME_QUARTZ, "create_quartz_ora.sql");
        m.put(DBParam.DB_NAME_PENT_OP_MART, "pentaho_mart_oracle.sql");
        dbFileMap.put(DB.Orcl, m);

        m = new HashMap<>();
        m.put(DBParam.DB_NAME_HIBERNATE, "create_repository_sqlServer.sql");
        m.put(DBParam.DB_NAME_JACKRABBIT, "create_jcr_sqlServer.sql");
        m.put(DBParam.DB_NAME_QUARTZ, "create_quartz_sqlServer.sql");
        m.put(DBParam.DB_NAME_PENT_OP_MART, "pentaho_mart_sqlserver.sql");
        dbFileMap.put(DB.Sqlserver, m);
    }

    private DB dbType;
    private SERVER serverType;
    private Map<String, DBInstance> dbInstanceMap;
    private boolean manualCreateDb = true;

    //Read from CLI
    private Scanner scanner;
    //Read from property file
    private Properties installProp;

    public DatabaseInfoCollector(Properties installProp, Scanner scanner) {
        this.installProp = installProp;
        this.scanner = scanner;
    }

    public void setDbType(DB dbType) {
        this.dbType = dbType;
    }

    public void setServerType(SERVER serverType) {
        this.serverType = serverType;
    }

    public ActionResult execute() {
        dbInstanceMap = DBParam.initDbInstances(dbType);
        initSqlScriptFiles();

        if (PostInstaller.SILENT) {
            readFromFile();
        } else {
            readFromCLI();
        }

        return new DBActionResult(dbInstanceMap, manualCreateDb);
    }

    private void readFromFile() {
        DBParam dbParam = new DBParam();
        dbParam.setType(dbType);

        //Validate database host, port, password
        String dbHost = installProp.getProperty("db_host");
        if (!InstallUtil.isBlankOrNull(dbHost)) {
            dbParam.setHost(dbHost);
        }
        String dbPort = installProp.getProperty("db_port");
        if (!InstallUtil.isBlankOrNull(dbPort)) {
            try {
                Integer.parseInt(dbPort);
            } catch (Exception ex) {
                System.out.println("Invalid database port:" + dbPort);
                System.exit(0);
            }
            dbParam.setPort(dbPort);
        }
        String dbAdminUser = installProp.getProperty("db_admin_user").trim();
        if (!InstallUtil.isBlankOrNull(dbAdminUser)) {
            dbParam.setAdminUser(dbAdminUser);
        }
        String dbAdminPassword = installProp.getProperty("db_admin_password").trim();
        if (!InstallUtil.isBlankOrNull(dbAdminPassword)) {
            dbParam.setAdminPassword(dbAdminPassword);
        }

        manualCreateDb = "false".equals(installProp.get("installer_create_db"));

        String dbNameStr = "db_instance_%s_name";
        String dbUserStr = "db_instance_%s_user";
        String dbPassStr = "db_instance_%s_password";

        Map<String, String> fileMap = dbFileMap.get(dbType);
        for (Map.Entry<String, DBInstance> entry : dbInstanceMap.entrySet()) {
            String dbName = entry.getKey();
            Logger.log("\nDatabase: " + dbName);

            DBInstance dbInstance = entry.getValue();
            Logger.log("\tinstance name: " + dbInstance.getName() + ", user: " + dbInstance.getUsername() + ", pass: " + dbInstance.getPassword());

            String dbFileName = fileMap.get(dbName);
            dbInstance.setDbFileName(dbFileName);

            String name = this.installProp.getProperty(String.format(dbNameStr, dbName));
            String user = this.installProp.getProperty(String.format(dbUserStr, dbName));
            String pass = this.installProp.getProperty(String.format(dbPassStr, dbName));
            Logger.log("\tproperty name: " + name + ", user: " + user + ", pass: " + pass);

            boolean dbSettingChanged = !(name.equals(dbInstance.getName()) &&
                    user.equals(dbInstance.getUsername()) &&
                    pass.equals(dbInstance.getPassword()));

            if (dbSettingChanged) {
                dbInstance.setName(name);
                dbInstance.setUsername(user);
                dbInstance.setPassword(pass);
                dbInstance.setCustomed(true);
            }

            dbInstance.setType(dbParam.getType());
            dbInstance.setHost(dbParam.getHost());
            dbInstance.setPort(dbParam.getPort());
            dbInstance.setAdminUser(dbParam.getAdminUser());
            dbInstance.setAdminPassword(dbParam.getAdminPassword());

            Logger.log(dbInstance.toString());
        }
    }

    private String getInstanceDetail() {
        StringBuilder buf = new StringBuilder("\n\n");
        for (Map.Entry<String, DBInstance> entry : dbInstanceMap.entrySet()) {
            buf.append(entry.getValue().toString());
            buf.append(InstallUtil.shortBar());
        }
        buf.append("\n");
        return buf.toString();
    }

    private void readFromCLI() {
        DBParam dbParam = new DBParam();
        dbParam.setType(dbType);

        Dialect dialect = InstallUtil.createDialect(dbType);

        //Ask for db host
        String defaultHost = dbParam.getHost();
        StringInput dbHostInput = new StringInput(String.format("Database hostname [%s]: ", defaultHost));
        dbHostInput.setDefaultValue(defaultHost);
        InstallUtil.ask(scanner, dbHostInput);
        dbParam.setHost(dbHostInput.getValue());

        //Ask for db port
        String defaultPort = dialect.getDefaultPort();
        IntegerInput dbPortInput = new IntegerInput("Database port [" + defaultPort + "]: ");
        dbPortInput.setDefaultValue(defaultPort);
        InstallUtil.ask(scanner, dbPortInput);
        dbParam.setPort(dbPortInput.getValue());

        if (InstallUtil.isOrcl(dbType)) {
            //Ask for Oracle SID
            StringInput oracleSidInput = new StringInput(String.format("Oracle SID [%s]: ", DBParam.DEFAULT_ORACLE_SID));
            oracleSidInput.setDefaultValue(DBParam.DEFAULT_ORACLE_SID);
            InstallUtil.ask(scanner, oracleSidInput);
            dbParam.setOracleSid(oracleSidInput.getValue());

            StringInput oracleTablespaceInput = new StringInput(String.format("Oracle table space [%s]: ", DBParam.DEFAULT_ORACLE_TABLESPACE));
            oracleTablespaceInput.setDefaultValue(DBParam.DEFAULT_ORACLE_TABLESPACE);
            InstallUtil.ask(scanner, oracleTablespaceInput);
            dbParam.setOracleTablespace(oracleTablespaceInput.getValue());
        }

        InstallUtil.newLine();

        BooleanInput createDbInput = new BooleanInput("Pentaho server requires the following databases to operate: " + dbInstanceMap.keySet() + ". Do you want installer to create them for you [y/n]? ");
        InstallUtil.ask(scanner, createDbInput);

        if (createDbInput.yes()) {
            try {
                String jdbcDriverClass = dialect.getJdbcDriverClass();
                Logger.log("Looking for JDBC driver class: " + jdbcDriverClass);
                Class.forName(jdbcDriverClass);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                InstallUtil.error("Installer could not locate the JDBC driver");
                System.exit(0);
            }
            Logger.log("\t[ok]");

            String defaultAdminUser = dialect.getDefaultAdmin();
            StringInput adminUserInput = new StringInput("Input database admin username [" + defaultAdminUser + "]: ");
            adminUserInput.setDefaultValue(defaultAdminUser);
            InstallUtil.ask(scanner, adminUserInput);
            dbParam.setAdminUser(adminUserInput.getValue());

            StringInput adminPasswordInput = new StringInput("Input database admin password: ");
            InstallUtil.ask(scanner, adminPasswordInput);
            dbParam.setAdminPassword(adminPasswordInput.getValue());

            //TODO test JDBC connection before asking other parameters

            InstallUtil.newLine();
            System.out.println("Installer is going to create database with admin username [" + dbParam.getAdminUser() + "]");
            InstallUtil.newLine();

            manualCreateDb = false;
        } else {
            System.out.println("Please manually create databases and come back to this installer to proceed.");
        }

        do {
            InstallUtil.newLine();
            //If the databases were created with customized name/username/password, here is the chance to tell installer
            BooleanInput customDbInput = new BooleanInput("Do you want to customize database name, username or password [y/n]? ");
            InstallUtil.ask(scanner, customDbInput);
            boolean customDb = customDbInput.yes();

            Map<String, String> fileMap = dbFileMap.get(dbType);
            for (Map.Entry<String, DBInstance> entry : dbInstanceMap.entrySet()) {
                String dbName = entry.getKey();
                Logger.log("\nDatabase: " + dbName);

                DBInstance dbInstance = entry.getValue();
                String dbFileName = fileMap.get(dbName);
                dbInstance.setDbFileName(dbFileName);

                dbInstance.setHost(dbParam.getHost());
                dbInstance.setPort(dbParam.getPort());
                dbInstance.setAdminUser(dbParam.getAdminUser());
                dbInstance.setAdminPassword(dbParam.getAdminPassword());

                if (customDb) {
                    InstallUtil.newLine();

                    //Ask database name
                    dbName = dialect.promptDbName(dbName, dbInstance, scanner);

                    dialect.setDefaultUsername(dbName, dbInstance, serverType);

                    String dbUsername, dbPassword;
                    if (DBParam.DB_NAME_PENT_OP_MART.equals(dbInstance.getDefaultName())/* || DBParam.DB_NAME_PDI_OP_MART.equals(dbInstance.getDefaultName())*/) {
                        //Pentaho operations mart uses same credentials as hibernate
                        DBInstance hibernateInstance = dbInstanceMap.get(DBParam.DB_NAME_HIBERNATE);
                        dbUsername = hibernateInstance.getUsername();
                        dbPassword = hibernateInstance.getPassword();
                        InstallUtil.output("This database uses same username/password as database [hibernate]");
                    } else {
                        dbUsername = dialect.promptDbUsername(dbName, dbInstance, scanner);
                        dbPassword = dialect.promptDbPassword(dbName, dbInstance, scanner);
                    }

                    //TODO merge these two
                    dbInstance.setName(dbName);
                    dbInstance.setSid(dbName);
                    dbInstance.setUsername(dbUsername);
                    dbInstance.setPassword(dbPassword);
                    dbInstance.setCustomed(true);
                }

                Logger.log("");
            }

            if (!manualCreateDb) {
                BooleanInput askForProceed = new BooleanInput("Installer is going to create these database(s):" + getInstanceDetail() + "\nDo you want to continue [y/n]? ");
                InstallUtil.ask(scanner, askForProceed);
                if (askForProceed.yes()) {
                    break;
                }
            } else {
                InstallUtil.newLine();
                InstallUtil.output("Database configurations:");
                InstallUtil.output(getInstanceDetail());

                break;
            }

            //TODO reset parameters
        } while (true);
    }
}
