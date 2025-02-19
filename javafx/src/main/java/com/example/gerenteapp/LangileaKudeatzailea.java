package com.example.gerenteapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LangileaKudeatzailea {

    public static void insertLangilea(Langilea langilea) {
        String sql = "INSERT INTO langilea (izena, abizena, email, pasahitza, nivel_permisos) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer los parámetros del PreparedStatement con los valores del objeto Langilea
            stmt.setString(1, langilea.getIzena());
            stmt.setString(2, langilea.getAbizena());
            stmt.setString(3, langilea.getEmail());
            stmt.setString(4, langilea.getPasahitza());
            stmt.setInt(5, langilea.getNivelPermisos());

            // Ejecutar la sentencia SQL
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("¡Langilea creado exitosamente!");
            }

        } catch (SQLException e) {
            System.err.println("Errorea langilea sortzerakoan: " + e.getMessage());
        }
    }

        public static boolean deleteLangilea(String id) {
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        String sql = "UPDATE langilea SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setInt(1, Integer.parseInt(id));

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                                return true;
                        } else {
                                System.out.println("No se encontró un langilea con ese ID.");
                                return false;
                        }
                } catch (SQLException e) {
                        System.err.println("Error al eliminar langilea: " + e.getMessage());
                } catch (NumberFormatException e) {
                        System.err.println("ID no válido.");
                }
                return false;
        }

        public static boolean berreskuratuLangilea(String id) {
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        String sql = "UPDATE langilea SET deleted_at = null WHERE id = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setInt(1, Integer.parseInt(id));

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                                return true;
                        } else {
                                System.out.println("No se encontró un langilea con ese ID.");
                                return false;
                        }
                } catch (SQLException e) {
                        System.err.println("Error al eliminar langilea: " + e.getMessage());
                } catch (NumberFormatException e) {
                        System.err.println("ID no válido.");
                }
                return false;
        }


        public static boolean editLangilea(Langilea selectedLangilea) {

        // SQL para actualizar el registro
        String sql = "UPDATE langilea SET izena = ?, abizena = ?, email = ?, pasahitza = ?, nivel_permisos = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBKonexioa.getKonexioa();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Establecer los parámetros del PreparedStatement
            stmt.setString(1, selectedLangilea.getIzena());
            stmt.setString(2, selectedLangilea.getAbizena());
            stmt.setString(3, selectedLangilea.getEmail());
            stmt.setString(4, selectedLangilea.getPasahitza());
            stmt.setInt(5, selectedLangilea.getNivelPermisos());
            stmt.setInt(6, selectedLangilea.getId());

            // Ejecutar la sentencia SQL
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("¡Langilea actualizado exitosamente!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Errorea langilea eguneratzerakoan: " + e.getMessage());
        }
        return false;
    }
}
