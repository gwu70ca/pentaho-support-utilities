package com.pentaho.support.security;

import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;

public class CryptoUtil {
	public static void main(String[] args) {
		listCryptos();
	}

	public static void listCryptos() {
		for (Provider provider : Security.getProviders()) {
			System.out.println("Provider: " + provider.getName());
			
			System.out.println("--------------------");
			for (String key : provider.stringPropertyNames()) {
				System.out.println("\tp: " + key + "\t" + provider.getProperty(key));
			}
			
			System.out.println("services:");
			System.out.println("--------------------");
			for (Service svc : provider.getServices()) {
				System.out.println("\t" + svc.getType() + " ==> " + svc);
			}
			
			System.out.println("=============================================");
		}
	}
}
