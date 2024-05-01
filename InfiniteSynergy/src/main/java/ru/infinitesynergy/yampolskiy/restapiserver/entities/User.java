package ru.infinitesynergy.yampolskiy.restapiserver.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.PasswordIsNullException;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.UserNameIsNullException;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize
@JsonDeserialize
public class User {
    private Long id;
    private String login;
    private String password;
    private List<Long> bankAccountList = new ArrayList<>();

    public User(){

    }

    private User(String login, String password) {
        this.login = login;
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

    public String getLogin() {
        return login;
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

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addBankAccount(Long bankAccount) {
        this.bankAccountList.add(bankAccount);
    }
}
