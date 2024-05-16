package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.BankAccount;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.Error;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.TransferMoneyDTO;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.TokenIsNotValidException;
import ru.infinitesynergy.yampolskiy.restapiserver.utils.JwtUtils;
import ru.infinitesynergy.yampolskiy.restapiserver.utils.ObjectMapperSingleton;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.*;
import ru.infinitesynergy.yampolskiy.restapiserver.service.BankAccountService;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse.getErrorResponse;

public class MoneyRoute implements Route {
    private final UserService userService;
    private final BankAccountService bankAccountService;

    public MoneyRoute(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public HttpResponse execute(HttpRequest httpRequest) throws JsonProcessingException {
        try {
            if (httpRequest.getMethod().equals(HttpMethod.GET)) {
                return handleGetRequest(httpRequest);
            } else if (httpRequest.getMethod().equals(HttpMethod.POST)) {
                return handlePostRequest(httpRequest);
            } else {
                return getErrorResponse(httpRequest, HttpStatus.METHOD_NOT_ALLOWED, "Некорректный метод запроса: " + httpRequest.getMethod());
            }
        } catch (IllegalArgumentException | TokenIsNotValidException e) {
            return getErrorResponse(httpRequest, HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            return getErrorResponse(httpRequest, HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }

    private HttpResponse handlePostRequest(HttpRequest httpRequest) throws JsonProcessingException {
        String username = getUsernameFromToken(httpRequest.getHeaders().getHeader(HttpHeader.AUTHORIZATION.getHeaderName()));
        TransferMoneyDTO transferMoneyDTO = ObjectMapperSingleton.getInstance().readValue(httpRequest.getBody(), TransferMoneyDTO.class);
        User sender = userService.getUserByUserName(username);
        sender.addBankAccounts(bankAccountService.getUsersBankAccounts(sender.getId()));
        User receiver = userService.getUserByUserName(transferMoneyDTO.getTo());
        receiver.addBankAccounts(bankAccountService.getUsersBankAccounts(receiver.getId()));
        double amount = transferMoneyDTO.getAmount();
        BankAccount from = sender.getBankAccountList().stream().filter(bankAccount -> bankAccount.getAmount() > amount).findFirst().orElse(null);

        if(from == null) {
            String message = "Ни на одном счете нет достаточного количества средств для перевода";
            return getErrorResponse(httpRequest, HttpStatus.FORBIDDEN, message);
        }

        BankAccount to = receiver.getBankAccountList().stream().findFirst().orElse(null);
        if(to == null) {
            String message = "У получателя не открыт ни один счет";
            return getErrorResponse(httpRequest, HttpStatus.NOT_FOUND, message);
        }
        //TODO: подумать над транзакциями
        from.setAmount(from.getAmount() - amount);
        to.setAmount(to.getAmount() + amount);
        bankAccountService.updateBankAccount(from);
        bankAccountService.updateBankAccount(to);

        String responseBody = ObjectMapperSingleton.getInstance().writeValueAsString(from);
        return buildResponse(httpRequest.getProtocolVersion(), HttpStatus.OK, responseBody);
    }

    private HttpResponse handleGetRequest(HttpRequest httpRequest) throws JsonProcessingException {
        String username = getUsernameFromToken(httpRequest.getHeaders().getHeader(HttpHeader.AUTHORIZATION.getHeaderName()));
        User user = userService.getUserByUserName(username);
        String responseBody = ObjectMapperSingleton.getInstance().writeValueAsString(bankAccountService.getUsersBankAccounts(user.getId()));
        return buildResponse(httpRequest.getProtocolVersion(), HttpStatus.OK, responseBody);
    }

    private HttpResponse buildResponse(String protocolVersion, HttpStatus status, String responseBody) {
        return new HttpResponse.Builder()
                .setProtocolVersion(protocolVersion)
                .setStatus(status)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "BankServer/0.1")
                .addHeader(HttpHeader.CONTENT_TYPE, "application/json")
                .addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length))
                .setBody(responseBody)
                .build();
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
