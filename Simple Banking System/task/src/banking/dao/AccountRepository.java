package banking.dao;

import banking.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public AccountRepository(Connection connection) {
        this.connection = connection;
    }

    public Account createAccount(Account account) {

        String sql = "INSERT INTO card(number, pin, balance) VALUES(?,?,?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, account.getCardNumber());
            pstmt.setString(2, account.getPin());
            pstmt.setInt(3, account.getBalance());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return account;
    }

    public Account getAccountByCardNumber(String cardNumber) {

        Account account;
        String sql = "SELECT * FROM card where number=?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            try (ResultSet resultSet = pstmt.executeQuery()) {

                String id = resultSet.getString("id");
                String number = resultSet.getString("number");
                String pin = resultSet.getString("pin");
                int balance = resultSet.getInt("balance");
                account = new Account(id, number, pin, balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return account;
    }

    public List<Account> getAllAccounts() {

        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM card";

        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String number = resultSet.getString("number");
                    String pin = resultSet.getString("pin");
                    int balance = resultSet.getInt("balance");
                    accounts.add(new Account(id, number, pin, balance));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return accounts;
    }

    public boolean addIncome(Account account, int income) {

        String sql = "UPDATE card SET balance = balance + ? WHERE id = ?";
        int row = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, income);
            pstmt.setInt(2, Integer.parseInt(account.getId()));
            row = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        account.setBalance(account.getBalance() + income);
        return row == 1;
    }

    public boolean transferIncome(Account account, Account accountToBeIncomed, int transferIncome) throws SQLException {

        String addBalance = "UPDATE card SET balance = balance + ? WHERE id = ?";
        String decreaseBalance = "UPDATE card SET balance = balance - ? WHERE id = ?";
        int row1 = 0;
        int row2 = 0;
        connection.setAutoCommit(false);

        Savepoint savepoint_0 = connection.setSavepoint();

        try (PreparedStatement add = connection.prepareStatement(addBalance);
             PreparedStatement decrease = connection.prepareStatement(decreaseBalance)) {
            decrease.setInt(1, transferIncome);
            decrease.setInt(2, Integer.parseInt(account.getId()));
            row2 = decrease.executeUpdate();

            add.setInt(1, transferIncome);
            add.setInt(2, Integer.parseInt(accountToBeIncomed.getId()));
            row1 = add.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            System.out.println("SQLException. Executing rollback to savepoint...");
            connection.rollback(savepoint_0);
        }

        return (row1 + row2) == 2;
    }

    public Account deleteAccount(Account account) {

        String deleteAccount = "DELETE FROM card WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteAccount)) {
            pstmt.setInt(1, Integer.parseInt(account.getId()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return account;
    }
}