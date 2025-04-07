package com.example.gerenteapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;

public class TxataController extends BaseController {

        @FXML
        private VBox chatBox;
        @FXML
        private TextField messageField;
        @FXML
        private Button sendButton;
        @FXML
        private ScrollPane scrollPane;

        private String Izena;
        private ChatClient chatClient;
        private List<String> messageHistory = new ArrayList<>();
        private static final String SHA_CRYPT = "SHA-256";
        private static final String AES_ALGORITHM = "AES";
        private static final String AES_ALGORITHM_GCM = "AES/GCM/NoPadding";
        private static final int IV_LENGTH = 12;
        private static final int TAG_LENGTH = 16;
        private static final String PASSPHRASE = "mySecurePassphrase123!";

        @FXML
        public void initialize() {
                chatClient = new ChatClient(this);
                chatClient.connect();
                sendButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");

                // Cargar historial al iniciar
                loadMessageHistory();
        }

        public void setLangilea(Langilea langilea) {
                if (langilea != null) {
                        this.Izena = langilea.getIzena();
                        System.out.println("🟢 Langilea asignado a TxataController: " + this.Izena);

                        // Actualizar la interfaz con el nombre correcto
                        Platform.runLater(() -> {
                                if (messageField != null) {
                                        messageField.setPromptText("Escribe tu mensaje como " + this.Izena);
                                }
                        });
                } else {
                        System.err.println("🔴 Error: Langilea recibido es null.");
                }
        }

        @FXML
        private void sendMessage() {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                        try {
                                JSONObject jsonMessage = new JSONObject();
                                jsonMessage.put("usuario", this.Izena); // Usar el nombre del usuario actual
                                jsonMessage.put("mensaje", message);
                                jsonMessage.put("timestamp", System.currentTimeMillis());

                                // Encriptar el mensaje completo
                                String encryptedMessage = encrypt(jsonMessage.toString());
                                String base64Message = Base64.getEncoder().encodeToString(encryptedMessage.getBytes(StandardCharsets.UTF_8));

                                // Guardar en el historial local (cifrado)
                                messageHistory.add(base64Message);

                                // Mostrar el mensaje localmente (descifrado)
                                displayMessage(jsonMessage.toString(), true);

                                // Enviar el mensaje cifrado al servidor
                                chatClient.sendMessage(base64Message);
                                messageField.clear();
                        } catch (Exception e) {
                                e.printStackTrace();
                                displayError("❌ Error al enviar el mensaje");
                        }
                }
        }

        public String getIzena() {
                return this.Izena;
        }

        public void receiveMessage(String encryptedMessage) {
                try {
                        String decrypted = processEncryptedMessage(encryptedMessage);
                        System.out.println("🔓 Decrypted: " + decrypted);

                        // Verificar si el mensaje es un array JSON
                        if (decrypted.trim().startsWith("[")) {
                                // Intentar procesar como un array JSON
                                try {
                                        JSONArray jsonArray = new JSONArray(decrypted);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject json = jsonArray.getJSONObject(i);
                                                String senderName = json.optString("usuario", "Desconocido");
                                                String message = json.getString("mensaje");
                                                System.out.println("Mensaje de " + senderName + ": " + message);

                                                // Mostrar en la interfaz
                                                displayMessage(json.toString(), senderName.equals(this.Izena));
                                        }
                                } catch (JSONException e) {
                                        // Si no es un array válido, manejar el error
                                        System.out.println("Error al procesar el array JSON: " + e.getMessage());
                                        displayError("❌ Error al procesar el mensaje (formato incorrecto)");
                                }
                        } else if (decrypted.trim().startsWith("{")) {
                                // Si es un único objeto JSON, procesar como objeto único
                                try {
                                        JSONObject json = new JSONObject(decrypted);
                                        String senderName = json.optString("usuario", "Desconocido");
                                        String message = json.getString("mensaje");
                                        System.out.println("Mensaje de " + senderName + ": " + message);

                                        // Mostrar en la interfaz
                                        displayMessage(decrypted, senderName.equals(this.Izena));
                                } catch (JSONException e) {
                                        System.out.println("Error al procesar el objeto JSON: " + e.getMessage());
                                        displayError("❌ Error al procesar el mensaje (formato incorrecto)");
                                }
                        } else {
                                // Si no es JSON válido, mostrar un error
                                System.out.println("⚠️ Formato de mensaje no reconocido: " + decrypted);
                                displayError("❌ Formato de mensaje no reconocido");
                        }
                } catch (JSONException e) {
                        System.out.println("JSON Error: " + e.getMessage());
                        displayError("❌ Error en formato de mensaje: " + e.getMessage());
                } catch (Exception e) {
                        e.printStackTrace();
                        displayError("❌ Error al procesar mensaje");
                }
        }


        private void displayMessage(String messageData, boolean isCurrentUser) {
                Platform.runLater(() -> {
                        try {
                                JSONObject jsonMessage = new JSONObject(messageData);
                                String senderName = jsonMessage.getString("usuario");
                                String msg = jsonMessage.getString("mensaje");

                                // Verificar si el mensaje es del usuario actual
                                boolean isUser = senderName.equals(this.Izena);

                                // Crear elemento de mensaje
                                Text text = new Text(senderName + ": " + msg);
                                text.setFill(isUser ? Color.DARKBLUE : Color.BLACK);

                                TextFlow textFlow = new TextFlow(text);
                                textFlow.setMaxWidth(300);
                                textFlow.setStyle(isUser
                                        ? "-fx-background-color: #ADD8E6; -fx-padding: 5px; -fx-background-radius: 10px;"
                                        : "-fx-background-color: #D3D3D3; -fx-padding: 5px; -fx-background-radius: 10px;");

                                HBox messageBox = new HBox(textFlow);
                                messageBox.setStyle(isUser
                                        ? "-fx-alignment: center-right;"
                                        : "-fx-alignment: center-left;");

                                chatBox.getChildren().add(messageBox);
                                scrollPane.setVvalue(1.0);
                        } catch (Exception e) {
                                e.printStackTrace();
                                displayError("❌ Error al mostrar mensaje");
                        }
                });
        }

        private void loadMessageHistory() {
                try {
                        // Limpiar chat antes de cargar historial
                        chatBox.getChildren().clear();

                        // Procesar cada mensaje del historial
                        for (String encryptedMsg : messageHistory) {
                                String decrypted = processEncryptedMessage(encryptedMsg);
                                JSONObject jsonMsg = new JSONObject(decrypted);
                                boolean isCurrentUser = jsonMsg.getString("usuario").equals(this.Izena);
                                displayMessage(decrypted, isCurrentUser);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        displayError("❌ Error al cargar historial");
                }
        }

        public void setMessageHistory(List<String> history) {
                this.messageHistory = new ArrayList<>(history);
                loadMessageHistory();
        }

        public List<String> getMessageHistory() {
                return new ArrayList<>(messageHistory);
        }

        private String processEncryptedMessage(String encryptedMessage) throws Exception {
                System.out.println("Mensaje recibido (crudo): " + encryptedMessage);

                // Primero intentar como texto plano (por si ya está decodificado)
                try {
                        // Si parece JSON válido, devolver directamente
                        if (encryptedMessage.trim().startsWith("{") || encryptedMessage.trim().startsWith("[")) {
                                return encryptedMessage;
                        }
                } catch (Exception ignored) {}

                // Si no, intentar decodificar base64
                try {
                        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
                        String decryptedStr = new String(decodedBytes, StandardCharsets.UTF_8);
                        System.out.println("Después de base64: " + decryptedStr);

                        // Si el resultado parece cifrado AES, descifrar
                        if (decryptedStr.length() > IV_LENGTH && !decryptedStr.trim().startsWith("{")) {
                                return decrypt(decryptedStr);
                        }
                        return decryptedStr;
                } catch (IllegalArgumentException e) {
                        System.out.println("No es base64 válido, procesando como texto plano");
                        return encryptedMessage;
                }
        }

        void displayError(String errorMessage) {
                Text errorText = new Text(errorMessage);
                errorText.setFill(Color.RED);
                TextFlow errorFlow = new TextFlow(errorText);
                HBox errorBox = new HBox(errorFlow);
                chatBox.getChildren().add(errorBox);
        }

        private String encrypt(String message) throws Exception {
                // Implementación de la función de encriptación (AES GCM)
                return encryptAES(message, PASSPHRASE);
        }

        private String decrypt(String encryptedMessage) throws Exception {
                // Implementación de la función de desencriptación (AES GCM)
                return decryptAES(encryptedMessage, PASSPHRASE);
        }

        private String encryptAES(String message, String passphrase) throws Exception {
                // Detalle de la implementación de cifrado (AES)
                // Deberás agregar aquí el cifrado en AES/GCM
                return message;
        }

        private String decryptAES(String encryptedMessage, String passphrase) throws Exception {
                // Detalle de la implementación de descifrado (AES)
                // Deberás agregar aquí el descifrado en AES/GCM
                return encryptedMessage;
        }
}
