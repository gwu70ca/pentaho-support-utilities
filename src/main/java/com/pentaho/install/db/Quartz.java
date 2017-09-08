package com.pentaho.install.db;

import com.pentaho.install.DBInstance;

public class Quartz implements PentahoDB {
    private Dialect dialect;

    public Quartz(Dialect dialect) {
        this.dialect = dialect;
    }

    public void setDefaultDbProperties(DBInstance instance) {
        String quartzUsername = dialect.getDefaultQuartzUsername();
        instance.setUsername(quartzUsername);
        instance.setDefaultUsername(quartzUsername);

        instance.setResourceName(dialect.getQuartzResourceName());
    }
}
