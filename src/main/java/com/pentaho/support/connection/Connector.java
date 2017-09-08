package com.pentaho.support.connection;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.LDAPParam;
import com.pentaho.install.LDAPParam.LDAP;
import com.pentaho.install.db.Dialect;
import com.pentaho.install.input.*;
import com.pentaho.support.Pentsu;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Scanner;

import static com.pentaho.install.InstallUtil.EXIT;
import static com.pentaho.install.InstallUtil.NEW_LINE;

public class Connector {
    static String LDAP_SERVER = "ldap_server";
    static String LDAP_PORT = "ldap_port";
    static String LDAP_USER = "ldap_user";
    static String LDAP_USER_CN = "ldap_user_cn";
    static String LDAP_USER_SAM = "ldap_user_sam";
    static String LDAP_USER_SEARCH_BASE = "ldap_user_search_base";
    static String LDAP_GROUP_CN = "ldap_group_cn";
    static String LDAP_GROUP_SAM = "ldap_group_sam";
    static String LDAP_GROUP_SEARCH_BASE = "ldap_group_search_base";
    static String DB_SERVER = "db_host";
    static String DB_PORT = "db_port";
    static String DB_USER = "db_user";

    private Scanner scanner;
    private Properties history;

    private File pensuFile = new File(System.getProperty("user.home"), Pentsu.PENSU);

    public Connector(Scanner scanner) {
        this.scanner = scanner;
    }

    private String[] menuPrompt() {
        StringBuffer txt = new StringBuffer();
        txt.append(NEW_LINE).append(InstallUtil.bar()).append(NEW_LINE);

        int index = 1;
        txt.append(index++ + ": Test JDBC Connection").append(NEW_LINE);
        txt.append(index++ + ": Test LDAP Connection").append(NEW_LINE);
        txt.append(InstallUtil.exitMenuEntry()).append(NEW_LINE);

        txt.append(InstallUtil.bar()).append(NEW_LINE);
        txt.append("? ");
        return new String[]{txt.toString(), "0,1,2"};
    }

    private void loadHistory() {
        history = new Properties();

        if (pensuFile.exists()) {
            try {
                history.load(new FileReader(pensuFile.getAbsolutePath()));
            } catch (Exception ex) {
                InstallUtil.error(ex.getMessage());
            }
        }
    }

    private void saveHistory() {
        System.out.println(pensuFile.getAbsolutePath());

        try {
            history.store(new FileWriter(pensuFile.getAbsolutePath()), "");
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }

    }

    public void execute() {
        loadHistory();

        try {
            while (true) {
                InstallUtil.newLine();
                String[] menu = menuPrompt();
                SelectInput rootInput = new SelectInput(menu[0], menu[1].split(","));
                InstallUtil.ask(scanner, rootInput);

                String index = rootInput.getValue();
                if (EXIT.equals(index)) {
                    break;
                }

                switch (index) {
                    case "1":
                        testJdbc();
                        break;
                    case "2":
                        testLdap();
                        break;
                }
            }
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }
    }

    private String[] dbTypePrompt() {
        StringBuffer txt = new StringBuffer();
        StringBuffer opt = new StringBuffer();
        txt.append(NEW_LINE).append(InstallUtil.bar()).append(NEW_LINE);

        int index = 1;
        for (DB db : DB.values()) {
            opt.append(index).append(",");
            txt.append(index++).append(": ").append(db).append(NEW_LINE);
        }

        txt.append(InstallUtil.bar()).append(NEW_LINE);
        txt.append("Select the database type: ");
        return new String[]{txt.toString(), opt.substring(0, opt.length() - 1)};
    }

    private void testJdbc() {
        InstallUtil.newLine();
        String[] prompt = dbTypePrompt();
        SelectInput dbTypeInput = new SelectInput(prompt[0], prompt[1].split(","));
        InstallUtil.ask(scanner, dbTypeInput);
        DB dbType = DB.values()[Integer.parseInt(dbTypeInput.getValue()) - 1];
        System.out.println(dbType);

        Dialect dialect = InstallUtil.createDialect(dbType);

        String defaultAdminUser = dialect.getDefaultAdmin();
        DBInstance dbInstance = new DBInstance("", defaultAdminUser, "", dbType, "");

        StringInput dbHostInput = new StringInput("");
        setDefaultValue(DB_SERVER, dbInstance.getHost(), dbHostInput);
        dbHostInput.setPrompt(String.format("Database hostname or IP address [%s]: ", dbHostInput.getDefaultValue()));
        InstallUtil.ask(scanner, dbHostInput);
        dbInstance.setHost(dbHostInput.getValue());

        IntegerInput dbPortInput = new IntegerInput("");
        setDefaultValue(DB_PORT, dbInstance.getPort(), dbPortInput);
        dbPortInput.setPrompt(String.format("Database port [%s]: ", dbPortInput.getDefaultValue()));
        InstallUtil.ask(scanner, dbPortInput);
        dbInstance.setPort(dbPortInput.getValue());

        boolean winAuth = false, useJtds = false, needUsernameAndPassword = false;
        String domain = "";
        if (DB.Sqlserver.equals(dbType)) {
            BooleanInput jtdsInput = new BooleanInput("Do you want to use jDTS driver [y/n]? ");
            InstallUtil.ask(scanner, jtdsInput);
            useJtds = jtdsInput.yes();
            dbInstance.setJtds(useJtds);

            BooleanInput wiaInput = new BooleanInput("Do you want to use Integrated Windows Authentication [y/n]? ");
            InstallUtil.ask(scanner, wiaInput);
            winAuth = wiaInput.yes();
            dbInstance.setWinAuth(winAuth);

            if (useJtds && wiaInput.yes()) {
                try {
                    domain = InetAddress.getLocalHost().getCanonicalHostName();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                StringInput domainInput = new StringInput("What is the domain name? ");
                domainInput.setDefaultValue(domain);
                InstallUtil.ask(scanner, domainInput);
                dbInstance.setDomain(domainInput.getValue());
            }

            //Non Windows OS still needs username/password
            needUsernameAndPassword = !InstallUtil.isWindows();
        }

        if (!winAuth || needUsernameAndPassword) {
            StringInput userInput = new StringInput("");
            setDefaultValue(DB_USER, defaultAdminUser, userInput);
            userInput.setPrompt(String.format("Database username [%s]: ", userInput.getDefaultValue()));
            InstallUtil.ask(scanner, userInput);
            dbInstance.setUsername(userInput.getValue());

            StringInput passwordInput = new StringInput("Database password: ");
            InstallUtil.ask(scanner, passwordInput);
            dbInstance.setPassword(passwordInput.getValue());
        }

        if (!dbType.equals(DB.Orcl)) {
            DBNameInput dbNameInput = new DBNameInput(String.format("Input database name [%s]: ", ""), dialect.getDbNameLength());
            dbNameInput.setDefaultValue("");
            InstallUtil.ask(scanner, dbNameInput);

            dbInstance.setName(dbNameInput.getValue());
        }

        System.out.println(dbInstance);

        JDBCConnector connector = new JDBCConnector();
        if (connector.test(dbInstance)) {
            history.setProperty(DB_SERVER, dbInstance.getHost());
            history.setProperty(DB_PORT, dbInstance.getPort());
            history.setProperty(DB_USER, dbInstance.getUsername());

            BooleanInput input = new BooleanInput("Do you want to run a SQL query, [y/n]? ");
            InstallUtil.ask(scanner, input);
            if (input.yes()) {
                StringInput sqlInput = new StringInput("Input your SQL: \n");
                InstallUtil.ask(scanner, sqlInput);
                connector.executeSql(dbInstance, sqlInput.getValue());
            }
        }

        saveHistory();
    }

    private String[] ldapTypePrompt() {
        StringBuffer txt = new StringBuffer();
        StringBuffer opt = new StringBuffer();
        txt.append(NEW_LINE).append(InstallUtil.bar()).append(NEW_LINE);

        int index = 1;
        for (LDAP ldap : LDAP.values()) {
            opt.append(index).append(",");
            txt.append(index++).append(": ").append(ldap.getFullname()).append(NEW_LINE);
        }

        txt.append(InstallUtil.bar()).append(NEW_LINE);
        txt.append("Select the database type: ");
        return new String[]{txt.toString(), opt.substring(0, opt.length() - 1)};
    }

    public void setDefaultValue(String name, String defaultValue, Input input) {
        String value = history.getProperty(name);
        //System.out.println(name + "=" + value);
        if (InstallUtil.isBlankOrNull(value)) {
            value = defaultValue;
        }
        input.setDefaultValue(value);
    }

    private void testLdap() {
        InstallUtil.newLine();

        //Get LDAP server type
        String[] prompt = ldapTypePrompt();
        SelectInput ldapTypeInput = new SelectInput(prompt[0], prompt[1].split(","));
        InstallUtil.ask(scanner, ldapTypeInput);
        LDAP ldapType = LDAP.values()[Integer.parseInt(ldapTypeInput.getValue()) - 1];
        System.out.println(ldapType);

        LDAPParam ldapParam = new LDAPParam();
        ldapParam.setType(ldapType);

        //Get LDAP server hostname or ip address
        StringInput ldapHostInput = new StringInput("");
        setDefaultValue(LDAP_SERVER, ldapParam.getHost(), ldapHostInput);
        ldapHostInput.setPrompt(String.format("LDAP server hostname or IP address [%s]: ", ldapHostInput.getDefaultValue()));
        InstallUtil.ask(scanner, ldapHostInput);
        ldapParam.setHost(ldapHostInput.getValue());

        IntegerInput ldapPortInput = new IntegerInput("");
        setDefaultValue(LDAP_PORT, ldapParam.getPort(), ldapPortInput);
        ldapPortInput.setPrompt(String.format("LDAP server port [%s]: ", ldapPortInput.getDefaultValue()));
        InstallUtil.ask(scanner, ldapPortInput);
        ldapParam.setPort(ldapPortInput.getValue());

        StringInput adminUserInput = new StringInput("");
        setDefaultValue(LDAP_USER, "", adminUserInput);
        adminUserInput.setPrompt(String.format("DN or username [%s]: ", adminUserInput.getDefaultValue()));
        InstallUtil.ask(scanner, adminUserInput);
        ldapParam.setAdminUser(adminUserInput.getValue());

        StringInput adminPasswordInput = new StringInput("Password: ");
        InstallUtil.ask(scanner, adminPasswordInput);
        ldapParam.setAdminPassword(adminPasswordInput.getValue());

        LDAPConnector connector = new LDAPConnector();
        boolean connected = connector.test(ldapParam);

        if (connected) {
            history.setProperty(LDAP_SERVER, ldapParam.getHost());
            history.setProperty(LDAP_PORT, ldapParam.getPort());
            history.setProperty(LDAP_USER, ldapParam.getAdminUser());

            BooleanInput searchUserInput = new BooleanInput("\nDo you want to search user [y/n]? ");
            InstallUtil.ask(scanner, searchUserInput);
            if (searchUserInput.yes()) {
                if (LDAP.MSAD.equals(ldapType)) {
                    BooleanInput useSamInput = new BooleanInput("Do you want to use SAM Account Name to perform the search [y/n]? ");
                    InstallUtil.ask(scanner, useSamInput);
                    ldapParam.setUseSamAccountName(useSamInput.yes());
                }

                String accountType = !ldapParam.isUseSamAccountName() ? "Common Name [%s]: " : "Sam Account Name [%s]: ";
                StringInput ldapUserInput = new StringInput("");
                //Allow empty name to return all users
                ldapUserInput.setDefaultValue("");

                setDefaultValue(!ldapParam.isUseSamAccountName() ? LDAP_USER_CN : LDAP_USER_SAM, "", ldapUserInput);
                ldapUserInput.setPrompt(String.format(accountType, ldapUserInput.getDefaultValue()));

                InstallUtil.ask(scanner, ldapUserInput);
                ldapParam.setUserSearchFilter(InstallUtil.getLdapUserSearchFilter(ldapParam, ldapUserInput.getValue()));

                StringInput ldapSearchBaseInput = new StringInput("");
                setDefaultValue(LDAP_USER_SEARCH_BASE, "", ldapSearchBaseInput);
                ldapSearchBaseInput.setPrompt(String.format("LDAP user search base [%s]: ", ldapSearchBaseInput.getDefaultValue()));
                InstallUtil.ask(scanner, ldapSearchBaseInput);
                ldapParam.setUserSearchBase(ldapSearchBaseInput.getValue());

                connector.searchUser(ldapParam);

                history.setProperty(!ldapParam.isUseSamAccountName() ? LDAP_USER_CN : LDAP_USER_CN, ldapUserInput.getValue());
                history.setProperty(LDAP_USER_SEARCH_BASE, ldapSearchBaseInput.getValue());
            }

            ldapParam.setUseSamAccountName(false);
            InstallUtil.newLine();

            BooleanInput searchGroupInput = new BooleanInput("\nDo you want to search group [y/n]? ");
            InstallUtil.ask(scanner, searchGroupInput);
            if (searchGroupInput.yes()) {
                if (LDAP.MSAD.equals(ldapType)) {
                    BooleanInput useSamInput = new BooleanInput("Do you want to use SAM Account Name to perform the search [y/n]? ");
                    InstallUtil.ask(scanner, useSamInput);
                    ldapParam.setUseSamAccountName(useSamInput.yes());
                }

                String accountType = !ldapParam.isUseSamAccountName() ? "Common Name [%s]: " : "Sam Account Name [%s]: ";
                StringInput ldapGroupInput = new StringInput("");
                //Allow empty name to return all groups
                ldapGroupInput.setDefaultValue("");

                setDefaultValue(!ldapParam.isUseSamAccountName() ? LDAP_GROUP_CN : LDAP_GROUP_SAM, "", ldapGroupInput);
                ldapGroupInput.setPrompt(String.format(accountType, ldapGroupInput.getDefaultValue()));

                InstallUtil.ask(scanner, ldapGroupInput);
                ldapParam.setGroupSearchFilter(InstallUtil.getLdapGroupSearchFilter(ldapParam, ldapGroupInput.getValue()));

                StringInput ldapSearchBaseInput = new StringInput("");
                setDefaultValue(LDAP_GROUP_SEARCH_BASE, "", ldapSearchBaseInput);
                ldapSearchBaseInput.setPrompt(String.format("LDAP search base [%s]: ", ldapSearchBaseInput.getDefaultValue()));
                InstallUtil.ask(scanner, ldapSearchBaseInput);
                ldapParam.setGroupSearchBase(ldapSearchBaseInput.getValue());

                connector.searchGroup(ldapParam);

                history.setProperty(!ldapParam.isUseSamAccountName() ? LDAP_GROUP_CN : LDAP_GROUP_SAM, ldapGroupInput.getValue());
                history.setProperty(LDAP_GROUP_SEARCH_BASE, ldapSearchBaseInput.getValue());
            }
        }

        saveHistory();
    }

    public static void main(String[] args) {
        InstallUtil.output("This program verifies connectivity of Database server/LDAP server");

        Scanner scanner = new Scanner(System.in);
        Connector c = new Connector(scanner);
        c.execute();
        scanner.close();
    }
}
