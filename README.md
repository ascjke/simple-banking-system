# simple-banking-system


In this pet project we have a simple banking system, where we can create an account and also manage them (get balance, add income, do transfer, delete account).

    Here is used SQLite database.

    When we create an account, we create a card number which are passed Luhn algorithm.

    When we transfer some money from current account to another account, JDBCtransaction is used (two SQL query at the same time).
