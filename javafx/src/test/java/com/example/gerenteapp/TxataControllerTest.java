package com.example.gerenteapp;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class TxataControllerTest {

    private TxataController controller;

    @BeforeAll
    static void initJFX() {
        // Esta línea inicializa JavaFX correctamente incluso si no hay Stage
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new TxataController();
        controller.scrollPane = new ScrollPane();
        controller.chatBox = new VBox();
        controller.messageField = new TextField();
    }

    @Test
    void setIzena() {
        controller.setIzena("Ander");
        assertEquals("Ander", getPrivateField("Izena"));
    }

    @Test
    void setLangilea() {
        Langilea mockLangilea = Mockito.mock(Langilea.class);
        Mockito.when(mockLangilea.getIzena()).thenReturn("Ane");

        controller.setLangilea(mockLangilea);
        assertEquals("Ane", getPrivateField("Izena"));
    }

    @Test
    void displayMessage() throws Exception {
        controller.setIzena("User1");

        String message = "User1> Hello world!";
        runAndWait(() -> controller.displayMessage(message));

        // Esperar un poco para asegurarse de que la UI se ha actualizado
        Thread.sleep(500); // Ajusta este tiempo si es necesario

        assertFalse(controller.chatBox.getChildren().isEmpty());
    }


    @Test
    void displayFile() throws Exception {
        controller.setIzena("User1");

        String message = "User1> 📂: prueba.txt";
        String fileDataString = "[72, 101, 108, 108, 111]"; // "Hello"

        runAndWait(() -> controller.displayFile(message, fileDataString));

        // Espera un poco para asegurarse de que la UI se haya actualizado
        Thread.sleep(500); // Ajusta este tiempo si es necesario

        assertFalse(controller.chatBox.getChildren().isEmpty());
    }


    private void runAndWait(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });

        latch.await();
    }

    private String getPrivateField(String fieldName) {
        try {
            var field = TxataController.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (String) field.get(controller);
        } catch (NoSuchFieldException e) {
            fail("El campo " + fieldName + " no existe: " + e.getMessage());
        } catch (IllegalAccessException e) {
            fail("No se puede acceder al campo " + fieldName + ": " + e.getMessage());
        }
        return null;
    }
}
