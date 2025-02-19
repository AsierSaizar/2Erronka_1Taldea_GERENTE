package com.example.gerenteapp;

import java.sql.Connection;
import java.sql.SQLException;

public class MahaiaKudeatzailea {
        public static boolean editMahaia(Mahaia updatedMahaia) {
                // Actualizar en la base de datos
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        String updateQuery = "UPDATE mahaia SET eserlekuak = ?, habilitado = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                        var preparedStatement = connection.prepareStatement(updateQuery);
                        preparedStatement.setInt(1, updatedMahaia.getEserlekuak());
                        preparedStatement.setInt(2, updatedMahaia.isHabilitado());
                        preparedStatement.setInt(3, updatedMahaia.getId());

                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                                return true;
                        } else {
                                return false;
                        }
                } catch (SQLException e) {
                        System.err.println("Error al actualizar la base de datos: " + e.getMessage());
                }
                return false;
        }
}
