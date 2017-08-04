package com.pentaho.install.post.tomcat.conf.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Listener")
public class Listener {
    private String className;
    private String sslEngine = "on";

    public Listener() {
    }

    public Listener(String className, String sslEngine) {
        this.className = className;
        this.sslEngine = sslEngine;
    }

    public String getClassName() {
        return className;
    }

    @XmlAttribute
    public void setClassName(String className) {
        this.className = className;
    }

    public String getSslEngine() {
        return sslEngine;
    }

    @XmlAttribute
    public void setSslEngine(String sslEngine) {
        this.sslEngine = sslEngine;
    }
}
