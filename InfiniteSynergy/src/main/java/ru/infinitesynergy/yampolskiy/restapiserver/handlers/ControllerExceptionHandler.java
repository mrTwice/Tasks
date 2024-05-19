package ru.infinitesynergy.yampolskiy.restapiserver.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.Error;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidPathLocationException;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpHeader;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpStatus;
import ru.infinitesynergy.yampolskiy.restapiserver.utils.ObjectMapperSingleton;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ControllerExceptionHandler implements InvocationHandler {
    private static final Logger logger = LogManager.getLogger(ControllerExceptionHandler.class);
    private final Object target;

    public ControllerExceptionHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            return switch (cause) {
                case NotValidPathLocationException notValidPathLocationException ->
                        handleThrowable(notValidPathLocationException, (HttpRequest) args[0], HttpStatus.NOT_FOUND);
                case null, default -> {
                    assert cause != null;
                    yield handleGeneralException(cause, (HttpRequest) args[0]);
                }
            };
        }
    }

    private HttpResponse handleThrowable(Throwable ex, HttpRequest request, HttpStatus httpStatus) throws JsonProcessingException {
        System.err.println("Перехвачено исключение: " + ex.getMessage());
        logger.info("Перехвачено исключение: {}", ex.getMessage());
        return getErrorResponse(request, httpStatus, ex.getMessage());
    }

    private HttpResponse handleGeneralException(Throwable ex, HttpRequest request) throws JsonProcessingException {
        System.err.println("Перехвачено общее исключение: " + ex.getMessage());
        logger.info("Перехвачено общее исключение: {}", ex.getMessage());
        return getErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }

    public HttpResponse getErrorResponse(HttpRequest httpRequest, HttpStatus status, String message) throws JsonProcessingException {

        String responseBody = ObjectMapperSingleton.getInstance().writeValueAsString(
                Error.createError(status.getMessage(), status.getCode(), message));

        return new HttpResponse.Builder()
                .setProtocolVersion(httpRequest.getProtocolVersion())
                .setStatus(status)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "BankServer/0.1")
                .addHeader(HttpHeader.CONTENT_TYPE, "application/json")
                .addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length))
                .setBody(responseBody)
                .build();
    }
}
