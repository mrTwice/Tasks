package ru.infinitesynergy.yampolskiy.restapiserver.server;

import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import static ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpParser.parseRawHttp;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private Dispatcher dispatcher;


    public ClientHandler(Socket clientSocket, Dispatcher dispatcher) {
        this.clientSocket = clientSocket;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
        ) {
            String rawRequest = in.readLine();
            System.out.println(rawRequest);
            if (rawRequest != null) {
                HttpRequest httpRequest = parseRawHttp(rawRequest);
                HttpResponse httpResponse = dispatcher.createHttpResponse(httpRequest);
                out.write(httpResponse.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
