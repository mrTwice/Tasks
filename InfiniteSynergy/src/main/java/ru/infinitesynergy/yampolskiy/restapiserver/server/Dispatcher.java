package ru.infinitesynergy.yampolskiy.restapiserver.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidPathLocationException;
import ru.infinitesynergy.yampolskiy.restapiserver.repository.BankAccountRepository;
import ru.infinitesynergy.yampolskiy.restapiserver.repository.UserRepository;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;
import ru.infinitesynergy.yampolskiy.restapiserver.server.route.MoneyRoute;
import ru.infinitesynergy.yampolskiy.restapiserver.server.route.Route;
import ru.infinitesynergy.yampolskiy.restapiserver.server.route.SingInRoute;
import ru.infinitesynergy.yampolskiy.restapiserver.server.route.SingUpRoute;
import ru.infinitesynergy.yampolskiy.restapiserver.service.BankAccountService;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private UserService userService;
    private BankAccountService bankAccountService;
    private final Map<String, Route> routes;

    public Dispatcher() {
        this.userService = new UserService(new UserRepository());
        this.bankAccountService = new BankAccountService(new BankAccountRepository());
        this.routes = new HashMap<>();
        routes.put("/signup", new SingUpRoute(userService));
        routes.put("/signin", new SingInRoute());
        routes.put("/money", new MoneyRoute());
    }


    public HttpResponse createHttpResponse(HttpRequest httpRequest) throws JsonProcessingException {
        if(!routes.containsKey(httpRequest.getUri().toString())){
            System.out.println(httpRequest.getUri().toString());
            throw new NotValidPathLocationException("Запрашиваемый путь " + httpRequest.getUri().toString() + " не существует.");
        }
        return routes.get(httpRequest.getUri().toString()).execute(httpRequest);
    }
}
