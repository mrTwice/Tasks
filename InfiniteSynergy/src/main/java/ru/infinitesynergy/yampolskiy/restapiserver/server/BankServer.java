package ru.infinitesynergy.yampolskiy.restapiserver.server;

import ru.infinitesynergy.yampolskiy.restapiserver.service.BankAccountService;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer {
    private static final int PORT = 8080;
    private final UserService userService;
    private final BankAccountService bankAccountService;

    public BankServer(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port 8080...");
            while (true) {
                try(Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    // Обработка запроса в новом потоке
                    new Thread(new ClientHandler(clientSocket, userService, bankAccountService)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
