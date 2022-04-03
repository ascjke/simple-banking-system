package banking;

import banking.dao.AccountRepository;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class AccountCommandLineInterface implements AccountInterface {

    private static final Scanner scanner = new Scanner(System.in);
    private final AccountRepository repository;

    public AccountCommandLineInterface(AccountRepository repository) {
        this.repository = repository;
    }

    public void start() {

        int choice = mainMenuSelection();

        switch (choice) {
            case 1:
                createAccount();
                start();
                break;

            case 2:
                logIntoAccount();
                break;

            case 0:
                System.out.println("\nBye!");
                try {
                    repository.getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public int mainMenuSelection() {

        String choice;
        int digitChoice;

        while (true) {
            System.out.println("\n1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            choice = scanner.next();

            if (isDigit(choice)) {
                digitChoice = Integer.parseInt(choice);

                if (digitChoice < 0 || digitChoice > 2) {
                    System.out.println("Your choice is incorrect!");
                } else {
                    break;
                }
            } else {
                System.out.println("Your choice is incorrect!");
            }
        }

        return digitChoice;
    }

    public Account createAccount() {

        Account account = new Account();
        repository.createAccount(account);

        System.out.println("\nYour card has been created\n" + "Your card number:\n" + account.getCardNumber() + "\n" + "Your card PIN:\n" + account.getPin());

        return repository.getAccountByCardNumber(account.getCardNumber());
    }


    public void logIntoAccount() {

        Account currentAccount = null;

        System.out.println("\nEnter your card number:");
        String checkedCardNumber = scanner.next();

        for (Account account : repository.getAllAccounts()) {
            if (account.getCardNumber().equals(checkedCardNumber)) {
                currentAccount = account;
                break;
            }
        }

        if (currentAccount == null) {
            System.out.println("Wrong card number or PIN!");
            start();
            return;
        }

        System.out.println("Enter your PIN:");
        String checkedPin = scanner.next();

        if (currentAccount.getPin().equals(checkedPin)) {
            System.out.println("\nYou have successfully logged in!\n");
            accountMenuActions(currentAccount);
        } else {
            System.out.println("Wrong card number or PIN!");
            start();
        }
    }

    public int accountMenuSelections() {

        String choice;
        int digitChoice;

        while (true) {
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Logout");
            System.out.println("0. Exit");

            choice = scanner.next();

            if (isDigit(choice)) {
                digitChoice = Integer.parseInt(choice);
                if (digitChoice < 0 || digitChoice > 5) {
                    System.out.println("Your choice is incorrect!");
                } else {
                    break;
                }
            } else {
                System.out.println("Your choice is incorrect!");
            }
        }

        return digitChoice;
    }

    public void accountMenuActions(Account account) {

        int choice = accountMenuSelections();

        switch (choice) {
            case 1:
                int balance = repository.getAccountByCardNumber(account.getCardNumber()).getBalance();
                System.out.println("\nBalance : " + balance + "\n");
                accountMenuActions(account);
                break;

            case 2:
                System.out.println("Enter income:\n");
                int income = scanner.nextInt();
                repository.addIncome(account, income);
                System.out.println("Income was added!");
                accountMenuActions(account);
                break;

            case 3:
                System.out.println("\nTransfer");
                System.out.println("Enter card number:");
                String transferCardNumber = scanner.next();

                if (isDigit(transferCardNumber)) {

                    if (account.isPassLuhnAlgorithm(transferCardNumber)) {
                        Account accountToBeIncomed = null;
                        for (Account acc : repository.getAllAccounts()) {
                            if (acc.getCardNumber().equals(transferCardNumber)) {
                                accountToBeIncomed = acc;
                                break;
                            }
                        }
                        if (Objects.nonNull(accountToBeIncomed)) {
                            System.out.println("Enter how much money you want to transfer:");
                            int transferIncome = scanner.nextInt();
                            if (account.getBalance() < transferIncome) {
                                System.out.println("Not enough money!");
                                accountMenuActions(account);
                                break;
                            } else {
                                try {
                                    repository.transferIncome(account, accountToBeIncomed, transferIncome);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Success!");
                                accountMenuActions(account);
                                break;
                            }
                        } else {
                            System.out.println("Such a card does not exist.");
                            accountMenuActions(account);
                            break;
                        }
                    } else {
                        System.out.println("Probably you made a mistake in the card number. Please try again!");
                        accountMenuActions(account);
                        break;
                    }
                } else {
                    System.out.println("Such a card does not exist.");
                    accountMenuActions(account);
                    break;
                }

            case 4:
                repository.deleteAccount(account);
                System.out.println("\nThe account has been closed!");
                start();
                break;

            case 5:
                System.out.println("\nYou have successfully logged out!");
                start();
                break;

            case 0:
                System.out.println("Bye!");
                break;
        }
    }

    private boolean isDigit(String s) {

        boolean isDigit = true;
        for (char ch : s.toCharArray()) {
            if (!Character.isDigit(ch)) {
                isDigit = false;
                break;
            }
        }
        return isDigit;
    }
}
