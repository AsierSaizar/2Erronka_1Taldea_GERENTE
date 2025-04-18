package com.example.gerenteapp;

import javafx.scene.control.Alert;

public class Alertak {

        // Función ficticia para mostrar mensajes de alerta en euskera
        public static void mostrarMensajeAlerta(String mensaje) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Arazoa xml-a kargatzerakoan");
                alerta.setHeaderText(mensaje);
                alerta.showAndWait();
        }
        public static void mostrarMensajeFeddBack(String mensaje) {
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setTitle("Ondo atera da!");
                alerta.setHeaderText(mensaje);
                alerta.showAndWait();

        }
}
