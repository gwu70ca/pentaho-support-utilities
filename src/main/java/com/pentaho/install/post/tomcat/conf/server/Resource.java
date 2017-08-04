package com.pentaho.install.post.tomcat.conf.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Resource")
public class Resource {
    @XmlAttribute
    private String name = "UserDatabase";
    @XmlAttribute
    private String auth = "Container";
    @XmlAttribute
    private String type = "org.apache.catalina.UserDatabase";
    @XmlAttribute
    private String description = "User database that can be updated and saved";
    @XmlAttribute
    private String factory = "org.apache.catalina.users.MemoryUserDatabaseFactory";
    @XmlAttribute
    private String pathname = "conf/tomcat-users.xml";
}
