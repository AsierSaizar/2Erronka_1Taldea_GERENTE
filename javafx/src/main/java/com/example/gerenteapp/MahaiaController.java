package com.example.gerenteapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MahaiaController extends BaseController {

        private Stage usingStage;

        @FXML
        private TableView<Mahaia> mahailakTable;

        @FXML
        private TableColumn<Mahaia, Integer> idColumn;

        @FXML
        private TableColumn<Mahaia, Integer> mahailaZenbakiaColumn;

        @FXML
        private TableColumn<Mahaia, Integer> eserlekuakColumn;

        @FXML
        private TableColumn<Mahaia, Boolean> habilitadoColumn;
        @FXML
        private TableColumn<Mahaia, String> updated_atColumn;

        @FXML
        private TextField IDField1;

        @FXML
        private TextField mahaiZenbField1;

        @FXML
        private TextField eserlekuKantField1;

        @FXML
        private ComboBox gaitutaComboBox;

        private ObservableList<Mahaia> mahailakData = FXCollections.observableArrayList();

        @FXML
        public void initialize() {
                // Configurar las columnas para la tabla
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                mahailaZenbakiaColumn.setCellValueFactory(new PropertyValueFactory<>("mahaila_zenbakia"));
                eserlekuakColumn.setCellValueFactory(new PropertyValueFactory<>("eserlekuak"));
                habilitadoColumn.setCellValueFactory(new PropertyValueFactory<>("habilitado"));
                updated_atColumn.setCellValueFactory(new PropertyValueFactory<>("updated_at"));

                // Cargar los datos desde la base de datos
                loadMahailakData();

                // Agregar listener para detectar la selección en la tabla
                mahailakTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null) {
                                populateFields(newValue);
                        }
                });
        }

        private void loadMahailakData() {
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        // Limpiar la lista antes de cargar nuevos datos
                        mahailakData.clear();

                        // Crear una consulta SQL
                        String query = "SELECT * FROM 5_erronka1.mahaia";
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(query);

                        // Procesar el resultado de la consulta
                        while (resultSet.next()) {
                                int habilitado = 0;
                                String update_at = resultSet.getString("updated_at");
                                if(resultSet.getBoolean("habilitado")){
                                        habilitado = 1;
                                }
                                Mahaia mahaila = new Mahaia(
                                        resultSet.getInt("id"),
                                        resultSet.getInt("mahaila_zenbakia"),
                                        resultSet.getInt("eserlekuak"),
                                        habilitado,
                                        update_at
                                );
                                mahailakData.add(mahaila);
                        }

                        // Asignar los datos a la tabla
                        mahailakTable.setItems(mahailakData);
                } catch (SQLException e) {
                        System.err.println("Errorea mahailak datuak kargatzerakoan: " + e.getMessage());
                }
        }

        /**
         * Transferir los datos del registro seleccionado a los campos de texto.
         */
        private void populateFields(Mahaia selectedMahaia) {
                IDField1.setText(String.valueOf(selectedMahaia.getId()));
                mahaiZenbField1.setText(String.valueOf(selectedMahaia.getMahaila_zenbakia()));
                eserlekuKantField1.setText(String.valueOf(selectedMahaia.getEserlekuak()));
                gaitutaComboBox.getSelectionModel().select(selectedMahaia.isHabilitado());

        }

        public void mahaiaEraldatu(ActionEvent actionEvent) {

                Mahaia selectedItem = mahailakTable.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                        System.out.println("Por favor, selecciona un registro para editar.");
                        return;
                }

                try {
                        // Validar y obtener los valores del formulario
                        int newMahailaZenbakia = Integer.parseInt(mahaiZenbField1.getText());
                        int newEserlekuak = Integer.parseInt(eserlekuKantField1.getText());

                        int newHabilitado = Integer.parseInt((String) gaitutaComboBox.getValue());

                        String update_at = null;

                        // Crear una instancia actualizada de Mahaia con los valores modificados
                        Mahaia updatedMahaia = new Mahaia(
                                selectedItem.getId(), // El ID no cambia
                                newMahailaZenbakia,
                                newEserlekuak,
                                newHabilitado,
                                update_at
                        );

                        // Llamar al método para editar el registro en la base de datos
                        if (MahaiaKudeatzailea.editMahaia(updatedMahaia)) {
                                System.out.println("El registro se ha actualizado correctamente.");

                                // Recargar los datos de la tabla
                                loadMahailakData();
                                clearInputFields();
                        } else {
                                System.out.println("Hubo un error al actualizar el registro.");
                        }

                } catch (NumberFormatException e) {
                        System.out.println("Por favor, ingresa valores numéricos válidos.");
                }
        }

        /**
         * Limpiar los campos de entrada del formulario.
         */
        private void clearInputFields() {
                IDField1.clear();
                mahaiZenbField1.clear();
                eserlekuKantField1.clear();
                gaitutaComboBox.getSelectionModel().selectFirst();
        }

}
