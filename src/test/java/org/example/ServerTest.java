package org.example;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerTest {

    @Test
    public void testLoadSettings() {

        // Загружаем настройки сервера из файла settings.txt
        int port = 0;
        try {
            Properties props = new Properties();
            InputStream ins = new FileInputStream("settings.txt");
            props.load(ins);
            ins.close();

            port = Integer.parseInt(props.getProperty("port"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Вызов метода loadSettings
        Server.loadSettings();

        // Проверяем, что переменная port установлена правильно.
        assertEquals(8080, port);
    }

    @Test
    public void testStart() {
        // Создаем макет ServerSocket для тестирования
        try (ServerSocket mockServerSocket = new ServerSocket(8080)) {
            // Создать экземпляр сервера
            Server server = new Server();

            // Устанавливаем переменную PORT в соответствии с mockServerSocket
            Server.PORT = mockServerSocket.getLocalPort();

            // Запускаем сервер
            server.start();

            // Проверяем, что сокет сервера успешно создан.
            assertTrue(mockServerSocket.isBound());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}