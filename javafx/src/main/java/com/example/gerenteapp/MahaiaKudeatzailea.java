package com.example.gerenteapp;

import java.sql.Connection;
import java.sql.SQLException;

public class MahaiaKudeatzailea {
        public static boolean editMahaia(Mahaia updatedMahaia) {
                // Actualizar en la base de datos
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        String updateQuery = "UPDATE mahaia SET eserlekuak = ?, habilitado = ?, terraza = ?,updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                        var preparedStatement = connection.prepareStatement(updateQuery);
                        preparedStatement.setInt(1, updatedMahaia.getEserlekuak());
                        preparedStatement.setInt(2, updatedMahaia.isHabilitado());
                        preparedStatement.setInt(3, updatedMahaia.getTerraza());
                        preparedStatement.setInt(4, updatedMahaia.getId());

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

        public static boolean kanpokoMahaiEgoera2() {
                // Actualizar en la base de datos
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        String selectQuery = "SELECT * FROM 5_erronka1.mahaia WHERE terraza = 2;";
                        var preparedStatement = connection.prepareStatement(selectQuery);
                        var resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                                return true;
                        } else {
                                return false;
                        }
                } catch (SQLException e) {
                        System.err.println("Error al Mirar los datos: " + e.getMessage());
                }
                return false;
        }

        public static boolean kanpokoMahaiEgoera1() {
// Actualizar en la base de datos
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        String selectQuery = "SELECT * FROM 5_erronka1.mahaia WHERE terraza = 1;";
                        var preparedStatement = connection.prepareStatement(selectQuery);
                        var resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                                return true;
                        } else {
                                return false;
                        }
                } catch (SQLException e) {
                        System.err.println("Error al Mirar los datos: " + e.getMessage());
                }
                return false;
        }



        public static boolean kanpokoMahaiakGaitu() {

                // Actualizar en la base de datos
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        String updateQuery = "UPDATE mahaia SET terraza = 1 WHERE terraza = 2";
                        var preparedStatement = connection.prepareStatement(updateQuery);

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

        public static boolean kanpokoMahaiakDesgaitu() {
                // Actualizar en la base de datos
                try (Connection connection = DBKonexioa.getKonexioa()) {
                        String updateQuery = "UPDATE mahaia SET terraza = 2 WHERE terraza = 1";
                        var preparedStatement = connection.prepareStatement(updateQuery);

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
