package com.pentaho.install.post.jackrabbit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"fileSystem", "persistenceManager", "searchIndex", "workspaceSecurity"})
public class Workspace {
    private String name = "${wsp.name}";
    private FileSystem fileSystem = new FileSystem();
    private PersistenceManager persistenceManager = new PersistenceManager();
    private SearchIndex searchIndex = new SearchIndex();
    private WorkspaceSecurity workspaceSecurity = new WorkspaceSecurity();

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @XmlElement(name = "FileSystem")
    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    @XmlElement(name = "PersistenceManager")
    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public SearchIndex getSearchIndex() {
        return searchIndex;
    }

    @XmlElement(name = "SearchIndex")
    public void setSearchIndex(SearchIndex searchIndex) {
        this.searchIndex = searchIndex;
    }

    public WorkspaceSecurity getWorkspaceSecurity() {
        return workspaceSecurity;
    }

    @XmlElement(name = "WorkspaceSecurity")
    public void setWorkspaceSecurity(WorkspaceSecurity workspaceSecurity) {
        this.workspaceSecurity = workspaceSecurity;
    }

}

@XmlRootElement
class SearchIndex {
    private String clazz = "org.apache.jackrabbit.core.query.lucene.SearchIndex";
    private Param path = new Param("path", "${wsp.home}/index");
    private Param supportHighlighting = new Param("supportHighlighting", "true");

    public String getClazz() {
        return clazz;
    }

    @XmlAttribute(name = "class")
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Param getPath() {
        return path;
    }

    @XmlElement(name = "param")
    public void setPath(Param path) {
        this.path = path;
    }

    public Param getSupportHighlighting() {
        return supportHighlighting;
    }

    @XmlElement(name = "param")
    public void setSupportHighlighting(Param supportHighlighting) {
        this.supportHighlighting = supportHighlighting;
    }
}

@XmlRootElement
class WorkspaceSecurity {
    private AccessControlProvider accessControlProvider = new AccessControlProvider();

    public AccessControlProvider getAccessControlProvider() {
        return accessControlProvider;
    }

    @XmlElement(name="AccessControlProvider")
    public void setAccessControlProvider(AccessControlProvider accessControlProvider) {
        this.accessControlProvider = accessControlProvider;
    }
}

@XmlRootElement
@XmlType(propOrder = {"param0","param1","param2","param3","param4","param5"})
class AccessControlProvider {
    private String clazz = "org.apache.jackrabbit.core.security.authorization.acl.PentahoACLProvider";
    private Param param0 = new Param("magicAceDefinition0","{0};org.pentaho.security.administerSecurity;jcr:all;true;true;false");
    private Param param1 = new Param("magicAceDefinition1","{0};org.pentaho.repository.read;jcr:read,jcr:readAccessControl;true;false;true");
    private Param param2 = new Param("magicAceDefinition2","{0}/etc/pdi;org.pentaho.repository.read;jcr:read,jcr:readAccessControl;true;false;false");
    private Param param3 = new Param("magicAceDefinition3","{0}/etc/pdi;org.pentaho.repository.create;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;false;false");
    private Param param4 = new Param("magicAceDefinition4","{0}/etc;org.pentaho.security.publish;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;true;false");
    private Param param5 = new Param("magicAceDefinition5","{0}/etc/pdi/databases;org.pentaho.platform.dataaccess.datasource.security.manage;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;true;true");

    public String getClazz() {
        return clazz;
    }

    @XmlAttribute(name = "class")
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Param getParam0() {
        return param0;
    }

    @XmlElement(name="Param")
    public void setParam0(Param param0) {
        this.param0 = param0;
    }

    public Param getParam1() {
        return param1;
    }

    @XmlElement(name="Param")
    public void setParam1(Param param1) {
        this.param1 = param1;
    }

    public Param getParam2() {
        return param2;
    }

    @XmlElement(name="Param")
    public void setParam2(Param param2) {
        this.param2 = param2;
    }

    public Param getParam3() {
        return param3;
    }

    @XmlElement(name="Param")
    public void setParam3(Param param3) {
        this.param3 = param3;
    }

    public Param getParam4() {
        return param4;
    }

    @XmlElement(name="Param")
    public void setParam4(Param param4) {
        this.param4 = param4;
    }

    public Param getParam5() {
        return param5;
    }

    @XmlElement(name="Param")
    public void setParam5(Param param5) {
        this.param5 = param5;
    }
}

