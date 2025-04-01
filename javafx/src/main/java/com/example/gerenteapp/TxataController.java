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
import java.util.Base64;

public class TxataController extends BaseController {

        public HBox navBarContainer;
        @FXML
        private VBox chatBox;
        @FXML
        private TextField messageField;
        @FXML
        private Button sendButton;
        @FXML
        private ScrollPane scrollPane;

        private Langilea langilea;
        private ChatClient chatClient;
        private String Izena;

        @FXML
        public void initialize() {
                chatClient = new ChatClient(this);
                chatClient.connect();
                sendButton.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;");
        }

        public void setIzena(String Izena) {
                this.Izena = Izena;
        }

        @FXML
        private void sendMessage() {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                        try {
                                // Cifrar el mensaje
                                String encryptedMessage = AESUtil.encrypt(this.Izena + "> " + message);
                                System.out.println("Mensaje cifrado: " + encryptedMessage); // Debug

                                // Descifrar para verificar que funciona correctamente
                                String decryptedMessage = AESUtil.decrypt(encryptedMessage);
                                System.out.println("Mensaje descifrado: " + decryptedMessage); // Debug

                                chatClient.sendMessage(encryptedMessage);
                                displayMessage(this.Izena + "> " + message, true); // Mostrar mensaje sin cifrar al emisor
                                messageField.clear();
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
        }

        public void setLangilea(Langilea langilea) {
                this.langilea = langilea;
                setIzena(langilea.getIzena());
        }

        public void displayMessage(String encryptedMessage, boolean isUserMessage) {
                Platform.runLater(() -> {
                        try {
                                // Verificar si el mensaje es Base64 antes de descifrar
                                if (!isBase64(encryptedMessage)) {
                                        System.err.println("Mensaje recibido no es Base64 válido: " + encryptedMessage);
                                        return;
                                }

                                // Descifrar el mensaje
                                String message = AESUtil.decrypt(encryptedMessage);
                                Text text = new Text(message);
                                text.setFill(Color.BLACK);
                                TextFlow textFlow = new TextFlow(text);
                                textFlow.setMaxWidth(300);
                                textFlow.setMinHeight(50);

                                // Separar el nombre del remitente del mensaje
                                String[] parts = message.split(">", 2);
                                if (parts.length < 2) return;
                                String senderName = parts[0].trim();
                                String msg = parts[1].trim();

                                // Determinar si el mensaje es del usuario actual
                                boolean isUser = senderName.equals(this.Izena);

                                // Ajustar el estilo del TextFlow
                                textFlow.setStyle(isUser ? "-fx-background-color: #ADD8E6; -fx-padding: 5px;"
                                        : "-fx-background-color: #D3D3D3; -fx-padding: 5px;");

                                // Ajustar la alineación del mensaje
                                HBox messageBox = new HBox(textFlow);
                                messageBox.setStyle(isUser ? "-fx-alignment: center-right;" : "-fx-alignment: center-left;");
                                chatBox.getChildren().add(messageBox);
                                scrollPane.setVvalue(1.0); // Scroll automático al final
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                });
        }

        private boolean isBase64(String str) {
                try {
                        Base64.getDecoder().decode(str);
                        return true;
                } catch (IllegalArgumentException e) {
                        return false;
                }
        }
}