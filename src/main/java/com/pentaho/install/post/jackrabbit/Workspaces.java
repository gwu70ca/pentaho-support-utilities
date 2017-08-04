package com.pentaho.install.post.jackrabbit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Workspaces {
    private String rootPath = "${rep.home}/workspaces";
    private String defaultWorkspace= "default";

    public String getRootPath() {
        return rootPath;
    }

    @XmlAttribute
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getDefaultWorkspace() {
        return defaultWorkspace;
    }

    @XmlAttribute
    public void setDefaultWorkspace(String defaultWorkspace) {
        this.defaultWorkspace = defaultWorkspace;
    }
}
