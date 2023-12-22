package transaction;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;
import transaction.comparator.DateComparator;
import utils.ReadUtils;

public class TransactionRecord extends Transaction {
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
        LocalDate date = ReadUtils.readDate(scan, "Enter transaction date");
        TransactionType type = null;
        while (true) {
            try {
                System.out.print("Choose Transaction Type (One Time, Recurring, Emergency): ");
                type = TransactionType.valueOf(scan.nextLine().trim().toUpperCase().replace(" ", "_"));
                break;
            } catch (Exception e) {
                System.out.println("Invalid transaction type! Try again...");
            }
        }
        TransactionRecord tr = new TransactionRecord(btd.amount(), btd.description(), btd.tag(), date, type);
        // btd.alert().setTransaction(tr);
        return tr;
    }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "{ \"objectType\": \"RECORD\", \"type\": \"" + type.toString() + "\", \"amount\": " + getAmount()
                + ", \"description\": \"" + getDescription() + "\", \"tag\": " +
                "\"" + getTag() + "\"" + ", \"date\": \"" + getDate() + "\" }";
    }

    public static TransactionRecord fromJSON(HashMap<String, String> json) {
        double amount = Double.parseDouble(json.get("amount"));
        String description = json.get("description");
        String tag = json.get("tag");
        LocalDate date = LocalDate.parse(json.get("date"));
        TransactionType type = TransactionType.valueOf(json.get("type"));
        return new TransactionRecord(amount, description, tag, date, type);
    }

    public String toString() {
        return "Amount: " + amount + ", Description: " + description + ", Tag: " + tag + ", Date: " + date
                + ", Type: " + type.toString().replace("_", " ");
    }

    // public static LinkedHashMap<String, FilterView.ApplyFilter<? extends Transaction>> getFilterMap(Scanner scanner) {
    //     LinkedHashMap<String, FilterView.ApplyFilter<? extends Transaction>> filterMap = Transaction.getFilterMap(scanner);
    //     filterMap.put("Filter By Date", (FilterView<? extends TransactionRecord> filterView) -> {
    //         LocalDate startDate = ReadUtils.readDate(scanner,
    //                             "Enter start date(or nothing for no start date)", true);
    //         LocalDate endDate = ReadUtils.readDate(scanner, "Enter end date(or nothing for no end date)",
    //                             true);
                        
    //         if (startDate != null) {
    //             filterView.apply(record -> record.getDate().compareTo(startDate) >= 0);
    //         }
    //         if (endDate != null) {
    //             filterView.apply(record -> record.getDate().compareTo(endDate) <= 0);
    //         }
    //         filterView.sort(new DateComparator());
    //     });

    //     filterMap.put("Filter By Transaction Type", (FilterView<? extends TransactionRecord> filterView) -> {
    //         while(true) {
    //             try {
    //                 System.out.print("Enter Transaction Type(One Time, Recurring, Emergency): ");
    //                 String typeStr = scanner.nextLine().trim();
    //                 TransactionType type = TransactionType.valueOf(typeStr.toUpperCase().replace(" ", "_"));
    //                 filterView.apply(record -> record.getType().equals(type));
    //                 break;
    //             } catch(Exception e) {
    //                 System.out.println("Invalid transaction type! Try again...");
    //             }
    //         }
    //     });

    //     return filterMap;
    // }
}

