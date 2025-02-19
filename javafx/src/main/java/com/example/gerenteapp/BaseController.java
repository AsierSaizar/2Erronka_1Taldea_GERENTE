package com.example.gerenteapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class BaseController {

    protected Stage usingStage;

    protected NavBarController navBarController;

    @FXML
    private HBox navBarContainer;

    public Stage getUsingStage() {
        return usingStage;
    }

    public void setUsingStage(Stage usingStage) {
        this.usingStage = usingStage;
    }

    public void setStage(Stage stage) {
        this.usingStage = stage;
    }


    public void navBarKargatu(Langilea langilea){

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("NavBar.fxml"));
            AnchorPane navBar = loader.load();

            navBarController = loader.getController();
            navBarContainer.getChildren().add(navBar);
            navBarController.setLangilea(langilea);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar NavBar.fxml: " + e.getMessage());
        }

        if (navBarController != null) {
            System.out.println("Controlador de NavBar cargado correctamente.");
            navBarController.setStage(this.getUsingStage());
        }

    }
}