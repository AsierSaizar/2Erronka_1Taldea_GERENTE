package com.example.gerenteapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ChatClient {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private TxataController txataController;

        public ChatClient(TxataController txataController) {
                this.txataController = txataController;
        }

        public void connect() {
                new Thread(() -> {
                        try {
                                socket = new Socket("192.168.115.188", 5555);
                                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                writer = new PrintWriter(socket.getOutputStream(), true);

                                System.out.println("✅ Conectado al servidor de chat.");
                                listenForMessages();
                        } catch (IOException e) {
                                System.err.println("❌ Error al conectar con el servidor: " + e.getMessage());
                                e.printStackTrace();
                                socket = null;
                                reader = null;
                                writer = null;
                        }
                }).start();
        }

        public void sendMessage(String message) {
                if (writer == null) {
                        System.err.println("⚠️ No hay conexión al servidor. No se puede enviar el mensaje.");
                        return;
                }

                try {
                        // Cifrar el mensaje
                        String encryptedMessage = AESUtil.encrypt(message);

                        // Codificar el mensaje cifrado en Base64 antes de enviarlo
                        String base64Message = Base64.getEncoder().encodeToString(encryptedMessage.getBytes(StandardCharsets.UTF_8));
                        writer.println(base64Message);  // Enviar el mensaje codificado
                        System.out.println("📤 Mensaje enviado (Base64): " + base64Message);
                } catch (Exception e) {
                        System.err.println("❌ Error al enviar el mensaje: " + e.getMessage());
                        e.printStackTrace();
                }
        }


        private void listenForMessages() {
                try {
                        String message;
                        while ((message = reader.readLine()) != null) {
                                if (message.trim().isEmpty()) {
                                        System.err.println("⚠️ Mensaje vacío recibido. Ignorado.");
                                        continue;
                                }

                                // Aquí ya no decodificamos Base64, solo procesamos el mensaje recibido
                                System.out.println("📩 Mensaje recibido: " + message);
                                txataController.displayMessage(message, false);
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }


                public void disconnect() {
                        try {
                                if (socket != null && !socket.isClosed()) {
                                        socket.close();
                                        System.out.println("🔌 Conexión cerrada correctamente.");
                                }
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }

        }

