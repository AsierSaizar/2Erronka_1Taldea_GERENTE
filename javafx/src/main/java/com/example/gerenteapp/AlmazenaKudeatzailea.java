package com.example.gerenteapp;

import java.sql.Connection;
import java.sql.SQLException;

public class AlmazenaKudeatzailea {
        public static boolean editAlmazenaMinMax(Almazena updatedAlmazena) {
                // Actualizar en la base de datos
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        String updateQuery = "UPDATE almazena SET min = ?, max = ? WHERE id = ?";
                        var preparedStatement = connection.prepareStatement(updateQuery);
                        preparedStatement.setInt(1, updatedAlmazena.getMin());
                        preparedStatement.setInt(2, updatedAlmazena.getMax());
                        preparedStatement.setInt(3, updatedAlmazena.getId());

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
