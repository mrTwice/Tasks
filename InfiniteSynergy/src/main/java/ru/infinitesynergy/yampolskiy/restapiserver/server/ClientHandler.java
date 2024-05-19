package ru.infinitesynergy.yampolskiy.restapiserver.server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.RequestIsNullException;
import ru.infinitesynergy.yampolskiy.restapiserver.handlers.RouteExceptionHandler;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpParser.parseRawHttp;

public class ClientHandler extends Thread implements Handler{
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);
    private final List<Handler> handlers;
    private final Socket clientSocket;
    private final Controller requestController;


    public ClientHandler(List<Handler> handlers, Socket clientSocket, Controller requestController) throws Exception {
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

        } catch (Exception e) {
            System.err.printf("Ошибка в потоке: %s.\nСообщение: %s\n",Thread.currentThread().getName(), e.getMessage());
            logger.error("Ошибка в потоке: {} \n", Thread.currentThread().getName());
            logger.throwing( Level.WARN,e);
            removeHandler();
            this.interrupt();
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                logger.throwing( Level.WARN,e);
            }
        }
    }

}
