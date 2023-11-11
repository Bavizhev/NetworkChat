package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client2 {
    private static String HOST;
    private static int PORT;

    private String username;

    public static void main(String[] args) {
        loadSettings();
        Client2 client2 = new Client2();
        client2.start();
    }

    private static void loadSettings() {
        try {
            Properties props = new Properties();
            InputStream ins = new FileInputStream("settings.txt");
            props.load(ins);
            ins.close();

            PORT = Integer.parseInt(props.getProperty("port"));
            HOST = props.getProperty("adress");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter logWriter = new BufferedWriter(new FileWriter("file.log", true))) {

            System.out.print("Введите ваше имя: ");
            Scanner scanner = new Scanner(System.in);
            username = scanner.nextLine();
            writer.write(username);
            writer.newLine();
            writer.flush();

            Thread incomingThread = new Thread(() -> {
                try {
                    String message;
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
