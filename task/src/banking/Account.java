package banking;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Account {

    private String id;
    private static final String CARD_BIN = "4000 00";
    private String cardNumber = "";
    private String pin = "";
    private int balance = 0;

    public Account() {

        cardNumber = generateCardNumberWithoutChecksum();
        String checksum = generateChecksum(cardNumber);
        cardNumber += checksum;
        for (int i = 0; i < 4; i++) {
            pin += String.valueOf(new Random().nextInt(9) + 1);
        }
    }

    public Account(String id, String cardNumber, String pin, int balance) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    private String generateCardNumberWithoutChecksum() {

        String accountIdentifier = "";
        for (int i = 0; i < 9; i++) {
            accountIdentifier += String.valueOf(new Random().nextInt(9) + 1);
        }

        return (CARD_BIN + accountIdentifier.substring(0, 2) + " " + accountIdentifier.substring(2, 6) + " " +
                accountIdentifier.substring(6)).replaceAll(" ", "");
    }

    private String generateChecksum(String cardNumber) {

        int checksum;
        int sum = 0;
        List<Integer> numList = new ArrayList<>();

        for (int i = 1; i <= cardNumber.length(); i++) {
            Integer tempNum = Integer.parseInt(cardNumber.substring(i - 1, i));

            if (i % 2 != 0) {
                if (tempNum * 2 > 9) {
                    String value = String.valueOf(tempNum * 2);
                    Integer digit_1 = Integer.parseInt(value.substring(0, 1));
                    Integer digit_2 = Integer.parseInt(value.substring(1));
                    numList.add(digit_1 + digit_2);
                } else {
                    numList.add(tempNum * 2);
                }
            } else {
                numList.add(tempNum);
            }
        }

        for (Integer num : numList) {
            sum += num;
        }

        checksum = (Math.abs((sum % 10) - 10));
        if (checksum == 10) {
            return "0";
        } else {
            return String.valueOf(checksum);
        }
    }

    public boolean isPassLuhnAlgorithm(String cardNumber) {

        String cardNumberWithoutChecksum = cardNumber.substring(0, cardNumber.length() - 1);
        String cardNumberChecked = cardNumberWithoutChecksum + generateChecksum(cardNumberWithoutChecksum);

        if (cardNumber.equals(cardNumberChecked)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", pin='" + pin + '\'' +
                ", balance=" + balance +
                '}';
    }
}