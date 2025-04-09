package com.example.gerenteapp;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LangileaKudeatzailea {

    private static boolean isTestMode = false;

    // Método para activar o desactivar el modo de prueba
    public static void setTestMode(boolean isTestMode) {
        LangileaKudeatzailea.isTestMode = isTestMode;
    }

    public static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        if (System.getProperty("test.env") != null) return; // No mostrar nada si está en test

        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
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

    public static Langilea getLangileaById(String id) {
        String query = "SELECT * FROM langilea WHERE id = ? AND deleted_at IS NULL"; // Asumimos un soft delete
        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);  // Establecemos el ID en la consulta

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {  // Si encontramos un resultado
                    // Crear un nuevo objeto Langilea con los datos obtenidos
                    Langilea langilea = new Langilea(
                            rs.getInt("id"),
                            rs.getString("izena"),
                            rs.getString("abizena"),
                            rs.getString("pasahitza"),
                            rs.getString("email"),
                            rs.getInt("nivel_permisos"),
                            rs.getString("txat_permiso"),
                            rs.getInt("otros_campos") // Si tienes otros campos, añádelos aquí
                    );
                    return langilea;
                } else {
                    return null; // Si no se encuentra un Langilea con ese ID
                }
            }
        } catch (SQLException e) {
            // Mostrar alerta si ocurre un error con la base de datos
            mostrarAlerta("Error en búsqueda", "Error al obtener langilea por ID: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
            return null;
        }
    }
}
