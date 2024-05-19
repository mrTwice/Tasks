package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidMethodException;
import ru.infinitesynergy.yampolskiy.restapiserver.utils.ObjectMapperSingleton;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.*;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class SingUpRoute implements Route {
    private static final Logger logger = LogManager.getLogger(SingUpRoute.class);
    private final UserService userService;

    public SingUpRoute(UserService userService) {
        this.userService = userService;
    }

    @Override
    public HttpResponse execute(HttpRequest httpRequest) throws Exception {
        if (!httpRequest.getMethod().equals(HttpMethod.POST)) {
            throw new NotValidMethodException("Некорректный метод запроса: " + httpRequest.getMethod());
        }

        String stringUserDTO = httpRequest.getBody();
        User newUser = userService.createNewUser(ObjectMapperSingleton.getInstance().readValue(stringUserDTO, User.class));

        HttpResponse httpResponse = new HttpResponse.Builder()
                .setProtocolVersion(httpRequest.getProtocolVersion())
                .setStatus(HttpStatus.CREATED)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "BankServer/0.1")
                .addHeader(HttpHeader.LOCATION, "https://localhost:8080/signin")
                .addHeader(HttpHeader.CONTENT_TYPE, "application/octet-stream")
                .addHeader(HttpHeader.CONNECTION, "close")
                .setBody("")
                .build();

        logger.info("Зарегистрирован пользователь: {}", newUser.getLogin());
        return httpResponse;
    }

}
