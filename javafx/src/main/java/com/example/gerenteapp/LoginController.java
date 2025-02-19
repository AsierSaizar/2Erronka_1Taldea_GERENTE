package com.example.gerenteapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController extends BaseController {

        @FXML
        private TextField emai;
        @FXML
        private TextField pasa;


        @FXML
        protected void loginBotoia() throws IOException {
                String email = emai.getText();
                String pasahitza = pasa.getText();
                Langilea langileaLogin = LoginKudeatzailea.erabiltzaileaKomprobatu(email, pasahitza);

                if (langileaLogin != null) {
                        try {
                                if (this.getUsingStage() == null) {
                                        throw new IllegalStateException("Stage no inicializado. Asegúrate de llamar a setStage antes de usar sartuBotoia.");
                                }

                                FXMLLoader langileaLoader = new FXMLLoader(getClass().getResource("langileakTable.fxml"));
                                Scene langileaScene = new Scene(langileaLoader.load(), 1800, 850);

                                // Forzar layout después de cargar la escena
                                langileaScene.getRoot().layout();

                                LangileaController langileaController = langileaLoader.getController();
                                langileaController.setStage(this.getUsingStage());
                                langileaController.navBarKargatu(langileaLogin);
                                this.getUsingStage().setTitle("Langilea Table");
                                this.getUsingStage().setScene(langileaScene);


                                // Asegurar que el diseño se recalcula cuando se muestra la ventana
                                this.getUsingStage().show();
                                this.getUsingStage().getScene().getRoot().layout();

                        } catch (IOException e) {
                                System.err.println("Error al cargar la siguiente vista: " + e.getMessage());
                                mezuaPantailaratu("Error", "Hubo un problema al cargar la vista", Alert.AlertType.ERROR);
                        }
                }
        }






        private void mezuaPantailaratu(String izena, String mezuLuzea, Alert.AlertType mota) {
                Alert alerta = new Alert(mota);
                alerta.setTitle(izena);
                alerta.setHeaderText(null); // Header hutsa
                alerta.setContentText(mezuLuzea); // Mezu printzipala
                alerta.showAndWait(); // mezua pantailaratu eta itxi
        }


}