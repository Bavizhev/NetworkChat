package org.example;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    public static String HOST;
    public static int PORT;

    public String username;

    // Загружаем настройки и запускаем клиент
    public static void main(String[] args) {
        loadSettings();
        Client client = new Client();
        client.start();
    }

    // Метод для загрузки настроек сервера
    static void loadSettings() {
        try {
            Properties props = new Properties();
            InputStream ins = new FileInputStream("settings.txt");
            props.load(ins);
            ins.close();

            PORT = Integer.parseInt(props.getProperty("port"));
            HOST = props.getProperty("address");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Запуск клиента
    public void start() {
        try (Socket socket = new Socket(HOST, PORT); // Сокет, который устанавливает соединение с сервером
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // Буферный писатель для отправки сообщений на сервер
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Буферный считыватель для получения сообщений от сервера
             BufferedWriter logWriter = new BufferedWriter(new FileWriter("file.log", true))) { // Буферный писатель для записи сообщений в файл "file.log"

            System.out.print("Введите ваше имя: ");
            Scanner scanner = new Scanner(System.in);
            username = scanner.nextLine(); // Пользователю предлагается ввести свое имя (username)
            writer.write(username); // Имя отправляется на сервер
            writer.newLine();
            writer.flush();

            // Читаем от других клиентов сообщения
            Thread incomingThread = new Thread(() -> {
                try {
                    String message;
                    // Выводим и логируем считанные сообщения
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);

                        logWriter.write(message);
                        logWriter.newLine();
                        logWriter.flush();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            incomingThread.start();

            // Отправляем на сервер сообщения или команду.
            String message;
            while (true) {
                if (!(message = scanner.nextLine()).equals("/exit")) {
                    writer.write(message);
                    writer.newLine();
                    writer.flush();
                } else {
                    writer.write(message);
                    writer.newLine();
                    writer.flush();
                    break;
                }
            }

            incomingThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}