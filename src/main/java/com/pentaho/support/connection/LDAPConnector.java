package com.pentaho.support.connection;

import com.pentaho.install.InstallUtil;
import com.pentaho.install.LDAPParam;
import com.pentaho.install.post.PostInstaller;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LDAPConnector {
    public static void usage() {
        System.out.println("=================================================================================");
        System.out.println("options:");
        System.out.println("  -H, --host          LDAP server hostname or IP address");
        System.out.println("  -P, --port          LDAP server port number");
        System.out.println("  -U, --user          LDAP server username");
        System.out.println("  -W, --pass          LDAP server password");
        System.out.println("  -T, --type          LDAP server vendor [msad|openldap|apacheds]");
        System.out.println("");
        System.out.println("examples:");
        System.out.println("  1. Test connection to Microsoft Active Directory");
        System.out.println("     -H 10.0.0.1 -P 389 -U \"CN=Bob Smith,OU=SoftwareSupport,DC=pentaho,DC=com\" -W mypassword -T msad");
        System.out.println("");
        System.out.println("  2. Test connection to OpenLDAP");
        System.out.println("     -H 10.0.0.1 -P 389 -U \"cn=admin,dc=SoftwareSupport,ds=pentaho,dc=com\" -W mypassword -T openldap");
        System.out.println("");
        System.out.println("  3. Test connection to ApacheDS");
        System.out.println("     --host 10.0.0.1 --port 10389 --user \"uid=admin,ou=system\" --type apacheds");
        System.out.println("=================================================================================");
    }

	public static void main(String[] args) {
		String host = null, port = null, user = null, pass = null;
		LDAPParam.LDAP type = null;

        if (args.length == 0) {
            usage();
            System.exit(0);
        } else {
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
			System.out.println(ldapParam.getAdminUser());

			Hashtable<String, String> ldapEnv = new Hashtable<>(11);
			ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			ldapEnv.put(Context.PROVIDER_URL, url);
			ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			ldapEnv.put(Context.SECURITY_PRINCIPAL, ldapParam.getAdminUser());
			ldapEnv.put(Context.SECURITY_CREDENTIALS, ldapParam.getAdminPassword());
			ldapContext = new InitialDirContext(ldapEnv);
		} catch (CommunicationException ce) {
			InstallUtil.error(String.format("Could not connect to server [%s]:[%s]", ldapParam.getHost(), ldapParam.getPort()));
		} catch (Exception ex) {
		    String error = ex.getMessage();
		    if (error.contains("LDAP: error code 49")) {
		        System.out.println("User credentials are not correct");
            }

            if (PostInstaller.DEBUG) {
		        ex.printStackTrace();
            }
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
