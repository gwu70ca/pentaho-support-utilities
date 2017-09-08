package com.pentaho.install.db;

import com.pentaho.install.DBInstance;

public class PenOpMart implements PentahoDB {
    private Dialect dialect;

    public PenOpMart(Dialect dialect) {
        this.dialect = dialect;
    }

    public void setDefaultDbProperties(DBInstance instance) {
        String pomUsername = dialect.getDefaultPenOpMartUsername();
        instance.setUsername(pomUsername);
        instance.setDefaultUsername(pomUsername);

        instance.setResourceName(dialect.getDefaultPenOpMartResourcename());
    }
}
