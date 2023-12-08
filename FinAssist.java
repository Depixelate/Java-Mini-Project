import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.text.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.*;
import java.time.temporal.ChronoUnit;

// Enum to represent transaction types
enum TransactionType {
    ONE_TIME, RECURRING, EMERGENCY
}

// Transaction interface

record BasicTransactionData(double amount, String description, String tag, Alert alert) {
}

class NegativeAmountException extends Exception {
    public NegativeAmountException() {
        super("Negative amount not allowed!");
    }
}

class InvalidAlertTypeException extends Exception {
    public InvalidAlertTypeException() {
        super("Invalid alert type!");
    }
}

class ReadUtils {
    public static LocalDate readDate(Scanner scan, String msg) {
        while (true) {
            try {
                System.out.print(msg + " (yyyy-MM-dd): ");
                String dateStr = scan.nextLine();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateStr, dtf);
                return date;
            } catch (DateTimeParseException dtpe) {
                System.out.println("Error, not a valid date! Try again...");
            }
        }
    }

    public static BasicTransactionData readBasicTransactionData(Scanner scan, boolean allowNegative) {
        Alert alert = null;
        while (true) {
            try {
                System.out.print("Choose Alert Type (1. Mild, 2. Emergency): ");
                int alertTypeChoice = Integer.parseInt(scan.nextLine());
                switch (alertTypeChoice) {
                    case 1:
                        alert = new MildAlert();
                        break;
                    case 2:
                        alert = new EmergencyAlert();
                        break;
                    default:
                        throw new InvalidAlertTypeException();
                }
                break;
            } catch (InvalidAlertTypeException iate) {
                System.out.println(iate.getMessage());
                System.out.println("Try again...");
            }
        }

        double amount = 0;
        while (true) {
            try {
                String msg = "Enter amount";
                if (allowNegative) {
                    msg += " (Positive for deposit, Negative for withdrawal)";
                }
                System.out.print(msg + ": ");
                System.out.print("Enter amount: ");
                amount = Double.parseDouble(scan.nextLine());
                if (amount < 0 && !allowNegative) {
                    throw new NegativeAmountException();
                }
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Error, not a valid transaction value! Try again...");
            } catch (NegativeAmountException nae) {
                System.out.println(nae.getMessage());
                System.out.println("Try again...");
            }
        }

        // double amount = Double.parseDouble(scan.nextLine());
        // scan.nextLine(); // Consume the newline character

        System.out.print("Enter description: ");
        String description = scan.nextLine();

        System.out.print("Enter tag: ");
        String tag = scan.nextLine();
        if (tag.isBlank()) {
            tag = "";
        }

        return new BasicTransactionData(amount, description, tag, alert);
    }
}

class JSON {
    public static String toJsonArray(String[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append("\"").append(array[i]).append("\"");
            if (i != array.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static String[] fromJsonArray(String json) {
        return json.replace("\"", "").replace("[", "").replace("]", "").split(", ");
    }

    public static HashMap<String, String> jsonToHashMap(String json) {
        json.replace("\n", "");
        json.replace("\"", "");
        HashMap<String, String> map = new HashMap<>();
        String[] pairs = json.replace("{", "").replace("}", "").split(", (?!h)"); // The regex here is a bit hacky, used
                                                                                  // to split on commas but not commas
                                                                                  // outside square brackets
        for (String pair : pairs) {
            String[] keyValue = pair.split(": ");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }
}

interface JSONConvertable {
    String toJSON();
}

abstract class Transaction implements JSONConvertable {
    protected double amount;
    protected String description;
    protected String tag;

    // Constructor
    public Transaction(double amount, String description, String tag) {
        this.amount = amount;
        this.description = description;
        this.tag = tag;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getDescription() {
        return this.description;
    }

    public String getTag() {
        return this.tag;
    }

    public String toString() {
        return "Amount: " + amount + ", Description: " + description + ", Tag: " + tag;
    }
}

class TransactionRecord extends Transaction {
    private LocalDate date;
    private TransactionType type;

    public TransactionRecord(double amount, String description, String tag, LocalDate date, TransactionType type) {
        super(amount, description, tag);
        this.date = date;
        this.type = type;
    }

    public TransactionType getType() {
        return this.type;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public static TransactionRecord readTransactionRecord(Scanner scan) {
        BasicTransactionData btd = ReadUtils.readBasicTransactionData(scan, true);
        LocalDate date = ReadUtils.readDate(scan, "Enter transaction date: ");
        System.out.print("Choose Transaction Type (1. One Time, 2. Recurring, 3. Emergency): ");
        TransactionType type = TransactionType.valueOf(scan.nextLine().toUpperCase().replace(" ", "_"));
        TransactionRecord tr = new TransactionRecord(btd.amount(), btd.description(), btd.tag(), date, type);
        // btd.alert().setTransaction(tr);
        return tr;
    }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "{ \"objectType\": \"RECORD\", \"type\": \"" + type.toString() + "\", \"amount\":" + getAmount()
                + ", \"description\": \"" + getDescription() + "\",  \"tag\":" +
                getTag() + ", \"date\": \"" + getDate() + "\" }";
    }

    public static TransactionRecord fromJSON(HashMap<String, String> json) {
        double amount = Double.parseDouble(json.get("amount"));
        String description = json.get("description");
        String tag = json.get("tags");
        LocalDate date = LocalDate.parse(json.get("date"));
        TransactionType type = TransactionType.valueOf(json.get("type"));
        return new TransactionRecord(amount, description, tag, date, type);
    }

    public String toString() {
        return "Amount: " + amount + ", Description: " + description + ", Tag: " + tag + ", Date: " + date
                + ", Type: " + type;
    }
}

abstract class DatedTransaction extends Transaction {
    protected Alert alert;

    public DatedTransaction(double amount, String description, String tag, Alert alert) {
        super(amount, description, tag);
        this.alert = alert;
    }

    abstract LocalDate getNextDueDate();
    abstract TransactionRecord payTransaction();

    public Alert getAlert() {
        return this.alert;
    }
}

// OneTimeTransaction class implementing Transaction interface
class OneTimeTransaction extends DatedTransaction {
    LocalDate dueDate;

    // Constructor
    public OneTimeTransaction(double amount, String description, String tag, LocalDate dueDate, Alert alert) {
        super(amount, description, tag, alert);
        this.dueDate = dueDate;
    }

    public LocalDate getNextDueDate() {
        return this.dueDate;
    }

    public TransactionRecord payTransaction() {
        return new TransactionRecord(amount, description, tag, LocalDate.now(), TransactionType.ONE_TIME);
    }

    // Static method to create a OneTimeTransaction based on user input
    public static OneTimeTransaction readOneTimeTransaction(Scanner scan) {
        BasicTransactionData btd = ReadUtils.readBasicTransactionData(scan, false);
        LocalDate dueDate = ReadUtils.readDate(scan, "Enter due date");
        OneTimeTransaction ott = new OneTimeTransaction(btd.amount(), btd.description(), btd.tag(), dueDate,
                btd.alert());
        // btd.alert().setTransaction(ott);
        return ott;
    }
    // @Override
    // public void recordTransaction() {
    // // Implementation to record one-time transaction
    // }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "{ \"objectType\": \"TRANSACTION\", \"type\": \"ONE_TIME\", \"amount\":" + amount
                + ", \"description\": \"" + description + "\",  \"tag\":" +
                tag + ", \"dueDate\": \"" + dueDate + "\", \"alert\": " + getAlert().toJSON() + "}";
    }

    public static OneTimeTransaction fromJSON(HashMap<String, String> json) {
        double amount = Double.parseDouble(json.get("amount"));
        String description = json.get("description");
        String tag = json.get("tag");
        LocalDate dueDate = LocalDate.parse(json.get("dueDate"));
        Alert alert = Alert.fromJSON(JSON.jsonToHashMap(json.get("alert")));
        return new OneTimeTransaction(amount, description, tag, dueDate, alert);
    }

    public String toString() {
        return "Type: One-Time, Amount: " + amount + ", Description: " + description + ", Tag: " + tag + ", Due Date: " + dueDate;
    }
}

// RecurringTransaction class implementing Transaction interface
class RecurringTransaction extends DatedTransaction {
    private LocalDate startDate;
    private Period periodBetweenPayments;

    // Constructor
    public RecurringTransaction(double amount, String description, String tag, LocalDate startDate,
            Period periodBetweenPayments, Alert alert) {
        super(amount, description, tag, alert);
        this.startDate = startDate;
        this.periodBetweenPayments = periodBetweenPayments;
    }

    public TransactionRecord payTransaction() {
        startDate = getNextDueDate().plus(periodBetweenPayments);
        return new TransactionRecord(amount, description, tag, LocalDate.now(), TransactionType.RECURRING);
    }

    public LocalDate getNextDueDate() {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return startDate;
        }
        Period timeBetween = Period.between(startDate, today);
        int daysBetween = timeBetween.getDays();
        int daysLeft = daysBetween % periodBetweenPayments.getDays();
        return today.plusDays(daysLeft);
    }

    public static RecurringTransaction readRecurringTransaction(Scanner scan) {
        BasicTransactionData btd = ReadUtils.readBasicTransactionData(scan, false);
        LocalDate startDate = ReadUtils.readDate(scan, "Enter start date");
        int daysBetweenPayments;
        while (true) {
            try {
                System.out.print("Enter days between payments: ");
                daysBetweenPayments = Integer.parseInt(scan.nextLine());
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Error, not a valid number! Try again...");
            }
        }
        Period periodBetweenPayments = Period.ofDays(daysBetweenPayments);

        RecurringTransaction rt = new RecurringTransaction(btd.amount(), btd.description(), btd.tag(), startDate,
                periodBetweenPayments, btd.alert());
        // rt.getAlert().setTransaction(rt);
        return rt;
    }

    public String toString() {
        return "Type: Recurring, Amount: " + amount + ", Description: " + description + ", Tag: " + tag + ", Start Date: " + startDate + ", Days Between Payments: " + periodBetweenPayments.getDays() + ", Alert: " + alert;
    }

    // @Override
    // public void recordTransaction() {
    // // Implementation to record recurring transaction
    // }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "{ \"objectType\": \"TRANSACTION\", \"type\": \"RECURRING\", \"amount\":" + getAmount()
                + ", \"description\": \"" + getDescription() + "\",  \"tag\":" +
                getTag() + ", \"startDate\": \"" + startDate + "\", \"periodBetweenPayments\": \""
                + periodBetweenPayments + "\", \"alert\": " + getAlert().toJSON() + "}";
    }

    public static RecurringTransaction fromJSON(HashMap<String, String> json) {
        double amount = Double.parseDouble(json.get("amount"));
        String description = json.get("description");
        String tag = json.get("tag");
        LocalDate startDate = LocalDate.parse(json.get("startDate"));
        Period periodBetweenPayments = Period.parse(json.get("periodBetweenPayments"));
        Alert alert = Alert.fromJSON(JSON.jsonToHashMap(json.get("alert")));
        return new RecurringTransaction(amount, description, tag, startDate, periodBetweenPayments, alert);
    }
}

// EmergencyTransaction class implementing Transaction interface
// class EmergencyTransaction extends Transaction {
// private double amount;
// private String description;
// private String tag;
// private Date dueDate;

// // Constructor
// public EmergencyTransaction(double amount, String description, String tag,
// Date dueDate) {
// this.amount = amount;
// this.description = description;
// this.tag = tag;
// this.dueDate = dueDate;
// }

// // Static method to create an EmergencyTransaction based on user input
// public static EmergencyTransaction createEmergencyTransaction(Scanner scan) {
// System.out.print("Enter amount: ");
// double amount = scan.nextDouble();
// scan.nextLine(); // Consume the newline character

// System.out.print("Enter description: ");
// String description = scan.nextLine();

// System.out.print("Enter tag: ");
// String tag = scan.nextLine();

// System.out.print("Enter due date (yyyy-MM-dd): ");
// String dueDateString = scan.nextLine();

// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
// Date dueDate = null;
// try {
// dueDate = dateFormat.parse(dueDateString);
// } catch (ParseException e) {
// System.out.println("Invalid date format. Transaction creation failed.");
// return null;
// }

// return new EmergencyTransaction(amount, description, tag, dueDate);
// }

// @Override
// public void recordTransaction() {
// // Implementation to record emergency transaction
// }

// @Override
// public String toJSON() {
// // Implementation to convert to JSON format
// return "{ \"type\": \"EMERGENCY\", \"amount\": " + amount + ",
// \"description\": \"" + description + "\", \"tag\": \"" + tag + "\",
// \"dueDate\": \"" + dueDate + "\" }";
// }
// }

// Abstract class for alerts
abstract class Alert implements JSONConvertable {

    abstract void send(DatedTransaction transaction);

    public static Alert fromJSON(HashMap<String, String> json) {
        String type = json.get("type");
        switch (type) {
            case "EMERGENCY":
                return new EmergencyAlert();
            case "MILD":
                return new MildAlert();
            default:
                return null;
        }
    }
}

// Subclass for emergency alerts
class EmergencyAlert extends Alert {
    @Override
    void send(DatedTransaction transaction) {
        if(transaction.getNextDueDate().isBefore(LocalDate.now())) {
            System.out.println("EMERGENCY ALERT: You have missed your bill payment!");
            System.out.println("This payment is very important, please pay your bill ASAP!");
            return;
        }

        System.out.println("EMERGENCY ALERT: You only have "
                + ChronoUnit.DAYS.between(LocalDate.now(), transaction.getNextDueDate()) + " days to pay your bill!");
        System.out.println("This payment is very important, please pay your bill ASAP!");
        // Add any additional logic needed for emergency alerts
    }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "\"EMERGENCY\"";
    }

    public String toString() {
        return "EMERGENCY";
    }
}

// Subclass for mild alerts
class MildAlert extends Alert {

    @Override
    void send(DatedTransaction transaction) {
        if(transaction.getNextDueDate().isBefore(LocalDate.now())) {
            System.out.println("Mild Alert: You have missed your bill payment!");
            System.out.println("Please pay your bill soon.");
            return;
        }
        System.out.println("Mild Alert: You have a bill due in "
                + ChronoUnit.DAYS.between(LocalDate.now(), transaction.getNextDueDate()) + " days.");
        System.out.println("Consider paying your bill soon.");
        // Add any additional logic needed for mild alerts
    }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "\"MILD\"";
    }

    public String toString() {
        return "MILD";
    }
}

// Comparator classes for sorting transactions
class ValueComparator implements Comparator<TransactionRecord> {
    @Override
    public int compare(TransactionRecord t1, TransactionRecord t2) {
        return Double.compare(t1.getAmount(), t2.getAmount());
    }
}

class DateComparator implements Comparator<TransactionRecord> {
    @Override
    public int compare(TransactionRecord t1, TransactionRecord t2) {
        return t1.getDate().compareTo(t2.getDate());
    }
}

class TagComparator implements Comparator<TransactionRecord> {
    @Override
    public int compare(TransactionRecord t1, TransactionRecord t2) {
        return t1.getTag().compareTo(t2.getTag());
    }
}

class DatedTransactionComparator implements Comparator<DatedTransaction> {
    @Override
    public int compare(DatedTransaction t1, DatedTransaction t2) {
        return t1.getNextDueDate().compareTo(t2.getNextDueDate());
    }
}

class QuickSort {
    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        quickSort(list, 0, list.size() - 1, comparator);
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<? super T> comparator) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, comparator);
            quickSort(list, low, pivotIndex - 1, comparator);
            quickSort(list, pivotIndex + 1, high, comparator);
        }
    }

    private static <T> int partition(List<T> list, int low, int high, Comparator<? super T> comparator) {
        T pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) < 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }
}

// Interface for JSON conversion

// Exception class for custom exceptions
// class FinAssistException extends Exception {
// public FinAssistException(String message) {
// super(message);
// }
// }

// class FinAssistApp {
// private Map<String, List<Transaction>> accountsTransactions;

// // Constructor
// public FinAssistApp() {
// this.accountsTransactions = new HashMap<>();
// }

// // Method to record a transaction for a specific account
// public void recordTransaction(Account account, Transaction transaction) {
// String accountNumber = account.getAccountNumber();

// // If the account is not found, create a new list for this account
// accountsTransactions.computeIfAbsent(accountNumber, k -> new
// ArrayList<>()).add(transaction);

// transaction.recordTransaction();
// account.displayAccountDetails(); // Display updated account details
// System.out.println("Transaction recorded successfully.");
// }

// // Method to save transactions to a file for a specific account
// public void saveTransactionsToFile(Account account, String fileName) {
// String accountNumber = account.getAccountNumber();
// List<Transaction> accountTransactions =
// accountsTransactions.get(accountNumber);

// if (accountTransactions != null) {
// try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
// for (Transaction transaction : accountTransactions) {
// writer.println(transaction.toJSON());
// }
// System.out.println("Transactions saved successfully for account: " +
// accountNumber);
// } catch (IOException e) {
// System.err.println("Error saving transactions to file: " + e.getMessage());
// }
// } else {
// System.out.println("Account not found. Transactions not saved.");
// }
// }

// // Method to load transactions from a file for a specific account
// public void loadTransactionsFromFile(Account account, String fileName) {
// String accountNumber = account.getAccountNumber();

// try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
// String line;
// while ((line = reader.readLine()) != null) {
// // Assuming each line in the file represents a JSON-formatted transaction
// Transaction transaction = parseJSON(line);
// if (transaction != null) {
// accountsTransactions.computeIfAbsent(accountNumber, k -> new
// ArrayList<>()).add(transaction);
// }
// }
// System.out.println("Transactions loaded successfully for account: " +
// accountNumber);
// } catch (IOException e) {
// System.err.println("Error loading transactions from file: " +
// e.getMessage());
// }
// }

// // Helper method to parse JSON and create a Transaction object
// private Transaction parseJSON(String json) {
// // Implementation to parse JSON and create a Transaction object
// // You need to implement this based on your JSON format and transaction
// classes
// return null;
// }
// }

// class Account {
// private String panNumber;
// private String accountName;
// private String accountNumber;
// private double balance;
// private int pin;

// public Account(String panNumber, String accountName, String accountNumber,
// double balance) {
// this.panNumber = panNumber;
// this.accountName = accountName;
// this.accountNumber = accountNumber;
// this.balance = balance;
// this.pin = generateRandomPIN(); // Generate and assign a random 4-digit PIN
// System.out.println("Your PIN is: " + pin);
// displayAccountDetails();
// }

// public String getAccountNumber() {
// return this.accountNumber;
// }

// private int generateRandomPIN() {
// Random random = new Random();
// return 1000 + random.nextInt(9000);
// }

// public void displayAccountDetails() {
// System.out.println("Account Details:");
// System.out.println("Account Name: " + accountName);
// System.out.println("Account Number: " + accountNumber);
// System.out.println("Balance: " + balance);
// }

// public void deposit(double amount) {
// Scanner scan = new Scanner(System.in);
// System.out.println("Enter your PAN number to deposit:");
// String enteredPanNumber = scan.nextLine();
// try {
// if (enteredPanNumber.equals(panNumber)) {
// balance += amount;
// System.out.println("Deposited: " + amount + ", Current Balance: " + balance);
// } else {
// scan.close();
// throw new IncorrectPanException("Incorrect PAN number. Deposit not
// allowed.");
// }
// } catch (IncorrectPanException e) {
// System.out.println(e.getMessage());
// }
// scan.close();
// balance += amount;
// System.out.println("Deposited: " + amount + ", Current Balance: " + balance);
// }

// public void withdraw(double amount) {
// if (balance >= amount) {
// balance -= amount;
// System.out.println("Debited: " + amount + ", Current Balance: " + balance);
// } else {
// System.out.println("Insufficient funds.");
// }
// }

// public static Account createAccount() {
// Scanner scan = new Scanner(System.in);

// System.out.println("Enter PAN Number:");
// String panNumber = scan.nextLine();

// System.out.println("Enter Account Name:");
// String accountName = scan.nextLine();

// System.out.println("Enter Account Number:");
// String accountNumber = scan.nextLine();

// System.out.println("Enter Initial Balance:");
// double initialBalance = scan.nextDouble();

// // Close the scan to prevent resource leak
// scan.close();

// // Create and return a new account
// return new Account(panNumber, accountName, accountNumber, initialBalance);
// }

// // New method to check account balance
// public double checkAccountBalance() {
// return this.balance;
// }
// }

// class IncorrectPanException extends Exception {
// public IncorrectPanException(String message) {
// super(message);
// }
// }

public class FinAssist {
    public static Map<Integer, List<TransactionRecord>> groupByYear(List<TransactionRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(record -> record.getDate().getYear()));
    }

    // Group by Month
    public static Map<Integer, List<TransactionRecord>> groupByMonth(List<TransactionRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(record -> record.getDate().getMonthValue()));
    }

    // Group by Day
    public static Map<Integer, List<TransactionRecord>> groupByDay(List<TransactionRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(record -> record.getDate().getDayOfMonth()));
    }

    public static ArrayList<TransactionRecord> readTransactionRecords() throws IOException {
        File records = new File("records.txt");
        ArrayList<TransactionRecord> transactionRecords = new ArrayList<>();
        if (!records.exists()) {
            return transactionRecords;
        }
        BufferedReader reader = new BufferedReader(new FileReader(records));

        String line;
        while ((line = reader.readLine()) != null) {
            transactionRecords.add(TransactionRecord.fromJSON(JSON.jsonToHashMap(line)));
        }
        reader.close();
        return transactionRecords;
    }

    public static void writeTransactionRecords(ArrayList<TransactionRecord> transactionRecords) throws IOException {
        File records = new File("records.txt");
        PrintWriter writer = new PrintWriter(new FileWriter(records));
        for (TransactionRecord transactionRecord : transactionRecords) {
            writer.println(transactionRecord.toJSON());
        }
        writer.close();
    }

    public static ArrayList<DatedTransaction> readDatedTransactions() throws IOException {
        File transactions = new File("datedTransactions.txt");
        ArrayList<DatedTransaction> datedTransactions = new ArrayList<>();
        if (!transactions.exists()) {
            return datedTransactions;
        }
        BufferedReader reader = new BufferedReader(new FileReader(transactions));

        String line;
        while ((line = reader.readLine()) != null) {
            HashMap<String, String> json = JSON.jsonToHashMap(line);
            String objectType = json.get("type");
            switch (objectType) {
                case "ONE_TIME":
                    datedTransactions.add(OneTimeTransaction.fromJSON(json));
                    break;
                case "RECURRING":
                    datedTransactions.add(RecurringTransaction.fromJSON(json));
                    break;
            }
        }
        reader.close();

        return datedTransactions;
    }

    public static void writeDatedTransactions(ArrayList<DatedTransaction> datedTransactions) throws IOException {
        File datedTransactionFile = new File("datedTransactions.txt");
        PrintWriter writer = new PrintWriter(new FileWriter(datedTransactionFile));
        for (DatedTransaction datedTransaction : datedTransactions) {
            writer.println(datedTransaction.toJSON());
        }
        writer.close();
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ArrayList<TransactionRecord> transactionRecords = null;
        ArrayList<DatedTransaction> datedTransactions = null;
        try {
            transactionRecords = readTransactionRecords();
            datedTransactions = readDatedTransactions();
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
            System.out.println("Exiting...");
            System.exit(1);
        }

        // Account userAccount = null; // Initialize user account variable
        // FinAssistApp app = new FinAssistApp();
        while (true) {

            // Display the menu
            System.out.println("\n===== FinAssist Menu =====");
            // System.out.println("1. Create an Account");
            // System.out.println("2. Check Account Balance");
            // System.out.println("3. Deposit");
            // System.out.println("4. Withdraw");
            System.out.println("1. Record Transaction");
            System.out.println("2. View Transactions");
            System.out.println("3. View/Add Scheduled Transactions");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            // Read user choice
            int choice = Integer.parseInt(scan.nextLine());

            switch (choice) {
                case 1:
                    // Create an account
                    TransactionRecord tr = TransactionRecord.readTransactionRecord(scan);
                    System.out.println("Transaction Recorded!");
                    transactionRecords.add(tr);
                case 2:
                    System.out.println("1. View transactions by Date");
                    System.out.println("2. View transactions by Tag");
                    System.out.println("3. View transactions by Amount");
                    System.out.println("4. View transactions by Transaction Type");
                    System.out.print("Enter your choice: ");
                    int viewChoice = Integer.parseInt(scan.nextLine());

                    switch (viewChoice) {
                        case 1:
                            LocalDate startDate = ReadUtils.readDate(scan, "Enter start date");
                            LocalDate endDate = ReadUtils.readDate(scan, "Enter end date");
                            List<TransactionRecord> records = transactionRecords.stream()
                                    .filter(record -> record.getDate().isAfter(startDate)
                                            && record.getDate().isBefore(endDate))
                                    .collect(Collectors.toList());
                            QuickSort.sort(records, new DateComparator());
                            for (TransactionRecord record : records) {
                                System.out.println(record);
                            }
                            break;
                        case 2:
                            System.out.println("Enter tags to filter by [separated by tab]: ");
                            String[] tags = scan.nextLine().split("\t");
                            final String[] newTags = Arrays.stream(tags).filter(tag -> !tag.isBlank()).toArray(String[]::new);
                            records = transactionRecords.stream().filter(record -> {
                                for (String tag : newTags) {
                                    if (record.getTag().equals(tag)) {
                                        return true;
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                            QuickSort.sort(records, new TagComparator());
                            for (TransactionRecord record : records) {
                                System.out.println(record);
                            }
                            // Call method to view transactions by Tag
                            break;
                        case 3:
                            System.out.println("Enter minimum amount: ");
                            double minAmount = Double.parseDouble(scan.nextLine());
                            System.out.println("Enter maximum amount: ");
                            double maxAmount = Double.parseDouble(scan.nextLine());
                            records = transactionRecords.stream()
                                    .filter(record -> record.getAmount() >= minAmount
                                            && record.getAmount() <= maxAmount)
                                    .collect(Collectors.toList());
                            QuickSort.sort(records, new ValueComparator());
                            for (TransactionRecord record : records) {
                                System.out.println(record);
                            }
                            // Call method to view transactions by Amount
                            break;
                        case 4:
                            System.out.println("Enter transaction type: ");
                            String type = scan.nextLine();
                            records = transactionRecords.stream()
                                    .filter(record -> record.getType().equals(TransactionType.valueOf(type.toUpperCase().replace(" ", "_"))))
                                    .collect(Collectors.toList());
                            for (TransactionRecord record : records) {
                                System.out.println(record);
                            }
                            // Call method to view transactions by Transaction Type
                            break;
                    }
                    break;
                    
                case 3:
                    for(int i = 0; i < datedTransactions.size(); i++) {
                        System.out.println(i + 1 + ". " + datedTransactions.get(i));
                    }

                    System.out.println();
                    System.out.println("1. Add One-Time Transaction");
                    System.out.println("2. Add Recurring Transaction");
                    System.out.println("3. Delete Transaction");
                    System.out.println("4. Pay Transaction");

                    System.out.print("Enter your choice: ");
                    int scheduledChoice = Integer.parseInt(scan.nextLine());
                    switch(scheduledChoice) {
                        case 1:
                            OneTimeTransaction ott = OneTimeTransaction.readOneTimeTransaction(scan);
                            datedTransactions.add(ott);
                            break;
                        case 2:
                            RecurringTransaction rt = RecurringTransaction.readRecurringTransaction(scan);
                            datedTransactions.add(rt);
                            break;
                        case 3:
                            System.out.print("Enter the index of the transaction to delete: ");
                            int index = Integer.parseInt(scan.nextLine());
                            datedTransactions.remove(index-1);
                            break;
                        case 4:
                            System.out.print("Enter the index of the transaction to pay: ");
                            index = Integer.parseInt(scan.nextLine());
                            TransactionRecord record = datedTransactions.get(index-1).payTransaction();
                            transactionRecords.add(record);
                            if(record.getType() == TransactionType.ONE_TIME) {
                                datedTransactions.remove(index-1);
                            }
                            break;
                    }
                    System.out.println("Transaction Recorded!");
                    break;
                case 4:
                    System.out.println("Exiting...");
                    try {
                        writeTransactionRecords(transactionRecords);
                        writeDatedTransactions(datedTransactions);
                    } catch (IOException e) {
                        System.out.println("Error writing to file: " + e.getMessage());
                    }
                    System.exit(0);
                    break;
                // case 5:
                //     // Record Transaction
                //     if (userAccount != null) {
                //         System.out.print("Choose Transaction Type (1. One-Time, 2. Recurring, 3. Emergency): ");
                //         int transactionTypeChoice = scan.nextInt();
                //         scan.nextLine(); // Consume the newline character

                //         Transaction transaction = null;
                //         switch (transactionTypeChoice) {
                //             case 1:
                //                 transaction = OneTimeTransaction.readOneTimeTransaction(scan);
                //                 break;
                //             case 2:
                //                 transaction = RecurringTransaction.createRecurringTransaction(scan);
                //                 break;
                //             case 3:
                //                 transaction = EmergencyTransaction.createEmergencyTransaction(scan);
                //                 break;
                //             default:
                //                 System.out.println("Invalid transaction type choice.");
                //         }

                //         if (transaction != null) {
                //             app.recordTransaction(userAccount, transaction);
                //         }
                //     } else {
                //         System.out.println("Account not created yet. Please create an account first.");
                //     }
                //     break;
                // case 6:
                //     // Save Transactions to File
                //     if (userAccount != null) {
                //         System.out.print("Enter the file name to save transactions: ");
                //         String fileName = scan.nextLine();
                //         app.saveTransactionsToFile(userAccount, fileName);
                //     } else {
                //         System.out.println("Account not created yet. Please create an account first.");
                //     }
                //     break;
                // case 7:
                //     // Load Transactions from File
                //     if (userAccount != null) {
                //         System.out.print("Enter the file name to load transactions: ");
                //         String fileName = scan.nextLine();
                //         app.loadTransactionsFromFile(userAccount, fileName);
                //     } else {
                //         System.out.println("Account not created yet. Please create an account first.");
                //     }
                //     break;
                // case 8:
                //     // Exit
                //     System.out.println("Exiting the FinAssist application. Goodbye!");
                //     scan.close();
                //     System.exit(0);
                //     break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");

            }
        }
    }
}