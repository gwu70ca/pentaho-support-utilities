package com.pentaho.install.verify;


import com.pentaho.install.*;
import com.pentaho.install.input.StringInput;
import com.pentaho.install.post.LocationChooser;
import com.pentaho.install.post.ServerChooser;
import com.pentaho.install.post.spring.Bean;
import com.pentaho.support.connection.LDAPConnector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;

import static com.pentaho.install.LDAPParam.LDAP;

public class Verifier {
    static Scanner scanner;
    static String auth;
    static String AUTH_LDAP = "ldap";
    static String AUTH_JACKRABBIT = "jackrabbit";

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

            findAuth(serverRoot);
            InstallUtil.output("provider: " + auth);

            if (AUTH_LDAP.equals(auth)) {
                verifyLdap(serverRoot);
            }

            //verifyJackrabbit(serverRoot);
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
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


    public static boolean verifyJackrabbit(String serverRoot) {
        return false;
    }
}
