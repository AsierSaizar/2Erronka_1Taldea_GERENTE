package com.example.gerenteapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginKudeatzailea {

        // Modificar la consulta para permitir login a cualquier trabajador
        static Langilea erabiltzaileaKomprobatu(String email, String pasa) {
                String query = "SELECT * FROM langilea WHERE email = ? AND pasahitza = ? and deleted_at IS NULL";

                try (Connection conn = DBKonexioa.getKonexioa();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                        stmt.setString(1, email);
                        stmt.setString(2, pasa);

                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                                if (rs.getInt("nivel_permisos") !=0){
                                        Alertak.mostrarMensajeAlerta("Erabiltzaile honek ez dauzka aplikazio hontarako baimenak.");
                                }else{
                                        // Crear y devolver una instancia de Langilea
                                        return new Langilea(
                                                rs.getInt("id"),
                                                rs.getString("izena"),
                                                rs.getString("abizena"),
                                                rs.getString("pasahitza"),
                                                rs.getString("email"),
                                                rs.getInt("nivel_permisos"),
                                                rs.getString("deleted_at"),
                                                rs.getInt("txat_permiso")
                                        );
                                }

                        }else{
                                Alertak.mostrarMensajeAlerta("Erabiltzaile eta pasahitzak ez dira zuzenak, saiatu berriro.");
                        }

                } catch (SQLException e) {
                        System.err.println("Errorea autentikazioan: " + e.getMessage());
                        Alertak.mostrarMensajeAlerta("Datu basera konektatzeko arazoak.");
                }

                // Si no hay resultados o hay un error, devolver null
                return null;
        }
}
