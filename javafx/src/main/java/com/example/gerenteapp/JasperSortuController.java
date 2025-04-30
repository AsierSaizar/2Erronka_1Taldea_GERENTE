package com.example.gerenteapp;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class JasperSortuController {
    public static String JasperSortu() {
        try {
            String firstName = "Saboreame";

            // Rutas de los archivos
            String jrxmlPath = "./javafx/src/main/resources/templates/2ERRONKA_JatetxeInformea_1Taldea.jrxml";

            // Parámetros del informe
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("firstName", firstName);

            // Obtener datos de ambas listas
            List<Platerak_Jasper> listaDePlaterak = Platerak_Jasper.loadPlaterakList();
            JRBeanCollectionDataSource platerakDataSource = new JRBeanCollectionDataSource(listaDePlaterak);
            parameters.put("platerakDataset", platerakDataSource);

            List<Langilea_Jasper> listaDeLangileak = Langilea_Jasper.loadLangileak();
            JRBeanCollectionDataSource langileakDataSource = new JRBeanCollectionDataSource(listaDeLangileak);
            parameters.put("LangileakDataset", langileakDataSource);

            // Compilar y generar el informe
            String jasperPath = "./javafx/src/main/resources/templates/2ERRONKA_JatetxeInformea_1Taldea.jasper";
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(jasperPath));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            // Exportar a PDF
            String home = System.getProperty("user.home");
            String outputPath = home + "/Desktop/2ERRONKA_JatetxeInformea_1Taldea.pdf";

            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);

            System.out.println("Reporte generado en: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
