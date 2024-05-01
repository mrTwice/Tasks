package ru.infinitesynergy.yampolskiy.restapiserver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class BankServer {
    private static final int PORT = 8080;
    private static final LinkedList<ClientHandler> handlers = new LinkedList<>();
    private static final Dispatcher dispatcher = new Dispatcher();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту: " + PORT);
            while (true) {
                Socket socket = server.accept();
                System.out.printf("Адрес клиента: %s\n Порт клиента: %s\n ",socket.getInetAddress(), socket.getPort());
                ClientHandler handler = new ClientHandler(socket, dispatcher);
                handlers.add(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

