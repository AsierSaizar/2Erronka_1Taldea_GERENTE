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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

        @FXML
        public void initialize() {
                chatClient = new ChatClient(this);
                chatClient.connect();
                sendButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
        }

        public void setLangilea(Langilea langilea) {
                if (langilea != null) {
                        this.Izena = langilea.getIzena();  // Asigna el nombre del usuario
                        System.out.println("🟢 Langilea asignado a TxataController: " + this.Izena);
                } else {
                        System.err.println("🔴 Error: Langilea recibido es null.");
                }
        }


        @FXML
        private void sendMessage() {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                        try {
                                // Crear JSON con el usuario (this.Izena) y mensaje
                                String jsonMessage = String.format("{\"usuario\":\"%s\", \"mensaje\":\"%s\"}",
                                        this.Izena, message);

                                // Cifrar y codificar en Base64
                                String encryptedMessage = AESUtil.encrypt(jsonMessage);
                                String base64Message = Base64.getEncoder().encodeToString(encryptedMessage.getBytes(StandardCharsets.UTF_8));

                                // Mostrar en el chat local
                                displayMessage(this.Izena + ": " + message, true);

                                // Enviar al servidor
                                chatClient.sendMessage(base64Message);

                                messageField.clear();
                        } catch (Exception e) {
                                e.printStackTrace();
                                displayMessage("❌ Error al enviar el mensaje", false);
                        }
                }
        }


        public void displayMessage(String receivedMessage, boolean isUserMessage) {
                Platform.runLater(() -> {
                        try {
                                String decryptedMessage;
                                String encryptedMessage;

                                // Intentar decodificar Base64 solo si es necesario
                                if (isBase64(receivedMessage)) {
                                        byte[] decodedMessageBytes = Base64.getDecoder().decode(receivedMessage);
                                        encryptedMessage = new String(decodedMessageBytes, StandardCharsets.UTF_8);
                                        System.out.println("📥 Mensaje Base64 decodificado: " + encryptedMessage);
                                } else {
                                        encryptedMessage = receivedMessage;
                                        System.out.println("⚠️ Mensaje recibido sin Base64.");
                                }

                                // Intentar desencriptar el mensaje con AES
                                try {
                                        decryptedMessage = AESUtil.decrypt(encryptedMessage);
                                        System.out.println("🔓 Mensaje desencriptado: " + decryptedMessage);
                                } catch (Exception e) {
                                        System.err.println("⚠️ Error al desencriptar el mensaje. Mostrando sin cambios.");
                                        decryptedMessage = encryptedMessage;  // Si ocurre un error, mostramos el mensaje cifrado
                                }

                                if (decryptedMessage == null || decryptedMessage.trim().isEmpty()) {
                                        System.err.println("⚠️ Mensaje vacío después de desencriptar. Ignorado.");
                                        return;
                                }

                                // Mostrar el mensaje en el chat
                                String[] parts = decryptedMessage.split(":", 2);
                                if (parts.length < 2) {
                                        System.err.println("⚠️ Error al separar el mensaje: " + decryptedMessage);
                                        return;
                                }

                                String senderName = parts[0].trim();
                                String msg = parts[1].trim();

                                boolean isUser = senderName.equals(this.Izena);
                                Text text = new Text(senderName + ": " + msg);
                                text.setFill(Color.BLACK);

                                TextFlow textFlow = new TextFlow(text);
                                textFlow.setMaxWidth(300);
                                textFlow.setStyle(isUser ? "-fx-background-color: #ADD8E6; -fx-padding: 5px; -fx-background-radius: 10px;"
                                        : "-fx-background-color: #D3D3D3; -fx-padding: 5px; -fx-background-radius: 10px;");

                                HBox messageBox = new HBox(textFlow);
                                messageBox.setStyle(isUser ? "-fx-alignment: center-right;" : "-fx-alignment: center-left;");
                                chatBox.getChildren().add(messageBox);

                                scrollPane.setVvalue(1.0);  // Auto-scroll al final
                        } catch (Exception e) {
                                e.printStackTrace();
                                displayMessage("❌ Error al recibir el mensaje. Inténtalo de nuevo.", false);
                        }
                });
        }


        private boolean isBase64(String str) {
                if (str == null || str.length() % 4 != 0 || str.contains(" ")) {
                        return false;
                }
                try {
                        Base64.getDecoder().decode(str);
                        return true;
                } catch (IllegalArgumentException e) {
                        return false;
                }
        }
}
