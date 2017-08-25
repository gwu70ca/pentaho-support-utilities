package com.pentaho.install.post.spring;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Bean {
    private String id;
    private String clazz;
    private ConstructorArg constructorArg;
    private Property userDn;
    private Property password;

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "class")
    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    @XmlElement(name = "constructor-arg")
    public ConstructorArg getConstructorArg() {
        return constructorArg;
    }

    public void setConstructorArg(ConstructorArg constructorArg) {
        this.constructorArg = constructorArg;
    }

    @XmlElement(name = "property")
    public Property getUserDn() {
        return userDn;
    }

    public void setUserDn(Property userDn) {
        this.userDn = userDn;
    }

    @XmlElement(name = "property")
    public Property getPassword() {
        return password;
    }

    public void setPassword(Property password) {
        this.password = password;
    }
}
