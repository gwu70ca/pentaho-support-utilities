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

    public DBInstance(String name, String username, String password) {
        this.name = this.defaultName = name;
        this.username = this.defaultUsername = username;
        this.password = this.defaultPassword = password;
    }

    /*public DBInstance(String name, String username, String password, String adminUser, String adminPassword, DBParam.DB type, boolean custom) {
        this.name = this.defaultName = name;
        this.username = this.defaultUsername = username;
        this.password = this.defaultPassword = password;

        this.setAdminUser(adminUser);
        this.setAdminPassword(adminPassword);
        this.setType(type);
        this.customed = custom;
    }*/
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

    public String toString() {
        return super.toString() + "\nDB name: " + this.name + ", username: " + username + ", password: " + password + ", is customized: " + this.customed + "\n"
                + "DB script file: " + this.dbFileName;
    }
}
