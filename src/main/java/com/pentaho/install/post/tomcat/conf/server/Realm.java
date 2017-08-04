package com.pentaho.install.post.tomcat.conf.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Realm")
public class Realm {
    @XmlAttribute
    private String className;
    @XmlAttribute
    private String resourceName;
    @XmlElement(name="Realm")
    private Realm realm;

    public Realm() {}

    public Realm(String className, String resourceName) {
        this.className = className;
        this.resourceName = resourceName;

        if ("org.apache.catalina.realm.LockOutRealm".equals(className)) {
            this.realm = new Realm("org.apache.catalina.realm.UserDatabaseRealm", "UserDatabase");
        }
    }
}
