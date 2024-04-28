package ru.infinitesynergy.yampolskiy.restapiserver.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Users";
    private static final String POSTGRES_USER = "User";
    private static final String POSTGRES_PASSWORD = "Password";
    private static DataBaseManager dataBaseManager;

    private DataBaseManager(){
    }

    public static DataBaseManager getDataBaseManager() {
        if(dataBaseManager == null) {
            dataBaseManager = new DataBaseManager();
        }
        return dataBaseManager;
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DATABASE_URL, POSTGRES_USER, POSTGRES_PASSWORD);

        } catch (SQLException  e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

}
