package com.example.gerenteapp;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;

import java.io.File;
import net.sf.jasperreports.engine.JasperCompileManager;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class JasperSortuController {
    public static void main(String[] args) {
        //compile();
        JasperSortu();
    }

    public static void compile(){
        try {
            // Ruta al archivo .jrxml (fuente del informe)
            String jrxmlPath = "javafx/src/main/resources/templates/2ERRONKA_JatetxeInformea_1Taldea.jrxml";

            // Ruta donde se guardará el .jasper compilado
            String jasperPath = "javafx/src/main/resources/templates/2ERRONKA_JatetxeInformea_1Taldea.jasper";

            // Compilar el .jrxml al .jasper
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);

            System.out.println("Informe compilado correctamente en: " + jasperPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String JasperSortu() {
        try {
            String firstName = "Saboreame";

            // Rutas de los archivos
            String jrxmlPath = "src/main/resources/templates/2ERRONKA_JatetxeInformea_1Taldea.jrxml";

            // Parámetros del informe
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("firstName", firstName);

            // Obtener datos de ambas listas
            List<Platerak_Jasper> listaDePlaterak;
            try {
                listaDePlaterak = Platerak_Jasper.loadPlaterakList();
            } catch (Exception e) {
                System.out.println("Error al cargar Platerak");
                return "Error al cargar Platerak";
            }

            JRBeanCollectionDataSource platerakDataSource = new JRBeanCollectionDataSource(listaDePlaterak);
            parameters.put("platerakDataset", platerakDataSource);

            List<Langilea_Jasper> listaDeLangileak;
            try {
                listaDeLangileak = Langilea_Jasper.loadLangileak();
            } catch (Exception e) {
                System.out.println("Error al cargar Langileak");
                return "Error al cargar Langileak";
            }

            JRBeanCollectionDataSource langileakDataSource = new JRBeanCollectionDataSource(listaDeLangileak);
            parameters.put("LangileakDataset", langileakDataSource);

            // Compilar y generar el informe
            String jasperPath = "src/main/resources/templates/2ERRONKA_JatetxeInformea_1Taldea.jasper";
            try {
                JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);
            } catch (Exception e) {
                e.printStackTrace(); // ← Esto imprime el error real en la consola
                System.out.println("Error al compilar el jrxml");
                System.out.println(e.getMessage());
                return "Error al compilar el jrxml";
            }


            JasperReport jasperReport;
            try {
                jasperReport = (JasperReport) JRLoader.loadObject(new File(jasperPath));
            } catch (Exception e) {
                System.out.println("Error al cargar el .jasper");
                return "Error al cargar el .jasper";
            }

            JasperPrint jasperPrint;
            try {
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            } catch (Exception e) {
                System.out.println("Error al rellenar el informe");
                return "Error al rellenar el informe";
            }

            // Exportar a PDF
            try {
                String home = System.getProperty("user.home");
                String outputPath = home + "/Desktop/2ERRONKA_JatetxeInformea_1Taldea2.pdf";
                JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
            } catch (Exception e) {
                e.printStackTrace(); // ← Esto imprime el error real en la consola
                System.out.println("Error al exportar el PDF");
                return "Error al exportar el PDF";
            }

            System.out.println("Reporte generado en el escritorio correctamente");
            return "OK";

        } catch (Exception e) {
            System.out.println("Error inesperado");
            return "Error inesperado";
        }
    }


}

