package com.example.gerenteapp;

import javafx.application.Platform;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ChatClient {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private TxataController txataController;
        private List<String> messageHistory = new ArrayList<>();
        private volatile boolean isConnected = false;
        private String username;  // Variable para almacenar el nombre de usuario
        private static final int CONNECTION_TIMEOUT = 5000; // 5 segundos de timeout
        private static final String SERVER_IP = "192.168.115.188";
        private static final int SERVER_PORT = 5555;

        public ChatClient(TxataController txataController) {
                this.txataController = txataController;
        }

        public void setUsername(String username) {
                this.username = username;  // Asigna el nombre de usuario
        }

        public void connect() {
                if (isConnected) {
                        System.out.println("ℹ️ Ya conectado al servidor");
                        return;
                }

                new Thread(() -> {
                        try {
                                // Intentar conexión con timeout
                                socket = new Socket();
                                socket.connect(new java.net.InetSocketAddress(SERVER_IP, SERVER_PORT), CONNECTION_TIMEOUT);

                                socket.setSoTimeout(30000); // Timeout de lectura de 30 segundos
                                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                writer = new PrintWriter(socket.getOutputStream(), true);
                                isConnected = true;

                                System.out.println("✅ Conectado al servidor de chat en " + SERVER_IP + ":" + SERVER_PORT);

                                // Solicitar historial al conectar
                                requestMessageHistory();

                                // Iniciar escucha de mensajes
                                listenForMessages();

                        } catch (SocketTimeoutException e) {
                                System.err.println("⌛ Timeout al conectar con el servidor después de " +
                                        (CONNECTION_TIMEOUT / 1000) + " segundos");
                                showConnectionError("El servidor no responde. Verifica que esté en ejecución.");
                        } catch (ConnectException e) {
                                System.err.println("❌ Conexión rechazada por el servidor: " + e.getMessage());
                                showConnectionError("No se pudo conectar al servidor. Verifica la dirección y puerto.");
                        } catch (IOException e) {
                                System.err.println("❌ Error de conexión: " + e.getMessage());
                                showConnectionError("Error al conectar con el servidor: " + e.getMessage());
                        } catch (Exception e) {
                                System.err.println("❌ Error inesperado: " + e.getMessage());
                                showConnectionError("Error inesperado: " + e.getMessage());
                        } finally {
                                if (!isConnected) {
                                        disconnect();
                                }
                        }
                }).start();
        }

        private void showConnectionError(String message) {
                Platform.runLater(() -> {
                        if (txataController != null) {
                                txataController.displayError(message);
                        }
                });
        }

        public void sendMessage(String message) {
                if (!isConnected || writer == null) {
                        System.err.println("⚠️ No hay conexión al servidor. No se puede enviar el mensaje.");
                        return;
                }

                try {
                        // El mensaje ya viene cifrado y en Base64 desde el controller
                        writer.println(message);
                        System.out.println("📤 Mensaje enviado (Base64): " + message.substring(0, Math.min(message.length(), 50)) + "...");

                        // Añadir al historial local
                        messageHistory.add(message);
                } catch (Exception e) {
                        System.err.println("❌ Error al enviar el mensaje: " + e.getMessage());
                        e.printStackTrace();
                }
        }

        private void requestMessageHistory() {
                if (txataController != null && username != null) {
                        try {
                                JSONObject request = new JSONObject();
                                request.put("action", "get_history");
                                request.put("usuario", username);

                                String encryptedRequest = AESUtil.encrypt(request.toString());
                                String base64Request = Base64.getEncoder().encodeToString(
                                        encryptedRequest.getBytes(StandardCharsets.UTF_8));

                                writer.println(base64Request);
                                System.out.println("📥 Solicitando historial de mensajes para: " + username);
                        } catch (Exception e) {
                                System.err.println("❌ Error al solicitar historial: " + e.getMessage());
                        }
                }
        }

        private void listenForMessages() {
                try {
                        String message;
                        while (isConnected && (message = reader.readLine()) != null) {
                                // desencriptar mensaje
                                if (message.trim().isEmpty()) {
                                        System.err.println("⚠️ Mensaje vacío recibido. Ignorado.");
                                        continue;
                                }

                                System.out.println("📩 Mensaje recibido: " + message.substring(0, Math.min(message.length(), 50)) + "...");

                                // Añadir al historial local
                                messageHistory.add(message);

                                // Procesar el mensaje en el controller
                                String finalMessage = message;
                                Platform.runLater(() -> {
                                        if (txataController != null) {
                                                txataController.receiveMessage(finalMessage);
                                        }
                                });
                        }
                } catch (IOException e) {
                        if (isConnected) { // Solo mostrar error si la desconexión no fue solicitada
                                System.err.println("❌ Error en la conexión: " + e.getMessage());
                                e.printStackTrace();
                        }
                } finally {
                        disconnect();
                }
        }

        public void disconnect() {
                isConnected = false;
                try {
                        if (socket != null && !socket.isClosed()) {
                                socket.close();
                        }
                        if (writer != null) {
                                writer.close();
                        }
                        if (reader != null) {
                                reader.close();
                        }
                        System.out.println("🔌 Conexión cerrada correctamente.");
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public List<String> getMessageHistory() {
                return new ArrayList<>(messageHistory);
        }

        public boolean isConnected() {
                return isConnected;
        }
}
