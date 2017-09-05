package com.pentaho.install.post.jackrabbit;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataStore {
    @XmlAttribute(name = "class")
    private String clazz = "org.apache.jackrabbit.core.data.db.DbDataStore";

    @XmlElement(name = "param")
    private List<Param> paramList;

    public List<Param> getParamList() {
        return paramList;
    }

    public void setParamList(List<Param> paramList) {
        this.paramList = paramList;
    }
}
