package com.pentaho.install.post.jackrabbit;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Security")
@XmlAccessorType(XmlAccessType.FIELD)
public class Security {
    @XmlAttribute
    private String appName = "Jackrabbit";
    @XmlElement(name = "SecurityManager")
    private SecurityManager securityManager = new SecurityManager();
    @XmlElement(name = "AccessManager")
    private AccessManager accessManager = new AccessManager();
    @XmlElement(name = "LoginModule")
    private LoginModule loginModule = new LoginModule();

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public AccessManager getAccessManager() {
        return accessManager;
    }

    public void setAccessManager(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    public LoginModule getLoginModule() {
        return loginModule;
    }

    public void setLoginModule(LoginModule loginModule) {
        this.loginModule = loginModule;
    }
}

@XmlRootElement(name = "SecurityManager")
@XmlAccessorType(XmlAccessType.FIELD)
class SecurityManager {
    @XmlAttribute(name = "class")
    private String clazz = "org.apache.jackrabbit.core.DefaultSecurityManager";
    @XmlAttribute(name = "workspaceName")
    private String workspaceName = "security";

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }
}

@XmlRootElement(name = "AccessManager")
@XmlAccessorType(XmlAccessType.FIELD)
class AccessManager {
    @XmlAttribute(name = "class")
    private String clazz = "org.apache.jackrabbit.core.security.DefaultAccessManager";

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}

@XmlRootElement(name = "LoginModule")
@XmlAccessorType(XmlAccessType.FIELD)
class LoginModule {
    @XmlAttribute(name = "class")
    private String clazz = "org.pentaho.platform.repository2.unified.jcr.jackrabbit.security.SpringSecurityLoginModule";

    @XmlElement(name = "param")
    private List<Param> paramList;

    public LoginModule() {
        paramList = new ArrayList<>();
        paramList.add(new Param("anonymousId", "anonymous"));
        paramList.add(new Param("adminId", "pentahoRepoAdmin"));
        paramList.add(new Param("principalProvider", "org.pentaho.platform.repository2.unified.jcr.jackrabbit.security.SpringSecurityPrincipalProvider"));
        paramList.add(new Param("preAuthenticationTokens", "ZchBOvP8q9FQ"));
        paramList.add(new Param("trust_credentials_attribute", "pre_authentication_token"));
    }
}