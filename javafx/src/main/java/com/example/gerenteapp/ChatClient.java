package com.example.gerenteapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private TxataController txataController;


        public ChatClient(TxataController txataController) {
                this.txataController = txataController;
        }


        public void connect() {
                new Thread(() -> {
                        try {
                                socket = new Socket("192.168.115.155", 5555);
                                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                writer = new PrintWriter(socket.getOutputStream(), true);

                                System.out.println("✅ Conectado al servidor de chat.");
                                listenForMessages();
                        } catch (IOException e) {
                                System.err.println("❌ Error al conectar con el servidor: " + e.getMessage());
                                e.printStackTrace();

                                // Asegurar que los recursos no queden en estado inconsistente
                                socket = null;
                                reader = null;
                                writer = null;
                        }
                }).start();
        }


        public void sendMessage(String message) {
                if (writer == null) {
                        System.err.println("⚠️ No hay conexión al servidor. No se puede enviar el mensaje.");
                        return;
                }

                writer.println(message);
        }

        private void listenForMessages() {
                try {
                        String message;
                        while ((message = reader.readLine()) != null) {
                                txataController.displayMessage(message, false);
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public void disconnect() {
                try {
                        if (reader != null) reader.close();
                        if (writer != null) writer.close();
                        if (socket != null) socket.close();
                        System.out.println("🔌 Desconectado del servidor.");
                } catch (IOException e) {
                        System.err.println("❌ Error al cerrar la conexión: " + e.getMessage());
                        e.printStackTrace();
                }
        }

}
