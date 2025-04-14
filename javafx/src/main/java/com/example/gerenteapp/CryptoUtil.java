package com.example.gerenteapp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CryptoUtil {
        // Clave privada compartida (debe ser de 16, 24 o 32 bytes para AES)
        private static final String PRIVATE_KEY = "TralaleroTralala"; // Ejemplo: 16 caracteres

        // Método para encriptar un mensaje
        public static String encrypt(String plainText) throws Exception {
                if (plainText == null || plainText.isEmpty()) {
                        throw new IllegalArgumentException("El texto a encriptar no puede estar vacío.");
                }

                // Crear la clave secreta a partir de la clave privada
                SecretKeySpec secretKey = new SecretKeySpec(PRIVATE_KEY.getBytes(StandardCharsets.UTF_8), "AES");

                // Inicializar el cifrador en modo encriptación
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);

                // Encriptar el texto plano
                byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

                // Codificar el resultado en Base64 para facilitar su transmisión
                return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        // Método para desencriptar un mensaje
        public static String decrypt(String encryptedText) throws Exception {
                if (encryptedText == null || encryptedText.isEmpty()) {
                        throw new IllegalArgumentException("El texto a desencriptar no puede estar vacío.");
                }

                // Crear la clave secreta a partir de la clave privada
                SecretKeySpec secretKey = new SecretKeySpec(PRIVATE_KEY.getBytes(StandardCharsets.UTF_8), "AES");

                // Inicializar el cifrador en modo desencriptación
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);

                // Decodificar el texto en Base64 a bytes
                byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);

                // Desencriptar los bytes
                byte[] decryptedBytes = cipher.doFinal(decodedBytes);

                // Convertir los bytes desencriptados a texto plano
                return new String(decryptedBytes, StandardCharsets.UTF_8);
        }

        // Método principal para pruebas
        public static void main(String[] args) {
                try {
                        String originalMessage = "Asffffffffffier: Kaixo";
                        // Encriptar el mensaje
                        String encryptedMessage = CryptoUtil.encrypt(originalMessage);

                        String decryptedMessage = CryptoUtil.decrypt(encryptedMessage);
                        System.out.println(encryptedMessage);
                        System.out.println(decryptedMessage);
                } catch (Exception e) {
                        System.err.println("❌ Error durante la encriptación/desencriptación: " + e.getMessage());
                        e.printStackTrace();
                }
        }
}