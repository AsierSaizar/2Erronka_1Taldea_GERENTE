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
import javafx.stage.FileChooser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class TxataController extends BaseController {

        @FXML
        public HBox navBarContainer;
        @FXML
        VBox chatBox;
        @FXML
        TextField messageField;
        @FXML
        private Button sendButton;
        @FXML
        ScrollPane scrollPane;
        @FXML
        private Button attachButton; // Nuevo botón para adjuntar archivos

        private Langilea langilea;
        private ChatClient chatClient;
        private String Izena;

        @FXML
        public void initialize() {
                chatClient = new ChatClient(this);
                chatClient.connect();


                // Configurar el evento del botón de adjuntar archivos
                attachButton.setOnAction(event -> handleAttachFile());
        }

        public void setIzena(String Izena) {
                this.Izena = Izena;
        }

        @FXML
        private void sendMessage() throws Exception {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                        message = this.Izena + "> " + message;
                        chatClient.sendMessage(message);
                        displayMessage(message);
                        messageField.clear();
                }
        }

        public void setLangilea(Langilea langilea) {
                this.langilea = langilea;
                setIzena(langilea.getIzena());
        }


        public void displayMessage(String message) {
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

        // Método para manejar la selección de archivos
        private void handleAttachFile() {
                // Crear un FileChooser
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Seleccionar archivo");

                // Configurar filtros para tipos de archivo (opcional)
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Todos los archivos", "*.*"),
                        new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"),
                        new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.docx")
                );

                // Mostrar el diálogo de selección de archivo
                File selectedFile = fileChooser.showOpenDialog(null);

                if (selectedFile != null) {
                        // Leer el archivo seleccionado
                        try {
                                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());

                                // Construir el mensaje con el nombre del archivo
                                String fileName = selectedFile.getName();
                                String message = this.Izena + "> 📂: " + fileName;

                                // Enviar el archivo a través del cliente de chat
                                ChatClient.sendFile(fileContent, fileName, this.Izena);

                                // Mostrar el archivo adjunto en el chat
                                displayFile(message, Arrays.toString(fileContent));
                        } catch (Exception e) {
                                e.printStackTrace();
                                System.err.println("Error al leer el archivo: " + e.getMessage());
                        }
                }
        }

        public void displayFile(String message, String fileDataString) {
                Platform.runLater(() -> {
                        // Separar el nombre del remitente del mensaje
                        String[] parts = message.split(">", 2);
                        if (parts.length < 2) return;
                        String senderName = parts[0].trim();
                        String fileName = parts[1].trim(); // Aquí asumimos que el segundo parte es el nombre del archivo

                        String[] partsName = fileName.split(":", 2);
                        String fileNameOrigin = partsName[1].trim();

                        // Determinar si el mensaje es del usuario actual
                        boolean isUser = senderName.equals(this.Izena);

                        // Crear el texto "usuario> nombre del archivo"
                        Text text = new Text(senderName + "> " + fileName);
                        text.setFill(Color.BLACK);
                        text.setUnderline(true); // Subrayar el texto para indicar que es clickeable

                        // Crear un TextFlow para contener el texto
                        TextFlow textFlow = new TextFlow(text);
                        textFlow.setMaxWidth(300);
                        textFlow.setMinHeight(50);

                        // Ajustar el estilo del TextFlow según el remitente
                        textFlow.setStyle(isUser
                                ? "-fx-background-color: #ADD8E6; -fx-alignment: center-right; -fx-padding: 5px;"
                                : "-fx-background-color: #D3D3D3; -fx-alignment: center-left; -fx-padding: 5px;"
                        );

                        // Crear un HBox para alinear el mensaje
                        HBox messageBox = new HBox(textFlow);
                        messageBox.setStyle(isUser
                                ? "-fx-alignment: center-right;"
                                : "-fx-alignment: center-left;"
                        );

                        // Agregar funcionalidad de clic para descargar el archivo
                        textFlow.setOnMouseClicked(event -> {
                                try {
                                        // Convertir el string del archivo a bytes
                                        byte[] fileBytes = Arrays.stream(fileDataString.replaceAll("[\\[\\]]", "").split(","))
                                                .map(String::trim)
                                                .mapToInt(Integer::parseInt)
                                                .collect(() -> new ByteArrayOutputStream(),
                                                        (baos, i) -> baos.write(i),
                                                        (baos1, baos2) -> {}).toByteArray();

                                        // Crear un FileChooser para guardar el archivo
                                        FileChooser fileChooser = new FileChooser();
                                        fileChooser.setInitialFileName(fileNameOrigin); // Nombre predeterminado del archivo
                                        File selectedFile = fileChooser.showSaveDialog(null);

                                        if (selectedFile != null) {
                                                // Escribir los bytes del archivo en el disco
                                                Files.write(selectedFile.toPath(), fileBytes);
                                                System.out.println("Archivo descargado: " + selectedFile.getAbsolutePath());
                                        }
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        System.err.println("Error al descargar el archivo.");
                                }
                        });

                        // Agregar el mensaje al chat y hacer scroll hacia abajo
                        chatBox.getChildren().add(messageBox);
                        scrollPane.setVvalue(1.0); // Scroll to the bottom
                });
        }
}