package com.pentaho.support.connection;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.pentaho.install.InstallUtil;
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
		} else if (str.contains("openldap")) {
			return LDAPParam.LDAP.OPENLDAP;
		}
		return LDAPParam.LDAP.valueOf(str);
	}

	public boolean test(LDAPParam ldapParam) {
		boolean connected = false;
		DirContext ldapContext = null;
		try {
			ldapContext = connect(ldapParam);
			if (ldapContext != null) {
				connected = true;
				System.out.println("\t[connected]");
			}
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			close(ldapContext);
		}
		return connected;
	}

	private DirContext connect(LDAPParam ldapParam) {
		DirContext ldapContext = null;
		try {
			String url = "ldap://" + ldapParam.getHost() + ":" + ldapParam.getPort();
			System.out.println("Connecting to " + url);

			Hashtable<String, String> ldapEnv = new Hashtable<>(11);
			ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			ldapEnv.put(Context.PROVIDER_URL, url);
			ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			ldapEnv.put(Context.SECURITY_PRINCIPAL, ldapParam.getAdminUser());
			ldapEnv.put(Context.SECURITY_CREDENTIALS, ldapParam.getAdminPassword());
			ldapContext = new InitialDirContext(ldapEnv);
		} catch (Exception ex) {
			System.err.println(ex);
		}

		return ldapContext;
	}


	public void searchUser(LDAPParam ldapParam) {
		DirContext ldapContext = null;
		try {
			ldapContext = connect(ldapParam);
			if (ldapContext != null) {
				String searchFilter = ldapParam.getUserSearchFilter();
				String searchBase = ldapParam.getUserSearchBase();

				System.out.println("User search filter: " + searchFilter);
				System.out.println("User search base: " + searchBase);

				SearchControls searchControls = new SearchControls();
				searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				searchControls.setReturningAttributes(new String[]{"sn","cn"});

				NamingEnumeration<SearchResult> results = ldapContext.search(searchBase, searchFilter, searchControls);

				System.out.println("User search result:");
				InstallUtil.bar();
				while (results.hasMoreElements()) {
					SearchResult searchResult = results.nextElement();

					Attributes attrs = searchResult.getAttributes();
					NamingEnumeration ne = attrs.getAll();
					while (ne.hasMoreElements()) {
						BasicAttribute attr = (BasicAttribute)ne.nextElement();
						System.out.println("\t" + attr);
					}
					System.out.println("------------------------------");
				}
			}
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			close(ldapContext);
		}
	}

	public void searchGroup(LDAPParam ldapParam) {
		DirContext ldapContext = null;
		try {
			ldapContext = connect(ldapParam);
			if (ldapContext != null) {
				String searchFilter = ldapParam.getGroupSearchFilter();
				String searchBase = ldapParam.getGroupSearchBase();

				System.out.println("Group search filter: " + searchFilter);
				System.out.println("Group search base: " + searchBase);

				SearchControls searchControls = new SearchControls();
				searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				searchControls.setReturningAttributes(new String[]{"uniqueMember","cn"});

				NamingEnumeration<SearchResult> results = ldapContext.search(searchBase, searchFilter, searchControls);

				System.out.println("Group search result:");
				InstallUtil.bar();
				while (results.hasMoreElements()) {
					SearchResult searchResult = results.nextElement();

					Attributes attrs = searchResult.getAttributes();
					NamingEnumeration ne = attrs.getAll();
					while (ne.hasMoreElements()) {
						BasicAttribute attr = (BasicAttribute)ne.nextElement();
						System.out.println("\t" + attr);
					}
					System.out.println("------------------------------");
				}
			}
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
