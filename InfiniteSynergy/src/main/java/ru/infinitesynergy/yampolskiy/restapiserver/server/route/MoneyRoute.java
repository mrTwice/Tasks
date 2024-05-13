package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.BankAccount;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.TransferMoneyDTO;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidMethodException;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.TokenIsNotValidException;
import ru.infinitesynergy.yampolskiy.restapiserver.jwt.JwtUtils;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.*;
import ru.infinitesynergy.yampolskiy.restapiserver.service.BankAccountService;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MoneyRoute implements Route {
    private UserService userService;
    private BankAccountService bankAccountService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public MoneyRoute(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public HttpResponse execute(HttpRequest httpRequest) throws JsonProcessingException {
        return switch (httpRequest.getMethod()) {
            case GET -> handleGetRequest(httpRequest);
            case POST -> handlePostRequest(httpRequest);
            default -> throw new NotValidMethodException("Некорректный метод запроса: " + httpRequest.getMethod());
        };
    }

    private HttpResponse handlePostRequest(HttpRequest httpRequest) throws JsonProcessingException {
        String username = getUsernameFromToken(httpRequest.getHeaders().getHeader(HttpHeader.AUTHORIZATION.getHeaderName()));
        TransferMoneyDTO transferMoneyDTO = objectMapper.readValue(httpRequest.getBody(), TransferMoneyDTO.class);
        User sender = userService.getUserByUserName(username);
        sender.addBankAccounts(bankAccountService.getUsersBankAccounts(sender.getId()));
        User receiver = userService.getUserByUserName(transferMoneyDTO.getTo());
        receiver.addBankAccounts(bankAccountService.getUsersBankAccounts(receiver.getId()));
        double amount = transferMoneyDTO.getAmount();
        BankAccount from = sender.getBankAccountList().stream().filter(bankAccount -> bankAccount.getAmount() > amount).findFirst().orElse(null);
        if(from == null) {
            throw new RuntimeException("Ни на одном счете нет достаточного количества средств для перевода");
        }

        BankAccount to = receiver.getBankAccountList().stream().findFirst().orElse(null);
        if(to == null) {
            throw new RuntimeException("у получателя не открыт ни один счет");
        }
        //TODO: подумать над транзакциями
        from.setAmount(from.getAmount() - amount);
        to.setAmount(to.getAmount() + amount);
        bankAccountService.updateBankAccount(from);
        bankAccountService.updateBankAccount(to);

        String responseBody = objectMapper.writeValueAsString(from);
        return buildResponse(httpRequest.getProtocolVersion(), HttpStatus.OK, responseBody);
    }

    private HttpResponse handleGetRequest(HttpRequest httpRequest) throws JsonProcessingException {
        String username = getUsernameFromToken(httpRequest.getHeaders().getHeader(HttpHeader.AUTHORIZATION.getHeaderName()));
        User user = userService.getUserByUserName(username);
        String responseBody = objectMapper.writeValueAsString(bankAccountService.getUsersBankAccounts(user.getId()));
        return buildResponse(httpRequest.getProtocolVersion(), HttpStatus.OK, responseBody);
    }

    private HttpResponse buildResponse(String protocolVersion, HttpStatus status, String responseBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader(HttpHeader.DATE.getHeaderName(), ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
        headers.addHeader(HttpHeader.SERVER.getHeaderName(), "BankServer/0.1");
        headers.addHeader(HttpHeader.CONTENT_TYPE.getHeaderName(), "application/json");
        headers.addHeader(HttpHeader.CONTENT_LENGTH.getHeaderName(), String.valueOf(responseBody.getBytes().length));

        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setProtocolVersion(protocolVersion);
        httpResponse.setStatus(status);
        httpResponse.setHeaders(headers);
        httpResponse.setBody(responseBody);

        return httpResponse;
    }

    private String getUsernameFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Отсутствует или неверный формат заголовка Authorization");
        }
        String jwtToken = authorizationHeader.substring("Bearer ".length());
        if(!JwtUtils.isValidToken(jwtToken)){
            throw new TokenIsNotValidException("Токен не валидный");
        }
        return JwtUtils.extractUsername(jwtToken);
    }
}
