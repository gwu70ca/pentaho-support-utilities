package com.pentaho.install.db;

import com.pentaho.install.DBInstance;

public class Hibernate implements PentahoDB {
    private Dialect dialect;

    public Hibernate(Dialect dialect) {
        this.dialect = dialect;
    }

    public void setDefaultDbProperties(DBInstance instance) {
        String hibernateUsername = dialect.getDefaultHibernateUsername();
        instance.setUsername(hibernateUsername);
        instance.setDefaultUsername(hibernateUsername);

        instance.setResourceName(dialect.getHibernateResourceName());
    }
}
