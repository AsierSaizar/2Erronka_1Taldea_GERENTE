package com.example.gerenteapp;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtil {
    private static final String SHA_CRYPT = "SHA-256";
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_ALGORITHM_GCM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 16;
    private static final String PASSPHRASE = "mySecurePassphrase123!";  // Cambia esta clave si es necesario

    // Cifrar un mensaje
    public static String encrypt(String plainText) throws Exception {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        SecretKeySpec aesKey = generateAesKeyFromPassphrase();
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] combinedIvAndCipherText = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combinedIvAndCipherText, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combinedIvAndCipherText, iv.length, encryptedBytes.length);
        return Base64.getEncoder().encodeToString(combinedIvAndCipherText);
    }

    // Descifrar un mensaje
    public static String decrypt(String encryptedBase64) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
        byte[] iv = new byte[IV_LENGTH];
        byte[] cipherText = new byte[encryptedBytes.length - IV_LENGTH];

        System.arraycopy(encryptedBytes, 0, iv, 0, IV_LENGTH);
        System.arraycopy(encryptedBytes, IV_LENGTH, cipherText, 0, cipherText.length);

        SecretKeySpec aesKey = generateAesKeyFromPassphrase();
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);
        byte[] decryptedBytes = cipher.doFinal(cipherText);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // Generar la clave AES a partir de la passphrase
    private static SecretKeySpec generateAesKeyFromPassphrase() throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance(SHA_CRYPT);
        byte[] keyBytes = sha256.digest(PASSPHRASE.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }
}
