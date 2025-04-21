package com.example.gerenteapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LangileaController extends BaseController {

        @FXML
        private TableView<Langilea> langileakTable;

        @FXML
        private TableColumn<Langilea, Integer> idColumn;

        @FXML
        private TableColumn<Langilea, String> izenaColumn;

        @FXML
        private TableColumn<Langilea, String> abizenaColumn;

        @FXML
        private TableColumn<Langilea, String> emailColumn;

        @FXML
        private TableColumn<Langilea, String> pasahitzaColumn;

        @FXML
        private TableColumn<Langilea, Integer> nivelPermisosColumn;

        @FXML
        private TableColumn<Langilea, Integer> deleted_atColumn;

        @FXML
        private TableColumn<Langilea, Integer> txatPermisosColumn;

        private ObservableList<Langilea> langileakData = FXCollections.observableArrayList();

        @FXML
        private TextField deleted_atEditField;

        @FXML
        private Button btnSortu;

        @FXML
        public void initialize() throws IOException {
                assert deleted_atEditField != null : "El campo deleted_atEditField no ha sido inyectado correctamente";
                deleted_atEditField.setText("some text");

                if (btnSortu == null) {
                        System.err.println("Error: El botón 'btnSortu' no está conectado en el FXML.");
                } else {
                        System.out.println("El botón 'btnSortu' está correctamente vinculado.");
                }

                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                izenaColumn.setCellValueFactory(new PropertyValueFactory<>("izena"));
                abizenaColumn.setCellValueFactory(new PropertyValueFactory<>("abizena"));
                emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                pasahitzaColumn.setCellValueFactory(new PropertyValueFactory<>("pasahitza"));
                nivelPermisosColumn.setCellValueFactory(new PropertyValueFactory<>("nivelPermisos"));
                deleted_atColumn.setCellValueFactory(new PropertyValueFactory<>("deleted_at"));
                txatPermisosColumn.setCellValueFactory(new PropertyValueFactory<>("txatPermiso"));

                loadLangileakData();

                nivelPermisosComboBoxEdit.setItems(FXCollections.observableArrayList(0, 1, 2));
                txatPermisosEditComboBox.setItems(FXCollections.observableArrayList(0, 1));

                langileakTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null) {
                                IdDeleteField.setText(String.valueOf(newSelection.getId()));
                                izenaDeleteField.setText(newSelection.getIzena());
                                izenaEditField.setText(newSelection.getIzena());
                                abizenaEditField.setText(newSelection.getAbizena());
                                emailaEditField.setText(newSelection.getEmail());
                                pasahitzaEditField.setText(newSelection.getPasahitza());
                                nivelPermisosComboBoxEdit.getSelectionModel().select(newSelection.getNivelPermisos());
                                txatPermisosEditComboBox.getSelectionModel().select(newSelection.getTxatPermiso());
                                deleted_atEditField.setText(String.valueOf(newSelection.getDeleted_at()));
                        }
                });
        }

        private void loadLangileakData() {
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        langileakData.clear();
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM 5_erronka1.langilea");

                        while (resultSet.next()) {
                                langileakData.add(new Langilea(
                                        resultSet.getInt("id"),
                                        resultSet.getString("izena"),
                                        resultSet.getString("abizena"),
                                        resultSet.getString("pasahitza"),
                                        resultSet.getString("email"),
                                        resultSet.getInt("nivel_permisos"),
                                        resultSet.getString("deleted_at"),
                                        resultSet.getInt("txat_permiso")
                                ));
                        }

                        langileakTable.setItems(langileakData);
                } catch (SQLException e) {
                        System.err.println("Error al cargar los datos de langileak: " + e.getMessage());
                        e.printStackTrace();  // Imprime el detalle completo de la excepción
                }
        }

        @FXML
        private TextField izenaField;

        @FXML
        private TextField abizenaField;

        @FXML
        private TextField emailaField;

        @FXML
        private TextField pasahitzaField;

        @FXML
        private ComboBox<Integer> nivelPermisosComboBox;

        @FXML
        private ComboBox<Integer> txatPermisosComboBox;



// ...

        public void createLangilea(ActionEvent actionEvent) {
                String izena = izenaField.getText().trim();
                String abizena = abizenaField.getText().trim();
                String emaila = emailaField.getText().trim();
                String pasahitza = pasahitzaField.getText().trim();
                Integer txatPermisos = txatPermisosComboBox.getValue();
                Integer nivelPermisos = nivelPermisosComboBox.getValue();

                // Validación de campos vacíos
                StringBuilder mensaje = new StringBuilder("Langilea ez da sortu propietate hau bete gabe utzi duzu:\n");
                boolean hayCamposVacios = false;

                if (izena.isEmpty()) {
                        mensaje.append(" Izena\n");
                        hayCamposVacios = true;
                }
                if (abizena.isEmpty()) {
                        mensaje.append(" Abizena\n");
                        hayCamposVacios = true;
                }
                if (emaila.isEmpty()) {
                        mensaje.append(" Emaila\n");
                        hayCamposVacios = true;
                }
                if (pasahitza.isEmpty()) {
                        mensaje.append(" Pasahitza\n");
                        hayCamposVacios = true;
                }
                if (nivelPermisos == null) {
                        mensaje.append(" Baimen maila\n");
                        hayCamposVacios = true;
                }
                if (txatPermisos == null) {
                        mensaje.append(" txat baimena\n");
                        hayCamposVacios = true;
                }

                if (hayCamposVacios) {
                        Alert alerta = new Alert(Alert.AlertType.ERROR);
                        alerta.setTitle("Kanpo hutsak");
                        alerta.setHeaderText("Ezin izan da langilea sortu");
                        alerta.setContentText(mensaje.toString());
                        alerta.showAndWait();
                        return;
                }

                // Mensajes de depuración
                System.out.println("Gehitutako datuak:");
                System.out.println("Izena: " + izena);
                System.out.println("Abizena: " + abizena);
                System.out.println("Emaila: " + emaila);
                System.out.println("Pasahitza: " + pasahitza);
                System.out.println("Nivel Permisos: " + nivelPermisos);
                System.out.println("Txat Permisos: " + txatPermisos);

                Langilea langilea = new Langilea(0, izena, abizena, pasahitza, emaila, nivelPermisos, null, txatPermisos);
                boolean success = LangileaKudeatzailea.insertLangilea(langilea);

                if (success) {
                        System.out.println("Langilea zuzen sortu da.");
                } else {
                        Alert alerta = new Alert(Alert.AlertType.ERROR);
                        alerta.setTitle("Akatsa sortzerakoan");
                        alerta.setHeaderText("Ezin izan da langilea sortu");
                        alerta.setContentText("Errore bat gertatu da langilea sartzerakoan.");
                        alerta.showAndWait();
                }

                clearInputFields();
                loadLangileakData();
        }



        private void clearInputFields() {
                izenaField.clear();
                abizenaField.clear();
                emailaField.clear();
                pasahitzaField.clear();
                nivelPermisosComboBox.getSelectionModel().selectFirst();
                txatPermisosComboBox.getSelectionModel().clearSelection();
        }

        @FXML
        private TextField izenaDeleteField;

        @FXML
        private TextField IdDeleteField;

        public void deleteLangilea(ActionEvent actionEvent) {
                String id = IdDeleteField.getText();

                if (id.isEmpty()) {
                        mostrarAlerta(AlertType.WARNING, "Error", "Por favor, selecciona un langilea antes de continuar.");
                        return;
                }

                if (LangileaKudeatzailea.deleteLangilea(id)) {
                        mostrarAlerta(AlertType.INFORMATION, "Éxito", "Langilea ondo ezabatu da.");
                        loadLangileakData();
                        IdDeleteField.clear();
                        izenaDeleteField.clear();
                } else {
                        mostrarAlerta(AlertType.ERROR, "Error", "No se pudo eliminar el langilea. Verifica si ya ha sido eliminado.");
                }
        }

        // Método para recuperar langilea
        public void berreskuratuLangilea(ActionEvent actionEvent) {
                String id = IdDeleteField.getText();

                if (id.isEmpty()) {
                        mostrarAlerta(AlertType.WARNING, "Error", "Por favor, selecciona un langilea antes de continuar.");
                        return;
                }

                if (LangileaKudeatzailea.berreskuratuLangilea(id)) {
                        mostrarAlerta(AlertType.INFORMATION, "Éxito", "Langilea ondo berreskuratu da da.");
                        loadLangileakData();
                        IdDeleteField.clear();
                        izenaDeleteField.clear();
                } else {
                        mostrarAlerta(AlertType.ERROR, "Error", "No se pudo recuperar el langilea. Verifica si ya fue eliminado previamente.");
                }
        }
        private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
                Alert alerta = new Alert(tipo);
                alerta.setTitle(titulo);
                alerta.setHeaderText(null); // Sin encabezado adicional
                alerta.setContentText(mensaje);
                alerta.showAndWait();
        }

        @FXML
        private TextField izenaEditField;
        @FXML
        private TextField abizenaEditField;
        @FXML
        private TextField emailaEditField;
        @FXML
        private TextField pasahitzaEditField;
        @FXML
        private ComboBox<Integer> txatPermisosEditComboBox;
        @FXML
        private ComboBox<Integer> nivelPermisosComboBoxEdit;

        @FXML
        public void editLangilea(ActionEvent actionEvent) {
                Langilea selectedLangilea = langileakTable.getSelectionModel().getSelectedItem();

                if (selectedLangilea == null) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Ningún Langilea seleccionado", "Por favor, selecciona un registro para editar.");
                        return;
                }

                // Obtener y validar campos de texto
                String izena = izenaEditField.getText().trim();
                String abizena = abizenaEditField.getText().trim();
                String emaila = emailaEditField.getText().trim();
                String pasahitza = pasahitzaEditField.getText().trim();

                // Validaciones negativas de caja negra
                if (izena.isEmpty() || abizena.isEmpty() || emaila.isEmpty() || pasahitza.isEmpty()) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Campos vacíos", "No se puede actualizar", "Todos los campos deben estar completos.");
                        return;
                }

                // Validaciones adicionales
                if (!emaila.matches("^\\S+@\\S+\\.\\S+$")) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Email inválido", "Formato de correo incorrecto", "Introduce un correo electrónico válido.");
                        return;
                }

                if (pasahitza.length() < 6) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Contraseña débil", "La contraseña es demasiado corta", "Debe tener al menos 6 caracteres.");
                        return;
                }

                if (!izena.matches("^[A-Za-zÀ-ÿ\\s]+$") || !abizena.matches("^[A-Za-zÀ-ÿ\\s]+$")) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Nombre o Apellido inválidos", "Solo se permiten letras", "Revisa los campos de nombre y apellido.");
                        return;
                }

                // Obtener valores de ComboBox
                Integer nivelPermisos = nivelPermisosComboBoxEdit.getSelectionModel().getSelectedItem();
                Integer txatPermisos = txatPermisosEditComboBox.getSelectionModel().getSelectedItem();

                // Validar ComboBox seleccionados
                if (nivelPermisos == null || txatPermisos == null) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Permisos requeridos", "Selección incompleta", "Selecciona un nivel de permisos y permiso de chat.");
                        return;
                }

                // Actualizar datos
                selectedLangilea.setIzena(izena);
                selectedLangilea.setAbizena(abizena);
                selectedLangilea.setEmail(emaila);
                selectedLangilea.setPasahitza(pasahitza);
                selectedLangilea.setNivelPermisos(nivelPermisos);
                selectedLangilea.setTxatPermiso(txatPermisos);

                boolean success = LangileaKudeatzailea.editLangilea(selectedLangilea);

                if (success) {
                        System.out.println("Langilea actualizado correctamente.");
                        izenaEditField.clear();
                        abizenaEditField.clear();
                        emailaEditField.clear();
                        pasahitzaEditField.clear();
                        nivelPermisosComboBoxEdit.getSelectionModel().clearSelection();
                        txatPermisosEditComboBox.getSelectionModel().clearSelection();
                        langileakTable.refresh();
                        loadLangileakData();
                } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar", "No se pudo actualizar el Langilea", "Ocurrió un error al intentar guardar los cambios en la base de datos.");
                }
        }

        // Función auxiliar para mostrar alertas
        private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String contenido) {
                Alert alerta = new Alert(tipo);
                alerta.setTitle(titulo);
                alerta.setHeaderText(cabecera);
                alerta.setContentText(contenido);
                alerta.showAndWait();
        }



}
