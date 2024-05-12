package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidMethodException;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.*;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SingUpRoute implements Route {
    private UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public SingUpRoute(UserService userService) {
        this.userService = userService;
    }

    @Override
    public HttpResponse execute(HttpRequest httpRequest) throws JsonProcessingException {
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
        userService.createNewUser(objectMapper.readValue(stringUserDTO, User.class));
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setProtocolVersion(httpRequest.getProtocolVersion());
        httpResponse.setStatus(HttpStatus.CREATED);
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeader.DATE.getHeaderName(), ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
        headers.addHeader(HttpHeader.SERVER.getHeaderName(), "BankServer/0.1");
        headers.addHeader(HttpHeader.LOCATION.getHeaderName(), "https://localhost:8080/signin");
        headers.addHeader(HttpHeader.CONTENT_TYPE.getHeaderName(), "application/octet-stream");
        headers.addHeader(HttpHeader.CONTENT_LENGTH.getHeaderName(), "0");
        headers.addHeader(HttpHeader.CONNECTION.getHeaderName(), "close");
        httpResponse.setHeaders(headers);
        httpResponse.setBody("");


        System.out.println("*********** HTTPRESPONSE ************");
        System.out.println(httpResponse);
        System.out.println("*********** HTTPRESPONSE ************");
        return httpResponse;
    }

}
