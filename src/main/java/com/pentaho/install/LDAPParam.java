package com.pentaho.install;

import java.util.HashMap;
import java.util.Map;

public class LDAPParam {
	public enum LDAP {
		APACHEDS ("Apache Directory Server"),
		MSAD ("Microsoft Active Directory");

		private final String fullname;

		LDAP(String name) {
			this.fullname = name;
		}

		public String getFullname() {
			return this.fullname;
		}
	}

	public static Map<LDAP, String> LDAP_PORT;
	//public static Map<LDAP, String> LDAP_USER_SEARCH_FILTER;

	public static final String AHACHEDS_USER_SEARCH_FILTER_CN = "(objectClass=person)";
	public static final String AHACHEDS_GROUP_SEARCH_FILTER_CN = "(objectClass=groupOfUniqueNames)";

	public static final String MSAD_USER_SEARCH_FILTER = "(objectClass=user)";
	public static final String MSAD_GROUP_SEARCH_FILTER = "(objectClass=group)";

	static {
		LDAP_PORT = new HashMap<>();
		LDAP_PORT.put(LDAP.APACHEDS, "10389");
		LDAP_PORT.put(LDAP.MSAD, "389");

		//LDAP_USER_SEARCH_FILTER = new HashMap<>();
		//LDAP_USER_SEARCH_FILTER.put(LDAP.APACHEDS, "(objectClass=person)");
		//LDAP_USER_SEARCH_FILTER.put(LDAP.MSAD, "(sAMAccountName=*)");
	}

	private String adminUser, adminPassword;
	private LDAP type;
	private String host = "localhost";
	private String port = "";

	private String userSearchFilter;
	private String userSearchBase;
	private boolean useSamAccountName;
	private String groupSearchFilter;
	private String groupSearchBase;

	public LDAPParam() {
	}

	public String getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public LDAP getType() {
		return type;
	}

	public void setType(LDAP type) {
		this.type = type;
		this.port = LDAP_PORT.get(type);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String toString() {
		return "Host: " + this.host + ", Port: " + this.port + ", User: " + this.adminUser + ", Pass: " + this.adminPassword
				+ ", type: " + this.type;
	}

	public String getUserSearchFilter() {
		return userSearchFilter;
	}

	public void setUserSearchFilter(String userSearchFilter) {
		this.userSearchFilter = userSearchFilter;
	}

	public String getUserSearchBase() {
		return userSearchBase;
	}

	public void setUserSearchBase(String userSearchBase) {
		this.userSearchBase = userSearchBase;
	}

	public boolean isUseSamAccountName() {
		return useSamAccountName;
	}

	public void setUseSamAccountName(boolean useSamAccountName) {
		this.useSamAccountName = useSamAccountName;
	}

	public String getGroupSearchFilter() {
		return groupSearchFilter;
	}

	public void setGroupSearchFilter(String groupSearchFilter) {
		this.groupSearchFilter = groupSearchFilter;
	}

	public String getGroupSearchBase() {
		return groupSearchBase;
	}

	public void setGroupSearchBase(String groupSearchBase) {
		this.groupSearchBase = groupSearchBase;
	}
}
