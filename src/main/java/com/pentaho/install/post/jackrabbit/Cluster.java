package com.pentaho.install.post.jackrabbit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
public class Cluster {
    private String id = "node1";
    private Journal journal = new Journal();

    public String getId() {
        return id;
    }

    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }

    public Journal getJournal() {
        return journal;
    }

    @XmlElement(name = "Journal")
    public void setJournal(Journal journal) {
        this.journal = journal;
    }
}

@XmlRootElement
@XmlType(propOrder = {"revision","url","driver","user","password","schema","schemaObjectPrefix"})
class Journal {
    private String clazz = "org.apache.jackrabbit.core.journal.MemoryJournal";
    private Param revision;
    private Param url;
    private Param driver;
    private Param user;
    private Param password;
    private Param schema;
    private Param schemaObjectPrefix;

    public String getClazz() {
        return clazz;
    }

    @XmlAttribute(name="class")
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Param getRevision() {
        return revision;
    }

    @XmlElement
    public void setRevision(Param revision) {
        this.revision = revision;
    }

    public Param getUrl() {
        return url;
    }

    @XmlElement
    public void setUrl(Param url) {
        this.url = url;
    }

    public Param getDriver() {
        return driver;
    }

    @XmlElement
    public void setDriver(Param driver) {
        this.driver = driver;
    }

    public Param getUser() {
        return user;
    }

    @XmlElement
    public void setUser(Param user) {
        this.user = user;
    }

    public Param getPassword() {
        return password;
    }

    @XmlElement
    public void setPassword(Param password) {
        this.password = password;
    }

    public Param getSchema() {
        return schema;
    }

    @XmlElement
    public void setSchema(Param schema) {
        this.schema = schema;
    }

    public Param getSchemaObjectPrefix() {
        return schemaObjectPrefix;
    }

    @XmlElement
    public void setSchemaObjectPrefix(Param schemaObjectPrefix) {
        this.schemaObjectPrefix = schemaObjectPrefix;
    }
}


