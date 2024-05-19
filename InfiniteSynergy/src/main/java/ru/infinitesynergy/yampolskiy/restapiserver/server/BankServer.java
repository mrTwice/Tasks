package ru.infinitesynergy.yampolskiy.restapiserver.server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.infinitesynergy.yampolskiy.restapiserver.handlers.ControllerExceptionHandler;
import ru.infinitesynergy.yampolskiy.restapiserver.handlers.RouteLoggingHandler;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BankServer {
    private static final int PORT = 8080;
    private static final List<Handler> handlers = new CopyOnWriteArrayList<>();
    private static final Controller requestController = createControllerProxy(new RequestController());
    private static final Logger logger = LogManager.getLogger(BankServer.class);

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту: " + PORT);
            while (true) {
                Socket socket = server.accept();
                System.out.printf("Адрес клиента: %s\n Порт клиента: %s\n ",socket.getInetAddress(), socket.getPort());
                Handler handler = new ClientHandler(handlers,socket, requestController);
                handlers.add(handler);
            }
        } catch (Exception e) {
            logger.throwing( Level.WARN,e);
        }
    }

    private static Controller createControllerProxy(Controller controller) {
        return (Controller) Proxy.newProxyInstance(
                Controller.class.getClassLoader(),
                new Class<?>[]{Controller.class},
                new ControllerExceptionHandler(controller)
        );
    }
}

