package org.example;

import org.junit.jupiter.api.Test;;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {


    @Test
    public void testLoadSettings() {
        // Create a temporary settings file for testing
        try (PrintWriter writer = new PrintWriter("settings.txt")) {
            writer.println("address=127.0.0.1");
            writer.println("port=8080");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Call the loadSettings method
        Client.loadSettings();

        // Verify that the PORT and HOST variables are set correctly
        assertEquals(8080, Client.PORT);
        assertEquals("127.0.0.1", Client.HOST);
    }

}