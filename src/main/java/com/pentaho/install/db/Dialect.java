package com.pentaho.install.db;

import com.pentaho.install.DBInstance;
import com.pentaho.install.PentahoServerParam;

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
    String getJdbcDriverClass();
    String getJdbcUrl(DBInstance instance, boolean isAdmin);

    default boolean isConnect(String sql) {
        return false;
    }

    default boolean canBeIgnored(String sql) {
        return false;
    }

    default boolean isCompletedSql(String sql) {
        return sql.endsWith(";");
    }

    default void setDefaultUsername(String dbName, DBInstance dbInstance, PentahoServerParam.SERVER serverType) {
    }
}
