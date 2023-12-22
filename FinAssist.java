import java.util.*;
import java.util.stream.Collectors;

import json.JSON;
import utils.ReadUtils;

import java.io.*;
import java.time.*;

import transaction.DatedTransaction;
import transaction.OneTimeTransaction;
import transaction.RecurringTransaction;
import transaction.TransactionRecord;
import transaction.TransactionType;
import transaction.comparator.DateComparator;
import transaction.comparator.DatedTransactionComparator;
import transaction.comparator.TagComparator;
import transaction.comparator.ValueComparator;
import utils.InvalidInputException;
import utils.QuickSort;

public class FinAssist {

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

    public static void writeJSON(TransactionRecord[] transactionRecords) throws IOException {
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

    public static void writeJSON(DatedTransaction[] datedTransactions) throws IOException {
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

        System.out.println("ALERTS: ");
        for (DatedTransaction datedTransaction : datedTransactions) {
            datedTransaction.getAlert().send(datedTransaction);
        }

        while (true) {

            int choice = -1;
            while (true) {
                try {
                    System.out.println("\n===== FinAssist Menu =====");
                    System.out.println("1. Record Transaction");
                    System.out.println("2. View Transactions");
                    System.out.println("3. Manage Scheduled Transactions");
                    System.out.println("4. Exit");
                    System.out.print("Enter your choice: ");

                    // Read user choice
                    choice = Integer.parseInt(scan.nextLine());
                    if (choice < 1 || choice > 4) {
                        throw new InvalidInputException();
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid choice! Try again...");
                }
            }
            // Display the menu

            switch (choice) {
                case 1:
                    // Create an account
                    TransactionRecord tr = TransactionRecord.readTransactionRecord(scan);
                    System.out.println("Transaction Recorded!");
                    transactionRecords.add(tr);
                    break;
                case 2:
                    int viewChoice = -1;
                    while (true) {
                        try {
                            System.out.println("1. View transactions by Date");
                            System.out.println("2. View transactions by Tag");
                            System.out.println("3. View transactions by Amount");
                            System.out.println("4. View transactions by Transaction Type");
                            System.out.print("Enter your choice: ");
                            viewChoice = Integer.parseInt(scan.nextLine());
                            if (viewChoice < 1 || viewChoice > 4) {
                                throw new InvalidInputException();
                            }
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid choice ! Try again...");
                        }
                    }

                    switch (viewChoice) {
                        case 1:
                            LocalDate startDate = ReadUtils.readDate(scan,
                                    "Enter start date(or nothing for no start date)", true);
                            LocalDate endDate = ReadUtils.readDate(scan, "Enter end date(or nothing for no end date)",
                                    true);
                            var stream = transactionRecords.stream();
                            if (startDate != null) {
                                stream = stream.filter(record -> record.getDate().compareTo(startDate) >= 0);
                            }
                            if (endDate != null) {
                                stream = stream.filter(record -> record.getDate().compareTo(endDate) <= 0);
                            }

                            List<TransactionRecord> records = stream.collect(Collectors.toList());
                            QuickSort.sort(records, new DateComparator());
                            System.out.println("\nTRANSACTIONS: ");
                            for (TransactionRecord record : records) {
                                System.out.println(record);
                            }
                            break;
                        case 2:
                            System.out.print("Enter tags to filter by [separated by TAB]: ");
                            String[] tags = scan.nextLine().trim().toLowerCase().split("\t");
                            final String[] newTags = Arrays.stream(tags).filter(tag -> !tag.isBlank())
                                    .map(tag -> tag.trim()).toArray(String[]::new);
                            records = transactionRecords.stream().filter(record -> {
                                for (String tag : newTags) {
                                    if (record.getTag().equals(tag)) {
                                        return true;
                                    }
                                }
                                return false;
                            }).collect(Collectors.toList());
                            QuickSort.sort(records, new TagComparator());
                            System.out.println("\nTRANSACTIONS: ");
                            for (TransactionRecord record : records) {
                                System.out.println(record);
                            }
                            break;
                        case 3:
                            double minAmount = ReadUtils.readDouble(scan, "Enter Minimum Amount(Negative for Withdrawals, Positive for Deposits): ");
                            double maxAmount = ReadUtils.readDouble(scan, "Enter Maximum Amount(Negative for Withdrawals, Positive for Deposits): ");
                            records = transactionRecords.stream()
                                    .filter(record -> record.getAmount() >= minAmount
                                            && record.getAmount() <= maxAmount)
                                    .collect(Collectors.toList());
                            QuickSort.sort(records, new ValueComparator());
                            System.out.println("\nTRANSACTIONS: ");
                            for (TransactionRecord record : records) {
                                System.out.println(record);
                            }
                            break;
                        case 4:
                            TransactionType type = null;
                            while(true) {
                                try {
                                    System.out.print("Enter Transaction Type(One Time, Recurring, Emergency): ");
                                    String typeStr = scan.nextLine().trim();
                                    type = TransactionType.valueOf(typeStr.toUpperCase().replace(" ", "_"));
                                    break;
                                } catch(Exception e) {
                                    System.out.println("Invalid transaction type! Try again...");
                                }
                            }

                            final TransactionType newType = type;
                            
                            records = transactionRecords.stream()
                                            .filter(record -> record.getType()
                                                    .equals(newType))
                                            .collect(Collectors.toList());
                            
                            System.out.println("\nTRANSACTIONS: ");
                            for (TransactionRecord record : records) {
                                System.out.println(record);
                            }
                            break;
                    }
                    break;

                case 3:
                    System.out.println("\nSCHEDULED TRANSACTIONS: ");
                    datedTransactions.sort(new DatedTransactionComparator());
                    for (int i = 0; i < datedTransactions.size(); i++) {
                        System.out.println(i + 1 + ". " + datedTransactions.get(i));
                    }

                    int scheduledChoice = -1;
                    while (true) {
                        try {
                            System.out.println();
                            System.out.println("1. Add One-Time Transaction");
                            System.out.println("2. Add Recurring Transaction");
                            System.out.println("3. Delete Transaction");
                            System.out.println("4. Pay Transaction");
                            System.out.println("5. Exit");

                            System.out.print("Enter your choice: ");
                            scheduledChoice = Integer.parseInt(scan.nextLine());
                            if (scheduledChoice < 1 || scheduledChoice > 5) {
                                throw new InvalidInputException();
                            }
                            break;
                        } catch (Exception e) {
                            System.out.println("Invalid choice! Try again...");
                        }
                    }

                    switch (scheduledChoice) {
                        case 1:
                            OneTimeTransaction ott = OneTimeTransaction.readOneTimeTransaction(scan);
                            datedTransactions.add(ott);
                            break;
                        case 2:
                            RecurringTransaction rt = RecurringTransaction.readRecurringTransaction(scan);
                            datedTransactions.add(rt);
                            break;
                        case 3:
                            int index = ReadUtils.readInt(scan, "Enter the index of the transaction to delete: ", 1, datedTransactions.size());
                            datedTransactions.remove(index - 1);
                            break;
                        case 4:
                            index = ReadUtils.readInt(scan, "Enter the index of the transaction to pay: ", 1, datedTransactions.size());
                            TransactionRecord record = datedTransactions.get(index - 1).payTransaction();
                            transactionRecords.add(record);
                            if (record.getType() == TransactionType.ONE_TIME) {
                                datedTransactions.remove(index - 1);
                            }
                            break;
                        case 5:
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter a valid option.");
                    }
                    break;
                case 4:
                    System.out.println("Exiting...");
                    try {
                        writeJSON(transactionRecords.toArray(TransactionRecord[]::new));
                        writeJSON(datedTransactions.toArray(DatedTransaction[]::new));
                    } catch (IOException e) {
                        System.out.println("Error writing to file: " + e.getMessage());
                    }
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");

            }
        }
    }
}