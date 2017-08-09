package com.pentaho.install.post;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam.SERVER;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class JackRabbitRepoXMLHelper extends XMLHelper {
	String DOC_TYPE_STR = "<!DOCTYPE Repository" + System.lineSeparator() +
			"    PUBLIC \"-//The Apache Software Foundation//DTD Jackrabbit 2.0//EN\"" + System.lineSeparator() +
			"    \"http://jackrabbit.apache.org/dtd/repository-2.0.dtd\">" + System.lineSeparator();
	
	String SECURITY_EXTRA_STR = 
			"    <!--" + System.lineSeparator() +
			"      security manager:" + System.lineSeparator() +
			"      class: FQN of class implementing the JackrabbitSecurityManager interface" + System.lineSeparator() +
			"    -->" + System.lineSeparator() +
			"    <SecurityManager class=\"org.apache.jackrabbit.core.DefaultSecurityManager\" workspaceName=\"security\">" + System.lineSeparator() +
			"      <!--" + System.lineSeparator() +
			"        workspace access:" + System.lineSeparator() +
			"        class: FQN of class implementing the WorkspaceAccessManager interface" + System.lineSeparator() +
			"      -->" + System.lineSeparator() +
			"      <!-- <WorkspaceAccessManager class=\"...\"/> -->" + System.lineSeparator() +
			"      <!-- <param name=\"config\" value=\"${rep.home}/security.xml\"/> -->" + System.lineSeparator() +
			"    </SecurityManager>" + System.lineSeparator() + System.lineSeparator() +
			"    <!--" + System.lineSeparator() +
			"      access manager:" + System.lineSeparator() +
			"      class: FQN of class implementing the AccessManager interface" + System.lineSeparator() +
			"    -->" + System.lineSeparator() +
			"    <AccessManager class=\"org.apache.jackrabbit.core.security.DefaultAccessManager\">" + System.lineSeparator() +
			"      <!-- <param name=\"config\" value=\"${rep.home}/access.xml\"/> -->" + System.lineSeparator() +
			"    </AccessManager>" + System.lineSeparator() + System.lineSeparator() +
			"    <LoginModule class=\"org.pentaho.platform.repository2.unified.jcr.jackrabbit.security.SpringSecurityLoginModule\">" + System.lineSeparator() +
			"      <!--" + System.lineSeparator() +
			"        anonymous user name ('anonymous' is the default value)" + System.lineSeparator() +
			"      -->" + System.lineSeparator() +
			"      <param name=\"anonymousId\" value=\"anonymous\"/>" + System.lineSeparator() +
			"      <!--" + System.lineSeparator() +
			"        administrator user id (default value if param is missing is 'admin')" + System.lineSeparator() +
			"      -->" + System.lineSeparator() +
			"      <param name=\"adminId\" value=\"pentahoRepoAdmin\"/>" + System.lineSeparator() +
			"      <param name=\"principalProvider\"" + System.lineSeparator() +
			"        value=\"org.pentaho.platform.repository2.unified.jcr.jackrabbit.security.SpringSecurityPrincipalProvider\"/>" + System.lineSeparator() +
			"      <!-- comma separated list of pre-authentication tokens, one per application -->" + System.lineSeparator() +
			"      <param name=\"preAuthenticationTokens\" value=\"ZchBOvP8q9FQ\"/>" + System.lineSeparator() +
			"      <!-- must match PentahoSessionCredentialsStrategy.ATTR_PRE_AUTHENTICATION_TOKEN -->" + System.lineSeparator() +
			"      <param name=\"trust_credentials_attribute\" value=\"pre_authentication_token\"/>" + System.lineSeparator() +
			"    </LoginModule>";
	
    String WORKSPACE_EXTRA_STR = System.lineSeparator() +
			"    <SearchIndex class=\"org.apache.jackrabbit.core.query.lucene.SearchIndex\">" + System.lineSeparator() +
			"      <param name=\"path\" value=\"${wsp.home}/index\"/>" + System.lineSeparator() +
			"      <param name=\"supportHighlighting\" value=\"true\"/>" + System.lineSeparator() +
			"    </SearchIndex>" + System.lineSeparator() + System.lineSeparator() + 
			"    <WorkspaceSecurity>" + System.lineSeparator() +
			"      <AccessControlProvider class=\"org.apache.jackrabbit.core.security.authorization.acl.PentahoACLProvider\">" + System.lineSeparator() +
			"        <!-- Param names have the prefix magicAceDefinition along with a consecutive integer -->" + System.lineSeparator() +
			"        <!-- Param values have four values separated by colons. -->" + System.lineSeparator() +
			"        <!-- Param value subitem #1: path possibly including {0} which will be replaced by tenantId -->" + System.lineSeparator() +
			"        <!-- Param value subitem #2: ABS action name -->" + System.lineSeparator() +
			"        <!-- Param value subitem #3: comma-separated list of JCR privileges -->" + System.lineSeparator() +
			"        <!-- Param value subitem #4: boolean where true means that path is a prefix and not to be matched exactly -->" + System.lineSeparator() +
			"        <param name=\"magicAceDefinition0\" value=\"{0};org.pentaho.security.administerSecurity;jcr:all;true;true;false\" />" + System.lineSeparator() +
			"        <param name=\"magicAceDefinition1\"" + System.lineSeparator() +
			"value=\"{0};org.pentaho.repository.read;jcr:read,jcr:readAccessControl;true;false;true\" />" + System.lineSeparator() +
			"        <param name=\"magicAceDefinition2\"" + System.lineSeparator() +
			"value=\"{0}/etc/pdi;org.pentaho.repository.read;jcr:read,jcr:readAccessControl;true;false;false\" />" + System.lineSeparator() +
			"        <param name=\"magicAceDefinition3\"" + System.lineSeparator() +
			"value=\"{0}/etc/pdi;org.pentaho.repository.create;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;false;false\" />" + System.lineSeparator() +
			"        <param name=\"magicAceDefinition4\"" + System.lineSeparator() +
			"value=\"{0}/etc;org.pentaho.security.publish;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;true;false\" />" + System.lineSeparator() +
			"        <param name=\"magicAceDefinition5\"" + System.lineSeparator() +
			"value=\"{0}/etc/pdi/databases;org.pentaho.platform.dataaccess.datasource.security.manage;jcr:read,jcr:readAccessControl,jcr:write,jcr:modifyAccessControl,jcr:lockManagement,jcr:versionManagement,jcr:nodeTypeManagement;true;true;true\" />" + System.lineSeparator() +
			"      </AccessControlProvider>" + System.lineSeparator() +
			"    </WorkspaceSecurity>";
	
	private DBInstance jackrabbitInstance;
	private DB dbType;
	private SERVER serverType;
	
	public void setJackrabbitInstance(DBInstance jackrabbitInstance) {
		this.jackrabbitInstance = jackrabbitInstance;
		this.dbType = jackrabbitInstance.getType();
	}

	public void setServerType(SERVER serverType) {
		this.serverType = serverType;
	}

	public boolean updateRepositoryXml(File repoFile) {
		boolean success = false;
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			createRepoXml(doc);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(repoFile);

			// Output to console for testing
			//StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);
			
			List<String> allLines = Files.readAllLines(repoFile.toPath());
			List<String> lines = new ArrayList<String>(allLines.size());
			for (String line : allLines) {
				if (line.indexOf("<Repository>") >= 0) {
					line = DOC_TYPE_STR + System.lineSeparator() + "<Repository>" + System.lineSeparator();
				} else if (line.indexOf("<Security appName=\"Jackrabbit\"/>") >= 0) {
					line = line.replace("<Security appName=\"Jackrabbit\"/>", 
							System.lineSeparator() + "  <Security appName=\"Jackrabbit\">" + System.lineSeparator() + SECURITY_EXTRA_STR + System.lineSeparator() + "  </Security>" + System.lineSeparator());
				} else if (line.indexOf("</Workspace>") >= 0) {
					line = line.replace("</Workspace>", WORKSPACE_EXTRA_STR + System.lineSeparator() + "  </Workspace>");
				}
				lines.add(line);
			}
			Files.write(repoFile.toPath(), lines, StandardOpenOption.WRITE);
			
			success = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return success;
	}
	
	private void createRepoXml(Document doc) {
		Element root = doc.createElement("Repository");
		doc.appendChild(root);
		
		createFileSystem(doc, root, "fs_repos_");
		createDataStore(doc, root, "ds_repos_");
		createSecurityManager(doc, root);
		createWorkspaces(doc, root);
		createWorkspace(doc, root);
		createVersioning(doc, root);
		createCluster(doc, root);
	}
	
	private void createFileSystem(Document doc, Element parent, String fs) {
		Element fileSystemElement = doc.createElement("FileSystem");
		fileSystemElement.setAttribute("class", getFileSystemClass());
		parent.appendChild(fileSystemElement);
		
		Element driver = getDriver(doc, true);
		if (driver != null) {
			fileSystemElement.appendChild(driver);
		}
		
		Element url = getUrl(doc);
		if (url != null) {
			fileSystemElement.appendChild(url);
		}
		
		fileSystemElement.appendChild(createParamElement(doc, new String[][]{{"name", "user"},{"value", jackrabbitInstance.getUsername()}}));
		fileSystemElement.appendChild(createParamElement(doc, new String[][]{{"name", "password"},{"value", jackrabbitInstance.getPassword()}}));
				
		Element schema = getSchema(doc, true);
		if (schema != null) {
			fileSystemElement.appendChild(schema);
		}

		fileSystemElement.appendChild(createParamElement(doc, new String[][]{{"name", "schemaObjectPrefix"},{"value", fs}}));
		
		Element tablespace = getTablespace(doc);
		if (tablespace != null) {
			fileSystemElement.appendChild(tablespace);
		}
	}
	
	private void createDataStore(Document doc, Element root, String fs) {
		Element dataStoreElement = doc.createElement("DataStore");
		dataStoreElement.setAttribute("class", "org.apache.jackrabbit.core.data.db.DbDataStore"); 
		root.appendChild(dataStoreElement);
		
		Element url = getUrl(doc);
		if (url != null) {
			dataStoreElement.appendChild(url);
		}
		
		Element driver = getDriver(doc, false);
		if (driver != null) {
			dataStoreElement.appendChild(driver);
		}
		
		dataStoreElement.appendChild(createParamElement(doc, new String[][]{{"name", "user"},{"value", jackrabbitInstance.getUsername()}}));
		dataStoreElement.appendChild(createParamElement(doc, new String[][]{{"name", "password"},{"value", jackrabbitInstance.getPassword()}}));
		dataStoreElement.appendChild(getDatabaseTypeElement(doc));
		dataStoreElement.appendChild(createParamElement(doc, new String[][]{{"name", "minRecordLength"},{"value", "1024"}}));
		dataStoreElement.appendChild(createParamElement(doc, new String[][]{{"name", "maxConnections"},{"value", "3"}}));
		dataStoreElement.appendChild(createParamElement(doc, new String[][]{{"name", "copyWhenReading"},{"value", "true"}}));
		dataStoreElement.appendChild(createParamElement(doc, new String[][]{{"name", "tablePrefix"},{"value", ""}}));
		dataStoreElement.appendChild(createParamElement(doc, new String[][]{{"name", "schemaObjectPrefix"},{"value", fs}}));
	}
	
	private void createSecurityManager(Document doc, Element root) {
		Element security = doc.createElement("Security");
		security.setAttribute("appName", "Jackrabbit");
		root.appendChild(security);
	}
	
	private void createWorkspaces(Document doc, Element root) {
		Element workspaces = doc.createElement("Workspaces");
		workspaces.setAttribute("defaultWorkspace", "default");		
		workspaces.setAttribute("rootPath", "${rep.home}/workspaces");
		root.appendChild(workspaces);
	}
	
	private void createWorkspace(Document doc, Element root) {
		Element workspace = doc.createElement("Workspace");
		workspace.setAttribute("name", "${wsp.name}");
		root.appendChild(workspace);
		
		createFileSystem(doc, workspace, "fs_ws_");
		createPersistenceManager(doc, workspace, "${wsp.name}_pm_ws_");
	}
	
	private void createVersioning(Document doc, Element root) {
		Element versioning = doc.createElement("Versioning");
		versioning.setAttribute("rootPath", "${rep.home}/version");
		root.appendChild(versioning);
		
		createFileSystem(doc, versioning, "fs_ver_");
		createPersistenceManager(doc, versioning, "pm_ver_");
	}
	
	private void createCluster(Document doc, Element root) {
		Element cluster = doc.createElement("Cluster");
		cluster.setAttribute("id", "node1");
		root.appendChild(cluster);
		
		createJournal(doc, cluster);
	}
	
	private void createJournal(Document doc, Element parent) {
		Element journal = doc.createElement("Journal");
		journal.setAttribute("class", getJournalClass());
		parent.appendChild(journal);
		
		if (serverType.equals(SERVER.DI)) {
			journal.appendChild(createParamElement(doc, new String[][]{{"name", "revision"}, {"value", "${rep.home}/revision"}}));
			
			Element driver = getDriver(doc, false);
			if (driver != null) {
				journal.appendChild(driver);
			}
			
			Element url = getUrl(doc);
			if (url != null) {
				journal.appendChild(url);
			}
		
			journal.appendChild(createParamElement(doc, new String[][]{{"name", "user"},{"value", jackrabbitInstance.getUsername()}}));
			journal.appendChild(createParamElement(doc, new String[][]{{"name", "password"},{"value", jackrabbitInstance.getPassword()}}));
			
			Element schema = getSchema(doc, false);
			if (schema != null) {
				journal.appendChild(schema);
			}
			
			journal.appendChild(createParamElement(doc, new String[][]{{"name", "schemaObjectPrefix"},{"value", "J_C_"}}));
		}
	}
	
	private void createPersistenceManager(Document doc, Element parent, String fs) {
		Element persistenceManager = doc.createElement("PersistenceManager");
		persistenceManager.setAttribute("class", getPersistenceManagerClass());
		parent.appendChild(persistenceManager);
		
		Element driver = getDriver(doc, false);
		if (driver != null) {
			persistenceManager.appendChild(driver);
		}
		
		Element url = getUrl(doc);
		if (url != null) {
			persistenceManager.appendChild(url);
		}
		
		persistenceManager.appendChild(createParamElement(doc, new String[][]{{"name", "user"},{"value", jackrabbitInstance.getUsername()}}));
		persistenceManager.appendChild(createParamElement(doc, new String[][]{{"name", "password"},{"value", jackrabbitInstance.getPassword()}}));
		
		Element schema = getSchema(doc, false);
		if (schema != null) {
			persistenceManager.appendChild(schema);
		}
		
		persistenceManager.appendChild(createParamElement(doc, new String[][]{{"name", "schemaObjectPrefix"},{"value", fs}}));
		
		Element tablespace = getTablespace(doc);
		if (tablespace != null) {
			persistenceManager.appendChild(tablespace);
		}		
	}
	
	private String getPersistenceManagerClass() {
		String className = "org.apache.jackrabbit.core.persistence.bundle.MySqlPersistenceManager";
		if (dbType == DB.MySQL) {
		} else if (dbType == DB.MSSQLServer) {
			className = "org.apache.jackrabbit.core.persistence.bundle.MSSqlPersistenceManager";
		} else if (dbType == DB.Oracle) {
			className = "org.apache.jackrabbit.core.persistence.bundle.OraclePersistenceManager";
		} else if (dbType == DB.PostgreSQL) {
			className = "org.apache.jackrabbit.core.persistence.bundle.PostgreSQLPersistenceManager";
		}
		return className;
	}
	
	private Element getDatabaseTypeElement(Document doc) {
		Element databaseType = doc.createElement("param");
		databaseType.setAttribute("name", "databaseType");
		
		String value = "mysql";
		if (dbType == DB.MySQL) {
		} else if (dbType == DB.MSSQLServer) {
			value = "mssql";
		} else if (dbType == DB.Oracle) {
			value = "oracle";
		} else if (dbType == DB.PostgreSQL) {
			value = "postgresql";
		}
		databaseType.setAttribute("value", value);
		return databaseType;
	}

	private Element getSchema(Document doc, boolean isFileSystem) {
		Element e = doc.createElement("param");
		
		String name = "schema";
		String value = "mysql";
		
		if (dbType == DB.MySQL) {
		} else if (dbType == DB.MSSQLServer) {
			value = "mssql";
		} else if (dbType == DB.Oracle) {
			if (isFileSystem) {
				return null;
			}
			value = "oracle";
		} else if (dbType == DB.PostgreSQL) {
			value = "postgresql";
		}
		
		e.setAttribute("name", name);
		e.setAttribute("value", value);
		return e;
	}
	
	private Element getTablespace(Document doc) {
		if (dbType != DB.Oracle) {
			return null;
		}
		
		Element e = doc.createElement("param");
		e.setAttribute("name", "tablespace");
		e.setAttribute("value", InstallUtil.isBA(serverType) ? "pentaho_tablespace" : "di_pentaho_tablespace");
		return e;
	}
	
	private Element getUrl(Document doc) {
		String url = InstallUtil.getJdbcUrl(this.jackrabbitInstance);
		
		Element e = doc.createElement("param");
		e.setAttribute("name", "url");
		e.setAttribute("value", url);
		return e;
	}
	
	private Element getDriver(Document doc, boolean isFileSystem) {
		if (isFileSystem && dbType == DB.Oracle) {
			return null;
		}
		String driverValue = InstallUtil.getJdbcDriverClass(jackrabbitInstance.getType());
		Element e = doc.createElement("param");
		e.setAttribute("name", "driver");
		e.setAttribute("value", driverValue);
		
		return e;
	}
	
	private String getFileSystemClass() {
		String fileSystemClass = "org.apache.jackrabbit.core.fs.db.DbFileSystem";
		if (dbType == DB.MSSQLServer) {
			fileSystemClass = "org.apache.jackrabbit.core.fs.db.MSSqlFileSystem";
		} else if (dbType == DB.Oracle) {
			fileSystemClass = "org.apache.jackrabbit.core.fs.db.OracleFileSystem";
		} else if (dbType == DB.PostgreSQL) {
			fileSystemClass = "org.apache.jackrabbit.core.fs.db.DbFileSystem";
		}
		
		return fileSystemClass;
	}
	
	private String getJournalClass() {
		if (InstallUtil.isBA(serverType)) {
			return "org.apache.jackrabbit.core.journal.MemoryJournal";
		} 
		
		if (dbType.equals(DB.MSSQLServer)) {
			return "org.apache.jackrabbit.core.journal.MSSqlDatabaseJournal";
		} else if (dbType.equals(DB.Oracle)) {
			return "org.apache.jackrabbit.core.journal.OracleDatabaseJournal";
		} else {
			return "org.apache.jackrabbit.core.journal.DatabaseJournal";
		}
	}
}
