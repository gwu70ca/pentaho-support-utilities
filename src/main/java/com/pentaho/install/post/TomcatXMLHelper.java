package com.pentaho.install.post;

import com.pentaho.install.*;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.PentahoServerParam.SERVER;
import com.pentaho.install.post.tomcat.conf.server.Server;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TomcatXMLHelper extends XMLHelper {
	static Map<String, String> RESOURCE_MAP;
	
	static {
		RESOURCE_MAP = new LinkedHashMap<String, String>();
		RESOURCE_MAP.put("name","");
		RESOURCE_MAP.put("auth","Container");
		RESOURCE_MAP.put("type","javax.sql.DataSource");
		RESOURCE_MAP.put("factory","org.apache.commons.dbcp.BasicDataSourceFactory"); 
		RESOURCE_MAP.put("maxTotal","20");
		RESOURCE_MAP.put("maxIdle","5");
		RESOURCE_MAP.put("maxWaitMillis","10000");
		RESOURCE_MAP.put("username","r");
		RESOURCE_MAP.put("password","");
		RESOURCE_MAP.put("driverClassName","");
		RESOURCE_MAP.put("url","");
		RESOURCE_MAP.put("validationQuery","");
	}
	
	InstallParam installParam;
	public TomcatXMLHelper(InstallParam installParam) {
		this.installParam = installParam;
	}
	
	public boolean updateContextXml() throws Exception {
		boolean success = false;
		
		String tomcatDir = installParam.installDir + "/server/" + PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType) + "/" + installParam.appServerDir;
		
		try {
			String appName = InstallUtil.getWebAppName(installParam.pentahoServerType);
			String contextDir = tomcatDir + "/webapps/" + appName + "/META-INF";
			contextDir = contextDir.replace('/', File.separatorChar);
			String ctxFile = contextDir + File.separator + "context.xml";
			System.out.print("\nUpdating tomcat configuration file " + ctxFile);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			createContextXml(doc);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(ctxFile));
			transformer.transform(source, result);
			
			if (installParam.pentahoServerType.equals(SERVER.DI)) {
				String confDir = tomcatDir + "/conf";
				confDir = confDir.replace('/', File.separatorChar);
				File serverFile = new File(confDir, "server.xml");
				List<String> lines = Files.readAllLines(serverFile.toPath());
				for (String line : lines) {
					line = line.replace("port=\"8080\"", "port=\"9080\"").replace("port=\"9443\"", "port=\"9443\"");
				}
				
				Files.write(serverFile.toPath(), lines, StandardOpenOption.WRITE);
			}
			
			success = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return success;
	}
	
	private void createContextXml(Document doc) {
		String hibernate = InstallUtil.getHibernateDatabaseName(installParam.pentahoServerType);
		DBInstance hibernateDbInstance = installParam.dbInstanceMap.get(hibernate);
		
		String quartz = InstallUtil.getQuartzDatabaseName(installParam.pentahoServerType);
		DBInstance quartzDbInstance = installParam.dbInstanceMap.get(quartz);
		
		String[][][] resources = {
				{
					{"name","jdbc/Hibernate"},
					{"username",hibernateDbInstance.getUsername()},
					{"password",hibernateDbInstance.getPassword()},
					{"url",InstallUtil.getJdbcUrl(hibernateDbInstance)}
				},
				{
					{"name","jdbc/Audit"},
					{"username",hibernateDbInstance.getUsername()},
					{"password",hibernateDbInstance.getPassword()},
					{"url",InstallUtil.getJdbcUrl(hibernateDbInstance)}
				},
				{
					{"name","jdbc/Quartz"},
					{"username",quartzDbInstance.getUsername()},
					{"password",quartzDbInstance.getPassword()},
					{"url",InstallUtil.getJdbcUrl(quartzDbInstance)}
				},
				{
					{"name","jdbc/pentaho_operations_mart"},
					{"username",(hibernateDbInstance.getType() == DB.Oracle ? "pentaho_operations_mart" : hibernateDbInstance.getUsername())},
					{"url",InstallUtil.getJdbcUrl(hibernateDbInstance/*, TODO "pentaho_operations_mart"*/)}
				},
				{
					{"name","jdbc/PDI_Operations_Mart"},
					{"username",(hibernateDbInstance.getType() == DB.Oracle ? "pentaho_operations_mart" : hibernateDbInstance.getUsername())},
					{"url",InstallUtil.getJdbcUrl(hibernateDbInstance/*, TODO "pentaho_operations_mart"*/)}
				}
		};
		
		Element root = doc.createElement("Context");
		doc.appendChild(root);
		addAttribute(root, new String[][]
				{{"path","/pentaho"},{"docbase","webapps/pentaho/"}}
		);
		
		//shared attributes for all Resources for this particular database type
		Map<String, String> map = new HashMap<String, String>();
		map.put("driverClassName",InstallUtil.getJdbcDriverClass(hibernateDbInstance.getType()));
		map.put("validationQuery",DBParam.getValidationQuery(hibernateDbInstance.getType()));
		
		for (String[][] resource : resources) {
			for (String[] r : resource) {
				map.put(r[0],r[1]);
			}
			RESOURCE_MAP.putAll(map);
			
			Element e = doc.createElement("Resource");
			addResourceAttribute(e);
			root.appendChild(e);
		}
	}
	
	private void addResourceAttribute(Element e) {
		for (String key : RESOURCE_MAP.keySet()) {
			e.setAttribute(key, RESOURCE_MAP.get(key));
		}
	}

	public void create() throws Exception {
        Server tomcatServer = new Server();

        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stringWriter);

        JAXBContext context = JAXBContext.newInstance(Server.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(tomcatServer, writer);
        writer.close();

        String xmlString = stringWriter.toString();
        //System.out.println(xmlString);

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = documentBuilder.parse(new InputSource(new StringReader(xmlString)));

        addComment(doc);


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        //StreamResult result = new StreamResult(new File(ctxFile));
        StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result);
    }

    private void addComment(Document doc) {
        Element e = doc.getDocumentElement();
	    String tagName = e.getTagName();
        System.out.println("tag: " + tagName);

        if ("Server".equals(tagName)) {
            Comment comment = doc.createComment("Comment before Server");
            e.getParentNode().insertBefore(comment, e);
        }

    }
}
