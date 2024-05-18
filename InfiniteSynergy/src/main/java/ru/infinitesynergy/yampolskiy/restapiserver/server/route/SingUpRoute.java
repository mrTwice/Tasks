package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidMethodException;
import ru.infinitesynergy.yampolskiy.restapiserver.utils.ObjectMapperSingleton;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.*;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class SingUpRoute implements Route {
    private final UserService userService;

    public SingUpRoute(UserService userService) {
        this.userService = userService;
    }

    @Override
    public HttpResponse execute(HttpRequest httpRequest) throws Exception {
        if (!httpRequest.getMethod().equals(HttpMethod.POST)) {
            throw new NotValidMethodException("Некорректный метод запроса: " + httpRequest.getMethod());
        }
        System.out.println("*********** HTTPREQUEST after PARSING ************");
        System.out.println("Метод: " + httpRequest.getMethod().toString());
        System.out.println("Путь: " + httpRequest.getUri().toString());
        System.out.println("Протокол: " + httpRequest.getProtocolVersion());
        System.out.println("Заголовки: " + httpRequest.getHeaders().getAllHeaders());
        System.out.println("Тело: " + httpRequest.getBody());
        System.out.println("*********** HTTPREQUEST after PARSING ************");

        String stringUserDTO = httpRequest.getBody();
        userService.createNewUser(ObjectMapperSingleton.getInstance().readValue(stringUserDTO, User.class));

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

        System.out.println("*********** HTTPRESPONSE ************");
        System.out.println(httpResponse);
        System.out.println("*********** HTTPRESPONSE ************");
        return httpResponse;
    }

}
