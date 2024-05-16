package ru.infinitesynergy.yampolskiy.restapiserver.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.infinitesynergy.yampolskiy.restapiserver.repository.BankAccountRepository;
import ru.infinitesynergy.yampolskiy.restapiserver.repository.UserRepository;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpStatus;
import ru.infinitesynergy.yampolskiy.restapiserver.server.route.*;
import ru.infinitesynergy.yampolskiy.restapiserver.service.BankAccountService;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse.getErrorResponse;


public class RequestController {
    private UserService userService;
    private BankAccountService bankAccountService;
    private final Map<String, Route> routes;

    public RequestController() {
        this.userService = new UserService(new UserRepository());
        this.bankAccountService = new BankAccountService(new BankAccountRepository());
        this.routes = new HashMap<>();
        routes.put("/signup", new SingUpRoute(userService));
        routes.put("/signin", new SingInRoute(userService));
        routes.put("/create/bankaccount", new CreateBankAccount(userService, bankAccountService));
        routes.put("/money", new MoneyRoute(userService, bankAccountService));
    }


    public HttpResponse createHttpResponse(HttpRequest httpRequest) throws JsonProcessingException {
        if(!routes.containsKey(httpRequest.getUri().toString())){
            String message = "Запрашиваемый путь " + httpRequest.getUri().toString() + " не существует.";
            return getErrorResponse(httpRequest, HttpStatus.NOT_FOUND, message);
        }
        return routes.get(httpRequest.getUri().toString()).execute(httpRequest);
    }


}
