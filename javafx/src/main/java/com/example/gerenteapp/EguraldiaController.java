package com.example.gerenteapp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EguraldiaController {

        public void xmlPush() throws Exception {
                // Crear el DocumentBuilder para parsear el XML
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                // Obtener el XML desde la URL (o archivo)
                String url = "https://www.aemet.es/xml/municipios/localidad_20076.xml";
                ArrayList<Object> files = getFiles(true, url);
                InputStream iXmlFile = (InputStream) files.get(0);
                Document document = dBuilder.parse(iXmlFile);

                // Crear el objeto XPath
                XPath xpath = XPathFactory.newInstance().newXPath();

                // Seleccionar todos los nodos <dia> de la predicción
                NodeList diaNodes = (NodeList) xpath.evaluate("/root/prediccion/dia", document, XPathConstants.NODESET);

                // Crear un nuevo documento XML
                Document newDoc = dBuilder.newDocument();
                Element root = newDoc.createElement("prediccion");
                newDoc.appendChild(root);

                
                // Iterar sobre los nodos <dia> del XML original
                for (int i = 0; i < diaNodes.getLength(); i++) {
                        Element dia = (Element) diaNodes.item(i);

                        // Crear nodo para el día (se usa "eguraldia" y el atributo "data" en euskera)
                        Element eguraldia = newDoc.createElement("eguraldia");
                        eguraldia.setAttribute("data", dia.getAttribute("fecha"));

                        // --- Tenperatura ---
                        String tempMax = xpath.evaluate("temperatura/maxima", dia).trim();
                        String tempMin = xpath.evaluate("temperatura/minima", dia).trim();

                        Element tenperaturaGorakoa = newDoc.createElement("tenperatura_gorakoa");
                        tenperaturaGorakoa.setTextContent(tempMax);
                        eguraldia.appendChild(tenperaturaGorakoa);

                        Element tenperaturaBehekoa = newDoc.createElement("tenperatura_behekoa");
                        tenperaturaBehekoa.setTextContent(tempMin);
                        eguraldia.appendChild(tenperaturaBehekoa);

                        // --- Euri (Precipitazioa) ---
                        NodeList probNodes = (NodeList) xpath.evaluate("prob_precipitacion", dia, XPathConstants.NODESET);
                        double sumaEuri = 0.0;
                        int kontEuri = 0;
                        for (int j = 0; j < probNodes.getLength(); j++) {
                                Element prob = (Element) probNodes.item(j);
                                String probValue = prob.getTextContent().trim();
                                if (!probValue.isEmpty()) {
                                        try {
                                                double balio = Double.parseDouble(probValue);
                                                sumaEuri += balio;
                                                kontEuri++;
                                        } catch (NumberFormatException e) {
                                                // Balio ez denean, salto egin
                                        }
                                }
                        }
                        Element euri = newDoc.createElement("euri");
                        Element euriBatezbestekoa = newDoc.createElement("euri_batezbestekoa");
                        if (kontEuri > 0) {
                                int mediaEuri = (int) Math.round(sumaEuri / kontEuri);
                                euriBatezbestekoa.setTextContent(String.valueOf(mediaEuri));
                        } else {
                                euriBatezbestekoa.setTextContent("Ez dago");
                        }
                        euri.appendChild(euriBatezbestekoa);
                        eguraldia.appendChild(euri);

                        // --- Zeru egoera ---
                        Map<String, String> espEus = new HashMap<>();
                        espEus.put("Cubierto con lluvia", "Euria estalituta");
                        espEus.put("Despejado", "Argi");
                        espEus.put("Poco nuboso", "Gutxi hodeitsu");
                        espEus.put("Muy nuboso", "Oso hodeitsu");
                        espEus.put("Cubierto", "Hodeituta");
                        espEus.put("Intervalos nubosos con lluvia escasa", "Euria gutxi duten hodeitsu tarteak");

                        NodeList estadoNodes = (NodeList) xpath.evaluate("estado_cielo", dia, XPathConstants.NODESET);
                        double sumaEgoera = 0.0;
                        int kontEgoera = 0;
                        // Kontatu deskribapenak (espainol) eta egin erregistro
                        Map<String, Integer> descFreq = new HashMap<>();
                        for (int j = 0; j < estadoNodes.getLength(); j++) {
                                Element estado = (Element) estadoNodes.item(j);
                                String testua = estado.getTextContent().trim();
                                // Atera balio numerikoa, adibidez "15n" → "15"
                                String balioNumerikoa = testua.replaceAll("[^\\d.]", "");
                                if (!balioNumerikoa.isEmpty()) {
                                        try {
                                                double balio = Double.parseDouble(balioNumerikoa);
                                                sumaEgoera += balio;
                                                kontEgoera++;
                                        } catch (NumberFormatException e) {
                                                // Salto egin, ez baliozko zenbakia bada
                                        }
                                }
                                String deskr = estado.getAttribute("descripcion").trim();
                                if (!deskr.isEmpty()) {
                                        descFreq.put(deskr, descFreq.getOrDefault(deskr, 0) + 1);
                                }
                        }
                        Element zeruEgoera = newDoc.createElement("zeru_egoera");
                        Element egoeraBalioBatezbestekoa = newDoc.createElement("balio_batezbestekoa");
                        if (kontEgoera > 0) {
                                int mediaEgoera = (int) Math.round(sumaEgoera / kontEgoera);
                                egoeraBalioBatezbestekoa.setTextContent(String.valueOf(mediaEgoera));
                        } else {
                                egoeraBalioBatezbestekoa.setTextContent("Ez dago");
                        }
                        zeruEgoera.appendChild(egoeraBalioBatezbestekoa);

                        // Deskribapen errepresentatiboa: gehien agertzen dena
                        String deskribapenEsp = "Ez dago";
                        int maxFreq = 0;
                        for (Map.Entry<String, Integer> entry : descFreq.entrySet()) {
                                if (entry.getValue() > maxFreq) {
                                        maxFreq = entry.getValue();
                                        deskribapenEsp = entry.getKey();
                                }
                        }
                        // Itzuli deskribapena euskerara
                        String deskribapenEus = espEus.getOrDefault(deskribapenEsp, deskribapenEsp);
                        Element deskribapena = newDoc.createElement("deskribapena");
                        deskribapena.setTextContent(deskribapenEus);
                        zeruEgoera.appendChild(deskribapena);

                        eguraldia.appendChild(zeruEgoera);

                        // --- Haize azterketa ---
                        NodeList haizeNodes = (NodeList) xpath.evaluate("viento", dia, XPathConstants.NODESET);
                        double sumaHaize = 0.0;
                        int kontHaize = 0;
                        Map<String, Integer> norabaitikFreq = new HashMap<>();
                        for (int j = 0; j < haizeNodes.getLength(); j++) {
                                Element haizeElem = (Element) haizeNodes.item(j);
                                String abiaduraStr = xpath.evaluate("velocidad", haizeElem).trim();
                                String norab = xpath.evaluate("direccion", haizeElem).trim();
                                if (!abiaduraStr.isEmpty()) {
                                        try {
                                                double abiadura = Double.parseDouble(abiaduraStr);
                                                sumaHaize += abiadura;
                                                kontHaize++;
                                        } catch (NumberFormatException e) {
                                                // Salto egin
                                        }
                                }
                                if (!norab.isEmpty()) {
                                        norabaitikFreq.put(norab, norabaitikFreq.getOrDefault(norab, 0) + 1);
                                }
                        }
                        String haizeDeskribapena;
                        if (kontHaize > 0) {
                                int mediaHaize = (int) Math.round(sumaHaize / kontHaize);
                                String norabPredom = "Ez dago";
                                int maxNor = 0;
                                for (Map.Entry<String, Integer> entry : norabaitikFreq.entrySet()) {
                                        if (entry.getValue() > maxNor) {
                                                maxNor = entry.getValue();
                                                norabPredom = entry.getKey();
                                        }
                                }
                                haizeDeskribapena = "Haize batezbesteko abiadura: " + mediaHaize + " km/h";
                                if (!norabPredom.equals("Ez dago")) {
                                        haizeDeskribapena += " eta " + norabPredom + " norabaitik dator";
                                }
                        } else {
                                haizeDeskribapena = "Ez dago haize daturik";
                        }
                        Element haizeAzterketa = newDoc.createElement("haize_azterketa");
                        haizeAzterketa.setTextContent(haizeDeskribapena);
                        eguraldia.appendChild(haizeAzterketa);

                        // Gorde eguraldiaren nodoa dokumentu errorean
                        root.appendChild(eguraldia);
                }






                // Guardar el nuevo XML en un archivo
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Para formato legible
                DOMSource source = new DOMSource(newDoc);
                File outputFile = new File("../2Erronka_1Taldea_EGURALDIA_XML/eguraldia.xml");
                StreamResult result = new StreamResult(outputFile);
                transformer.transform(source, result);

                System.out.println("El XML filtrado se ha guardado en: " + outputFile.getAbsolutePath());

                // Ejecutar el archivo .bat
                batExecute();
        }






        public static ArrayList<Object> getFiles(boolean aukeratu, String url)
                throws KeyManagementException, NoSuchAlgorithmException, MalformedURLException, IOException {
                File fXmlFile = null;
                InputStream iXmlFile = null;

                if (url != null) {
                        aukeratu = false;
                        MyUrlConnection.disableSSLCertificateValidation();
                        iXmlFile = MyUrlConnection.getFileFromURL(url);
                }

                if (aukeratu) {
                        fXmlFile = FileChoser.chooseWindow();
                } else {
                        fXmlFile = FileChoser.getFileFromRoute();
                }

                ArrayList<Object> a = new ArrayList<>();
                a.add(iXmlFile);
                a.add(fXmlFile);

                return a;
        }

        public static void batExecute() {
                String batFilePath = "eguraldiaPush.bat";


                // Crea un ProcessBuilder que ejecuta cmd.exe con el parámetro /c para ejecutar el .bat
                ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", batFilePath);

                // Opcional: redirigir la salida de error al flujo de salida para poder leer todo
                processBuilder.redirectErrorStream(true);

                try {
                        // Inicia el proceso
                        Process process = processBuilder.start();

                        // Lee la salida del proceso
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                                System.out.println(line);
                        }

                        // Espera a que el proceso termine
                        int exitCode = process.waitFor();
                        System.out.println("El proceso terminó con el código: " + exitCode);

                } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                }
        }
}
