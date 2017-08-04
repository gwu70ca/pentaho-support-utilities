package com.pentaho.install.post.tomcat.conf.server;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Connector")
public class Connector {
    private String uriEncoding = "UTF-8";
    private String port = "8080";
    private String protocol = "HTTP/1.1";
    private String connectionTimeout = "20000";
    private String redirectPort = "8443";
    private String maxThreads = null;
    private String SSLEnabled = null;
    private String scheme = null;
    private String secure = null;
    private String clientAuth = null;
    private String sslProtocol = null;
    private String keystoreFile = null;
    private String keystorePass = null;

    public Connector() {

    }

    public Connector(String uriEncoding, String port, String protocol, String redirectPort) {
        this.uriEncoding = uriEncoding;
        this.port = port;
        this.protocol = protocol;
        this.redirectPort = redirectPort;
    }

    public Connector(String uriEncoding, String port, String protocol, String connectionTimeout, String redirectPort) {
        this.uriEncoding = uriEncoding;
        this.port = port;
        this.protocol = protocol;
        this.connectionTimeout = connectionTimeout;
        this.redirectPort = redirectPort;
    }

    @XmlAttribute(name = "URIEncoding")
    public String getUriEncoding() {
        return uriEncoding;
    }

    public void setUriEncoding(String uriEncoding) {
        this.uriEncoding = uriEncoding;
    }

    @XmlAttribute
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @XmlAttribute
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @XmlAttribute
    public String getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(String connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @XmlAttribute
    public String getRedirectPort() {
        return redirectPort;
    }

    public void setRedirectPort(String redirectPort) {
        this.redirectPort = redirectPort;
    }

    @XmlAttribute
    public String getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(String maxThreads) {
        this.maxThreads = maxThreads;
    }

    @XmlAttribute
    public String getSSLEnabled() {
        return SSLEnabled;
    }

    public void setSSLEnabled(String SSLEnabled) {
        this.SSLEnabled = SSLEnabled;
    }

    @XmlAttribute
    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @XmlAttribute
    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    @XmlAttribute
    public String getClientAuth() {
        return clientAuth;
    }

    public void setClientAuth(String clientAuth) {
        this.clientAuth = clientAuth;
    }

    @XmlAttribute
    public String getSslProtocol() {
        return sslProtocol;
    }

    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    @XmlAttribute
    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    @XmlAttribute
    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }
}
