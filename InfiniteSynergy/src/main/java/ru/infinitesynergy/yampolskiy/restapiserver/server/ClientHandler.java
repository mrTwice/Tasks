package ru.infinitesynergy.yampolskiy.restapiserver.server;

import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.RequestIsNullException;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpParser.parseRawHttp;

public class ClientHandler extends Thread {
    private List<ClientHandler> handlers;
    private Socket clientSocket;
    private RequestController requestController;


    public ClientHandler(List<ClientHandler> handlers, Socket clientSocket, RequestController requestController) {
        this.handlers = handlers;
        this.clientSocket = clientSocket;
        this.requestController = requestController;

        start();
    }

    public void removeHandler() {
        handlers.remove(this);
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {

            StringBuilder stringBuffer = new StringBuilder();
            String rawRequest;
            while ((rawRequest = in.readLine()) != null) {
                stringBuffer.append(rawRequest).append("\r\n");
                //TODO: решить проблему с зависанием на последнем символе
            }

            if (stringBuffer.isEmpty()) {
                throw new RequestIsNullException("Пустой запрос на входе");
            }

            HttpRequest httpRequest = parseRawHttp(stringBuffer.toString());
            HttpResponse httpResponse = requestController.createHttpResponse(httpRequest);
            out.write(httpResponse.toString());
            out.flush();

        } catch (RequestIsNullException e) {
            removeHandler();
            this.interrupt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
