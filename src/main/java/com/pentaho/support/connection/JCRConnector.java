package com.pentaho.support.connection;

import org.apache.jackrabbit.commons.JcrUtils;

import javax.jcr.GuestCredentials;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.util.HashMap;
import java.util.Map;

public class JCRConnector {
    public static void main(String[] args) throws Exception {
        login();
    }

    /**
     * Anonymously login
     * @throws Exception
     */
    public static void anonymousLogin() throws Exception {
        Repository repository = JcrUtils.getRepository();

        Session session = repository.login(new GuestCredentials());
        try {

            String user = session.getUserID();
            String name = repository.getDescriptor(Repository.REP_NAME_DESC);
            System.out.println(
                    "Logged in as " + user + " to a " + name + " repository.");
        } finally {
            session.logout();
        }
    }

    public static void login() throws Exception {
        String url = "http://172.20.64.126:8080/server";
        System.out.println("Connecting to " + url);

        Map<String, String> parameters = new HashMap();
        parameters.put("org.apache.jackrabbit.repository.uri", url);

        Repository repository = JcrUtils.getRepository(parameters);
        SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
        Session jcrSession = repository.login(creds);// .login(creds, "default");
        System.out.println("Login successful, workspace: " + jcrSession.getWorkspace());
    }
}
