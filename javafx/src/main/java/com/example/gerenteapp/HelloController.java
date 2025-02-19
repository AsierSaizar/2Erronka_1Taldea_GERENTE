package com.example.gerenteapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class HelloController extends BaseController {
        @FXML
        protected void sartuBotoia() throws IOException {
                if (this.getUsingStage() == null) {
                        throw new IllegalStateException("Stage no inicializado. Asegúrate de llamar a setStage antes de usar sartuBotoia.");
                }

                // Cargar el archivo FXML de login
                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
                Scene loginScene = new Scene(loginLoader.load());

                // Obtener el controlador de la nueva escena
                LoginController loginController = loginLoader.getController();
                loginController.setStage(this.getUsingStage());

                // Establecer la nueva escena en el Stage
                this.getUsingStage().setScene(loginScene);
                this.getUsingStage().setTitle("Login pantalla");

                // Usar Platform.runLater para garantizar que el diseño se ajuste después de que se muestre la ventana
                this.getUsingStage().show();
                Platform.runLater(() -> {
                        loginScene.getRoot().layout();
                });
        }


}
