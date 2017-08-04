package com.pentaho.install.post.jackrabbit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Security")
@XmlType(propOrder = {"securityManager", "accessManager", "loginModule"})
public class Security {
    private String appName = "Jackrabbit";
    private SecurityManager securityManager = new SecurityManager();
    private AccessManager accessManager = new AccessManager();
    private LoginModule loginModule = new LoginModule();

    public String getAppName() {
        return appName;
    }

    @XmlAttribute
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    @XmlElement(name = "SecurityManager")
    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public AccessManager getAccessManager() {
        return accessManager;
    }

    @XmlElement(name = "AccessManager")
    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    public LoginModule getLoginModule() {
        return loginModule;
    }

    @XmlElement(name = "LoginModule")
    public void setLoginModule(LoginModule loginModule) {
        this.loginModule = loginModule;
    }
}

@XmlRootElement(name = "SecurityManager")
class SecurityManager {
    private String clazz = "org.apache.jackrabbit.core.DefaultSecurityManager";
    private String workspaceName = "security";

    public String getClazz() {
        return clazz;
    }

    @XmlAttribute(name = "class")
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    @XmlAttribute(name = "workspaceName")
    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }
}

@XmlRootElement(name = "AccessManager")
class AccessManager {
    private String clazz = "org.apache.jackrabbit.core.security.DefaultAccessManager";

    public String getClazz() {
        return clazz;
    }

    @XmlAttribute(name = "class")
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}

@XmlRootElement(name = "LoginModule")
@XmlType(propOrder = {"anonymousId","adminId","principalProvider","preAuthenticationTokens","trustCredentialsAttribute"})
class LoginModule {
    private String clazz = "org.pentaho.platform.repository2.unified.jcr.jackrabbit.security.SpringSecurityLoginModule";
    private Param anonymousId = new Param("anonymousId", "anonymous");
    private Param adminId = new Param("adminId", "pentahoRepoAdmin");
    private Param principalProvider = new Param("principalProvider", "org.pentaho.platform.repository2.unified.jcr.jackrabbit.security.SpringSecurityPrincipalProvider");
    private Param preAuthenticationTokens = new Param("preAuthenticationTokens", "ZchBOvP8q9FQ");
    private Param trustCredentialsAttribute = new Param("trust_credentials_attribute", "pre_authentication_token");

    public String getClazz() {
        return clazz;
    }

    @XmlAttribute(name = "class")
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Param getAnonymousId() {
        return anonymousId;
    }

    @XmlElement(name = "param")
    public void setAnonymousId(Param anonymousId) {
        this.anonymousId = anonymousId;
    }

    public Param getAdminId() {
        return adminId;
    }

    @XmlElement(name = "param")
    public void setAdminId(Param adminId) {
        this.adminId = adminId;
    }

    public Param getPrincipalProvider() {
        return principalProvider;
    }

    @XmlElement(name = "param")
    public void setPrincipalProvider(Param principalProvider) {
        this.principalProvider = principalProvider;
    }

    public Param getPreAuthenticationTokens() {
        return preAuthenticationTokens;
    }

    @XmlElement(name = "param")
    public void setPreAuthenticationTokens(Param preAuthenticationTokens) {
        this.preAuthenticationTokens = preAuthenticationTokens;
    }

    public Param getTrustCredentialsAttribute() {
        return trustCredentialsAttribute;
    }

    @XmlElement(name = "param")
    public void setTrustCredentialsAttribute(Param trustCredentialsAttribute) {
        this.trustCredentialsAttribute = trustCredentialsAttribute;
    }
}