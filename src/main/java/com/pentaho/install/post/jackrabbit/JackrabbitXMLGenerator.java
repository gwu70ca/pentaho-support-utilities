package com.pentaho.install.post.jackrabbit;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.post.PentahoXMLConfig;
import com.pentaho.install.post.XMLGenerator;

import java.util.Map;

import static com.pentaho.install.DBParam.DB;
import static com.pentaho.install.PentahoServerParam.SERVER;

public class JackrabbitXMLGenerator extends XMLGenerator {
    private InstallParam installParam;

    public JackrabbitXMLGenerator(InstallParam installParam) {
        this.installParam = installParam;
    }

    private PentahoXMLConfig createRepository() {
        Repository repository = new Repository();

        String jackrabbitDbName = InstallUtil.getJackrabbitDatabaseName(installParam.pentahoServerType);
        DBInstance jackrabbitInstance = installParam.dbInstanceMap.get(jackrabbitDbName);
        jackrabbitInstance.setType(installParam.dbType);

        boolean isOracle = InstallUtil.isOracle(jackrabbitInstance);

        Param driver = new Param("driver", InstallUtil.getJdbcDriverClass(installParam.dbType));
        Param url = new Param("url", InstallUtil.getJdbcUrl(jackrabbitInstance));
        Param user = new Param("user", jackrabbitInstance.getUsername());
        Param password = new Param("password", jackrabbitInstance.getPassword());
        Param schema = new Param("schema", getSchema());
        Param schemaObjectPrefix = new Param("schemaObjectPrefix", "fs_repos_");
        Param tablespace = new Param("tablespace", "pentaho_tablespace");
        Param databaseType = new Param("databaseType", getDatabaseType());

        String fileSystemClass = getFileSystemClass();
        String persistenceManagerClass = getPersistenceManagerClass();

        updateFileSystem(repository.getFileSystem(), fileSystemClass, url, driver, user, password, schema, schemaObjectPrefix, tablespace, isOracle);
        updateDataStore(repository.getDataStore(), url, driver, user, password, databaseType);
        updateWorkspace(repository.getWorkspace(), fileSystemClass, persistenceManagerClass, url, driver, user, password, schema, tablespace, isOracle);
        updateVersioning(repository.getVersioning(), fileSystemClass, persistenceManagerClass, url, driver, user, password, schema, tablespace, isOracle);
        updateCluster(repository.getCluster(), url, driver, user, password, schema);

        return repository;
    }

    private void updateCluster(Cluster cluster, Param url, Param driver, Param user, Param password, Param schema) {
        Journal journal = cluster.getJournal();

        if (InstallUtil.isBA(installParam.pentahoServerType)) {
            return;
        } else if (InstallUtil.isDI(installParam.pentahoServerType) || InstallUtil.isHYBRID(installParam.pentahoServerType)) {
            journal.setClazz(this.getJournalClass());
            journal.setRevision(new Param("revision", getRevision()));
            journal.setUrl(url);
            journal.setDriver(driver);
            journal.setUser(user);
            journal.setPassword(password);
            journal.setSchema(schema);
            journal.setSchemaObjectPrefix(new Param("schemaObjectPrefix", getSchemaObjectPrefix()));
        } else {
        }
    }

    private void updateVersioning(Versioning versioning, String fileSystemClass, String persistenceManageClass, Param url, Param driver, Param user, Param password, Param schema, Param tablespace, boolean isOracle) {
        Param wsSchemaObjectPrefix = new Param("schemaObjectPrefix", "fs_ver_");
        FileSystem fileSystem = versioning.getFileSystem();
        updateFileSystem(fileSystem, fileSystemClass, url, driver, user, password, schema, wsSchemaObjectPrefix, tablespace, isOracle);

        Param pmSchemaObjectPrefix = new Param("schemaObjectPrefix", "pm_ver_");
        PersistenceManager persistenceManager = versioning.getPrsistenceManager();
        updatePersistenceManager(persistenceManager, persistenceManageClass, url, driver, user, password, schema, pmSchemaObjectPrefix, tablespace, isOracle);
    }

    private void updateWorkspace(Workspace workspace, String fileSystemClass, String persistenceManagerClass, Param url, Param driver, Param user, Param password, Param schema, Param tablespace, boolean isOracle) {
        Param wsSchemaObjectPrefix = new Param("schemaObjectPrefix", "fs_ws_");
        FileSystem fileSystem = workspace.getFileSystem();
        updateFileSystem(fileSystem, fileSystemClass, url, driver, user, password, schema, wsSchemaObjectPrefix, tablespace, isOracle);

        Param pmSchemaObjectPrefix = new Param("schemaObjectPrefix", "${wsp.name}_pm_ws_");
        PersistenceManager persistenceManager = workspace.getPersistenceManager();
        updatePersistenceManager(persistenceManager, persistenceManagerClass, url, driver, user, password, schema, pmSchemaObjectPrefix, tablespace, isOracle);
    }

    private void updatePersistenceManager(PersistenceManager persistenceManager, String persistenceManagerClass, Param url, Param driver, Param user, Param password, Param schema, Param schemaObjectPrefix, Param tablespace, boolean isOracle) {
        persistenceManager.setClazz(persistenceManagerClass);
        persistenceManager.setDriver(driver);
        persistenceManager.setUrl(url);
        persistenceManager.setUser(user);
        persistenceManager.setPassword(password);
        persistenceManager.setSchema(schema);
        persistenceManager.setSchemaObjectPrefix(schemaObjectPrefix);
        if (isOracle) {
            persistenceManager.setTablespace(tablespace);
        }
    }

    private void updateDataStore(DataStore dataStore, Param url, Param driver, Param user, Param password, Param databaseType) {
        dataStore.setUrl(url);
        dataStore.setDriver(driver);
        dataStore.setUser(user);
        dataStore.setPassword(password);
        dataStore.setDatabaseType(databaseType);
    }

    private void updateFileSystem(FileSystem fileSystem, String fileSystemClass, Param url, Param driver, Param user, Param password, Param schema, Param schemaObjectPrefix, Param tablespace, boolean isOracle) {
        fileSystem.setClazz(fileSystemClass);

        fileSystem.setUrl(url);
        if (!isOracle) {
            fileSystem.setDriver(driver);
        }
        fileSystem.setUser(user);
        fileSystem.setPassword(password);
        if (!isOracle) {
            fileSystem.setSchema(schema);
        }
        fileSystem.setSchemaObjectPrefix(schemaObjectPrefix);
        if (isOracle) {
            fileSystem.setTablespace(tablespace);
        }
    }

    public boolean createJackrabbitConfig() {
        boolean success = false;
        try {
            PentahoXMLConfig repository = createRepository();
            String repositoryFile = InstallUtil.getJackrabbitRepositoryFilePath(installParam);
            success = createXml(repository, repositoryFile, "Updating Jackrabbit repository configuration file: ");
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }

        return success;
    }

    private String getFileSystemClass() {
        DB dbType = installParam.dbType;

        String fileSystemClass = "org.apache.jackrabbit.core.fs.db.DbFileSystem";
        if (dbType == DB.MSSQLServer) {
            fileSystemClass = "org.apache.jackrabbit.core.fs.db.MSSqlFileSystem";
        } else if (dbType == DB.Oracle) {
            fileSystemClass = "org.apache.jackrabbit.core.fs.db.OracleFileSystem";
        } else if (dbType == DB.PostgreSQL) {
        }

        return fileSystemClass;
    }

    private String getPersistenceManagerClass() {
        DB dbType = installParam.dbType;

        String className = "org.apache.jackrabbit.core.persistence.bundle.PostgreSQLPersistenceManager";
        if (dbType == DB.MySQL) {
            className = "org.apache.jackrabbit.core.persistence.bundle.MySqlPersistenceManager";
        } else if (dbType == DB.MSSQLServer) {
            className = "org.apache.jackrabbit.core.persistence.bundle.MSSqlPersistenceManager";
        } else if (dbType == DB.Oracle) {
            className = "org.apache.jackrabbit.core.persistence.bundle.OraclePersistenceManager";
        } else if (dbType == DB.PostgreSQL) {
        }
        return className;
    }

    private String getSchema() {
        DB dbType = installParam.dbType;

        String schema = "postgresql";

        if (dbType == DB.MySQL) {
            schema = "mysql";
        } else if (dbType == DB.MSSQLServer) {
            schema = "mssql";
        } else if (dbType == DB.Oracle) {
            schema = "oracle";
        } else if (dbType == DB.PostgreSQL) {
        }

        return schema;
    }

    private String getDatabaseType() {
        DB dbType = installParam.dbType;

        String value = "postgresql";
        if (dbType == DB.MySQL) {
            value = "mysql";
        } else if (dbType == DB.MSSQLServer) {
            value = "mssql";
        } else if (dbType == DB.Oracle) {
            value = "oracle";
        } else if (dbType == DB.PostgreSQL) {
        }
        return value;
    }

    private String getJournalClass() {
        SERVER serverType = installParam.pentahoServerType;
        DB dbType = installParam.dbType;

        if (InstallUtil.isBA(serverType)) {
            return "org.apache.jackrabbit.core.journal.MemoryJournal";
        }

        if (dbType.equals(DB.MSSQLServer)) {
            return "org.apache.jackrabbit.core.journal.MSSqlDatabaseJournal";
        } else if (dbType.equals(DB.Oracle)) {
            return "org.apache.jackrabbit.core.journal.OracleDatabaseJournal";
        } else {
            return "org.apache.jackrabbit.core.journal.DatabaseJournal";
        }
    }

    private String getRevision() {
        DB dbType = installParam.dbType;
        if (dbType == DB.Oracle || dbType == DB.MSSQLServer) {
            return "${rep.home}/revision.log";
        } else if (dbType == DB.MySQL || dbType == DB.PostgreSQL) {
            return "${rep.home}/revision";
        }

        return "";
    }

    private String getSchemaObjectPrefix() {
        DB dbType = installParam.dbType;
        if (dbType == DB.Oracle || dbType == DB.MySQL) {
            return "J_C_";
        } else if (dbType == DB.MSSQLServer || dbType == DB.PostgreSQL) {
            return "cl_j_";
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("local", "true");

        InstallParam installParam = new InstallParam();
        installParam.dbType = DB.PostgreSQL;
        installParam.pentahoServerType = SERVER.HYBRID;
        Map<String, DBInstance> dbInstanceMap = DBParam.initDbInstances(installParam.pentahoServerType);
        installParam.dbInstanceMap = dbInstanceMap;

        JackrabbitXMLGenerator jxg = new JackrabbitXMLGenerator(installParam);
        jxg.createJackrabbitConfig();
    }
}
