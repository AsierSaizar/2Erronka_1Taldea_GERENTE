package com.example.gerenteapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AlmazenaController extends BaseController {
        private Stage usingStage;

        @FXML
        private TableView<Almazena> almazenaTable;

        @FXML
        private TableColumn<Almazena, Integer> idColumn;

        @FXML
        private TableColumn<Almazena, String> izenaColumn;

        @FXML
        private TableColumn<Almazena, String> motaColumn;

        @FXML
        private TableColumn<Almazena, String> ezaugarriakColumn;

        @FXML
        private TableColumn<Almazena, Integer> stockColumn;

        @FXML
        private TableColumn<Almazena, String> unitateaColumn;

        @FXML
        private TableColumn<Almazena, Integer> minColumn;

        @FXML
        private TableColumn<Almazena, Integer> maxColumn;

        @FXML
        private TableColumn<Almazena, String> createdAtColumn;

        @FXML
        private TableColumn<Almazena, Integer> createdByColumn;

        private ObservableList<Almazena> almazenaData = FXCollections.observableArrayList();


        @FXML
        private TextField IDField;

        @FXML
        private TextField izenaField;

        @FXML
        private TextField minField;

        @FXML
        private TextField maxField;


        @FXML
        public void initialize() {
                // Configuración de las columnas de la tabla
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                izenaColumn.setCellValueFactory(new PropertyValueFactory<>("izena"));
                motaColumn.setCellValueFactory(new PropertyValueFactory<>("mota"));
                ezaugarriakColumn.setCellValueFactory(new PropertyValueFactory<>("ezaugarria"));
                stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
                unitateaColumn.setCellValueFactory(new PropertyValueFactory<>("unitatea"));
                minColumn.setCellValueFactory(new PropertyValueFactory<>("min"));
                maxColumn.setCellValueFactory(new PropertyValueFactory<>("max"));
                createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
                createdByColumn.setCellValueFactory(new PropertyValueFactory<>("createdBy"));

                // Carga los datos de la tabla
                // Listener para detectar cambios en la selección de la tabla
                almazenaTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null) {
                                populateForm(newValue);
                        }
                });
                loadAlmazenaData();
        }

        private void populateForm(Almazena almazena) {
                IDField.setText(String.valueOf(almazena.getId()));
                izenaField.setText(almazena.getIzena());
                minField.setText(String.valueOf(almazena.getMin()));
                maxField.setText(String.valueOf(almazena.getMax()));
        }

        private void loadAlmazenaData() {
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        almazenaData.clear();
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM almazena");

                        while (resultSet.next()) {
                                almazenaData.add(new Almazena(
                                        resultSet.getInt("id"),
                                        resultSet.getString("izena"),
                                        resultSet.getString("mota"),
                                        resultSet.getString("ezaugarria"),
                                        resultSet.getInt("stock"),
                                        resultSet.getString("unitatea"),
                                        resultSet.getInt("min"),
                                        resultSet.getInt("max"),
                                        resultSet.getString("created_at"),
                                        resultSet.getInt("created_by")
                                ));
                        }

                        almazenaTable.setItems(almazenaData);
                } catch (SQLException e) {
                        System.err.println("Error loading almazena data: " + e.getMessage());
                }
        }


        public void editAlmazena(ActionEvent actionEvent) {
                // Obtener el registro seleccionado
                Almazena selectedItem = almazenaTable.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                        System.out.println("Por favor, selecciona un registro para editar.");
                        return;
                }

                try {
                        // Validar y obtener los valores del formulario
                        int newMin = Integer.parseInt(minField.getText());
                        int newMax = Integer.parseInt(maxField.getText());

                        // Validar que Min <= Max
                        if (newMin > newMax) {
                                System.out.println("El valor de 'Min' no puede ser mayor que 'Max'.");
                                return;
                        }

                        // Crear una instancia de Almazena con los valores modificados
                        Almazena updatedAlmazena = new Almazena(
                                selectedItem.getId(),
                                selectedItem.getIzena(),
                                selectedItem.getMota(),
                                selectedItem.getEzaugarria(),
                                selectedItem.getStock(),
                                selectedItem.getUnitatea(),
                                newMin, // Min modificado
                                newMax, // Max modificado
                                selectedItem.getCreatedAt(),
                                selectedItem.getCreatedBy()
                        );
                        if (AlmazenaKudeatzailea.editAlmazenaMinMax(updatedAlmazena)) {
                                System.out.println("Min eta Max ondo eraldatu da.");

                                loadAlmazenaData();
                                clearInputFields();
                        }

                } catch (NumberFormatException e) {
                        System.out.println("Por favor, ingresa valores numéricos válidos para 'Min' y 'Max'.");
                }
        }

        private void clearInputFields() {
                IDField.clear();
                minField.clear();
                maxField.clear();
        }

}
