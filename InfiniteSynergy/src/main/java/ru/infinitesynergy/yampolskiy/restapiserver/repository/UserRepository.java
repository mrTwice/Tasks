package ru.infinitesynergy.yampolskiy.restapiserver.repository;

import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final DataBaseManager dataBaseManager = DataBaseManager.getDataBaseManager();

    public User createUser(User user) {
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User readUser(Long id) {
        User user = null;
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = User.createUser(resultSet.getString("username"), resultSet.getString("password"));
                user.setId(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void updateUser(User user) {
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET username = ?, password = ? WHERE id = ?");
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setLong(3, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(Long id) {
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataBaseManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                User user = User.createUser(resultSet.getString("username"), resultSet.getString("password"));
                user.setId(resultSet.getLong("id"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getUserByUsername(String username) {
        User user = null;
        try (Connection connection = dataBaseManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = User.createUser(resultSet.getString("username"), resultSet.getString("password"));
                user.setId(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}

