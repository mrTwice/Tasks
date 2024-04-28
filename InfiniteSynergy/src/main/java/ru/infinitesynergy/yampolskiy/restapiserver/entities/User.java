package ru.infinitesynergy.yampolskiy.restapiserver.entities;

import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.PasswordIsNullException;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.UserNameIsNullException;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String username;
    private String password;
    private List<Long> bankAccountList = new ArrayList<>();

    private User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public static User createUser(String username, String password) {
        if(username == null){
            throw new UserNameIsNullException("Имя пользователя не может быть пустым.");
        }
        if(password == null) {
            throw  new PasswordIsNullException("Пароль не может быть пустым");
        }
        User user = new User(username, password);
        return user;
    }


    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Long> getBankAccountList() {
        return bankAccountList;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addBankAccount(Long bankAccount) {
        this.bankAccountList.add(bankAccount);
    }
}
