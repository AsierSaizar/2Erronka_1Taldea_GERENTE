package com.example.gerenteapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

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

        public void createLangilea(ActionEvent actionEvent) {
                String izena = izenaField.getText().trim();
                String abizena = abizenaField.getText().trim();
                String emaila = emailaField.getText().trim();
                String pasahitza = pasahitzaField.getText().trim();
                int txatPermisos;
                int nivelPermisos;

                try {
                        nivelPermisos = nivelPermisosComboBox.getValue() != null ? nivelPermisosComboBox.getValue() : 1;
                        txatPermisos = txatPermisosComboBox.getValue() != null ? txatPermisosComboBox.getValue() : 0;
                } catch (NullPointerException e) {
                        System.out.println("Error: Selecciona un nivel de permisos y un permiso de chat.");
                        return;
                }

                if (izena.isEmpty() || abizena.isEmpty() || emaila.isEmpty() || pasahitza.isEmpty()) {
                        System.out.println("Error: Todos los campos deben estar llenos.");
                        return;
                }

                // Mensajes de depuración
                System.out.println("Datos recogidos para inserción:");
                System.out.println("Izena: " + izena);
                System.out.println("Abizena: " + abizena);
                System.out.println("Emaila: " + emaila);
                System.out.println("Pasahitza: " + pasahitza);
                System.out.println("Nivel Permisos: " + nivelPermisos);
                System.out.println("Txat Permisos: " + txatPermisos);

                Langilea langilea = new Langilea(0, izena, abizena, pasahitza, emaila, nivelPermisos, null, txatPermisos);
                boolean success = LangileaKudeatzailea.insertLangilea(langilea);

                if (success) {
                        System.out.println("Langilea creado correctamente.");
                } else {
                        System.out.println("Error al crear el Langilea.");
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
                        System.out.println("Por favor, selecciona un langilea o introduce un ID válido.");
                        return;
                }
                if (LangileaKudeatzailea.deleteLangilea(id)) {
                        System.out.println("Langilea eliminado correctamente.");
                        loadLangileakData();
                        IdDeleteField.clear();
                        izenaDeleteField.clear();
                }
        }

        public void berreskuratuLangilea(ActionEvent actionEvent) {
                String id = IdDeleteField.getText();

                if (id.isEmpty()) {
                        System.out.println("Por favor, selecciona un langilea o introduce un ID válido.");
                        return;
                }
                if (LangileaKudeatzailea.berreskuratuLangilea(id)) {
                        System.out.println("Langilea berreskuratuta correctamente.");
                        loadLangileakData();
                        IdDeleteField.clear();
                        izenaDeleteField.clear();
                }
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
                // Obtención del Langilea seleccionado
                Langilea selectedLangilea = langileakTable.getSelectionModel().getSelectedItem();

                if (selectedLangilea == null) {
                        System.out.println("Por favor, selecciona un registro para editar.");
                        return;
                }

                // Obtener los valores de los campos de texto y ComboBox
                String izena = izenaEditField.getText().trim().isEmpty() ? selectedLangilea.getIzena() : izenaEditField.getText().trim();
                String abizena = abizenaEditField.getText().trim().isEmpty() ? selectedLangilea.getAbizena() : abizenaEditField.getText().trim();
                String emaila = emailaEditField.getText().trim().isEmpty() ? selectedLangilea.getEmail() : emailaEditField.getText().trim();
                String pasahitza = pasahitzaEditField.getText().trim().isEmpty() ? selectedLangilea.getPasahitza() : pasahitzaEditField.getText().trim();

                // Obtener los valores seleccionados en los ComboBox
                Integer nivelPermisos = nivelPermisosComboBoxEdit.getSelectionModel().getSelectedItem();
                Integer txatPermisos = txatPermisosEditComboBox.getSelectionModel().getSelectedItem();

                // Verificar que los valores de los ComboBox no sean null
                if (nivelPermisos == null || txatPermisos == null) {
                        System.out.println("Error: Nivel de permisos o permiso de chat no seleccionados.");
                        return;
                }

                // Verificar que los campos no estén vacíos antes de actualizar
                System.out.println("Valores a actualizar: ");
                System.out.println("Izena: " + izena);
                System.out.println("Abizena: " + abizena);
                System.out.println("Emaila: " + emaila);
                System.out.println("Pasahitza: " + pasahitza);
                System.out.println("NivelPermisos: " + nivelPermisos);
                System.out.println("TxatPermiso: " + txatPermisos);

                // Actualizar los valores en el objeto seleccionado
                selectedLangilea.setIzena(izena);
                selectedLangilea.setAbizena(abizena);
                selectedLangilea.setEmail(emaila);
                selectedLangilea.setPasahitza(pasahitza);
                selectedLangilea.setNivelPermisos(nivelPermisos);
                selectedLangilea.setTxatPermiso(txatPermisos);

                // Llamar al método de la clase LangileaKudeatzailea para actualizar en la base de datos
                boolean success = LangileaKudeatzailea.editLangilea(selectedLangilea);

                if (success) {
                        System.out.println("Langilea actualizado correctamente.");

                        // Limpiar los campos de edición después de actualizar
                        izenaEditField.clear();
                        abizenaEditField.clear();
                        emailaEditField.clear();
                        pasahitzaEditField.clear();
                        nivelPermisosComboBoxEdit.getSelectionModel().selectFirst();
                        txatPermisosEditComboBox.getSelectionModel().selectFirst();

                        // Actualizar la tabla y recargar los datos
                        langileakTable.refresh();
                        loadLangileakData();
                } else {
                        System.out.println("Error al actualizar el Langilea.");
                }
        }

}
