package com.pentaho.support;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.pentaho.install.DBParam;
import com.pentaho.install.LDAPParam;

public class LDAPConnector {
	public static void main(String[] args) {
		String host = null, port = null, user = null, pass = null;
		LDAPParam.LDAP type = null;

		for (int i=0;i<args.length;i++) {
			if (("--host".equals(args[i]) || "-H".equals(args[i])) && (i+1)<args.length) {
				host = args[++i];
			} else if (("--port".equals(args[i]) || "-P".equals(args[i])) && (i+1)<args.length) {
				port = args[++i];
			} else if (("--user".equals(args[i]) || "-U".equals(args[i])) && (i+1)<args.length) {
				user = args[++i];
			} else if (("--pass".equals(args[i]) || "-W".equals(args[i])) && (i+1)<args.length) {
				pass = args[++i];
			} else if (("--type".equals(args[i]) || "-T".equals(args[i])) && (i+1)<args.length) {
				String typeString = args[++i];
				try {
					type = guessLdapType(typeString);
				} catch (Exception ex) {
					System.err.println("Unknown LDAP server type: " + typeString);
				}
			}
		}

		if (user == null || pass == null || type == null) {
			System.err.println("Invalid LDAP parameters.");
			System.exit(0);
		}

		LDAPParam param = new LDAPParam();
		param.setHost(host==null?"":host);
		param.setPort(port==null?"":port);
		param.setAdminUser(user);
		param.setAdminPassword(pass);
		param.setType(type);

		System.out.println(param);

		LDAPConnector connector = new LDAPConnector();
		connector.test(param);
	}

	private static LDAPParam.LDAP guessLdapType(String typeString) {
		String str = typeString.toLowerCase();
		if (str.contains("ad") || str.contains("msad") || str.contains("active") || str.contains("microsoft")) {
			return LDAPParam.LDAP.MSAD;
		} else if (str.contains("apache") || str.contains("apacheds")) {
			return LDAPParam.LDAP.APACHEDS;
		}
		return LDAPParam.LDAP.valueOf(str);
	}

	public void test(LDAPParam ldapParam) {
		if (LDAPParam.LDAP.APACHEDS.equals(ldapParam.getType())) {
			String url = "ldap://" + ldapParam.getHost() + ":" + ldapParam.getPort();
			testApacheDSConnection(url, ldapParam.getAdminUser(), ldapParam.getAdminPassword());
		} else if (LDAPParam.LDAP.MSAD.equals(ldapParam.getType())) {
			String url = "ldap://" + ldapParam.getHost() + ":" + ldapParam.getPort();
			testMSADConnection(url, ldapParam.getAdminUser(), ldapParam.getAdminPassword());
		}
	}
	
	private void testMSADConnection(String url, String user, String password) {
		DirContext ldapContext = null;
		try {
			System.out.println("Connecting to " + url);

			Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
			ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			ldapEnv.put(Context.PROVIDER_URL, url);
			ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			ldapEnv.put(Context.SECURITY_PRINCIPAL, user);
			ldapEnv.put(Context.SECURITY_CREDENTIALS, password);
			ldapContext = new InitialDirContext(ldapEnv);
			
			System.out.println("\t[connected]");
			
			//searchUser(ldapContext,LDAPParam.LDAP.MSAD);
			//searchRole(ldapContext,LDAPParam.LDAP.MSAD);
			//searchGroup(ldapContext,LDAPParam.LDAP.MSAD);
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			close(ldapContext);
		}
	}

	private void testApacheDSConnection(String url, String user, String password) {
		DirContext ldapContext = null;

		try {
			System.out.println("Connecting to " + url);

			Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
			ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			ldapEnv.put(Context.PROVIDER_URL, url);
			ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			ldapEnv.put(Context.SECURITY_PRINCIPAL, user);
			ldapEnv.put(Context.SECURITY_CREDENTIALS, password);
			ldapContext = new InitialDirContext(ldapEnv);

			System.out.println("\t[connected]");

			//searchUser(ldapContext,LDAPParam.LDAP.APACHEDS);
			//searchRole(ldapContext,LDAPParam.LDAP.APACHEDS);
			//searchGroup(ldapContext,LDAPParam.LDAP.APACHEDS);
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			close(ldapContext);
		}
	}

	private void close(DirContext ldapContext) {
		if (ldapContext != null) {
			try {
				ldapContext.close();
			} catch (Exception ex) {
			}
		}
	}
}
