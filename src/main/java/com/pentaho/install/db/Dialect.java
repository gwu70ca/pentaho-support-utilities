package com.pentaho.install.db;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam;
import com.pentaho.install.input.DBUsernameInput;
import com.pentaho.install.input.StringInput;

import java.util.Map;
import java.util.Scanner;

public interface Dialect {
    String promptDbName(String dbName, DBInstance dbInstance, Scanner scanner);
    String polish(String sql, DBInstance dbInstance, Map<String, DBInstance> dbInstanceMap);
    String getFileSystemClass();
    String getPersistenceManagerClass();
    String getSchema();
    String getDatabaseType();
    String getJournalClass(PentahoServerParam.SERVER serverType);
    String getRevision();
    String getSchemaObjectPrefix();
    String getJdbcPrefix();
    String getJdbcDriverClass();
    String getJdbcUrl(DBInstance instance, boolean isAdmin);
    String getQuartzDriverDelegateClass();
    String getHibernateConfigFile();
    String getAuditDirName();
    String getDefaultPort();
    String getDefaultAdmin();
    String getScriptDirName();
    int getDbNameLength();
    int getDbUserNameLength();
    String[] parse(String url);

    default String getValidationQuery() {
        return "select 1";
    }

    default boolean isConnect(String sql) {
        return false;
    }

    default boolean canBeIgnored(String sql) {
        return false;
    }

    default boolean isCompletedSql(String sql) {
        return sql.endsWith(";");
    }

    default void setDefaultUsername(String dbName, DBInstance dbInstance, PentahoServerParam.SERVER serverType) {}

    default String promptDbUsername(String dbName, DBInstance dbInstance, Scanner scanner) {
        DBUsernameInput dbUsernameInput = new DBUsernameInput(String.format("Input username for [%s]: ", dbName), this);
        InstallUtil.ask(scanner, dbUsernameInput);
        return dbUsernameInput.getValue();
    }

    default String promptDbPassword(String dbName, DBInstance dbInstance, Scanner scanner) {
        StringInput dbPasswordInput = new StringInput(String.format("Input password for [%s]: ", dbName));
        InstallUtil.ask(scanner, dbPasswordInput);
        return dbPasswordInput.getValue();
    }

    default String getDefaultJackbbitUsername() {
        return "jcr_user";
    }

    default String getDefaultHibernateUsername() {
        return "hibuser";
    }

    default String getHibernateResourceName() {
        return DBParam.RESOURCE_NAME_HIBERNATE;
    }

    default String getDefaultQuartzUsername() {
        return "pentaho_user";
    }

    default String getQuartzResourceName() {
        return DBParam.RESOURCE_NAME_QUARTZ;
    }

    default String getDefaultPenOpMartUsername() {
        return "hibuser";
    }

    default String getDefaultPenOpMartResourcename() {
        return DBParam.RESOURCE_NAME_PENTAHO_OPERATIONS_MART;
    }

    String HIBERNATE_CFG_XML = "%s.hibernate.cfg.xml";
}
