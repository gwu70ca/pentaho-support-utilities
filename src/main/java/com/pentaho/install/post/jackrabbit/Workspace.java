package com.pentaho.install.post.jackrabbit;

import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlType(propOrder = {"fileSystem", "persistenceManager", "searchIndex", "workspaceSecurity"})
public class Workspace {
    private String name = "${wsp.name}";
    private FileSystem fileSystem;
    private PersistenceManager persistenceManager;
    private SearchIndex searchIndex;
    private WorkspaceSecurity workspaceSecurity;

    public Workspace() {
    }

    public Workspace(PentahoServerParam.SERVER pentahoServerType) {
        fileSystem = new FileSystem();
        persistenceManager = new PersistenceManager();
        searchIndex = new SearchIndex();
        workspaceSecurity = new WorkspaceSecurity(pentahoServerType);
    }

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
    private AccessControlProvider accessControlProvider;

    public WorkspaceSecurity() {
    }

    public WorkspaceSecurity(PentahoServerParam.SERVER pentahoServerType) {
        accessControlProvider = new AccessControlProvider(pentahoServerType);
    }

    public AccessControlProvider getAccessControlProvider() {
        return accessControlProvider;
    }

    @XmlElement(name = "AccessControlProvider")
    public void setAccessControlProvider(AccessControlProvider accessControlProvider) {
        this.accessControlProvider = accessControlProvider;
    }
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class AccessControlProvider {
    @XmlAttribute(name = "class")
    private String clazz = "org.apache.jackrabbit.core.security.authorization.acl.PentahoACLProvider";

    @XmlElement(name = "param")
    private List<Param> paramList;

    public AccessControlProvider() {
    }

    public AccessControlProvider(PentahoServerParam.SERVER pentahoServerType) {
        if (!InstallUtil.isHYBRID(pentahoServerType)) {
            paramList = new ArrayList<>();
            paramList.add(new Param("magicAceDefinition0", "{0};org.pentaho.security.administerSecurity;jcr:all;true;true;false"));
            paramList.add(new Param("magicAceDefinition1", "{0};org.pentaho.repository.read;jcr:read,jcr:readAccessControl;true;false;true"));

            if (InstallUtil.isBA(pentahoServerType)) {
                paramList.add(new Param("magicAceDefinition2", "{0}/etc/pdi;org.pentaho.repository.read;jcr:read,jcr:readAccessControl;true;false;false"));
                paramList.add(new Param("magicAceDefinition3", "{0}/etc/pdi;org.pentaho.repository.create;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;false;false"));
                paramList.add(new Param("magicAceDefinition4", "{0}/etc;org.pentaho.security.publish;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;true;false"));
                paramList.add(new Param("magicAceDefinition5", "{0}/etc/pdi/databases;org.pentaho.platform.dataaccess.datasource.security.manage;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;true;true"));
            } else {
                //This is DI
                paramList.add(new Param("magicAceDefinition2", "{0}/etc;org.pentaho.repository.read;jcr:read,jcr:readAccessControl;true;true;false;{0}/etc/pdi/databases"));
                paramList.add(new Param("magicAceDefinition3", "{0}/etc;org.pentaho.repository.create;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;true;false;{0}/etc/pdi/databases"));
                paramList.add(new Param("magicAceDefinition4", "{0}/etc;org.pentaho.security.publish;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;true;false;{0}/etc/pdi/databases"));
            }
        }
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}

