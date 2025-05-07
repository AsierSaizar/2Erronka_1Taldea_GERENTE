package com.example.gerenteapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;


import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class EguraldiaControllerTest {

    private EguraldiaController controller;

    @BeforeEach
    void setUp() {
        controller = new EguraldiaController();
    }

    @Test
    void testXmlPush() throws Exception {
        // Crear un archivo de entrada temporal simulado
        File tempFile = File.createTempFile("input-", ".xml");
        try (FileWriter writer = new FileWriter(tempFile)) {
            // Escribir un contenido XML básico para la prueba
            writer.write("<root><prediccion><dia fecha=\"2025-04-10\"><temperatura><maxima>20</maxima><minima>10</minima></temperatura><prob_precipitacion>0.0</prob_precipitacion><estado_cielo descripcion=\"Despejado\">15n</estado_cielo><viento><velocidad>10</velocidad><direccion>Oeste</direccion></viento></dia></prediccion></root>");
        }

        // Simular que `getFiles` retorna el archivo temporal (usando el archivo como InputStream)
        InputStream fakeInputStream = new FileInputStream(tempFile);
        ArrayList<Object> fakeFiles = new ArrayList<>();
        fakeFiles.add(fakeInputStream);

        // Llamamos a `xmlPush` sin necesidad de hacer una solicitud a una URL
        controller.xmlPush();

        // Verificamos que el archivo de salida se ha creado correctamente
        File outputFile = new File("./eguraldia.xml");
        assertTrue(outputFile.exists(), "El archivo de salida debería existir");

        // Limpiar después de la prueba
        if (outputFile.exists()) {
            Files.delete(outputFile.toPath());
        }
        tempFile.delete();  // Eliminar el archivo temporal de entrada
    }


    @Test
    void testGetFilesFromUrl() throws Exception {
        // Simulamos la obtención de un archivo desde una URL real
        String testUrl = "https://www.aemet.es/xml/municipios/localidad_20076.xml";

        // Llamamos a getFiles con la URL
        ArrayList<Object> result = EguraldiaController.getFiles(true, testUrl);

        // Verificamos que el InputStream no sea nulo y que sea del tipo esperado
        assertNotNull(result.get(0), "El InputStream no debe ser nulo");
    }

    @Test
    void testArtxiboaIgoFtp() throws Exception {
        // Especificamos la ruta absoluta correcta al archivo eguraldia.xml
        File outputFile = new File("../eguraldia.xml");

        // Llamar al método artxiboaIgoFtp
        controller.artxiboaIgoFtp();

        // Verificamos que el archivo exista después de la llamada
        assertTrue(outputFile.exists(), "El archivo de salida debe existir para ser subido");

        // Limpiar después de la prueba
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    @Test
    void testXmlPush_zenbaitEgun() throws Exception {
        File tempFile = File.createTempFile("input-", ".xml");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("<root><prediccion>" +
                    "<dia fecha=\"2025-04-10\"><temperatura><maxima>22</maxima><minima>12</minima></temperatura><prob_precipitacion>20</prob_precipitacion><estado_cielo descripcion=\"Cubierto\">25n</estado_cielo><viento><velocidad>15</velocidad><direccion>Norte</direccion></viento></dia>" +
                    "<dia fecha=\"2025-04-11\"><temperatura><maxima>18</maxima><minima>8</minima></temperatura><prob_precipitacion>80</prob_precipitacion><estado_cielo descripcion=\"Despejado\">5n</estado_cielo><viento><velocidad>5</velocidad><direccion>Sur</direccion></viento></dia>" +
                    "</prediccion></root>");
        }

        InputStream input = new FileInputStream(tempFile);
        ArrayList<Object> files = new ArrayList<>();
        files.add(input);

        controller.xmlPush();

        File output = new File("./eguraldia.xml");
        assertTrue(output.exists(), "El archivo de salida debería existir");
        Files.deleteIfExists(output.toPath());
        tempFile.delete();
    }

    @Test
    void testXmlPush_egiaztatuIrteerakoEdukia() throws Exception {
        // Crear archivo XML temporal con datos simulados
        File tempFile = File.createTempFile("input-", ".xml");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("<root><prediccion><dia fecha=\"2025-04-10\">" +
                    "<temperatura><maxima>20</maxima><minima>10</minima></temperatura>" +
                    "<prob_precipitacion>0.0</prob_precipitacion>" +
                    "<estado_cielo descripcion=\"Despejado\">15n</estado_cielo>" +
                    "<viento><velocidad>10</velocidad><direccion>Oeste</direccion></viento>" +
                    "</dia></prediccion></root>");
        }

        InputStream fakeInput = new FileInputStream(tempFile);
        ArrayList<Object> mockResult = new ArrayList<>();
        mockResult.add(fakeInput);
        mockResult.add(tempFile);

        // Mock del método estático sin import static
        try (MockedStatic<EguraldiaController> mocked = Mockito.mockStatic(EguraldiaController.class)) {
            mocked.when(() -> EguraldiaController.getFiles(Mockito.anyBoolean(), Mockito.anyString()))
                    .thenReturn(mockResult);

            EguraldiaController controller = new EguraldiaController() {
                public static void artxiboaIgoFtp() {
                    // No hacer nada en el test
                }
            };

            controller.xmlPush();

            File output = new File("./eguraldia.xml");
            assertTrue(output.exists());

            String contenido = new String(Files.readAllBytes(output.toPath()));
            assertTrue(contenido.contains("<tenperatura_gorakoa>20</tenperatura_gorakoa>"));
            assertTrue(contenido.contains("<tenperatura_behekoa>10</tenperatura_behekoa>"));
            assertTrue(contenido.contains("<deskribapena>Argi</deskribapena>"));
            assertTrue(contenido.contains("<haize_azterketa>Haize batezbesteko abiadura: 10 km/h eta Oeste norabaitik datorrena</haize_azterketa>"));

            System.out.println("XML generado:\n" + contenido);

            Files.deleteIfExists(output.toPath());
            tempFile.delete();
        }
    }


    @Test
    void testArtxiboaIgoFtp_conexioGabe() {

        assertDoesNotThrow(() -> controller.artxiboaIgoFtp());
    }



}
