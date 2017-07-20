package com.pentaho.support.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.*;
import java.security.Provider.Service;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CryptoUtil {
	public static void main(String[] args) {
	}

	public static void windowsKeystore(String windowsPassword) {
		try {
			KeyStore winks = KeyStore.getInstance("Windows-MY");
			winks.load(null, null);

			Enumeration aliases = winks.aliases();
			while (aliases.hasMoreElements()) {
				String alias = (String)aliases.nextElement();
				System.out.println("alias: " + alias);

				KeyStore.ProtectionParameter protectionParam =
						new KeyStore.PasswordProtection(windowsPassword.toCharArray());

				KeyStore.Entry entry = winks.getEntry(alias, protectionParam);
				KeyStore.PrivateKeyEntry priKeyEntry = (KeyStore.PrivateKeyEntry)entry;
				PrivateKey priKey = priKeyEntry.getPrivateKey();
				Certificate cert = priKeyEntry.getCertificate();

				System.out.println("certificate: " + cert);
				System.out.println("private key: " + priKey);
			}
		} catch (Exception ke) {
			System.out.println(ke.getMessage());
		}

		try {
			KeyStore winks = KeyStore.getInstance("Windows-ROOT");
			winks.load(null, null);

			Enumeration aliases = winks.aliases();
			while (aliases.hasMoreElements()) {
				String alias = (String)aliases.nextElement();
				System.out.println("alias: " + alias);

				KeyStore.Entry entry = winks.getEntry(alias, null);
				if (entry instanceof KeyStore.PrivateKeyEntry) {
					KeyStore.PrivateKeyEntry priKeyEntry = (KeyStore.PrivateKeyEntry)entry;
					PrivateKey priKey = priKeyEntry.getPrivateKey();
					Certificate cert = priKeyEntry.getCertificate();

					System.out.println("certificate: " + cert);
					System.out.println("private key: " + priKey);
				} else if (entry instanceof KeyStore.TrustedCertificateEntry) {
					KeyStore.TrustedCertificateEntry trustedEntry = (KeyStore.TrustedCertificateEntry)entry;
					Certificate cert = trustedEntry.getTrustedCertificate();
					System.out.println("certificate: " + cert);
				}
				System.out.println("----------------------------------------------------------------------------------------------------");
			}
		} catch (Exception ke) {
			System.out.println(ke.getMessage());
		}
	}

	public static void listKeystore(String keystoreFilename, String keystorePassword) {
		try {
			File keystoreFile = new File(keystoreFilename);
			FileInputStream fis = new FileInputStream(keystoreFile);
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

			KeyStore.ProtectionParameter protectionParam =
					new KeyStore.PasswordProtection(keystorePassword.toCharArray());

			keystore.load(fis, keystorePassword.toCharArray());
			Enumeration aliases = keystore.aliases();
			while (aliases.hasMoreElements()) {
				boolean needPassword = false;
				String alias = (String)aliases.nextElement();
				KeyStore.Entry entry = null;
				try {
					entry = keystore.getEntry(alias, null);
				} catch (UnrecoverableKeyException uke) {
					System.out.println(uke.getMessage());
					needPassword = true;
				}

				if (entry == null && needPassword) {
					entry = keystore.getEntry(alias, protectionParam);
				}

				if (entry != null) {
					System.out.println("----------------------------------------------------------------------------------------------------");
					if (entry instanceof KeyStore.TrustedCertificateEntry) {
						System.out.println("Entry is a trusted certificate");

						KeyStore.TrustedCertificateEntry trustedCert = (KeyStore.TrustedCertificateEntry)entry;
						Certificate cert = trustedCert.getTrustedCertificate();
						System.out.println(cert);
					} else if (entry instanceof KeyStore.PrivateKeyEntry) {
						System.out.println("Entry is a private key");

						KeyStore.PrivateKeyEntry priKey = (KeyStore.PrivateKeyEntry)entry;
						System.out.println("private key: " + priKey.getPrivateKey());

						Certificate cert = priKey.getCertificate();
						System.out.println("certificate type: " + cert.getType());
						PublicKey pubKey = cert.getPublicKey();
						System.out.println("pubKey: " + pubKey.toString());
					}
				}
			}
		} catch (FileNotFoundException fnfe) {
			//System.out.println("Could not find keystore file: " + keystoreFilename);
			System.out.println(fnfe.getMessage());
		} catch (KeyStoreException ke) {
			System.out.println(ke.getMessage());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static void findKey(String keystoreFilename, String keystorePassword, String keyPassword, String alias) {
		try {
			File keystoreFile = new File(keystoreFilename);
			FileInputStream fis = new FileInputStream(keystoreFile);
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

			keystore.load(fis, keystorePassword.toCharArray());
			Key key = keystore.getKey(alias, keyPassword.toCharArray());
			System.out.println(key);
			System.out.println("----------------------------------------------------------------------------------------------------");

			Certificate cert = keystore.getCertificate(alias);
			System.out.println(cert);
			System.out.println("----------------------------------------------------------------------------------------------------");

			PublicKey publicKey = cert.getPublicKey();
			System.out.println(publicKey);

		} catch (FileNotFoundException fnfe) {
			//System.out.println("Could not find keystore file: " + keystoreFilename);
			System.out.println(fnfe.getMessage());
		} catch (KeyStoreException ke) {
			System.out.println(ke.getMessage());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	private static void bc() {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void listCryptos() {
		for (Provider provider : Security.getProviders()) {
			System.out.println("Provider: " + provider.getName());
			System.out.println(provider.getInfo());
			System.out.println(provider.getVersion());
			System.out.println("--------------------");

			System.out.println("services:");
			System.out.println("--------------------");
			for (Service svc : provider.getServices()) {
				System.out.println("\ts\t" + svc);
			}

			System.out.println("properties:");
			System.out.println("--------------------");
			for (String key : provider.stringPropertyNames()) {
				System.out.println("\tp\t" + key + "=" + provider.getProperty(key));
			}

			System.out.println("=============================================");
		}
	}

	/**
	 * Test if the JDK has installed unlimited strengh policy files
	 */
	public static void isUnlimitedStrengthJCE() {
		String longKey = "pentaho secret key";	//18x8=144 bit
		String shortKey = "pentaho secret";		//14x8=112 bit
		String clearText = "Pentaho Business Analytics Server";

		try {
			boolean success = EncryptUtil.blowfish(shortKey, clearText);
		} catch (Exception ex) {
			System.out.println(ex);
		}

		try {
			boolean success = EncryptUtil.blowfish(longKey, clearText);
		} catch (java.security.InvalidKeyException ex) {
			System.out.println("-------------------------------------------------------------------------------------------------------------------");
			System.out.println("You don't have the JCE Unlimited Strength Jurisdiction Policy Files to use algorithm with keysize more than 128 bit");
			System.out.println("Go to this Oracle's page to download the files and place them at your JDK/JRE's lib/security directory");
			System.out.println("http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html");
			System.out.println("-------------------------------------------------------------------------------------------------------------------");
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

}
