package ru.infinitesynergy.yampolskiy.restapiserver.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;
import ru.infinitesynergy.yampolskiy.restapiserver.server.route.Route;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class RouteLoggingHandler implements InvocationHandler {
    private static final Logger logger = LogManager.getLogger(RouteLoggingHandler.class);
    private final Route target;

    public RouteLoggingHandler(Route target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        HttpRequest request = (HttpRequest) args[0];
        logRequest(request);

        try {
            Object result = method.invoke(target, args);
            logResponse((HttpResponse) result);
            return result;
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    private void logRequest(HttpRequest request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("[").append(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("] ");
        logMessage.append("Handling request: ").append(request.getMethod()).append(" ").append(request.getUri());
        if (request.getBody() != null && !request.getBody().isEmpty()) {
            logMessage.append(" with body: ").append(request.getBody());
        }
        System.out.println(logMessage);
        logger.info(logMessage.toString());
    }

    private void logResponse(HttpResponse response) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("[").append(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("] ");
        logMessage.append("Response status: ").append(response.getStatus());
        if (response.getBody() != null && !response.getBody().isEmpty()) {
            logMessage.append(" with body: ").append(response.getBody());
        }
        System.out.println(logMessage.toString());
        logger.info(logMessage.toString());
    }
}

