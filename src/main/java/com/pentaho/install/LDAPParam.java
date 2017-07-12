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

	static {
		LDAP_PORT = new HashMap<>();
		LDAP_PORT.put(LDAP.APACHEDS, "10389");
		LDAP_PORT.put(LDAP.MSAD, "389");
	}

	private String adminUser, adminPassword;
	private LDAP type;
	private String host = "localhost";
	private String port = "";

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
}
