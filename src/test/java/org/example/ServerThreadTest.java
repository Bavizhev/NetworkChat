package org.example;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerThreadTest {


    @Test
    public void testRun() {


        // Создайте мок сокета для тестирования.
        try (Socket mockSocket = new Socket()) {
            // Создаем экземпляр ServerThread.
            Server.ServerThread serverThread = new Server.ServerThread(mockSocket);

            // System.out для захвата вывода консоли
            ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(consoleOutput));

            // Запускаем метод
            serverThread.run();

            // Проверяем, что сообщение о подключении имени пользователя напечатано.
            assertEquals(" подключился", consoleOutput.toString());

            // Сбросить System.out
            System.setOut(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendToAllClients() {
        // Создание макетов экземпляров ServerThread
        Server.ServerThread client1 = new Server.ServerThread(new Socket());
        Server.ServerThread client2 = new Server.ServerThread(new Socket());

        // Добавление мок клиентов в список Server.clients.
        List<Server.ServerThread> clients = new ArrayList<>();
        clients.add(client1);
        clients.add(client2);
        Server.clients = clients;

        // Создание экземпляра ServerThread для тестирования.
        Server.ServerThread serverThread = new Server.ServerThread(new Socket());

        // Вызов метода sendToAllClients.
        serverThread.sendToAllClients("Test message");

        // Проверка, что сообщение отправлено всем клиентам.
        assertEquals("Test message\n", client1.writeClient.toString());
        assertEquals("Test message\n", client2.writeClient.toString());
    }
}
