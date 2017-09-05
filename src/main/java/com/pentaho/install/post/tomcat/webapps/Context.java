package com.pentaho.install.post.tomcat.webapps;

import com.pentaho.install.post.tomcat.TomcatConf;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "Context")
@XmlAccessorType(XmlAccessType.FIELD)
public class Context implements TomcatConf {
    @XmlAttribute
    private String docbase = "webapps/pentaho/";
    @XmlAttribute
    private String path = "/pentaho";
    @XmlElement(name = "Resource")
    private List<Resource> resourceList;

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

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
}
