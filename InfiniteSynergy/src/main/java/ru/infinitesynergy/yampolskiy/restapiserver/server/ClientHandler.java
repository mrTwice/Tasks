package ru.infinitesynergy.yampolskiy.restapiserver.server;

import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.RequestIsNullException;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

import static ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpParser.parseRawHttp;

public class ClientHandler extends Thread {
    private LinkedList<ClientHandler> handlers;
    private Socket clientSocket;
    private Dispatcher dispatcher;
    private BufferedReader in; // поток чтения из сокета
    private PrintWriter out; // поток записи в сокет


    public ClientHandler(LinkedList<ClientHandler> handlers,Socket clientSocket, Dispatcher dispatcher) throws IOException {
        this.handlers = handlers;
        this.clientSocket = clientSocket;
        this.dispatcher = dispatcher;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream());
        start();
    }

    public void removeHandler() {
        handlers.remove(this);;
    }

    @Override
    public void run() {
        try {
            StringBuilder stringBuffer = new StringBuilder();
            String rawRequest;
            while ((rawRequest = in.readLine()) != null) {
                stringBuffer.append(rawRequest).append("\r\n");
            }
            if(stringBuffer.isEmpty()) {
                throw new RequestIsNullException("Пустой запрос на входе");
            }
            HttpRequest httpRequest = parseRawHttp(stringBuffer.toString());
            HttpResponse httpResponse = dispatcher.createHttpResponse(httpRequest);
            out.write(httpResponse.toString());
            out.flush();

        } catch (RequestIsNullException | IOException e) {
            removeHandler();
            this.interrupt();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
