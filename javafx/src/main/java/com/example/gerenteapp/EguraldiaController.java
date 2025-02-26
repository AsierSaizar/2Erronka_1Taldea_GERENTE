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

                // Iterar sobre los nodos <dia>
                for (int i = 0; i < diaNodes.getLength(); i++) {
                        Element dia = (Element) diaNodes.item(i);

                        // Crear nodo <dia>
                        Element diaElement = newDoc.createElement("dia");
                        diaElement.setAttribute("fecha", dia.getAttribute("fecha"));

                        // Agregar temperatura máxima y mínima
                        String tempMax = xpath.evaluate("temperatura/maxima", dia).trim();
                        String tempMin = xpath.evaluate("temperatura/minima", dia).trim();

                        Element maxElement = newDoc.createElement("temperatura_maxima");
                        maxElement.setTextContent(tempMax);
                        diaElement.appendChild(maxElement);

                        Element minElement = newDoc.createElement("temperatura_minima");
                        minElement.setTextContent(tempMin);
                        diaElement.appendChild(minElement);

                        // Obtener las probabilidades de precipitación
                        NodeList probNodes = (NodeList) xpath.evaluate("prob_precipitacion", dia, XPathConstants.NODESET);
                        Element precipitacionElement = newDoc.createElement("precipitacion");

                        boolean hasData = false;
                        for (int j = 0; j < probNodes.getLength(); j++) {
                                Element prob = (Element) probNodes.item(j);
                                String periodo = prob.getAttribute("periodo").isEmpty() ? "No especificado" : prob.getAttribute("periodo");
                                String probValue = prob.getTextContent().trim();

                                if (!probValue.isEmpty()) {
                                        Element probElement = newDoc.createElement("probabilidad");
                                        probElement.setAttribute("hora", periodo);
                                        probElement.setTextContent(probValue);
                                        precipitacionElement.appendChild(probElement);
                                        hasData = true;
                                }
                        }

                        // Si no hay datos de precipitación, agregar un nodo vacío
                        if (!hasData) {
                                Element probElement = newDoc.createElement("probabilidad");
                                probElement.setAttribute("hora", "No especificado");
                                probElement.setTextContent("No disponible");
                                precipitacionElement.appendChild(probElement);
                        }

                        // Agregar precipitacion a diaElement
                        diaElement.appendChild(precipitacionElement);
                        root.appendChild(diaElement);
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
