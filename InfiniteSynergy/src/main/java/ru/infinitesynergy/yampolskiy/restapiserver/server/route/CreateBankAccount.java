package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.BankAccount;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidMethodException;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.TokenIsNotValidException;
import ru.infinitesynergy.yampolskiy.restapiserver.jwt.JwtUtils;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.*;
import ru.infinitesynergy.yampolskiy.restapiserver.service.BankAccountService;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CreateBankAccount implements Route{

    private UserService userService;
    private BankAccountService bankAccountService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public CreateBankAccount(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }
    @Override
    public HttpResponse execute(HttpRequest httpRequest) throws JsonProcessingException {
        if (!httpRequest.getMethod().equals(HttpMethod.GET)) {
            throw new NotValidMethodException("Некорректный метод запроса: " + httpRequest.getMethod());
        }

        String authorizationHeaderValue = httpRequest.getHeaders().getHeader(HttpHeader.AUTHORIZATION.getHeaderName());

        if (authorizationHeaderValue == null || !authorizationHeaderValue.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Отсутствует или неверный формат заголовка Authorization");
        }
        String jwtToken = authorizationHeaderValue.substring("Bearer ".length());
        if(!JwtUtils.isValidToken(jwtToken)){
            throw new TokenIsNotValidException("Токен не валидный");
        }
        String username = JwtUtils.extractUsername(jwtToken);
        User user = userService.getUserByUserName(username);
        BankAccount bankAccount = bankAccountService.createNewBankAccount(user.getId());
        String responseBody = objectMapper.writeValueAsString(bankAccount);
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setProtocolVersion(httpRequest.getProtocolVersion());
        httpResponse.setStatus(HttpStatus.CREATED);
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeader.DATE.getHeaderName(), ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
        headers.addHeader(HttpHeader.SERVER.getHeaderName(), "BankServer/0.1");
        headers.addHeader(HttpHeader.CONTENT_TYPE.getHeaderName(), "application/json");
        headers.addHeader(HttpHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(responseBody.getBytes().length));
        httpResponse.setHeaders(headers);
        httpResponse.setBody(responseBody);
        return httpResponse;
    }
}
