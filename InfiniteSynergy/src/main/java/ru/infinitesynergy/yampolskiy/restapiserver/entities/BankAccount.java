package ru.infinitesynergy.yampolskiy.restapiserver.entities;

import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.UserIdIsNullException;

import java.util.UUID;

public class BankAccount {
    private Long id;
    private Long userId;
    private String accountNumber;
    private double amount;

    private BankAccount(Long userId) {
        this.userId = userId;
        this.accountNumber = UUID.randomUUID().toString();
        this.amount = 0.0;
    }

    public static BankAccount createBankAccount(Long userId){
        if(userId == null) {
            throw new UserIdIsNullException("Счет должен быть привязан к пользователю. Укажите id пользователя");
        }
        return new BankAccount(userId);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAmount(double amount) {
        if(amount < 0) {
            throw new RuntimeException("Недостаточно средств!");
        }
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "id=" + id +
                ", userId=" + userId +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}
