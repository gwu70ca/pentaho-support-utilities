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
        if (dbName.equals(DBParam.DB_NAME_PENT_OP_MART)/* || dbName.equals(DBParam.DB_NAME_PDI_OP_MART)*/) {
            return dbName;
        }

        //no need to change pentaho_operations_mart's name
        DBNameInput dbNameInput = new DBNameInput(String.format("Input database name [%s]: ", dbInstance.getName()), this.getDbNameLength());
        dbNameInput.setDefaultValue(dbInstance.getName());
        InstallUtil.ask(scanner, dbNameInput);

        return dbNameInput.getValue();
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
        return sql.startsWith(DBParam.POSTGRESQL_CONNECT_COMMAND_L) || sql.startsWith(DBParam.POSTGRESQL_CONNECT_COMMAND_S);
    }

    public String getFileSystemClass() {
        return "org.apache.jackrabbit.core.fs.db.DbFileSystem";
    }

    public String getPersistenceManagerClass() {
        return "org.apache.jackrabbit.core.persistence.bundle.PostgreSQLPersistenceManager";
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

    public String getJdbcPrefix() {
        return "jdbc:postgresql://";
    }

    public String getJdbcDriverClass() {
        return "org.postgresql.Driver";
    }

    public String getJdbcUrl(DBInstance dbInstance, boolean isAdmin) {
        String dbName = dbInstance.getName();
        if (DBParam.DB_NAME_PENT_OP_MART.equals(dbName)/* || DBParam.DB_NAME_PDI_OP_MART.equals(dbName)*/) {
            dbName = DBParam.DB_NAME_HIBERNATE;
        }

        return dbInstance.getJdbcPrefix() + dbInstance.getHost() + ":" + dbInstance.getPort() + "/"
                + (isAdmin ? "" : dbName);
    }

    public String getDefaultPort() {
        return "5432";
    }

    public String getDefaultAdmin() {
        return "postgres";
    }

    public String getQuartzDriverDelegateClass() {
        return "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate";
    }

    public String getHibernateConfigFile() {
        return String.format(HIBERNATE_CFG_XML, DBParam.DB.Psql.code);
    }

    public String getAuditDirName() {
        return DBParam.DB.Psql.code;
    }

    public String getScriptDirName() {
        return DBParam.DB.Psql.code;
    }

    public int getDbNameLength() {
        return 63;
    }

    public int getDbUserNameLength() {
        return 63;
    }

    public String[] parse(String url) {
        return null;
    }
}
