package com.pentaho.install.post.jackrabbit;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "FileSystem")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersistenceManager {
    @XmlAttribute(name = "class")
    private String clazz = "org.apache.jackrabbit.core.fs.db.DbFileSystem";

    @XmlElement(name = "param")
    private List<Param> paramList;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public List<Param> getParamList() {
        return paramList;
    }

    public void setParamList(List<Param> paramList) {
        this.paramList = paramList;
    }

    /*
    private Param url;
    private Param driver;
    private Param user;
    private Param password;
    private Param schema;
    private Param schemaObjectPrefix;
    private Param tablespace;

    @XmlAttribute(name = "class")
    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @XmlElement(name = "param")
    public Param getDriver() {
        return driver;
    }

    public void setDriver(Param driver) {
        this.driver = driver;
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

    @XmlElement(name = "param")
    public Param getTablespace() {
        return tablespace;
    }

    public void setTablespace(Param tablespace) {
        this.tablespace = tablespace;
    }
    */
}
