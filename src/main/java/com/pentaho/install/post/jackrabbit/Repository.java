package com.pentaho.install.post.jackrabbit;

import com.pentaho.install.PentahoServerParam;
import com.pentaho.install.post.PentahoXMLConfig;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Repository")
@XmlType(propOrder = {"fileSystem", "dataStore", "security", "workspaces", "workspace", "versioning", "cluster"})
public class Repository implements PentahoXMLConfig {

    private FileSystem fileSystem;
    private DataStore dataStore;
    private Security security;
    private Workspaces workspaces;
    private Workspace workspace;
    private Versioning versioning;
    private Cluster cluster;

    public Repository() {
    }

    public Repository(PentahoServerParam.SERVER pentahoServerType) {
        fileSystem = new FileSystem();
        dataStore = new DataStore();
        security = new Security();
        workspaces = new Workspaces();
        workspace = new Workspace(pentahoServerType);
        versioning = new Versioning();
        cluster = new Cluster();
    }

    @XmlElement(name = "FileSystem")
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @XmlElement(name = "DataStore")
    public DataStore getDataStore() {
        return dataStore;
    }

    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @XmlElement(name = "Security")
    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    @XmlElement(name = "Workspaces")
    public Workspaces getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(Workspaces workspaces) {
        this.workspaces = workspaces;
    }

    @XmlElement(name = "Workspace")
    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @XmlElement(name = "Versioning")
    public Versioning getVersioning() {
        return versioning;
    }

    public void setVersioning(Versioning versioning) {
        this.versioning = versioning;
    }

    @XmlElement(name = "Cluster")
    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
}
