package ru.infinitesynergy.yampolskiy.restapiserver.server;

import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.service.BankAccountService;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpParser.parseRawHttp;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private UserService userService;
    private BankAccountService bankAccountService;


    public ClientHandler(Socket clientSocket, UserService userService, BankAccountService bankAccountService) {
        this.clientSocket = clientSocket;
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String rawRequest = in.readLine();
            HttpRequest httpRequest = parseRawHttp(rawRequest);
            if (rawRequest != null) {
                // TODO Реализовать функционал генерации HttpResponse<String> httpResponse = handleHttpRequest(httpRequest);
                //TODO Реализовать функционал отправки ответа sendHttpResponse(out, httpResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
