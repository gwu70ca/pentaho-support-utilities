package com.pentaho.install.post.hibernate;

import com.pentaho.install.post.spring.Property;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "session-factory")
public class SessionFactory {
    public static String cacheProviderClass = "cache.provider_class";
    public static String connectionDriverClass = "connection.driver_class";
    public static String connectionUrl = "connection.url";
    public static String dialect = "dialect";
    public static String connectionUsername = "connection.username";
    public static String connectionPassword = "connection.password";

    private List<Property> propertyList;

    public List<Property> getPropertyList() {
        return propertyList;
    }

    @XmlElement(name = "property")
    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }
}
