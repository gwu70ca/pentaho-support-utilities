package com.pentaho.install;

public class DBInstance extends DBParam {
    private String dbFileName;
    private String name;
    private String username;
    private String password;
    private String defaultName;
    private String defaultUsername;
    private String defaultPassword;
    private String resourceName;
    private boolean customed = false;
    private boolean winAuth;
    private boolean jtds;
    private String domain;

    public DBInstance(String name, String username, String password) {
        this.name = this.defaultName = name;
        this.username = this.defaultUsername = username;
        this.password = this.defaultPassword = password;
    }

    public DBInstance(String name, String username, String password, DB dbType, String resourceName) {
        this.name = this.defaultName = name;
        this.username = this.defaultUsername = username;
        this.password = this.defaultPassword = password;
        this.type = dbType;
        this.resourceName = resourceName;

        initDbProperty();
    }

    public String getDbFileName() {
        return dbFileName;
    }

    public void setDbFileName(String dbFileName) {
        this.dbFileName = dbFileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getDefaultUsername() {
        return defaultUsername;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public boolean isCustomed() {
        return customed;
    }

    public void setCustomed(boolean customed) {
        this.customed = customed;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public void setDefaultUsername(String defaultUsername) {
        this.defaultUsername = defaultUsername;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public boolean isWinAuth() {
        return winAuth;
    }

    public void setWinAuth(boolean winAuth) {
        this.winAuth = winAuth;
    }

    public boolean isJtds() {
        return jtds;
    }

    public void setJtds(boolean jtds) {
        this.jtds = jtds;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String toString() {
        return super.toString() + "\nResource name: " + resourceName + ", DB name: " + this.name + ", username: " + username + ", password: " + password + ", is customized: " + this.customed + "\n"
                + (this.dbFileName != null ? ("DB script file: " + this.dbFileName):"");
    }
}
