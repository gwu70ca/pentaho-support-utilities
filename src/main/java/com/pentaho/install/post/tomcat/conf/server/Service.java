package com.pentaho.install.post.tomcat.conf.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="Service")
@XmlType(propOrder={"httpConnector","ajpCconnector","engine"})
public class Service {
    private String name = "Catalina";

    private Connector httpConnector = new Connector("UTF-8", "8080", "HTTP/1.1", "20000", "8443");
    private Connector ajpCconnector = new Connector("UTF-8", "8009", "AJP/1.3", "8443");
    private Engine engine = new Engine();

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="Connector")
    public Connector getHttpConnector() {
        return httpConnector;
    }

    public void setHttpConnector(Connector httpConnector) {
        this.httpConnector = httpConnector;
    }

    @XmlElement(name="Connector")
    public Connector getAjpCconnector() {
        return ajpCconnector;
    }

    public void setAjpCconnector(Connector ajpCconnector) {
        this.ajpCconnector = ajpCconnector;
    }

    @XmlElement(name="Engine")
    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
