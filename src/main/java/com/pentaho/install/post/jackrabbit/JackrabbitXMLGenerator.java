package com.pentaho.install.post.jackrabbit;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.db.Dialect;
import com.pentaho.install.post.PentahoXMLConfig;
import com.pentaho.install.post.XMLGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class JackrabbitXMLGenerator extends XMLGenerator {
    private InstallParam installParam;
    private Scanner scanner;

    public JackrabbitXMLGenerator(InstallParam installParam, Scanner scanner) {
        this.installParam = installParam;
        this.scanner = scanner;
    }

    private PentahoXMLConfig createRepository() {
        Repository repository = new Repository();

        DBInstance jackrabbitInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_JACKRABBIT);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            jackrabbitInstance.setName(DBParam.DB_NAME_JACKRABBIT_DI);
        }
        Dialect dialect = InstallUtil.createDialect(jackrabbitInstance);

        boolean isOrcl = InstallUtil.isOrcl(jackrabbitInstance);

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

        updateFileSystem(repository.getFileSystem(), fileSystemClass, url, driver, user, password, schema, schemaObjectPrefix, tablespace, isOrcl);
        updateDataStore(repository.getDataStore(), url, driver, user, password, databaseType);
        updateWorkspace(repository.getWorkspace(), fileSystemClass, persistenceManagerClass, url, driver, user, password, schema, tablespace, isOrcl);
        updateVersioning(repository.getVersioning(), fileSystemClass, persistenceManagerClass, url, driver, user, password, schema, tablespace, isOrcl);
        updateCluster(repository.getCluster(), dialect, url, driver, user, password, schema);

        return repository;
    }

    private void updateCluster(Cluster cluster, Dialect dialect, Param url, Param driver, Param user, Param password, Param schema) {
        Journal journal = cluster.getJournal();

        if (InstallUtil.isBA(installParam.pentahoServerType)) {
            return;
        } else if (InstallUtil.isDI(installParam.pentahoServerType) || InstallUtil.isHYBRID(installParam.pentahoServerType)) {
            journal.setClazz(dialect.getJournalClass(installParam.pentahoServerType));
            journal.setRevision(new Param("revision", dialect.getRevision()));
            journal.setUrl(url);
            journal.setDriver(driver);
            journal.setUser(user);
            journal.setPassword(password);
            journal.setSchema(schema);
            journal.setSchemaObjectPrefix(new Param("schemaObjectPrefix", dialect.getSchemaObjectPrefix()));
        } else {
        }
    }

    private void updateVersioning(Versioning versioning, String fileSystemClass, String persistenceManageClass, Param url, Param driver, Param user, Param password, Param schema, Param tablespace, boolean isOrcl) {
        Param wsSchemaObjectPrefix = new Param("schemaObjectPrefix", "fs_ver_");
        FileSystem fileSystem = versioning.getFileSystem();
        updateFileSystem(fileSystem, fileSystemClass, url, driver, user, password, schema, wsSchemaObjectPrefix, tablespace, isOrcl);

        Param pmSchemaObjectPrefix = new Param("schemaObjectPrefix", "pm_ver_");
        PersistenceManager persistenceManager = versioning.getPrsistenceManager();
        updatePersistenceManager(persistenceManager, persistenceManageClass, url, driver, user, password, schema, pmSchemaObjectPrefix, tablespace, isOrcl);
    }

    private void updateWorkspace(Workspace workspace, String fileSystemClass, String persistenceManagerClass, Param url, Param driver, Param user, Param password, Param schema, Param tablespace, boolean isOrcl) {
        Param wsSchemaObjectPrefix = new Param("schemaObjectPrefix", "fs_ws_");
        FileSystem fileSystem = workspace.getFileSystem();
        updateFileSystem(fileSystem, fileSystemClass, url, driver, user, password, schema, wsSchemaObjectPrefix, tablespace, isOrcl);

        Param pmSchemaObjectPrefix = new Param("schemaObjectPrefix", "${wsp.name}_pm_ws_");
        PersistenceManager persistenceManager = workspace.getPersistenceManager();
        updatePersistenceManager(persistenceManager, persistenceManagerClass, url, driver, user, password, schema, pmSchemaObjectPrefix, tablespace, isOrcl);
    }

    private void updatePersistenceManager(PersistenceManager persistenceManager, String persistenceManagerClass, Param url, Param driver, Param user, Param password, Param schema, Param schemaObjectPrefix, Param tablespace, boolean isOrcl) {
        persistenceManager.setClazz(persistenceManagerClass);
        persistenceManager.setDriver(driver);
        persistenceManager.setUrl(url);
        persistenceManager.setUser(user);
        persistenceManager.setPassword(password);
        persistenceManager.setSchema(schema);
        persistenceManager.setSchemaObjectPrefix(schemaObjectPrefix);
        if (isOrcl) {
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

    private void updateFileSystem(FileSystem fileSystem, String fileSystemClass, Param url, Param driver, Param user, Param password, Param schema, Param schemaObjectPrefix, Param tablespace, boolean isOrcl) {
        fileSystem.setClazz(fileSystemClass);

        fileSystem.setUrl(url);
        if (!isOrcl) {
            fileSystem.setDriver(driver);
        }
        fileSystem.setUser(user);
        fileSystem.setPassword(password);
        if (!isOrcl) {
            fileSystem.setSchema(schema);
        }
        fileSystem.setSchemaObjectPrefix(schemaObjectPrefix);
        if (isOrcl) {
            fileSystem.setTablespace(tablespace);
        }
    }

    public boolean createJackrabbitConfig() {
        boolean success = false;
        try {
            PentahoXMLConfig repository = createRepository();
            String repositoryFile = InstallUtil.getJackrabbitRepositoryFilePath(installParam);

            File original = new File(repositoryFile);
            if (InstallUtil.backup(original, scanner)) {
                InstallUtil.output("Updating Jackrabbit repository configuration file");
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repositoryFile), StandardCharsets.UTF_8));
                    if (!createXml(repository, writer)) {
                        return success;
                    }
                } catch (Exception ex) {
                    close(writer);
                }
            }
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }

        return success;
    }
}
