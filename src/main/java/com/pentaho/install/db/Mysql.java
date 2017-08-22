package com.pentaho.install.db;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam;
import com.pentaho.install.input.DBNameInput;

import java.util.Map;
import java.util.Scanner;

public class Mysql implements Dialect {
    //TODO ask if enable remote access for mysql
    private boolean enableMysqlRemoteAccess = true;

    public String promptDbName(String dbName, DBInstance dbInstance, Scanner scanner) {
        DBNameInput dbNameInput = new DBNameInput(String.format("Input database name [%s]: ", dbInstance.getName()), this.getDbNameLength());
        dbNameInput.setDefaultValue(dbInstance.getName());
        InstallUtil.ask(scanner, dbNameInput);

        return dbNameInput.getValue();
    }

    public String polish(String sql, DBInstance instance, Map<String, DBInstance> dbInstanceMap) {
        sql = sql.replace(instance.getDefaultName(), instance.getName())
                .replace(instance.getDefaultUsername(), instance.getUsername())
                .replace(instance.getDefaultPassword(), instance.getPassword());

        return sql.replace("'localhost'", "'" + mySQLHostToGrant() + "'");
    }

    private String mySQLHostToGrant() {
        return enableMysqlRemoteAccess ? "%" : "localhost";
    }

    public String getFileSystemClass() {
        return "org.apache.jackrabbit.core.fs.db.DbFileSystem";
    }

    public String getPersistenceManagerClass() {
        return "org.apache.jackrabbit.core.persistence.bundle.MySqlPersistenceManager";
    }

    public String getSchema() {
        return "mysql";
    }

    public String getDatabaseType() {
        return "mysql";
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
        return "J_C_";
    }

    public String getJdbcPrefix() {
        return "jdbc:mysql://";
    }

    public String getJdbcDriverClass() {
        return "com.mysql.jdbc.Driver";
    }

    public String getJdbcUrl(DBInstance dbInstance, boolean isAdmin) {
        String dbName = dbInstance.getName();
        if (DBParam.DB_NAME_PENT_OP_MART.equals(dbName) || DBParam.DB_NAME_PDI_OP_MART.equals(dbName)) {
            dbName = DBParam.DB_NAME_PENT_OP_MART;
        }

        return dbInstance.getJdbcPrefix() + dbInstance.getHost() + ":" + dbInstance.getPort() + "/"
                + (isAdmin ? "" : dbName);
    }

    public String getDefaultPort() {
        return "3306";
    }

    public String getDefaultAdmin() {
        return "root";
    }

    public String getQuartzDriverDelegateClass() {
        return "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
    }

    public String getHibernateConfigFile() {
        return "mysql5.hibernate.cfg.xml";
    }

    public String getAuditDirName() {
        return "mysql5";
    }

    public String getScriptDirName() {
        return "mysql5";
    }

    public int getDbNameLength() {
        return 64;
    }

    public int getDbUserNameLength() {
        return 16;
    }
}
