package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.BankAccount;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidMethodException;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.TokenIsNotValidException;
import ru.infinitesynergy.yampolskiy.restapiserver.utils.JwtUtils;
import ru.infinitesynergy.yampolskiy.restapiserver.utils.ObjectMapperSingleton;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.*;
import ru.infinitesynergy.yampolskiy.restapiserver.service.BankAccountService;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CreateBankAccount implements Route{

    private final UserService userService;
    private final BankAccountService bankAccountService;

    public CreateBankAccount(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }
    @Override
    public HttpResponse execute(HttpRequest httpRequest) throws Exception {
        if (!httpRequest.getMethod().equals(HttpMethod.GET)) {
            throw new NotValidMethodException("Некорректный метод запроса: " + httpRequest.getMethod());
        }

        String authorizationHeaderValue = httpRequest.getHeaders().getHeader(HttpHeader.AUTHORIZATION.getHeaderName());

        if (authorizationHeaderValue == null || !authorizationHeaderValue.startsWith("Bearer ")) {
            throw new TokenIsNotValidException("Отсутствует или неверный формат заголовка Authorization");
        }

        String jwtToken = authorizationHeaderValue.substring("Bearer ".length());

        if(!JwtUtils.isValidToken(jwtToken)){
            throw new TokenIsNotValidException("Токен не валидный");
        }
        String username = JwtUtils.extractUsername(jwtToken);
        User user = userService.getUserByUserName(username);
        BankAccount bankAccount = bankAccountService.createNewBankAccount(user.getId());
        String responseBody = ObjectMapperSingleton.getInstance().writeValueAsString(bankAccount);

        return new HttpResponse.Builder()
                .setProtocolVersion(httpRequest.getProtocolVersion())
                .setStatus(HttpStatus.CREATED)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "BankServer/0.1")
                .addHeader(HttpHeader.CONTENT_TYPE, "application/json")
                .addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length))
                .setBody(responseBody)
                .build();
    }

}
