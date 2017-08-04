package com.pentaho.install.post.tomcat.webapps;

import com.pentaho.install.post.tomcat.TomcatConf;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Context")
@XmlType(propOrder = {"hibernate","audit","quartz","pentahoOpMart","pdiOpMart"})
public class Context implements TomcatConf {
    private String docbase = "webapps/pentaho/";
    private String path = "/pentaho";

    private Resource hibernate = new Resource();
    private Resource audit = new Resource();
    private Resource quartz = new Resource();
    private Resource pentahoOpMart = new Resource();
    private Resource pdiOpMart = new Resource();

    @XmlAttribute
    public String getDocbase() {
        return docbase;
    }

    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }

    @XmlAttribute
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlElement(name = "Resource")
    public Resource getHibernate() {
        return hibernate;
    }

    public void setHibernate(Resource hibernate) {
        this.hibernate = hibernate;
    }

    @XmlElement(name = "Resource")
    public Resource getAudit() {
        return audit;
    }

    public void setAudit(Resource audit) {
        this.audit = audit;
    }

    @XmlElement(name = "Resource")
    public Resource getQuartz() {
        return quartz;
    }

    public void setQuartz(Resource quartz) {
        this.quartz = quartz;
    }

    @XmlElement(name = "Resource")
    public Resource getPentahoOpMart() {
        return pentahoOpMart;
    }

    public void setPentahoOpMart(Resource pentahoOpMart) {
        this.pentahoOpMart = pentahoOpMart;
    }

    @XmlElement(name = "Resource")
    public Resource getPdiOpMart() {
        return pdiOpMart;
    }

    public void setPdiOpMart(Resource pdiOpMart) {
        this.pdiOpMart = pdiOpMart;
    }
}
