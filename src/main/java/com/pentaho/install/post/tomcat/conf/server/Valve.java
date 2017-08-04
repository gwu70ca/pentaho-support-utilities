package com.pentaho.install.post.tomcat.conf.server;

import javax.xml.bind.annotation.XmlAttribute;

public class Valve {
    private String className = "org.apache.catalina.valves.AccessLogValve";
    private String directory = "logs";
    private String prefix = "localhost_access";
    private String suffix = ".log";
    private String pattern = "%h %l %u %t &quot;%r&quot; %s %b";

    @XmlAttribute
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @XmlAttribute
    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @XmlAttribute
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @XmlAttribute
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @XmlAttribute
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
