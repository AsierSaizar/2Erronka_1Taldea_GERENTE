package com.example.gerenteapp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
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
                try {
                        // Crear el DocumentBuilder para parsear el XML
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                        // Obtener el XML desde la URL (o archivo)
                        String url = "https://www.aemet.es/xml/municipios/localidad_20076.xml";
                        ArrayList<Object> files = getFiles(true, url);

                        // Verificar si se ha obtenido correctamente el archivo XML
                        if (files == null || files.isEmpty()) {
                                Alertak.mostrarMensajeAlerta("Ezin izan da XML fitxategia deskargatu.");
                                return;
                        }

                        InputStream iXmlFile = (InputStream) files.get(0);

                        // Verificar si el InputStream es nulo o vacío
                        if (iXmlFile == null) {
                                Alertak.mostrarMensajeAlerta("Ezin izan da XML fitxategia irakurri: Stream hutsik dago.");
                                return;
                        }

                        // Parsear el XML
                        Document document;
                        try {
                                document = dBuilder.parse(iXmlFile);
                        } catch (Exception e) {
                                Alertak.mostrarMensajeAlerta("Errorea XML fitxategia analizatzean: " + e.getMessage());
                                return;
                        }

                        // Normalizar el documento XML
                        document.getDocumentElement().normalize();

                        // Verificar si el documento XML tiene contenido
                        if (document.getDocumentElement() == null) {
                                Alertak.mostrarMensajeAlerta("XML fitxategiak ez du edukirik edo ez dago elementu baliogarrik.");
                                return;
                        }

                        // Crear el objeto XPath
                        XPath xpath = XPathFactory.newInstance().newXPath();

                        // Seleccionar todos los nodos <dia> de la predicción
                        NodeList diaNodes;
                        try {
                                diaNodes = (NodeList) xpath.evaluate("/root/prediccion/dia", document, XPathConstants.NODESET);
                        } catch (Exception e) {
                                Alertak.mostrarMensajeAlerta("Errorea XPath aplikatzean: " + e.getMessage());
                                return;
                        }

                        // Verificar si se han encontrado nodos <dia>
                        if (diaNodes == null || diaNodes.getLength() == 0) {
                                Alertak.mostrarMensajeAlerta("Ez daude <dia> nodoak aurkitu XML fitxategian.");
                                return;
                        }

                        // Si llegamos aquí, todo está funcionando correctamente hasta este punto
                        System.out.println("XML fitxategia eta XPath arazorik gabe kargatu dira.");

                        // Resto del código (creación del nuevo XML, procesamiento de nodos, etc.)
                        Document newDoc = dBuilder.newDocument();
                        Element root = newDoc.createElement("prediccion");
                        newDoc.appendChild(root);

                        for (int i = 0; i < diaNodes.getLength(); i++) {
                                Element dia = (Element) diaNodes.item(i);
                                Element eguraldia = newDoc.createElement("eguraldia");
                                eguraldia.setAttribute("data", dia.getAttribute("fecha"));

                                // --- Procesamiento de temperatura, precipitación, estado del cielo, etc. ---
                                // (Este código permanece igual que en el original)

                                root.appendChild(eguraldia);
                        }

                        // Guardar el nuevo XML en un archivo
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                        DOMSource source = new DOMSource(newDoc);
                        File outputFile = new File("./eguraldia.xml");
                        StreamResult result = new StreamResult(outputFile);
                        transformer.transform(source, result);

                        System.out.println("Filtratutako XML fitxategia hona gorde da: " + outputFile.getAbsolutePath());

                        // Ejecutar el archivo .bat
                        artxiboaIgoFtp();
                } catch (Exception e) {
                        Alertak.mostrarMensajeAlerta("Errorea prozesu orokorrean: " + e.getMessage());
                }
        }




        public static ArrayList<Object> getFiles(boolean aukeratu, String url) throws KeyManagementException, NoSuchAlgorithmException, MalformedURLException, IOException {
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

        public static void artxiboaIgoFtp() {
                FTPClient client = new FTPClient();
                FileInputStream fis = null;

                try {
                        // Intentar conectar al servidor FTP
                        String ftpServer = "127.0.0.1"; // Cambia esto si es necesario
                        System.out.println("Conectando al servidor FTP: " + ftpServer);
                        client.connect(ftpServer);

                        // Verificar si la conexión fue exitosa
                        int replyCode = client.getReplyCode();
                        if (!FTPReply.isPositiveCompletion(replyCode)) {
                                Alertak.mostrarMensajeAlerta("Ezin izan da FTP zerbitzariarekin konektatu: Erantzun kodea " + replyCode);
                                return;
                        }

                        // Intentar iniciar sesión en el servidor FTP
                        boolean loginSuccess = client.login("Gerente", "Gerente");
                        if (!loginSuccess) {
                                Alertak.mostrarMensajeAlerta("Ezin izan da saioa hasteko FTP zerbitzarian.");
                                return;
                        }

                        // Ruta del archivo XML a subir
                        String filename = "./eguraldia.xml";

                        // Verificar si el archivo existe antes de intentar abrirlo
                        File file = new File(filename);
                        if (!file.exists() || !file.isFile()) {
                                Alertak.mostrarMensajeAlerta("Ez da aurkitu igotzeko fitxategia: " + filename);
                                return;
                        }

                        // Abrir el archivo como InputStream
                        fis = new FileInputStream(file);

                        // Intentar subir el archivo al servidor FTP
                        boolean uploadSuccess = client.storeFile(filename, fis);
                        if (!uploadSuccess) {
                                Alertak.mostrarMensajeAlerta("Errorea gertatu da fitxategia igotzean FTP zerbitzarian.");
                                return;
                        }

                        // Si llegamos aquí, el archivo se ha subido correctamente
                        System.out.println("Fitxategia ondo igota dago FTP zerbitzarian: " + filename);
                        Alertak.mostrarMensajeFeddBack("Fitxategia ondo igota dago FTP zerbitzarian: " + filename);

                } catch (IOException e) {
                        // Capturar errores de E/S y mostrar un mensaje de alerta
                        Alertak.mostrarMensajeAlerta("Errorea FTP prozesuan: " + e.getMessage());
                } finally {
                        // Cerrar recursos (InputStream y desconectar del servidor FTP)
                        try {
                                if (fis != null) {
                                        fis.close();
                                }
                                if (client.isConnected()) {
                                        client.logout();
                                        client.disconnect();
                                }
                        } catch (IOException e) {
                                Alertak.mostrarMensajeAlerta("Errorea baliabideak itxierakoan: " + e.getMessage());
                        }
                }
        }


}
