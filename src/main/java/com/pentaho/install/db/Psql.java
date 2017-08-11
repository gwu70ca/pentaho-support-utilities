package com.pentaho.install.db;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam;
import com.pentaho.install.input.DBNameInput;

import java.util.Map;
import java.util.Scanner;

public class Psql implements Dialect {
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

    public boolean isConnect(String sql) {
        return sql.startsWith(DBParam.POSTGRESQL_CONNECT_COMMAND_L);
    }

    public String getFileSystemClass() {
        return "org.apache.jackrabbit.core.fs.db.DbFileSystem";
    }

    public String getPersistenceManagerClass() {
        return "org.apache.jackrabbit.core.persistence.bundle.PsqlPersistenceManager";
    }

    public String getSchema() {
        return "postgresql";
    }

    public String getDatabaseType() {
        return "postgresql";
    }

    public String getJournalClass(PentahoServerParam.SERVER serverType) {
        if (InstallUtil.isBA(serverType)) {
            return "org.apache.jackrabbit.core.journal.MemoryJournal";
        }
        return "org.apache.jackrabbit.core.journal.DatabaseJournal";
    }

    public String getRevision() {
        return "${rep.home}/revision";
    }

    public String getSchemaObjectPrefix() {
        return "cl_j_";
    }

    public String getJdbcDriverClass() {
        return "org.postgresql.Driver";
    }

    public String getJdbcUrl(DBInstance dbInstance, boolean isAdmin) {
        return dbInstance.getJdbcPrefix() + dbInstance.getHost() + ":" + dbInstance.getPort() + "/"
                + (isAdmin ? "" : dbInstance.getName());
    }
}
