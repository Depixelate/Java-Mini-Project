import java.util.*;
import java.io.*;
import java.text.*;

// Enum to represent transaction types
enum TransactionType {
    ONE_TIME, RECURRING, EMERGENCY
}

// Transaction interface
interface Transaction extends JSONConvertable {
    void recordTransaction();
}

// OneTimeTransaction class implementing Transaction interface
class OneTimeTransaction implements Transaction {
    private double amount;
    private String description;
    private String tag;
    private Date date;

    // Constructor
    public OneTimeTransaction(double amount, String description, String tag, Date date) {
        this.amount = amount;
        this.description = description;
        this.tag = tag;
        this.date = date;
    }

    // Static method to create a OneTimeTransaction based on user input
    public static OneTimeTransaction createOneTimeTransaction(Scanner scan) {
        System.out.print("Enter amount: ");
        double amount = scan.nextDouble();
        scan.nextLine(); // Consume the newline character

        System.out.print("Enter description: ");
        String description = scan.nextLine();

        System.out.print("Enter tag: ");
        String tag = scan.nextLine();

        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateString = scan.nextLine();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Transaction creation failed.");
            return null;
        }

        return new OneTimeTransaction(amount, description, tag, date);
    }
    @Override
    public void recordTransaction() {
        // Implementation to record one-time transaction
    }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "{ \"type\": \"ONE_TIME\", \"amount\": " + amount + ", \"description\": \"" + description + "\", \"tag\": \"" + tag + "\", \"date\": \"" + date + "\" }";
    }
}

// RecurringTransaction class implementing Transaction interface
class RecurringTransaction implements Transaction {
    private double amount;
    private String description;
    private String tag;
    private Date startDate;

    // Constructor
    public RecurringTransaction(double amount, String description, String tag, Date startDate) {
        this.amount = amount;
        this.description = description;
        this.tag = tag;
        this.startDate = startDate;
    }

    public static RecurringTransaction createRecurringTransaction(Scanner scan) {
        System.out.print("Enter amount: ");
        double amount = scan.nextDouble();
        scan.nextLine(); // Consume the newline character

        System.out.print("Enter description: ");
        String description = scan.nextLine();

        System.out.print("Enter tag: ");
        String tag = scan.nextLine();

        System.out.print("Enter start date (yyyy-MM-dd): ");
        String startDateString = scan.nextLine();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        try {
            startDate = dateFormat.parse(startDateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Transaction creation failed.");
            return null;
        }

        return new RecurringTransaction(amount, description, tag, startDate);
    }

    @Override
    public void recordTransaction() {
        // Implementation to record recurring transaction
    }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "{ \"type\": \"RECURRING\", \"amount\": " + amount + ", \"description\": \"" + description + "\", \"tag\": \"" + tag + "\", \"startDate\": \"" + startDate + "\" }";
    }
}

// EmergencyTransaction class implementing Transaction interface
class EmergencyTransaction implements Transaction {
    private double amount;
    private String description;
    private String tag;
    private Date dueDate;

    // Constructor
    public EmergencyTransaction(double amount, String description, String tag, Date dueDate) {
        this.amount = amount;
        this.description = description;
        this.tag = tag;
        this.dueDate = dueDate;
    }

    // Static method to create an EmergencyTransaction based on user input
    public static EmergencyTransaction createEmergencyTransaction(Scanner scan) {
        System.out.print("Enter amount: ");
        double amount = scan.nextDouble();
        scan.nextLine(); // Consume the newline character

        System.out.print("Enter description: ");
        String description = scan.nextLine();

        System.out.print("Enter tag: ");
        String tag = scan.nextLine();

        System.out.print("Enter due date (yyyy-MM-dd): ");
        String dueDateString = scan.nextLine();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dueDate = null;
        try {
            dueDate = dateFormat.parse(dueDateString);
        } catch (ParseException e) {
            System.out.println("Invalid date format. Transaction creation failed.");
            return null;
        }

        return new EmergencyTransaction(amount, description, tag, dueDate);
    }

    @Override
    public void recordTransaction() {
        // Implementation to record emergency transaction
    }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "{ \"type\": \"EMERGENCY\", \"amount\": " + amount + ", \"description\": \"" + description + "\", \"tag\": \"" + tag + "\", \"dueDate\": \"" + dueDate + "\" }";
    }
}

// Abstract class for alerts
abstract class Alerts {
    abstract void sendAlert();
}

// Subclass for emergency alerts
class EmergencyAlerts extends Alerts {
    @Override
    void sendAlert() {
        System.out.println("Emergency Alert: Pay your bill immediately!");
        // Add any additional logic needed for emergency alerts
    }
}

// Subclass for mild alerts
class MildAlerts extends Alerts {
    @Override
    void sendAlert() {
        System.out.println("Mild Alert: Don't forget to pay your bill in a few days.");
        // Add any additional logic needed for mild alerts
    }
}

// Comparator classes for sorting transactions
class ValueComparator implements Comparator<Transaction> {
  @Override
  public int compare(Transaction t1, Transaction t2) {
    // Compare transactions based on value
    return 0;
  }
}

class DateComparator implements Comparator<Transaction> {
  @Override
  public int compare(Transaction t1, Transaction t2) {
    // Compare transactions based on date
    return 0;
  }
}

class CategoryComparator implements Comparator<Transaction> {
  @Override
  public int compare(Transaction t1, Transaction t2) {
    // Compare transactions based on category
    return 0;
  }
}

// Generic class for RadixSort
class RadixSort {
  public static <T> void sort(T[] elems) {
    // Implementation for RadixSort
  }
}

// Interface for JSON conversion
interface JSONConvertable {
  String toJSON();
}

// Exception class for custom exceptions
class FinAssistException extends Exception {
  public FinAssistException(String message) {
    super(message);
  }
}

class FinAssistApp {
    private Map<String, List<Transaction>> accountsTransactions;

    // Constructor
    public FinAssistApp() {
        this.accountsTransactions = new HashMap<>();
    }

    // Method to record a transaction for a specific account
    public void recordTransaction(Account account, Transaction transaction) {
        String accountNumber = account.getAccountNumber();

        // If the account is not found, create a new list for this account
        accountsTransactions.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(transaction);

        transaction.recordTransaction();
        account.displayAccountDetails(); // Display updated account details
        System.out.println("Transaction recorded successfully.");
    }

    // Method to save transactions to a file for a specific account
    public void saveTransactionsToFile(Account account, String fileName) {
        String accountNumber = account.getAccountNumber();
        List<Transaction> accountTransactions = accountsTransactions.get(accountNumber);

        if (accountTransactions != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                for (Transaction transaction : accountTransactions) {
                    writer.println(transaction.toJSON());
                }
                System.out.println("Transactions saved successfully for account: " + accountNumber);
            } catch (IOException e) {
                System.err.println("Error saving transactions to file: " + e.getMessage());
            }
        } else {
            System.out.println("Account not found. Transactions not saved.");
        }
    }

    // Method to load transactions from a file for a specific account
    public void loadTransactionsFromFile(Account account, String fileName) {
        String accountNumber = account.getAccountNumber();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming each line in the file represents a JSON-formatted transaction
                Transaction transaction = parseJSON(line);
                if (transaction != null) {
                    accountsTransactions.computeIfAbsent(accountNumber, k -> new ArrayList<>()).add(transaction);
                }
            }
            System.out.println("Transactions loaded successfully for account: " + accountNumber);
        } catch (IOException e) {
            System.err.println("Error loading transactions from file: " + e.getMessage());
        }
    }

    // Helper method to parse JSON and create a Transaction object
    private Transaction parseJSON(String json) {
        // Implementation to parse JSON and create a Transaction object
        // You need to implement this based on your JSON format and transaction classes
        return null;
    }
}

class Account {
    private String panNumber;
    private String accountName;
    private String accountNumber;
    private double balance;
    private int pin;

    public Account(String panNumber, String accountName, String accountNumber, double balance) {
        this.panNumber = panNumber;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.pin = generateRandomPIN(); // Generate and assign a random 4-digit PIN
        System.out.println("Your PIN is: " + pin);
        displayAccountDetails();
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }
    private int generateRandomPIN() {
        Random random = new Random();
        return 1000 + random.nextInt(9000);
    }
    public void displayAccountDetails() {
      System.out.println("Account Details:");
      System.out.println("Account Name: " + accountName);
      System.out.println("Account Number: " + accountNumber);
      System.out.println("Balance: " + balance);
  }

    public void deposit(double amount) {
      Scanner scan = new Scanner(System.in);
      System.out.println("Enter your PAN number to deposit:");
      String enteredPanNumber = scan.nextLine();
      try {
          if (enteredPanNumber.equals(panNumber)) {
                balance += amount;
                System.out.println("Deposited: " + amount + ", Current Balance: " + balance);
          } else {
                scan.close();
                throw new IncorrectPanException("Incorrect PAN number. Deposit not allowed.");
          }
      } catch (IncorrectPanException e) {
          System.out.println(e.getMessage());
      }
        scan.close();     
        balance += amount;
        System.out.println("Deposited: " + amount + ", Current Balance: " + balance);
    }

    public void withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            System.out.println("Debited: " + amount + ", Current Balance: " + balance);
        } else {
            System.out.println("Insufficient funds.");
        }
    }
    public static Account createAccount() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter PAN Number:");
        String panNumber = scan.nextLine();

        System.out.println("Enter Account Name:");
        String accountName = scan.nextLine();

        System.out.println("Enter Account Number:");
        String accountNumber = scan.nextLine();

        System.out.println("Enter Initial Balance:");
        double initialBalance = scan.nextDouble();

        // Close the scan to prevent resource leak
        scan.close();

      // Create and return a new account
      return new Account(panNumber, accountName, accountNumber, initialBalance);
  }
    // New method to check account balance
    public double checkAccountBalance() {
        return this.balance;
    }
}

class IncorrectPanException extends Exception {
    public IncorrectPanException(String message) {
        super(message);
    }
}

public class FinAssist {
  public static void main(String[] args) {
      Scanner scan = new Scanner(System.in);
      Account userAccount = null; // Initialize user account variable
        FinAssistApp app = new FinAssistApp();
      while (true) {
          // Display the menu
          System.out.println("\n===== FinAssist Menu =====");
          System.out.println("1. Create an Account");
          System.out.println("2. Check Account Balance");
          System.out.println("3. Deposit");
          System.out.println("4. Withdraw");
          System.out.println("5. Record Transaction");
          System.out.println("6. Save Transactions to File");
          System.out.println("7. Load Transactions from File");
          System.out.println("8. Exit");
          System.out.print("Enter your choice: ");

          // Read user choice
          int choice = scan.nextInt();

          switch (choice) {
              case 1:
                  // Create an account
                  userAccount = Account.createAccount();
                  break;
              case 2:
                  // Check account balance
                  userAccount.checkAccountBalance();
                  break;
              case 3:
                  // Deposit
                  if (userAccount != null) {
                      System.out.print("Enter the deposit amount: ");
                      double depositAmount = scan.nextDouble();
                      userAccount.deposit(depositAmount);
                  } else {
                      System.out.println("Account not created yet. Please create an account first.");
                  }
                  break;
              case 4:
                  // Withdraw
                  if (userAccount != null) {
                      System.out.print("Enter the withdrawal amount: ");
                      double withdrawalAmount = scan.nextDouble();
                      userAccount.withdraw(withdrawalAmount);
                  } else {
                      System.out.println("Account not created yet. Please create an account first.");
                  }
                  break;                  
              case 5:
                  // Record Transaction
                  if (userAccount != null) {
                      System.out.print("Choose Transaction Type (1. One-Time, 2. Recurring, 3. Emergency): ");
                      int transactionTypeChoice = scan.nextInt();
                      scan.nextLine(); // Consume the newline character

                      Transaction transaction = null;
                      switch (transactionTypeChoice) {
                          case 1:
                              transaction = OneTimeTransaction.createOneTimeTransaction(scan);
                              break;
                          case 2:
                              transaction = RecurringTransaction.createRecurringTransaction(scan);
                              break;
                          case 3:
                              transaction = EmergencyTransaction.createEmergencyTransaction(scan);
                              break;
                          default:
                              System.out.println("Invalid transaction type choice.");
                      }

                      if (transaction != null) {
                          app.recordTransaction(userAccount,transaction);
                      }
                  } else {
                      System.out.println("Account not created yet. Please create an account first.");
                  }
                  break;
              case 6:
                  // Save Transactions to File
                  if (userAccount != null) {
                      System.out.print("Enter the file name to save transactions: ");
                      String fileName = scan.nextLine();
                      app.saveTransactionsToFile(userAccount,fileName);
                  } else {
                      System.out.println("Account not created yet. Please create an account first.");
                  }
                  break;
              case 7:
                  // Load Transactions from File
                  if (userAccount != null) {
                      System.out.print("Enter the file name to load transactions: ");
                      String fileName = scan.nextLine();
                      app.loadTransactionsFromFile(userAccount,fileName);
                  } else {
                      System.out.println("Account not created yet. Please create an account first.");
                  }
                  break;
              case 8:
                  // Exit
                  System.out.println("Exiting the FinAssist application. Goodbye!");
                  scan.close();
                  System.exit(0);
                  break;
              default:
                  System.out.println("Invalid choice. Please enter a valid option.");

          }
      }
  }
}