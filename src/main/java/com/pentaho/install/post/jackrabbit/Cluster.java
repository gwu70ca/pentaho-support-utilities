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
@XmlType(propOrder = {"revision", "url", "driver", "user", "password", "schema", "schemaObjectPrefix"})
class Journal {
    private String clazz = "org.apache.jackrabbit.core.journal.MemoryJournal";
    private Param revision;
    private Param url;
    private Param driver;
    private Param user;
    private Param password;
    private Param schema;
    private Param schemaObjectPrefix;

    @XmlAttribute(name = "class")
    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @XmlElement(name = "param")
    public Param getRevision() {
        return revision;
    }

    public void setRevision(Param revision) {
        this.revision = revision;
    }

    @XmlElement(name = "param")
    public Param getUrl() {
        return url;
    }

    public void setUrl(Param url) {
        this.url = url;
    }

    @XmlElement(name = "param")
    public Param getDriver() {
        return driver;
    }

    public void setDriver(Param driver) {
        this.driver = driver;
    }

    @XmlElement(name = "param")
    public Param getUser() {
        return user;
    }

    public void setUser(Param user) {
        this.user = user;
    }

    @XmlElement(name = "param")
    public Param getPassword() {
        return password;
    }

    public void setPassword(Param password) {
        this.password = password;
    }

    @XmlElement(name = "param")
    public Param getSchema() {
        return schema;
    }

    public void setSchema(Param schema) {
        this.schema = schema;
    }

    @XmlElement(name = "param")
    public Param getSchemaObjectPrefix() {
        return schemaObjectPrefix;
    }

    public void setSchemaObjectPrefix(Param schemaObjectPrefix) {
        this.schemaObjectPrefix = schemaObjectPrefix;
    }
}


