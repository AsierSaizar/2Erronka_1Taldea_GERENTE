package com.example.gerenteapp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Platerak_Jasper {
    private String plateraIzena;
    private int kantitatea;  // <-- Changed to int

    public Platerak_Jasper(String plateraIzena, int kantitatea) {  // <-- Updated constructor
        this.plateraIzena = plateraIzena;
        this.kantitatea = kantitatea;
    }

    public String getPlateraIzena() {
        return plateraIzena;
    }

    public void setPlateraIzena(String plateraIzena) {
        this.plateraIzena = plateraIzena;
    }

    public int getKantitatea() {  // <-- Updated getter
        return kantitatea;
    }

    public void setKantitatea(int kantitatea) {  // <-- Updated setter
        this.kantitatea = kantitatea;
    }

    @Override
    public String toString() {
        return "Platera{" +
                "plateraIzena='" + plateraIzena + '\'' +
                ", kantitatea=" + kantitatea +
                '}';
    }

    public static List<Platerak_Jasper> loadPlaterakList() {
        List<Platerak_Jasper> platerak = new ArrayList<>();

        try (
                Connection connection = DBKonexioa.getKonexioa();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT plato_nombre, cantidad_pedidos FROM 5_erronka1.view_plateracant")) {

            while (resultSet.next()) {
                String plateraIzena = resultSet.getString("plato_nombre");
                int kantitatea = resultSet.getInt("cantidad_pedidos");  // <-- Changed to getInt()

                platerak.add(new Platerak_Jasper(plateraIzena, kantitatea));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return platerak;
    }
}
