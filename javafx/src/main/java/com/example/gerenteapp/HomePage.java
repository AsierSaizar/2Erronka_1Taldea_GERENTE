package com.example.gerenteapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HomePage.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1800, 850);

        HelloController helloController = fxmlLoader.getController();
        helloController.setUsingStage(stage);

        stage.setTitle("Home");
        stage.setMaximized(true);

        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(

        );
    }
}