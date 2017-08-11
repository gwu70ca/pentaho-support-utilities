package com.pentaho.install.db;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam;
import com.pentaho.install.input.DBNameInput;

import java.util.Map;
import java.util.Scanner;

public class Sqlserver implements Dialect {
    public String promptDbName(String dbName, DBInstance dbInstance, Scanner scanner) {
        if (!dbName.equals(DBParam.DB_NAME_PENT_OP_MART)) {
            //no need to change pentaho_operations_mart's name
            DBNameInput dbNameInput = new DBNameInput(String.format("Input database name [%s]: ", dbInstance.getName()), dbInstance.getType());
            dbNameInput.setDefaultValue(dbInstance.getName());
            InstallUtil.ask(scanner, dbNameInput);

            dbName = dbNameInput.getValue();
        }

        return dbName;
    }

    public String polish(String sql, DBInstance instance, Map<String, DBInstance> dbInstanceMap) {
        String defaultDbName = instance.getDefaultName().equals(DBParam.DB_NAME_PENT_OP_MART) ? DBParam.DB_NAME_HIBERNATE : instance.getDefaultName();
        sql = sql.replace(defaultDbName, instance.getName())
                .replace(instance.getDefaultUsername(), instance.getUsername())
                .replace(instance.getDefaultPassword(), instance.getPassword());

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

    public String getJdbcDriverClass() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    public String getJdbcUrl(DBInstance dbInstance, boolean isAdmin) {
        String dbName = dbInstance.getName();
        if (DBParam.DB_NAME_PENT_OP_MART.equals(dbName)) {
            dbName = DBParam.DB_NAME_HIBERNATE;
        }
        return dbInstance.getJdbcPrefix() + dbInstance.getHost() + ":" + dbInstance.getPort()
                + (isAdmin ? "" : ";DatabaseName=" + dbName);
    }
}
