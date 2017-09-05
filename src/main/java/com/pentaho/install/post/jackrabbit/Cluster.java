package com.pentaho.install.post.jackrabbit;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
public class Cluster {
    private String id = "node1";
    private Journal journal = new Journal();

    public String getId() {
        return id;
    }

    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }

    public Journal getJournal() {
        return journal;
    }

    @XmlElement(name = "Journal")
    public void setJournal(Journal journal) {
        this.journal = journal;
    }
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class Journal {
    @XmlAttribute(name = "class")
    private String clazz = "org.apache.jackrabbit.core.journal.MemoryJournal";

    @XmlElement(name = "param")
    private List<Param> paramList;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public List<Param> getParamList() {
        return paramList;
    }

    public void setParamList(List<Param> paramList) {
        this.paramList = paramList;
    }
}


