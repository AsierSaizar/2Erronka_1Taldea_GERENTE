package com.example.gerenteapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ChatClient {
        private Socket socket;
        private BufferedReader reader;
        private static PrintWriter writer;
        private TxataController txataController;


        public ChatClient(TxataController txataController) {
                this.txataController = txataController;
        }


        public void connect() {
                new Thread(() -> {
                        try {
                                //socket = new Socket("192.168.115.188", 9090);
                                socket = new Socket("localhost", 9090);
                                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                writer = new PrintWriter(socket.getOutputStream(), true);
                                listenForMessages();
                        } catch (IOException e) {
                                e.printStackTrace();

                        }
                }).start();
        }

        public void sendMessage(String message) throws Exception {
                String user = message.split(">")[0];
                String encryptedMessage = CryptoUtil.encrypt(message.split(">")[1]);
                message = user + "> " + encryptedMessage;
                writer.println(message);
        }

        public static void sendFile(byte[] fileContent, String fileName, String izena) throws Exception {
                String message = izena + "> " + fileName + "> " + Arrays.toString(fileContent);
                writer.println(message);
        }

        private void listenForMessages() {
                try {
                        String message;
                        while ((message = reader.readLine()) != null) {
                                // Validar que el mensaje no sea nulo y contenga el carácter '>'
                                if (message == null || !message.contains(">")) {
                                        System.err.println("Mensaje mal formado: " + message);
                                        continue; // Saltar este mensaje y continuar con el siguiente
                                }
                                if (message.split(">", -1).length == 3) {



                                        int firstDelimiter = message.indexOf(">");
                                        String user = message.substring(0, firstDelimiter).trim();


                                        int secondDelimiter = message.indexOf(">", firstDelimiter + 1);
                                        String artxiboIzena = message.substring(firstDelimiter + 1, secondDelimiter).trim();


                                        String fileDataString = message.substring(secondDelimiter + 1).trim();

                                        message =  user+ "> 📂: " + artxiboIzena;

                                        // Mostrar el mensaje en la interfaz
                                        txataController.displayFile(message, fileDataString);


                                } else {



                                        // Dividir el mensaje en usuario y contenido cifrado
                                        String[] parts = message.split(">", 2); // Dividir en máximo 2 partes
                                        if (parts.length < 2) {
                                                System.err.println("Formato incorrecto del mensaje: " + message);
                                                continue; // Saltar este mensaje y continuar con el siguiente
                                        }

                                        String user = parts[0].trim(); // Obtener el usuario
                                        String encryptedContent = parts[1].trim(); // Obtener el contenido cifrado

                                        // Desencriptar el mensaje
                                        String decryptedMessage;
                                        try {
                                                decryptedMessage = CryptoUtil.decrypt(encryptedContent);
                                        } catch (Exception e) {
                                                System.err.println("Error al desencriptar el mensaje: " + encryptedContent);
                                                e.printStackTrace();
                                                continue; // Saltar este mensaje y continuar con el siguiente
                                        }

                                        // Construir el mensaje final
                                        message = user + "> " + decryptedMessage;

                                        // Mostrar el mensaje en la interfaz
                                        txataController.displayMessage(message);




                                }

                        }
                } catch (IOException e) {
                        e.printStackTrace();
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        public void disconnect() {
                try {
                        reader.close();
                        writer.close();
                        socket.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}