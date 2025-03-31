package com.example.gerenteapp;

import javafx.scene.control.Alert;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LangileaKudeatzailea {

    private static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static boolean insertLangilea(Langilea langilea) {
        String sql = "INSERT INTO langilea (izena, abizena, pasahitza, email, nivel_permisos, txat_permiso) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, langilea.getIzena());
            stmt.setString(2, langilea.getAbizena());
            stmt.setString(3, langilea.getPasahitza());
            stmt.setString(4, langilea.getEmail());
            stmt.setInt(5, langilea.getNivelPermisos());
            stmt.setInt(6, langilea.getTxatPermiso());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                mostrarAlerta("Éxito", "Langilea insertado correctamente: " + langilea.getIzena(), Alert.AlertType.INFORMATION);
                return true;
            } else {
                mostrarAlerta("Error", "No se insertó ninguna fila.", Alert.AlertType.ERROR);
                return false;
            }
        } catch (SQLException e) {
            mostrarAlerta("Error en inserción", "Error al insertar langilea: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean editLangilea(Langilea langilea) {
        if (langilea.getId() <= 0) {
            mostrarAlerta("Error", "El ID no es válido para actualizar.", Alert.AlertType.ERROR);
            return false;
        }

        String query = "UPDATE 5_erronka1.langilea SET izena = ?, abizena = ?, email = ?, pasahitza = ?, nivel_permisos = ?, txat_permiso = ? WHERE id = ?";

        try (Connection connection = DBKonexioa.getKonexioa();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, langilea.getIzena());
            preparedStatement.setString(2, langilea.getAbizena());
            preparedStatement.setString(3, langilea.getEmail());
            preparedStatement.setString(4, langilea.getPasahitza());
            preparedStatement.setInt(5, langilea.getNivelPermisos());
            preparedStatement.setInt(6, langilea.getTxatPermiso());
            preparedStatement.setInt(7, langilea.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                String mensaje = String.format(
                        "Langilea actualizado correctamente - ID: %d\n\nCambios realizados:\n- Nombre: %s\n- Apellido: %s\n- Email: %s\n- Contraseña: %s\n- Nivel de permisos: %d\n- Permiso de chat: %d",
                        langilea.getId(),
                        langilea.getIzena(),
                        langilea.getAbizena(),
                        langilea.getEmail(),
                        langilea.getPasahitza(),
                        langilea.getNivelPermisos(),
                        langilea.getTxatPermiso()
                );

                mostrarAlerta("Éxito", mensaje, Alert.AlertType.INFORMATION);
                return true;
            } else {
                mostrarAlerta("Error", "No se actualizó ninguna fila.", Alert.AlertType.ERROR);
                return false;
            }

        } catch (SQLException e) {
            mostrarAlerta("Error en actualización", "Error al actualizar langilea: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return false;
        }
    }


    public static boolean deleteLangilea(String id) {
        String query = "UPDATE 5_erronka1.langilea SET deleted_at = ? WHERE id = ?";
        try (Connection connection = DBKonexioa.getKonexioa();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            preparedStatement.setString(1, currentTimestamp);
            preparedStatement.setString(2, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                mostrarAlerta("Éxito", "Langilea marcado como eliminado - ID: " + id, Alert.AlertType.INFORMATION);
                return true;
            } else {
                mostrarAlerta("Error", "No se pudo eliminar langilea - ID: " + id, Alert.AlertType.ERROR);
                return false;
            }

        } catch (SQLException e) {
            mostrarAlerta("Error en eliminación", "Error haciendo soft delete en langilea: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public static boolean berreskuratuLangilea(String id) {
        String query = "UPDATE 5_erronka1.langilea SET deleted_at = NULL WHERE id = ?";
        try (Connection connection = DBKonexioa.getKonexioa();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                mostrarAlerta("Éxito", "Langilea restaurado correctamente - ID: " + id, Alert.AlertType.INFORMATION);
                return true;
            } else {
                mostrarAlerta("Error", "No se pudo restaurar langilea - ID: " + id, Alert.AlertType.ERROR);
                return false;
            }

        } catch (SQLException e) {
            mostrarAlerta("Error en restauración", "Error restaurando langilea: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }
}
