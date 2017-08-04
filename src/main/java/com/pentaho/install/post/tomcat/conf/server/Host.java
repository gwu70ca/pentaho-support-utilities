package com.pentaho.install.post.tomcat.conf.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Host {
    private String name = "localhost";
    private String appBase = "webapps";
    private String unpackWARs = "true";
    private String autoDeploy = "true";
    private Valve valve = new Valve();

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getAppBase() {
        return appBase;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    @XmlAttribute
    public String getUnpackWARs() {
        return unpackWARs;
    }

    public void setUnpackWARs(String unpackWARs) {
        this.unpackWARs = unpackWARs;
    }

    @XmlAttribute
    public String getAutoDeploy() {
        return autoDeploy;
    }

    public void setAutoDeploy(String autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    @XmlElement(name = "Value")
    public Valve getValve() {
        return valve;
    }

    public void setValve(Valve valve) {
        this.valve = valve;
    }
}
