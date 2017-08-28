package com.pentaho.install.verify;


import com.pentaho.install.*;
import com.pentaho.install.db.Dialect;
import com.pentaho.install.input.StringInput;
import com.pentaho.install.post.LocationChooser;
import com.pentaho.install.post.ServerChooser;
import com.pentaho.install.post.hibernate.Configuration;
import com.pentaho.install.post.hibernate.SessionFactory;
import com.pentaho.install.post.hibernate.Settings;
import com.pentaho.install.post.jackrabbit.Repository;
import com.pentaho.install.post.spring.Bean;
import com.pentaho.install.post.spring.Property;
import com.pentaho.install.post.tomcat.webapps.Context;
import com.pentaho.support.connection.JDBCConnector;
import com.pentaho.support.connection.LDAPConnector;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pentaho.install.LDAPParam.LDAP;

public class Verifier {
    static Scanner scanner;
    static String auth;
    static String dbString;
    static DBParam.DB dbType;
    static Dialect dialect;

    static String AUTH_LDAP = "ldap";
    static String AUTH_JACKRABBIT = "jackrabbit";

    static Pattern p = Pattern.compile("system/hibernate/(.*)\\.hibernate\\.cfg\\.xml");

    public static void main(String[] args) {
        verify();
    }

    public static void verify() {
        scanner = new Scanner(System.in);
        try {
            InstallParam installParam = new InstallParam();

            //Get server type
            ServerChooser sc = new ServerChooser(scanner);

            ActionResult result = sc.execute();
            installParam.pentahoServerType = (PentahoServerParam.SERVER) result.getReturnedValue();

            //Get server location
            LocationChooser loc = new LocationChooser(scanner);
            loc.setServerType(installParam.pentahoServerType);
            result = loc.execute();
            installParam.installDir = (String) result.getReturnedValue();

            String serverRoot = InstallUtil.getServerRootDir(installParam);
            InstallUtil.output("Server root: " + serverRoot);

            verifyHibernate(serverRoot);

            //verifyJackrabbit(serverRoot);

            //verifyTomcat(serverRoot);

            findAuth(serverRoot);
            InstallUtil.output("provider: " + auth);
            /*if (AUTH_LDAP.equals(auth)) {
                verifyLdap(serverRoot);
            }*/
        } catch (Exception ex) {
            //InstallUtil.error(ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    public static void findAuth(String serverRoot) throws Exception {
        File securityPropertiesFile = new File(serverRoot, "/pentaho-solutions/system/security.properties");
        if (!securityPropertiesFile.exists()) {
            throw new Exception(String.format("Could not find file [%s]", securityPropertiesFile.getAbsolutePath()));
        }

        Properties securityProp = new Properties();
        securityProp.load(new FileReader(securityPropertiesFile));
        auth = securityProp.getProperty("provider");
    }

    public static boolean verifyHibernate(String serverRoot) throws Exception {
        File hibernateSettingsFile = new File(serverRoot, "/pentaho-solutions/system/hibernate/hibernate-settings.xml");
        if (!hibernateSettingsFile.exists()) {
            throw new Exception(String.format("Could not find file [%s]", hibernateSettingsFile.getAbsolutePath()));
        }

        Settings hibernateSettings = new Settings();
        JAXBContext context = JAXBContext.newInstance(hibernateSettings.getClass());
        Unmarshaller unmarshaller = context.createUnmarshaller();
        hibernateSettings = (Settings)unmarshaller.unmarshal(new FileReader(hibernateSettingsFile));
        String configFile = hibernateSettings.getConfigFile();
        //System.out.println("configFile:" + configFile);
        Matcher m = p.matcher(configFile);
        if (m.find()) {
            dbString = m.group(1);
            System.out.println("dbString: " + dbString);
        }

        File dbHibernateFile = new File(serverRoot, "/pentaho-solutions/system/hibernate/" + dbString + ".hibernate.cfg.xml");
        if (!dbHibernateFile.exists()) {
            throw new Exception(String.format("Could not find file [%s]", dbHibernateFile.getAbsolutePath()));
        }
        System.out.println("dbHibernateFile: " + dbHibernateFile);

        Configuration configuration = new Configuration();
        context = JAXBContext.newInstance(configuration.getClass());
        unmarshaller = context.createUnmarshaller();
        configuration = (Configuration)unmarshaller.unmarshal(new FileReader(dbHibernateFile));
        SessionFactory factory = configuration.getSessionFactory();

        Map<String, Property> propertyMap = new HashMap<>();
        List<Property> pl = factory.getPropertyList();
        for (Property p : pl) {
            propertyMap.put(p.getName(), p);
            //System.out.println(p.getName() + ", " + p.getValue());
        }

        dbType = DBParam.DB.getDB(dbString);
        //System.out.println("dbType:" + dbType);

        dialect = InstallUtil.createDialect(dbType);

        String url = propertyMap.get(SessionFactory.connectionUrl).getValue();
        String username = propertyMap.get(SessionFactory.connectionUsername).getValue();
        String password = propertyMap.get(SessionFactory.connectionPassword).getValue();
        String[] dbParams = dialect.parse(url);

        DBInstance dbInstance = new DBInstance(dbParams[2], username, password, dbType);
        dbInstance.setHost(dbParams[0]);
        dbInstance.setPort(dbParams[1]);
        JDBCConnector jdbcConnector = new JDBCConnector();
        jdbcConnector.test(dbInstance);

        return false;
}

    public static boolean verifyJackrabbit(String serverRoot) throws Exception {
        File repositoryFile = new File(serverRoot, "/pentaho-solutions/system/jackrabbit/repository.xml");
        if (!repositoryFile.exists()) {
            throw new Exception(String.format("Could not find file [%s]", repositoryFile.getAbsolutePath()));
        }

        Repository repository = new Repository();
        JAXBContext context = JAXBContext.newInstance(repository.getClass());
        Unmarshaller unmarshaller = context.createUnmarshaller();
        repository = (Repository)unmarshaller.unmarshal(new FileReader(repositoryFile));

        //FileSystem fileSystem = repository.getFileSystem();
        //System.out.println("file system: " + fileSystem.getDriver().getValue());

        //DataStore dataStore = repository.getDataStore();
        //System.out.println("data store :" + dataStore.getDriver());

        return false;
    }

    public static boolean verifyTomcat(String serverRoot) throws Exception {
        boolean succeeded = false;

        File appCtxSecFile = new File(serverRoot, "/tomcat/webapps/pentaho/META-INF/context.xml");
        if (!appCtxSecFile.exists()) {
            throw new Exception(String.format("Could not find file [%s]", appCtxSecFile.getAbsolutePath()));
        }

        Context tomcat = new Context();
        JAXBContext context = JAXBContext.newInstance(tomcat.getClass());
        Unmarshaller unmarshaller = context.createUnmarshaller();
        tomcat = (Context)unmarshaller.unmarshal(new FileReader(appCtxSecFile));

        System.out.println(tomcat.getHibernate().getDriverClassName() + ", " + tomcat.getHibernate().getName());
        System.out.println(tomcat.getAudit().getDriverClassName() + ", " + tomcat.getHibernate().getName());

        return succeeded;
    }

    public static boolean verifyLdap(String serverRoot) throws Exception {
        boolean succeeded = false;

        File appCtxSecFile = new File(serverRoot, "/pentaho-solutions/system/applicationContext-security-ldap.properties");
        if (!appCtxSecFile.exists()) {
            throw new Exception(String.format("Could not find file [%s]", appCtxSecFile.getAbsolutePath()));
        }

        boolean ldapPasswordEncoded = checkPasswordService(serverRoot);
        InstallUtil.output("Encoded password: " + ldapPasswordEncoded);

        Properties securityProp = new Properties();
        securityProp.load(new FileReader(appCtxSecFile));

        LDAPParam ldapParam = new LDAPParam();

        String providerUrl = securityProp.getProperty("contextSource.providerUrl");
        System.out.println(providerUrl);

        String adminUser = securityProp.getProperty("contextSource.userDn");
        String adminPassword = securityProp.getProperty("contextSource.password");
        if (ldapPasswordEncoded) {
            adminPassword = new String(Base64.getDecoder().decode(adminPassword));
        }

        String[] strs = providerUrl.split(":");
        if (strs.length < 3) {
            throw new Exception(String.format("Invalid provider url [%s]", providerUrl));
        }
        String ldapHost = strs[1].substring(2);
        String ldapPort = strs[2];

        ldapParam.setAdminUser(adminUser);
        ldapParam.setAdminPassword(adminPassword);
        ldapParam.setHost(ldapHost);
        ldapParam.setPort(ldapPort);

        LDAPConnector ldapConnector = new LDAPConnector();
        if (!ldapConnector.test(ldapParam)) {
            InstallUtil.output("Could not connect to the LDAP server");
        } else {
            succeeded = true;

            String providerType = securityProp.getProperty("providerType");
            ldapParam.setType("ldapCustomConfiguration".equals(providerType) ? LDAP.MSAD : LDAP.APACHEDS);

            String userSearchFilter = securityProp.getProperty("userSearch.searchFilter");
            boolean useSamToSearchUser = ldapParam.getType() == LDAP.MSAD && !userSearchFilter.contains("cn=");
            ldapParam.setUseSamAccountName(useSamToSearchUser);

            //Ask for CN or SAM Account
            StringInput searchUserInput = new StringInput(String.format("%s of the user to search: ", (useSamToSearchUser ? "SAM Account Name: " : "CN")));
            InstallUtil.ask(scanner, searchUserInput);
            String userToSearch = searchUserInput.getValue();

            //Search user
            ldapParam.setUserSearchFilter(InstallUtil.getLdapUserSearchFilter(ldapParam, userToSearch));
            ldapParam.setUserSearchBase(securityProp.getProperty("userSearch.searchBase"));
            ldapConnector.searchUser(ldapParam);

            String groupSearchFilter = securityProp.getProperty("populator.groupSearchFilter");
            boolean useSamToSearchGroup = ldapParam.getType() == LDAP.MSAD && !groupSearchFilter.contains("member=");
            ldapParam.setUseSamAccountName(useSamToSearchGroup);

            StringInput searchGroupInput = new StringInput(String.format("%s of the group to search: ", (useSamToSearchGroup ? "SAM Account Name: " : "CN")));
            InstallUtil.ask(scanner, searchGroupInput);
            String groupToSearch = searchGroupInput.getValue();

            ldapParam.setGroupSearchFilter(InstallUtil.getLdapGroupSearchFilter(ldapParam, groupToSearch));
            ldapParam.setGroupSearchBase(securityProp.getProperty("populator.groupSearchBase"));
            ldapConnector.searchGroup(ldapParam);
        }

        return succeeded;
    }

    //Check if the LDAP password is base64 encoded
    private static boolean checkPasswordService(String serverRoot) {
        boolean encoded = false;
        File appCtxSprSecLdapFile = new File(serverRoot, "/pentaho-solutions/system/applicationContext-spring-security-ldap.xml");

        if (appCtxSprSecLdapFile.exists()) {
            try {
                String content = new String(Files.readAllBytes(appCtxSprSecLdapFile.toPath()), StandardCharsets.UTF_8);
                int start = content.indexOf("<bean id=\"contextSource\"");
                int end = content.indexOf("</bean>", start);
                String beanDefString = content.substring(start, end+7);

                Bean bean = new Bean();
                JAXBContext context = JAXBContext.newInstance(bean.getClass());
                Unmarshaller unmarshaller = context.createUnmarshaller();
                bean = (Bean)unmarshaller.unmarshal(new StringReader(beanDefString));

                String password = bean.getPassword().getValue();
                encoded = password != null && password.contains("decrypt");
            } catch (Exception ex) {
                InstallUtil.error(ex.getMessage());
            }
        }

        return encoded;
    }

    private Context getTomcatContext(File tomcatContextFile) {
        Context context = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(tomcatContextFile);
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }

        return context;
    }

}
