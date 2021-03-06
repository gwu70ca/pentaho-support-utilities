package com.pentaho.install.db;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam;
import com.pentaho.install.input.DBUsernameInput;
import com.pentaho.install.input.StringInput;

import java.util.Map;
import java.util.Scanner;

public class Orcl implements Dialect {
    public String promptDbName(String dbName, DBInstance dbInstance, Scanner scanner) {
        return dbName;
    }

    public String promptDbUsername(String dbName, DBInstance dbInstance, Scanner scanner) {
        DBUsernameInput dbUsernameInput = new DBUsernameInput(String.format("Input username for [%s]: ", dbInstance.getName()), this);
        InstallUtil.ask(scanner, dbUsernameInput);
        return dbUsernameInput.getValue();
    }

    public String promptDbPassword(String dbName, DBInstance dbInstance, Scanner scanner) {
        StringInput dbPasswordInput = new StringInput(String.format("Input password for [%s]: ", dbInstance.getName()));
        InstallUtil.ask(scanner, dbPasswordInput);
        return dbPasswordInput.getValue();
    }

    public String polish(String sql, DBInstance instance, Map<String, DBInstance> dbInstanceMap) {
        sql = sql
                .replace(instance.getDefaultUsername(), instance.getUsername())
                .replace(instance.getDefaultPassword(), instance.getPassword());
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        return sql;
    }

    public boolean isConnect(String sql) {
        return sql.startsWith(DBParam.ORACLE_CONNECT_COMMAND);
    }

    public boolean canBeIgnored(String sql) {
        return sql.startsWith("set");
    }

    public void setDefaultUsername(String dbName, DBInstance dbInstance, PentahoServerParam.SERVER serverType) {
        if (InstallUtil.isDI(serverType) && DBParam.DB_NAME_QUARTZ.equals(dbName)) {
            dbInstance.setDefaultUsername("di_quartz");
        }
    }

    public String getFileSystemClass() {
        return "org.apache.jackrabbit.core.fs.db.OracleFileSystem";
    }

    public String getPersistenceManagerClass() {
        return "org.apache.jackrabbit.core.persistence.bundle.OraclePersistenceManager";
    }

    public String getSchema() {
        return "oracle";
    }

    public String getDatabaseType() {
        return "oracle";
    }

    public String getJournalClass(PentahoServerParam.SERVER serverType) {
        if (InstallUtil.isBA(serverType)) {
            return "org.apache.jackrabbit.core.journal.MemoryJournal";
        }
        return "org.apache.jackrabbit.core.journal.OracleDatabaseJournal";
    }

    public String getRevision() {
        return "${rep.home}/revision.log";
    }

    public String getSchemaObjectPrefix() {
        return "J_C_";
    }

    public String getJdbcPrefix() {
        return "jdbc:oracle:thin:@";
    }

    public String getJdbcDriverClass() {
        return "oracle.jdbc.OracleDriver";
    }

    public String getJdbcUrl(DBInstance dbInstance, boolean isAdmin) {
        return dbInstance.getJdbcPrefix() + dbInstance.getHost() + ":" + dbInstance.getPort() + "/" + dbInstance.getSid();
    }

    public String getDefaultPort() {
        return "1521";
    }

    public String getDefaultAdmin() {
        return "system";
    }

    public String getQuartzDriverDelegateClass() {
        return "org.quartz.impl.jdbcjobstore.oracle.OracleDelegate";
    }

    public String getHibernateConfigFile() {
        return String.format(HIBERNATE_CFG_XML, DBParam.DB.Orcl.code);
    }

    public String getAuditDirName() {
        return DBParam.DB.Orcl.code;
    }

    public String getValidationQuery() {
        return "select 1 from dual";
    }

    public String getScriptDirName() {
        return DBParam.DB.Orcl.code;
    }

    public int getDbNameLength() {
        return 8;
    }

    public int getDbUserNameLength() {
        return 30;
    }

    public String[] parse(String url) {
        return null;
    }

    public String getDefaultQuartzUsername() {
        return "quartz";
    }
}
