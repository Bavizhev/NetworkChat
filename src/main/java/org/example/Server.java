package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Server {
    static int PORT;
    private static final String LOG_FILE = "file.log";

    static List<ServerThread> clients;

    public static void main(String[] args) {
        loadSettings();
        Server server = new Server();
        server.start();
    }

    public Server() {
        clients = new ArrayList<>();
    }

    // Загружаем настройки сервера из файла settings.txt
    static void loadSettings() {
        try {
            Properties props = new Properties();
            InputStream ins = new FileInputStream("settings.txt");
            props.load(ins);
            ins.close();

            PORT = Integer.parseInt(props.getProperty("port"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Запускаем сервер и ждем подключения
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();

                // Создаем отдельный поток для нового клиента и запускаем
                ServerThread thread = new ServerThread(socket);
                clients.add(thread);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class ServerThread extends Thread {
        private Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
        BufferedWriter writeClient;
        String logMessage;
        private String username;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                writeClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // Буферный писатель для отправки сообщений клиентам
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Буферный считыватель для получения сообщений от клиентов
                writer = new BufferedWriter(new FileWriter(LOG_FILE, true)); // Буферный писатель для записи сообщений в файл "file.log"

                username = reader.readLine(); // Cчитывается имя
                System.out.println(username + " подключился");

                // Выводим в консоль сообщения до тех пор, пока клиент не напишет команду /exit
                String message;
                while (true) {
                    message = reader.readLine();
                    if (!message.equals("/exit")) {
                        logMessage = "[" + LocalDateTime.now() + "] " + username + ": " + message;
                        System.out.println(logMessage);

                        // Логируем каждое сообщение в файл file.log
                        writer.write(logMessage);
                        writer.newLine();
                        writer.flush();

                        sendToAllClients(logMessage);
                    } else {
                        break;
                    }
                }
                Server.clients.remove(this);
                reader.close();
                writer.close();
                socket.close();

                System.out.println(username + " отключился");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Метод для отправки всем остальным подключенным клиентам.
        void sendToAllClients(String message) {
            if (!message.equals("/exit")) {
                for (ServerThread client : clients) {
                    try {
                        client.writeClient.write(message);
                        client.writeClient.newLine();
                        client.writeClient.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}