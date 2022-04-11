package banking;


import banking.dao.AccountRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {

        String database = args[1];
        Connection connection = connectToDataBase(database);
        createTable(connection);
        AccountRepository repository = new AccountRepository(connection);
        AccountInterface accountInterface = new AccountCommandLineInterface(repository);

        accountInterface.start();
    }

    private static Connection connectToDataBase(String database) {

        String url = "jdbc:sqlite:" + database;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (Objects.nonNull(connection)) {
            System.out.println("Connection to database is completed");
        }
        return connection;
    }

    private static void createTable(Connection connection) {

        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS card(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "number TEXT, " +
                "pin TEXT, " +
                "balance INTEGER DEFAULT 0" +
                ");";

        try (PreparedStatement statement = connection.prepareStatement(sqlCreateTable)) {
            statement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}