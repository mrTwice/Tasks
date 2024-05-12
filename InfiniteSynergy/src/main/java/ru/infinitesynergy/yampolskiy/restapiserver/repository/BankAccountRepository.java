package ru.infinitesynergy.yampolskiy.restapiserver.repository;

import ru.infinitesynergy.yampolskiy.restapiserver.entities.BankAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankAccountRepository {
    private final DataBaseManager dataBaseManager = DataBaseManager.getDataBaseManager();

    public BankAccount createBankAccount(BankAccount bankAccount) {
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO bank_accounts (user_id, account_number, amount) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, bankAccount.getUserId());
            statement.setString(2, bankAccount.getAccountNumber());
            statement.setDouble(3, bankAccount.getAmount());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                bankAccount.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankAccount;
    }

    public BankAccount readBankAccount(Long id) {
        BankAccount bankAccount = null;
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM bank_accounts WHERE id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                bankAccount = BankAccount.createBankAccount(resultSet.getLong("user_id"));
                bankAccount.setId(resultSet.getLong("id"));
                bankAccount.setAccountNumber(resultSet.getString("account_number"));
                bankAccount.setAmount(resultSet.getDouble("amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankAccount;
    }

    public void updateBankAccount(BankAccount bankAccount) {
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("UPDATE bank_accounts SET user_id = ?, account_number = ?, amount = ? WHERE id = ?");
            statement.setLong(1, bankAccount.getUserId());
            statement.setString(2, bankAccount.getAccountNumber());
            statement.setDouble(3, bankAccount.getAmount());
            statement.setLong(4, bankAccount.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBankAccount(Long id) {
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("DELETE FROM bank_accounts WHERE id = ?");
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Long> getBankAccountIdsByUserId(Long userId) {
        List<Long> bankAccountIds = new ArrayList<>();
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM bank_accounts WHERE user_id = ?");
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                bankAccountIds.add(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankAccountIds;
    }

    public List<BankAccount> getBankAccountsByUserId(Long userId) {
        List<BankAccount> bankAccounts = new ArrayList<>();
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM bank_accounts WHERE user_id = ?");
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                BankAccount bankAccount = BankAccount.createBankAccount(resultSet.getLong("user_id"));
                bankAccount.setId(resultSet.getLong("id"));
                bankAccount.setAccountNumber(resultSet.getString("account_number"));
                bankAccount.setAmount(resultSet.getDouble("amount"));
                bankAccounts.add(bankAccount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankAccounts;

    }
}
