package com.pentaho.install;

import com.pentaho.install.db.Dialect;

import java.util.LinkedHashMap;
import java.util.Map;

public class DBParam {
    public static String DEFAULT_ORACLE_SID = "XE";
    public static String DEFAULT_ORACLE_TABLESPACE = "pentaho_tablespace";
    public static String DEFAULT_PASSWORD = "password";

    public enum DB {
        Mysql("MYSQL", "MySQL", "mysql5"),
        Psql("PSQL", "PostgreSQL", "postgresql"),
        Orcl("ORCL", "Oracle", "oracle10g"),
        Sqlserver("MSSQL", "Microsoft SQL Server", "sqlserver");

        public String name;
        public String longName;
        public String code;

        DB(String n, String ln, String c) {
            this.name = n;
            this.longName = ln;
            this.code = c;
        }

        public static DB getDB(String code) {
            for (DB db : values()) {
                if (db.code.equals(code)) {
                    return db;
                }
            }
            throw new IllegalArgumentException();
        }

        public String toString() {
            return this.longName;
        }
    }

    public static String POSTGRESQL_CONNECT_COMMAND_L = "\\connect";
    public static String POSTGRESQL_CONNECT_COMMAND_S = "\\c";
    public static String ORACLE_CONNECT_COMMAND = "conn";

    public static String RESOURCE_NAME_HIBERNATE = "jdbc/Hibernate";
    public static String RESOURCE_NAME_AUDIT = "jdbc/Audit";
    public static String RESOURCE_NAME_QUARTZ = "jdbc/Quartz";
    public static String RESOURCE_NAME_PENTAHO_OPERATIONS_MART = "jdbc/pentaho_operations_mart";
    //public static String RESOURCE_NAME_PDI_OPERATIONS_MART = "jdbc/PDI_Operations_Mart";

    public final static String DB_NAME_HIBERNATE = "hibernate";
    public final static String DB_NAME_JACKRABBIT = "jackrabbit";
    public final static String DB_NAME_QUARTZ = "quartz";
    public final static String DB_NAME_PENT_OP_MART = "pentaho_operations_mart";
    //public static String DB_NAME_PDI_OP_MART = "pdi_operations_mart";

    public final static String DB_NAME_HIBERNATE_DI = "di_hibernate";
    public final static String DB_NAME_JACKRABBIT_DI = "di_jackrabbit";
    public final static String DB_NAME_QUARTZ_DI = "di_quartz";

    public static Map<String, DBInstance> initDbInstances(DB dbType) {
        Map<String, DBInstance> dbInstanceMap = new LinkedHashMap<>();
        dbInstanceMap.put(DBParam.DB_NAME_HIBERNATE, new DBInstance(DBParam.DB_NAME_HIBERNATE, dbType));
        dbInstanceMap.put(DBParam.DB_NAME_JACKRABBIT, new DBInstance(DBParam.DB_NAME_JACKRABBIT, dbType));
        dbInstanceMap.put(DBParam.DB_NAME_QUARTZ, new DBInstance(DBParam.DB_NAME_QUARTZ, dbType));
        dbInstanceMap.put(DBParam.DB_NAME_PENT_OP_MART, new DBInstance(DBParam.DB_NAME_PENT_OP_MART, dbType));
        //dbInstanceMap.put(DBParam.DB_NAME_PDI_OP_MART, new DBInstance(DBParam.DB_NAME_PDI_OP_MART, dbType));

        return dbInstanceMap;
    }

    protected String adminUser, adminPassword;
    protected DB type;
    protected String jdbcPrefix;
    protected String host = "localhost";
    protected String port = "";
    protected String oracleSid = DEFAULT_ORACLE_SID;
    protected String oracleTablespace = DEFAULT_ORACLE_TABLESPACE;

    Dialect dialect;

    public DBParam() {
    }

    public DB getType() {
        return type;
    }

    public void setType(DB type) {
        this.type = type;
        this.dialect = InstallUtil.createDialect(this.type);

        this.jdbcPrefix = dialect.getJdbcPrefix();
        this.port = dialect.getDefaultPort();
    }

    public String getJdbcPrefix() {
        return jdbcPrefix;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getOracleSid() {
        return oracleSid;
    }

    public void setOracleSid(String oracleSid) {
        this.oracleSid = oracleSid;
    }

    public String getOracleTablespace() {
        return oracleTablespace;
    }

    public void setOracleTablespace(String oracleTablespace) {
        this.oracleTablespace = oracleTablespace;
    }

    public String toString() {
        return "Type: " + this.type + ", host: " + this.host + ", port: " + this.port;
    }
}
