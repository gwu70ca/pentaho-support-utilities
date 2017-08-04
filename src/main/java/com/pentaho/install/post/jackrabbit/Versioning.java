package com.pentaho.install.post.jackrabbit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Versioning {
    private String rootPath = "${rep.home}/version";
    private FileSystem fileSystem = new FileSystem();
    private PersistenceManager prsistenceManager = new PersistenceManager();

    public String getRootPath() {
        return rootPath;
    }

    @XmlAttribute
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @XmlElement(name = "FileSystem")
    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public PersistenceManager getPrsistenceManager() {
        return prsistenceManager;
    }

    @XmlElement(name = "PersistenceManager")
    public void setPrsistenceManager(PersistenceManager prsistenceManager) {
        this.prsistenceManager = prsistenceManager;
    }
}
