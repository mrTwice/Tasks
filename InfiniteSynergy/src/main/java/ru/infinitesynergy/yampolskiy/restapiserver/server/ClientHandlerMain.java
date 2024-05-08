package ru.infinitesynergy.yampolskiy.restapiserver.server;

import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.RequestIsNullException;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpParser.parseRawHttp;

public class ClientHandlerMain extends Thread implements Handler{
    private List<Handler> handlers;
    private Socket clientSocket;
    private RequestController requestController;


    public ClientHandlerMain(List<Handler> handlers, Socket clientSocket, RequestController requestController) throws IOException {
        this.handlers = handlers;
        this.clientSocket = clientSocket;
        this.requestController = requestController;
        start();
    }

    public void removeHandler() {
        handlers.remove(this);;
    }

    @Override
    public void run() {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             PrintWriter out = new PrintWriter(outputStream, true)
        ) {

            StringBuilder requestBuilder = new StringBuilder();
            char[] buffer = new char[8192];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                requestBuilder.append(buffer, 0, bytesRead);
                if (bytesRead < buffer.length) {
                    break;
                }
            }
            if(requestBuilder.isEmpty()) {
                throw new RequestIsNullException("Пустой запрос на входе");
            }
            HttpRequest httpRequest = parseRawHttp(requestBuilder.toString());
            HttpResponse httpResponse = requestController.createHttpResponse(httpRequest);
            out.write(httpResponse.toString());
            out.flush();

        } catch (RequestIsNullException | IOException e) {
            removeHandler();
            this.interrupt();
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
