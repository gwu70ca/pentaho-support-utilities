package com.pentaho.install.db;

import com.pentaho.install.DBInstance;

public class Jackrabbit implements PentahoDB {
    private Dialect dialect;

    public Jackrabbit(Dialect dialect) {
        this.dialect = dialect;
    }

    public void setDefaultDbProperties(DBInstance instance) {
        String jackrabbitUsername = dialect.getDefaultJackbbitUsername();
        instance.setUsername(jackrabbitUsername);
        instance.setDefaultUsername(jackrabbitUsername);

        //Jackrabbit doesn't need resource name
    }
}
