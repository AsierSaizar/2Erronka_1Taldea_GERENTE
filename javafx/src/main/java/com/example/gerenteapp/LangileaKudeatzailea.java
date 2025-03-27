package com.example.gerenteapp;

import java.sql.*;

public class LangileaKudeatzailea {

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
                System.out.println("Inserción exitosa");
                return true;
            } else {
                System.out.println("Error: No se insertó ninguna fila.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar langilea: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static boolean editLangilea(Langilea langilea) {
        // Validar el ID del Langilea
        if (langilea.getId() <= 0) {
            System.err.println("El ID no es válido para actualizar.");
            return false;
        }

        // Verificar que los valores de nivel de permisos y chat no sean null

        // Consulta SQL de actualización
        String query = "UPDATE 5_erronka1.langilea SET izena = ?, abizena = ?, email = ?, pasahitza = ?, " +
                "nivel_permisos = ?, txat_permiso = ? WHERE id = ?";

        try (Connection connection = DBKonexioa.getKonexioa();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Verificar la conexión a la base de datos
            if (connection == null) {
                System.err.println("No se pudo conectar a la base de datos.");
                return false;
            }

            // Asignar los parámetros a la consulta
            preparedStatement.setString(1, langilea.getIzena());
            preparedStatement.setString(2, langilea.getAbizena());
            preparedStatement.setString(3, langilea.getEmail());
            preparedStatement.setString(4, langilea.getPasahitza());
            preparedStatement.setInt(5, langilea.getNivelPermisos());
            preparedStatement.setInt(6, langilea.getTxatPermiso());
            preparedStatement.setInt(7, langilea.getId());

            // Mostrar consulta SQL y los valores de los parámetros para depuración
            System.out.println("Ejecutando SQL: " + query);
            System.out.println("Con valores: " + langilea.getIzena() + ", " + langilea.getAbizena() + ", " +
                    langilea.getEmail() + ", " + langilea.getPasahitza() + ", " +
                    langilea.getNivelPermisos() + ", " + langilea.getTxatPermiso() + ", ID: " + langilea.getId());

            // Ejecutar la actualización
            int rowsAffected = preparedStatement.executeUpdate();

            // Verificar si la actualización afectó filas
            if (rowsAffected > 0) {
                System.out.println("Langilea actualizado correctamente. Filas afectadas: " + rowsAffected);
                return true;
            } else {
                System.err.println("No se actualizó ninguna fila.");
                return false;
            }

        } catch (SQLException e) {
            // Capturar errores y mostrarlos en consola
            System.err.println("Error al actualizar langilea: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }






    public static boolean deleteLangilea(String id) {
        String query = "DELETE FROM 5_erronka1.langilea WHERE id = ?";
        try (Connection connection = DBKonexioa.getKonexioa();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting langilea: " + e.getMessage());
            return false;
        }
    }

    public static boolean berreskuratuLangilea(String id) {
        String query = "UPDATE 5_erronka1.langilea SET deleted_at = NULL WHERE id = ?";
        try (Connection connection = DBKonexioa.getKonexioa();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error restoring langilea: " + e.getMessage());
            return false;
        }
    }
}
