package com.pentaho.install.post.tomcat.conf.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Engine")
@XmlType(propOrder={"realm","host"})
public class Engine {
    private String name = "Catalina";
    private String defaultHost = "localhost";

    private Realm realm = new Realm("org.apache.catalina.realm.LockOutRealm", null);
    private Host host = new Host();

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getDefaultHost() {
        return defaultHost;
    }

    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }


    @XmlElement(name = "Realm")
    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    @XmlElement(name = "Host")
    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

}
