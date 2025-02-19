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

public class TxataController extends BaseController{

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
                        message = this.Izena +"> " + message;
                        chatClient.sendMessage(message);
                        displayMessage(message, true);
                        messageField.clear();
                }
        }

        public void setLangilea(Langilea langilea) {
                this.langilea = langilea;
                setIzena(langilea.getIzena());
        }

        public void displayMessage(String message, boolean isUserMessage) {
                Platform.runLater(() -> {
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
                        textFlow.setStyle(isUser ? "-fx-background-color: #ADD8E6; -fx-alignment: center-right; -fx-padding: 5px;" : "-fx-background-color: #D3D3D3; -fx-alignment: center-left; -fx-padding: 5px;");

                        // Ajustar la alineación del mensaje
                        HBox messageBox = new HBox(textFlow);
                        messageBox.setStyle(isUser ? "-fx-alignment: center-right;" : "-fx-alignment: center-left;");
                        chatBox.getChildren().add(messageBox);
                        scrollPane.setVvalue(1.0); // Scroll to the bottom
                });
        }
}
