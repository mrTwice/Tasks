package ru.infinitesynergy.yampolskiy.restapiserver;

import ru.infinitesynergy.yampolskiy.restapiserver.repository.BankAccountRepository;
import ru.infinitesynergy.yampolskiy.restapiserver.repository.UserRepository;
import ru.infinitesynergy.yampolskiy.restapiserver.server.BankServer;
import ru.infinitesynergy.yampolskiy.restapiserver.service.BankAccountService;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;

public class Main {
    private static final UserService userService = new UserService(new UserRepository());
    private static final BankAccountService bankAccountService = new BankAccountService(new BankAccountRepository());

    public static void main(String[] args) {
        BankServer bankServer = new BankServer();
        bankServer.start();
    }
}