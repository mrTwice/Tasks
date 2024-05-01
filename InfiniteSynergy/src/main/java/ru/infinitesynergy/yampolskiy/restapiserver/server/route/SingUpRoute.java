package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidMethodException;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpMethod;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpStatus;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        System.out.println("Метод: " + httpRequest.getMethod().toString());
        System.out.println("Путь: " + httpRequest.getUri().toString());
        System.out.println("Протокол: " + httpRequest.getProtocolVersion());
        System.out.println("Заголовки: " + httpRequest.getHeaders());
        System.out.println("Тело: " + httpRequest.getBody());

        String stringUserDTO = httpRequest.getBody();
        System.out.println("UserDTO: " + stringUserDTO);
        User user = userService.createNewUser(objectMapper.readValue(stringUserDTO, User.class));
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setProtocolVersion(httpRequest.getProtocolVersion());
        httpResponse.setStatus(HttpStatus.CREATED);
        Map<String, String> headers = new HashMap<>();
        headers.put("Date", LocalDateTime.now().toString());
        headers.put("Server", "BankServer/0.1");
        headers.put("Content-Length", "0");
        headers.put("Content-Type", "text/plain");
        headers.put("Connection", "close");
        httpResponse.setHeaders(headers);
        httpResponse.setBody("");
        return httpResponse;
    }

}
