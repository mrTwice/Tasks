package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.BankAccount;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.TransferMoneyDTO;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.BankAccountNotFoundException;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotFundsEnoughInAccountException;
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
import java.util.List;

public class MoneyRoute implements Route {

    private static final Logger logger = LogManager.getLogger(MoneyRoute.class);
    private final UserService userService;
    private final BankAccountService bankAccountService;

    public MoneyRoute(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public HttpResponse execute(HttpRequest httpRequest) throws Exception {
            if (httpRequest.getMethod().equals(HttpMethod.GET)) {
                return handleGetRequest(httpRequest);
            } else if (httpRequest.getMethod().equals(HttpMethod.POST)) {
                return handlePostRequest(httpRequest);
            } else {
                throw new NotValidMethodException("Некорректный метод запроса: " + httpRequest.getMethod());
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
            throw new NotFundsEnoughInAccountException("Ни на одном счете нет достаточного количества средств для перевода");
        }

        BankAccount to = receiver.getBankAccountList().stream().findFirst().orElse(null);
        if(to == null) {
            throw new BankAccountNotFoundException("У получателя не открыт ни один счет");
        }
        //TODO: подумать над транзакциями
        from.setAmount(from.getAmount() - amount);
        to.setAmount(to.getAmount() + amount);
        bankAccountService.updateBankAccount(from);
        bankAccountService.updateBankAccount(to);

        logger.info("Пользователь {} перевел {} y.e. со счета № {} на счет № {} пользователя {}", sender.getLogin(),
                amount, from.getAccountNumber(), to.getAccountNumber(), receiver.getLogin());

        String responseBody = ObjectMapperSingleton.getInstance().writeValueAsString(from);
        return buildResponse(httpRequest.getProtocolVersion(), HttpStatus.OK, responseBody);
    }

    private HttpResponse handleGetRequest(HttpRequest httpRequest) throws JsonProcessingException {
        String username = getUsernameFromToken(httpRequest.getHeaders().getHeader(HttpHeader.AUTHORIZATION.getHeaderName()));
        User user = userService.getUserByUserName(username);

        logger.info("Пользователь {} запросил баланс по счетам", user.getLogin());
        List<BankAccount> bankAccountList = bankAccountService.getUsersBankAccounts(user.getId());
        for(BankAccount bankAccount: bankAccountList){
            logger.info("На счете № {} пользователя {} {} y.e.", bankAccount.getAccountNumber(), user.getLogin(), bankAccount.getAmount());
        }

        String responseBody = ObjectMapperSingleton.getInstance().writeValueAsString(bankAccountList);
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
