package ru.infinitesynergy.yampolskiy.restapiserver.server;

import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;

import java.io.*;
import java.net.Socket;

import static ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpParser.parseRawHttp;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Dispatcher dispatcher;
    private BufferedReader in; // поток чтения из сокета
    private PrintWriter out; // поток записи в сокет


    public ClientHandler(Socket clientSocket, Dispatcher dispatcher) throws IOException {
        this.clientSocket = clientSocket;
        this.dispatcher = dispatcher;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream());
        start(); // вызываем run()
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
        try {
            StringBuilder stringBuffer = new StringBuilder();
            String rawRequest;// = in.readLine();
            // while ((rawRequest = in.readLine()) != null && !rawRequest.equals("")) {
            while ((rawRequest = in.readLine()) != null) {
                stringBuffer.append(rawRequest);
                stringBuffer.append("\r\n");
            }
            HttpRequest httpRequest = parseRawHttp(stringBuffer.toString());
            HttpResponse httpResponse = dispatcher.createHttpResponse(httpRequest);
            out.write(httpResponse.toString());
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
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
