package com.pentaho.install.post.hibernate;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "hibernate-configuration")
public class Configuration {
    private SessionFactory sessionFactory;

    @XmlElement(name = "session-factory")
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
