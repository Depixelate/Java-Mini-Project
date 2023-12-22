package transaction;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Scanner;
import utils.ReadUtils;
import alert.Alert;

public class OneTimeTransaction extends DatedTransaction {
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
        return new TransactionRecord(-amount, description, tag, LocalDate.now(), TransactionType.ONE_TIME);
    }

    // Static method to create a OneTimeTransaction based on user input
    public static OneTimeTransaction readOneTimeTransaction(Scanner scan) {
        BasicTransactionData btd = ReadUtils.readBasicTransactionData(scan, false);
        LocalDate dueDate = ReadUtils.readDate(scan, "Enter due date");
        var alert = ReadUtils.readAlert(scan);
        OneTimeTransaction ott = new OneTimeTransaction(btd.amount(), btd.description(), btd.tag(), dueDate,
                alert);
        // btd.alert().setTransaction(ott);
        return ott;
    }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "{ \"objectType\": \"TRANSACTION\", \"type\": \"ONE_TIME\", \"amount\": " + amount
                + ", \"description\": \"" + description + "\", \"tag\": " +
                "\"" + getTag() + "\"" + ", \"dueDate\": \"" + dueDate + "\", \"alert\": " + getAlert().toJSON() + "}";
    }

    public static OneTimeTransaction fromJSON(HashMap<String, String> json) {
        double amount = Double.parseDouble(json.get("amount"));
        String description = json.get("description");
        String tag = json.get("tag");
        LocalDate dueDate = LocalDate.parse(json.get("dueDate"));
        Alert alert = Alert.fromJSON(json.get("alert"));
        return new OneTimeTransaction(amount, description, tag, dueDate, alert);
    }

    public String toString() {
        return "Type: One-Time, Amount: " + amount + ", Description: " + description + ", Tag: " + tag + ", Due Date: "
                + dueDate;
    }
}
