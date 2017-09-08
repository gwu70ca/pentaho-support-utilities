package com.pentaho.install.post.jackrabbit;

import com.pentaho.install.*;
import com.pentaho.install.db.Dialect;
import com.pentaho.install.file.ConfigFileHandler;
import com.pentaho.install.file.TestConfigFileHandler;
import com.pentaho.install.post.PentahoXMLConfig;
import com.pentaho.install.post.XMLGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JackrabbitXMLGenerator extends XMLGenerator {
    private InstallParam installParam;
    private Scanner scanner;

    public JackrabbitXMLGenerator(InstallParam installParam, Scanner scanner) {
        this.installParam = installParam;
        this.scanner = scanner;
    }

    public PentahoXMLConfig createRepository() {
        DBInstance jackrabbitInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_JACKRABBIT);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            jackrabbitInstance.setName(DBParam.DB_NAME_JACKRABBIT_DI);
        }
        Dialect dialect = InstallUtil.createDialect(jackrabbitInstance);

        boolean isOrcl = InstallUtil.isOrcl(jackrabbitInstance);

        Repository repository = new Repository(installParam.pentahoServerType);

        Param driver = new Param("driver", dialect.getJdbcDriverClass());
        Param url = new Param("url", dialect.getJdbcUrl(jackrabbitInstance, false));
        Param user = new Param("user", jackrabbitInstance.getUsername());
        Param password = new Param("password", jackrabbitInstance.getPassword());
        Param schema = new Param("schema", dialect.getSchema());
        Param schemaObjectPrefix = new Param("schemaObjectPrefix", "fs_repos_");
        Param tablespace = new Param("tablespace", "pentaho_tablespace");
        Param databaseType = new Param("databaseType", dialect.getDatabaseType());

        String fileSystemClass = dialect.getFileSystemClass();
        String persistenceManagerClass = dialect.getPersistenceManagerClass();

        createFileSystem(repository.getFileSystem(), fileSystemClass, url, driver, user, password, schema, schemaObjectPrefix, tablespace, isOrcl);
        createDataStore(repository.getDataStore(), url, driver, user, password, databaseType);
        createWorkspace(repository.getWorkspace(), fileSystemClass, persistenceManagerClass, url, driver, user, password, schema, tablespace, isOrcl);
        createVersioning(repository.getVersioning(), fileSystemClass, persistenceManagerClass, url, driver, user, password, schema, tablespace, isOrcl);
        createCluster(repository.getCluster(), dialect, url, driver, user, password, schema);

        return repository;
    }

    private void createCluster(Cluster cluster, Dialect dialect, Param url, Param driver, Param user, Param password, Param schema) {
        Journal journal = cluster.getJournal();

        if (InstallUtil.isBA(installParam.pentahoServerType)) {
            //MemoryJournal is default, no need to set
            //journal.setClazz(dialect.getJournalClass(installParam.pentahoServerType));
        } else if (InstallUtil.isDI(installParam.pentahoServerType)) {
            journal.setClazz(dialect.getJournalClass(installParam.pentahoServerType));

            List<Param> paramList = new ArrayList<>();

            paramList.add(new Param("revision", dialect.getRevision()));
            paramList.add(url);
            paramList.add(driver);
            paramList.add(user);
            paramList.add(password);
            paramList.add(schema);
            paramList.add(new Param("schemaObjectPrefix", dialect.getSchemaObjectPrefix()));

            journal.setParamList(paramList);
        } else if (InstallUtil.isHYBRID(installParam.pentahoServerType)) {
            journal.setClazz(dialect.getJournalClass(installParam.pentahoServerType));

            List<Param> paramList = new ArrayList<>();

            paramList.add(new Param("revision", dialect.getRevision()));
            paramList.add(url);
            paramList.add(driver);
            paramList.add(user);
            paramList.add(password);
            paramList.add(schema);
            paramList.add(new Param("schemaObjectPrefix", dialect.getSchemaObjectPrefix()));

            paramList.add(new Param("janitorEnabled", "true"));
            paramList.add(new Param("janitorSleep", "86400"));
            paramList.add(new Param("janitorFirstRunHourOfDay", "3"));

            journal.setParamList(paramList);
        }
    }

    private void createVersioning(Versioning versioning, String fileSystemClass, String persistenceManageClass, Param url, Param driver, Param user, Param password, Param schema, Param tablespace, boolean isOrcl) {
        Param wsSchemaObjectPrefix = new Param("schemaObjectPrefix", "fs_ver_");
        FileSystem fileSystem = versioning.getFileSystem();
        createFileSystem(fileSystem, fileSystemClass, url, driver, user, password, schema, wsSchemaObjectPrefix, tablespace, isOrcl);

        Param pmSchemaObjectPrefix = new Param("schemaObjectPrefix", "pm_ver_");
        PersistenceManager persistenceManager = versioning.getPrsistenceManager();
        createPersistenceManager(persistenceManager, persistenceManageClass, url, driver, user, password, schema, pmSchemaObjectPrefix, tablespace, isOrcl);
    }

    private void createWorkspace(Workspace workspace, String fileSystemClass, String persistenceManagerClass, Param url, Param driver, Param user, Param password, Param schema, Param tablespace, boolean isOrcl) {
        Param wsSchemaObjectPrefix = new Param("schemaObjectPrefix", "fs_ws_");
        FileSystem fileSystem = workspace.getFileSystem();
        createFileSystem(fileSystem, fileSystemClass, url, driver, user, password, schema, wsSchemaObjectPrefix, tablespace, isOrcl);

        Param pmSchemaObjectPrefix = new Param("schemaObjectPrefix", "${wsp.name}_pm_ws_");
        PersistenceManager persistenceManager = workspace.getPersistenceManager();
        createPersistenceManager(persistenceManager, persistenceManagerClass, url, driver, user, password, schema, pmSchemaObjectPrefix, tablespace, isOrcl);
    }

    private void createPersistenceManager(PersistenceManager persistenceManager, String persistenceManagerClass, Param url, Param driver, Param user, Param password, Param schema, Param schemaObjectPrefix, Param tablespace, boolean isOrcl) {
        persistenceManager.setClazz(persistenceManagerClass);
        List<Param> paramList = new ArrayList<>();

        paramList.add(driver);
        paramList.add(url);
        paramList.add(user);
        paramList.add(password);
        paramList.add(schema);
        paramList.add(schemaObjectPrefix);
        if (isOrcl) {
            paramList.add(tablespace);
        }

        persistenceManager.setParamList(paramList);
    }

    private void createDataStore(DataStore dataStore, Param url, Param driver, Param user, Param password, Param databaseType) {
        List<Param> paramList = new ArrayList<>();

        paramList.add(url);
        paramList.add(driver);
        paramList.add(user);
        paramList.add(password);
        paramList.add(databaseType);
        paramList.add(new Param("minRecordLength", "1024"));
        paramList.add(new Param("maxConnections", "3"));
        paramList.add(new Param("copyWhenReading", "true"));
        paramList.add(new Param("tablePrefix", ""));
        paramList.add(new Param("schemaObjectPrefix", "ds_repos_"));

        dataStore.setParamList(paramList);
    }

    public void createFileSystem(FileSystem fileSystem, String fileSystemClass, Param url, Param driver, Param user, Param password, Param schema, Param schemaObjectPrefix, Param tablespace, boolean isOrcl) {
        fileSystem.setClazz(fileSystemClass);

        List<Param> paramList = new ArrayList<>();
        if (!isOrcl) {
            paramList.add(driver);
        }
        paramList.add(url);
        paramList.add(user);
        paramList.add(password);
        if (!isOrcl) {
            paramList.add(schema);
        }
        paramList.add(schemaObjectPrefix);
        if (isOrcl) {
            paramList.add(tablespace);
        }

        fileSystem.setParamList(paramList);
    }

    public boolean createJackrabbitConfig(ConfigFileHandler configFileHandler) {
        boolean success = false;
        try {
            PentahoXMLConfig repository = createRepository();

            success = configFileHandler.handleJackrabbitRepositiryFile(repository, installParam, scanner);
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }

        return success;
    }

    public static void main(String[] args) throws Exception {
        InstallParam installParam = new InstallParam();
        installParam.pentahoServerType = PentahoServerParam.SERVER.BA;
        installParam.dbType = DBParam.DB.Psql;
        installParam.dbInstanceMap = DBParam.initDbInstances(installParam.dbType);

        JackrabbitXMLGenerator g = new JackrabbitXMLGenerator(installParam, new Scanner(System.in));
        g.createJackrabbitConfig(new TestConfigFileHandler());
    }
}
