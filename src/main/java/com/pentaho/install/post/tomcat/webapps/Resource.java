package com.pentaho.install.post.tomcat.webapps;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Resource")
@XmlType(propOrder = {"auth", "driverClassName", "factory", "maxTotal", "maxIdle", "maxWaitMillis", "name", "password", "type", "url", "username", "validationQuery"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Resource {
    @XmlAttribute
    private String auth = "Container";
    @XmlAttribute
    private String driverClassName = "org.postgresql.Driver";
    @XmlAttribute
    private String factory = "org.apache.commons.dbcp.BasicDataSourceFactory";
    @XmlAttribute
    private String maxIdle = "5";
    @XmlAttribute
    private String maxTotal = "20";
    @XmlAttribute
    private String maxWaitMillis = "10000";
    @XmlAttribute
    private String name = "jdbc/Hibernate";
    @XmlAttribute
    private String password = "pentaho";
    @XmlAttribute
    private String type = "javax.sql.DataSource";
    @XmlAttribute
    private String url = "jdbc:postgresql://localhost:5432/hibernate";
    @XmlAttribute
    private String username = "hibuser";
    @XmlAttribute
    private String validationQuery = "select 1";

    public Resource() {
    }

    public Resource(String driverClassName, String name, String password, String url, String username, String validationQuery) {
        this.driverClassName = driverClassName;
        this.name = name;
        this.password = password;
        this.url = url;
        this.username = username;
        this.validationQuery = validationQuery;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(String maxIdle) {
        this.maxIdle = maxIdle;
    }

    public String getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(String maxTotal) {
        this.maxTotal = maxTotal;
    }

    public String getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(String maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public String toString() {
        return "name:" + name + ", driverClassName: " + driverClassName + ", url: " + url;
    }
}
