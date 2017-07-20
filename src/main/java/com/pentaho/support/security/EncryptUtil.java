package com.pentaho.support.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptUtil {
    public static void main(String[] args) throws Exception {
        blowfish();
    }

    public static void blowfish() throws Exception {
        String clearText = "Pentaho Business Analytics Server";

        //the key must be less than 56 chars (56 x 8 = 448)
        String key1 = "pentaho secret pentaho secret pentaho secret pentaho sec";
        blowfish(key1, clearText);
    }

    public static boolean blowfish(String key, String clearText) throws Exception {
        byte[] byteArray = key.getBytes(StandardCharsets.ISO_8859_1);
        SecretKeySpec keySpec = new SecretKeySpec(byteArray, "Blowfish");
        System.out.println("key size in byte: " + byteArray.length);

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        byte[] enb = cipher.doFinal(clearText.getBytes("UTF-8"));
        String encryptedText = Base64.getEncoder().encodeToString(enb);
        System.out.println("Encrypted: " + encryptedText);

        Cipher cipher2 = Cipher.getInstance("Blowfish");
        cipher2.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] deb = cipher2.doFinal(Base64.getDecoder().decode(encryptedText.getBytes("UTF-8")));
        String decryptedText = new String(deb, "UTF-8");
        System.out.println("Decrypted: " + decryptedText);

        return clearText.equals(decryptedText);
    }
}
