package com.pentaho.install.db;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam;
import com.pentaho.install.input.DBNameInput;

import java.util.Map;
import java.util.Scanner;

public class Sqlserver implements Dialect {
    static String JTDS_PREFIX = "jdbc:jtds:sqlserver://";

    public String promptDbName(String dbName, DBInstance dbInstance, Scanner scanner) {
        if (!dbName.equals(DBParam.DB_NAME_PENT_OP_MART)) {
            //no need to change pentaho_operations_mart's name
            DBNameInput dbNameInput = new DBNameInput(String.format("Input database name [%s]: ", dbInstance.getName()), this.getDbNameLength());
            dbNameInput.setDefaultValue(dbInstance.getName());
            InstallUtil.ask(scanner, dbNameInput);

            dbName = dbNameInput.getValue();
        }

        return dbName;
    }

    public String polish(String sql, DBInstance instance, Map<String, DBInstance> dbInstanceMap) {
        if (DBParam.DB_NAME_PENT_OP_MART.equals(instance.getDefaultName())) {
            DBInstance hibernate = dbInstanceMap.get(DBParam.DB_NAME_HIBERNATE);
            sql = sql.replace(DBParam.DB_NAME_HIBERNATE, hibernate.getName())
                    .replace(instance.getDefaultUsername(), hibernate.getUsername())
                    .replace(instance.getDefaultPassword(), hibernate.getPassword());
        } else {
            sql = sql.replace(instance.getDefaultName(), instance.getName())
                    .replace(instance.getDefaultUsername(), instance.getUsername())
                    .replace(instance.getDefaultPassword(), instance.getPassword());
        }
        return sql;
    }

    public boolean canBeIgnored(String sql) {
        return sql.equalsIgnoreCase("go");
    }

    public boolean isCompletedSql(String sql) {
        return sql.endsWith(";") || sql.startsWith("create database") || sql.startsWith("drop database");
    }

    public String getFileSystemClass() {
        return "org.apache.jackrabbit.core.fs.db.MSSqlFileSystem";
    }

    public String getPersistenceManagerClass() {
        return "org.apache.jackrabbit.core.persistence.bundle.MSSqlPersistenceManager";
    }

    public String getSchema() {
        return "mssql";
    }

    public String getDatabaseType() {
        return "mssql";
    }

    public String getJournalClass(PentahoServerParam.SERVER serverType) {
        if (InstallUtil.isBA(serverType)) {
            return "org.apache.jackrabbit.core.journal.MemoryJournal";
        }
        return "org.apache.jackrabbit.core.journal.MSSqlDatabaseJournal";
    }

    public String getRevision() {
        return "${rep.home}/revision.log";
    }

    public String getSchemaObjectPrefix() {
        return "cl_j_";
    }

    public String getJdbcPrefix() {
        return "jdbc:sqlserver://";
    }

    public String getJdbcDriverClass() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    public String getJdbcUrl(DBInstance dbInstance, boolean isAdmin) {
        String dbName = dbInstance.getName();
        if (DBParam.DB_NAME_PENT_OP_MART.equals(dbName) || DBParam.DB_NAME_PDI_OP_MART.equals(dbName)) {
            dbName = DBParam.DB_NAME_HIBERNATE;
        }

        String url;
        if (dbInstance.isJtds()) {
            url = JTDS_PREFIX + dbInstance.getHost() + ":" + dbInstance.getPort() + (isAdmin ? "" : ";DatabaseName=" + dbName);
            if (dbInstance.isWinAuth()) {
                url += ";domain=" + dbInstance.getDomain() + ";useNTLMv2=true";
            }
        } else {
            url = dbInstance.getJdbcPrefix() + dbInstance.getHost() + ":" + dbInstance.getPort() + (isAdmin ? "" : ";DatabaseName=" + dbName);
            if (dbInstance.isWinAuth()) {
                url += ";integratedSecurity=true";
            }
        }

        return url;
    }

    public String getDefaultPort() {
        return "1433";
    }

    public String getDefaultAdmin() {
        return "sa";
    }

    public String getQuartzDriverDelegateClass() {
        return "org.quartz.impl.jdbcjobstore.MSSQLDelegate";
    }

    public String getHibernateConfigFile() {
        return "sqlserver.hibernate.cfg.xml";
    }

    public String getAuditDirName() {
        return "sqlserver";
    }

    public String getScriptDirName() {
        return "sqlserver";
    }

    public int getDbNameLength() {
        return 128;
    }

    public int getDbUserNameLength() {
        return 128;
    }
}
