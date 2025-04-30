package com.example.gerenteapp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Langilea_Jasper {
    private String izena;
    private String abizena;

    public Langilea_Jasper(String izena, String abizena) {
        this.izena = izena;
        this.abizena = abizena;
    }

    public String getIzena() {
        return izena;
    }

    public void setIzena(String izena) {
        this.izena = izena;
    }

    public String getAbizena() {
        return abizena;
    }

    public void setAbizena(String abizena) {
        this.abizena = abizena;
    }



    @Override
    public String toString() {
        return "Langilea{" +
                "izena='" + izena + '\'' +
                ", abizena='" + abizena + '\'' +
                '}';
    }


    public static List<Langilea_Jasper> loadLangileak() {

        List<Langilea_Jasper> langileak = new ArrayList<>();

        try (
                Connection connection = DBKonexioa.getKonexioa();
                Statement statement = connection.createStatement();
                //ALDATU
                ResultSet resultSet = statement.executeQuery("SELECT Cantidad_de_personas, total_pedidos FROM 5_erronka1.view_pertsona_kopurua;")) {

            while (resultSet.next()) {
                String izena = resultSet.getString("Cantidad_de_personas");
                String abizena = resultSet.getString("total_pedidos");

                langileak.add(new Langilea_Jasper(izena, abizena));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return langileak;

    }
}

