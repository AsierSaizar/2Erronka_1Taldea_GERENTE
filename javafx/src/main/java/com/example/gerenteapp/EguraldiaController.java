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
                                espEus.put("Cubierto con lluvia", "Euriz beteta");
                                espEus.put("Despejado", "Argi");
                                espEus.put("Nuboso", "Hodeitsua");
                                espEus.put("Nuboso con tormenta", "Euritsua ekaitzarekin");
                                espEus.put("Muy nuboso con lluvia", "Oso hodeitsua euriarekin");
                                espEus.put("Muy nuboso con tormenta", "Oso euritsua ekaitzarekin");
                                espEus.put("Poco nuboso", "Hodei gutxiekin");
                                espEus.put("Muy nuboso", "Oso hodeitsua");
                                espEus.put("Nuboso con lluvia", "Hodeitsua euriarekin");
                                espEus.put("Cubierto", "Hodeituta");
                                espEus.put("Intervalos nubosos con lluvia", "Euria gutxi duten hodeiak noizbehinka");

                                NodeList estadoNodes = (NodeList) xpath.evaluate("estado_cielo", dia, XPathConstants.NODESET);
                                double sumaEgoera = 0.0;
                                int kontEgoera = 0;
                                Map<String, Integer> descFreq = new HashMap<>();

                                for (int j = 0; j < estadoNodes.getLength(); j++) {
                                        Element estado = (Element) estadoNodes.item(j);
                                        String testua = estado.getTextContent().trim();
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
                                                deskribapenEsp = entry.getKey().trim().toLowerCase(); // Para usar en el mapa
                                        }
                                }

                                // Convertir a formato con mayúscula inicial
                                if (!deskribapenEsp.equals("Ez dago")) {
                                        deskribapenEsp = deskribapenEsp.substring(0, 1).toUpperCase() + deskribapenEsp.substring(1);
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
                                                haizeDeskribapena += " eta " + norabPredom + " norabaitik datorrena";
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
                        String ftpServer = "192.168.115.188"; // Cambia esto si es necesario
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
