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
                                //socket = new Socket("localhost", 5555);
                                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                writer = new PrintWriter(socket.getOutputStream(), true);
                                listenForMessages();
                        } catch (IOException e) {
                                e.printStackTrace();

                        }
                }).start();
        }

        public void sendMessage(String message) {

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
                        reader.close();
                        writer.close();
                        socket.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}
