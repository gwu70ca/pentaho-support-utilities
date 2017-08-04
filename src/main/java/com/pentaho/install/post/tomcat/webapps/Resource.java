package com.pentaho.install.post.tomcat.webapps;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Resource")
public class Resource {
    private String auth = "Container";
    private String driverClassName = "org.postgresql.Driver";
    private String factory = "org.apache.commons.dbcp.BasicDataSourceFactory";
    private String maxIdle = "5";
    private String maxTotal = "20";
    private String maxWaitMillis = "10000";
    private String name = "jdbc/Hibernate";
    private String password = "pentaho";
    private String type = "javax.sql.DataSource";
    private String url = "jdbc:postgresql://localhost:5432/hibernate";
    private String username = "hibuser";
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

    @XmlAttribute
    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    @XmlAttribute
    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @XmlAttribute
    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    @XmlAttribute
    public String getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(String maxIdle) {
        this.maxIdle = maxIdle;
    }

    @XmlAttribute
    public String getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(String maxTotal) {
        this.maxTotal = maxTotal;
    }

    @XmlAttribute
    public String getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(String maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlAttribute
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlAttribute
    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }
}
