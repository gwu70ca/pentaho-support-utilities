package com.pentaho.install.post.tomcat.conf.server;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GlobalNamingResources")
//@XmlAccessorType(XmlAccessType.FIELD)
public class GlobalNamingResources {
    private Resource resource = new Resource();

    @XmlElement(name = "Resource")
    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
