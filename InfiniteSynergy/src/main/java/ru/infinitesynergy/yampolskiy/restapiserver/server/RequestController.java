package ru.infinitesynergy.yampolskiy.restapiserver.server;

import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidPathLocationException;
import ru.infinitesynergy.yampolskiy.restapiserver.handlers.RouteExceptionHandler;
import ru.infinitesynergy.yampolskiy.restapiserver.handlers.RouteLoggingHandler;
import ru.infinitesynergy.yampolskiy.restapiserver.repository.BankAccountRepository;
import ru.infinitesynergy.yampolskiy.restapiserver.repository.UserRepository;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;
import ru.infinitesynergy.yampolskiy.restapiserver.server.route.*;
import ru.infinitesynergy.yampolskiy.restapiserver.service.BankAccountService;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;


public class RequestController implements Controller {
    private final Map<String, Route> routes  = new HashMap<>();

    public RequestController() {
        UserService userService = new UserService(new UserRepository());
        BankAccountService bankAccountService = new BankAccountService(new BankAccountRepository());

        routes.put("/signup", createRouteProxy(new SingUpRoute(userService)));
        routes.put("/signin", createRouteProxy(new SingInRoute(userService)));
        routes.put("/create/bankaccount", createRouteProxy(new CreateBankAccount(userService, bankAccountService)));
        routes.put("/money", createRouteProxy(new MoneyRoute(userService, bankAccountService)));
    }


    @Override
    public HttpResponse createHttpResponse(HttpRequest httpRequest) throws Exception {
        if(!routes.containsKey(httpRequest.getUri().toString())){
            throw new NotValidPathLocationException("Ресурс " + httpRequest.getUri().toString() + " не существует.");
        }
        return routes.get(httpRequest.getUri().toString()).execute(httpRequest);
    }

    private Route createRouteProxy(Route route) {
        Route exceptionHandlingProxy = (Route) Proxy.newProxyInstance(
                Route.class.getClassLoader(),
                new Class<?>[]{Route.class},
                new RouteExceptionHandler(route)
        );

        return (Route) Proxy.newProxyInstance(
                Route.class.getClassLoader(),
                new Class<?>[]{Route.class},
                new RouteLoggingHandler(exceptionHandlingProxy)
        );
    }
}
