package com.pentaho.install.post.tomcat.conf.server;

import com.pentaho.install.post.tomcat.TomcatConf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Server")
@XmlType(propOrder = {"l1", "l2", "l3", "l4", "l5", "globalNamingResources", "service"})
public class Server implements TomcatConf {
    private String port = "8005";
    private String shutdown = "SHUTDOWN";

    private Listener l1 = new Listener("org.apache.catalina.startup.VersionLoggerListener", null);
    private Listener l2 = new Listener("org.apache.catalina.core.AprLifecycleListener", "on");
    private Listener l3 = new Listener("org.apache.catalina.core.JreMemoryLeakPreventionListener", null);
    private Listener l4 = new Listener("org.apache.catalina.mbeans.GlobalResourcesLifecycleListener", null);
    private Listener l5 = new Listener("org.apache.catalina.core.ThreadLocalLeakPreventionListener", null);

    private GlobalNamingResources globalNamingResources = new GlobalNamingResources();
    private Service service = new Service();

    @XmlAttribute
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @XmlAttribute
    public String getShutdown() {
        return shutdown;
    }

    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }

    @XmlElement(name = "Listener")
    public Listener getL1() {
        return l1;
    }

    public void setL1(Listener l1) {
        this.l1 = l1;
    }

    @XmlElement(name = "Listener")
    public Listener getL2() {
        return l2;
    }

    public void setL2(Listener l2) {
        this.l2 = l2;
    }

    @XmlElement(name = "Listener")
    public Listener getL3() {
        return l3;
    }

    public void setL3(Listener l3) {
        this.l3 = l3;
    }

    @XmlElement(name = "Listener")
    public Listener getL4() {
        return l4;
    }

    public void setL4(Listener l4) {
        this.l4 = l4;
    }

    @XmlElement(name = "Listener")
    public Listener getL5() {
        return l5;
    }

    public void setL5(Listener l5) {
        this.l5 = l5;
    }


    @XmlElement(name = "GlobalNamingResources")
    public GlobalNamingResources getGlobalNamingResources() {
        return globalNamingResources;
    }

    public void setGlobalNamingResources(GlobalNamingResources globalNamingResources) {
        this.globalNamingResources = globalNamingResources;
    }

    @XmlElement(name = "Service")
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

}
