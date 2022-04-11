package banking;


public interface AccountInterface {

    void start();

    int mainMenuSelection();

    Account createAccount();

    void logIntoAccount();

    int accountMenuSelections();

    void accountMenuActions(Account account);
}
