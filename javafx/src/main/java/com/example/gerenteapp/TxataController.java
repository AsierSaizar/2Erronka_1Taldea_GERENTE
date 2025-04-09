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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class TxataController extends BaseController {

        @FXML private VBox chatBox;
        @FXML private TextField messageField;
        @FXML private Button sendButton;
        @FXML private ScrollPane scrollPane;

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
                chatClient.connect();  // Suponiendo que esta función conecta y empieza a escuchar mensajes
                sendButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
                loadMessageHistory();
        }

        public void setLangilea(Langilea langilea) {
                if (langilea != null) {
                        this.Izena = langilea.getIzena();
                        System.out.println("🟢 Langilea asignado a TxataController: " + this.Izena);
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
                                jsonMessage.put("Nombre", this.Izena);
                                jsonMessage.put("mensaje", message);
                                jsonMessage.put("timestamp", System.currentTimeMillis());

                                String encryptedMessage = encrypt(jsonMessage.toString());
                                messageHistory.add(encryptedMessage);
                                displayMessage(jsonMessage.toString(), true);
                                chatClient.sendMessage(encryptedMessage);  // Enviar el mensaje cifrado
                                messageField.clear();
                        } catch (Exception e) {
                                e.printStackTrace();
                                displayError("❌ Error al enviar el mensaje");
                        }
                }
        }

        public void receiveMessage(String encryptedMessage) {
                try {
                        String decrypted = processEncryptedMessage(encryptedMessage);
                        System.out.println("🔓 Decrypted: " + decrypted);

                        if (decrypted.trim().startsWith("[")) {
                                JSONArray jsonArray = new JSONArray(decrypted);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject json = jsonArray.getJSONObject(i);
                                        String senderName = json.optString("Nombre", "Desconocido");
                                        String encryptedMsg = json.getString("mensaje");

                                        String decryptedMsg = decrypt(encryptedMsg);

                                        JSONObject mostrarJson = new JSONObject();
                                        mostrarJson.put("Nombre", senderName);
                                        mostrarJson.put("mensaje", decryptedMsg);

                                        displayMessage(mostrarJson.toString(), senderName.equals(this.Izena));
                                }
                        } else if (decrypted.trim().startsWith("{")) {
                                JSONObject json = new JSONObject(decrypted);
                                String senderName = json.optString("Nombre", "Desconocido");
                                String encryptedMsg = json.getString("mensaje");

                                String decryptedMsg = decrypt(encryptedMsg);

                                JSONObject mostrarJson = new JSONObject();
                                mostrarJson.put("Nombre", senderName);
                                mostrarJson.put("mensaje", decryptedMsg);

                                displayMessage(mostrarJson.toString(), senderName.equals(this.Izena));
                        } else {
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
                                String senderName = jsonMessage.getString("Nombre");
                                String msg = jsonMessage.getString("mensaje");

                                boolean isUser = senderName.equals(this.Izena);

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
                        chatBox.getChildren().clear();

                        for (String encryptedMsg : messageHistory) {
                                String decrypted = processEncryptedMessage(encryptedMsg);
                                JSONObject jsonMsg = new JSONObject(decrypted);
                                boolean isCurrentUser = jsonMsg.getString("Nombre").equals(this.Izena);
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

                try {
                        if (encryptedMessage.trim().startsWith("{") || encryptedMessage.trim().startsWith("[")) {
                                return encryptedMessage;
                        }
                } catch (Exception ignored) {}

                try {
                        return decrypt(encryptedMessage);
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
                return encryptAES(message, PASSPHRASE);
        }

        private String decrypt(String encryptedMessage) throws Exception {
                return decryptAES(encryptedMessage, PASSPHRASE);
        }

        private String encryptAES(String message, String passphrase) throws Exception {
                byte[] iv = new byte[IV_LENGTH];
                SecureRandom secureRandom = new SecureRandom();
                secureRandom.nextBytes(iv);

                SecretKeySpec keySpec = getKeyFromPassphrase(passphrase);
                Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
                GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH * 8, iv);
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

                byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

                byte[] combined = new byte[iv.length + encryptedBytes.length];
                System.arraycopy(iv, 0, combined, 0, iv.length);
                System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

                return Base64.getEncoder().encodeToString(combined);
        }

        private String decryptAES(String encryptedMessage, String passphrase) throws Exception {
                byte[] decoded = Base64.getDecoder().decode(encryptedMessage);
                byte[] iv = new byte[IV_LENGTH];
                byte[] cipherBytes = new byte[decoded.length - IV_LENGTH];

                System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
                System.arraycopy(decoded, IV_LENGTH, cipherBytes, 0, cipherBytes.length);

                SecretKeySpec keySpec = getKeyFromPassphrase(passphrase);
                Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
                GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH * 8, iv);
                cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

                byte[] decryptedBytes = cipher.doFinal(cipherBytes);
                return new String(decryptedBytes, StandardCharsets.UTF_8);
        }

        private SecretKeySpec getKeyFromPassphrase(String passphrase) throws Exception {
                MessageDigest digest = MessageDigest.getInstance(SHA_CRYPT);
                byte[] hash = digest.digest(passphrase.getBytes(StandardCharsets.UTF_8));
                return new SecretKeySpec(hash, AES_ALGORITHM);
        }
}
