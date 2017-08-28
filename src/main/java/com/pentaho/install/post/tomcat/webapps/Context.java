package com.pentaho.install.post.tomcat.webapps;

import com.pentaho.install.post.tomcat.TomcatConf;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Context")
@XmlType(propOrder = {"hibernate", "audit", "quartz", "pentahoOpMart"/*, "pdiOpMart"*/})
@XmlAccessorType(XmlAccessType.FIELD)
public class Context implements TomcatConf {
    @XmlAttribute
    private String docbase = "webapps/pentaho/";
    @XmlAttribute
    private String path = "/pentaho";

    @XmlElement(name = "Resource")
    private Resource hibernate = new Resource();
    @XmlElement(name = "Resource")
    private Resource audit = new Resource();
    @XmlElement(name = "Resource")
    private Resource quartz = new Resource();
    @XmlElement(name = "Resource")
    private Resource pentahoOpMart = new Resource();
    /*@XmlElement(name = "Resource")
    private Resource pdiOpMart = new Resource();*/

    public String getDocbase() {
        return docbase;
    }

    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Resource getHibernate() {
        return hibernate;
    }

    public void setHibernate(Resource hibernate) {
        this.hibernate = hibernate;
    }

    public Resource getAudit() {
        return audit;
    }

    public void setAudit(Resource audit) {
        this.audit = audit;
    }

    public Resource getQuartz() {
        return quartz;
    }

    public void setQuartz(Resource quartz) {
        this.quartz = quartz;
    }

    public Resource getPentahoOpMart() {
        return pentahoOpMart;
    }

    public void setPentahoOpMart(Resource pentahoOpMart) {
        this.pentahoOpMart = pentahoOpMart;
    }

    /*
    public Resource getPdiOpMart() {
        return pdiOpMart;
    }

    public void setPdiOpMart(Resource pdiOpMart) {
        this.pdiOpMart = pdiOpMart;
    }
    */
}
