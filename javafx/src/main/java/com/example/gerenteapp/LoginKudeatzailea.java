package com.example.gerenteapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginKudeatzailea {
        static Langilea erabiltzaileaKomprobatu(String email, String pasa) {
                String query = "SELECT * FROM langilea WHERE email = ? AND pasahitza = ? and nivel_permisos = 0 and deleted_at IS NULL";

                try (Connection conn = DBKonexioa.getKonexioa();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                        stmt.setString(1, email);
                        stmt.setString(2, pasa);

                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                                // Crear y devolver una instancia de Langilea
                                return new Langilea(
                                        rs.getInt("id"),
                                        rs.getString("izena"),
                                        rs.getString("abizena"),
                                        rs.getString("pasahitza"),
                                        rs.getString("email"),
                                        rs.getInt("nivel_permisos"),
                                        rs.getString("deleted_at")
                                );
                        }

                } catch (SQLException e) {
                        System.err.println("Errorea autentikazioan: " + e.getMessage());
                }

                // Si no hay resultados o hay un error, devolver null
                return null;
        }
}
