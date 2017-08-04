package com.pentaho.install.post.jackrabbit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"url","user","password","databaseType","driver","minRecordLength","maxConnections","copyWhenReading","tablePrefix","schemaObjectPrefix"})
public class DataStore {
    private String clazz = "org.apache.jackrabbit.core.data.db.DbDataStore";

    private Param driver;
    private Param url;
    private Param user;
    private Param password;
    private Param databaseType;
    private Param minRecordLength = new Param("minRecordLength", "1024");
    private Param maxConnections = new Param("maxConnections", "3");
    private Param copyWhenReading = new Param("copyWhenReading", "true");
    private Param tablePrefix = new Param("tablePrefix", "");
    private Param schemaObjectPrefix = new Param("schemaObjectPrefix", "ds_repos_");

    @XmlAttribute(name = "class")
    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @XmlElement(name = "param")
    public Param getUrl() {
        return url;
    }

    public void setUrl(Param url) {
        this.url = url;
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
    public Param getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(Param databaseType) {
        this.databaseType = databaseType;
    }

    @XmlElement(name = "param")
    public Param getDriver() {
        return driver;
    }

    public void setDriver(Param driver) {
        this.driver = driver;
    }

    @XmlElement(name = "param")
    public Param getMinRecordLength() {
        return minRecordLength;
    }

    public void setMinRecordLength(Param minRecordLength) {
        this.minRecordLength = minRecordLength;
    }

    @XmlElement(name = "param")
    public Param getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(Param maxConnections) {
        this.maxConnections = maxConnections;
    }

    @XmlElement(name = "param")
    public Param getCopyWhenReading() {
        return copyWhenReading;
    }

    public void setCopyWhenReading(Param copyWhenReading) {
        this.copyWhenReading = copyWhenReading;
    }

    @XmlElement(name = "param")
    public Param getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(Param tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    @XmlElement(name = "param")
    public Param getSchemaObjectPrefix() {
        return schemaObjectPrefix;
    }

    public void setSchemaObjectPrefix(Param schemaObjectPrefix) {
        this.schemaObjectPrefix = schemaObjectPrefix;
    }
}
