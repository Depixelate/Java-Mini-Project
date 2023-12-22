package transaction;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Scanner;
import alert.Alert;
import utils.ReadUtils;
import utils.InvalidInputException;

public class RecurringTransaction extends DatedTransaction {
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
        return new TransactionRecord(-amount, description, tag, LocalDate.now(), TransactionType.RECURRING);
    }

    public LocalDate getNextDueDate() {
        LocalDate today = LocalDate.now();
        if (today.compareTo(startDate) <= 0) {
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
                if(daysBetweenPayments <= 0) {
                    throw new InvalidInputException();
                }
                break;
            } catch (Exception e) {
                System.out.println("Error, not a valid number of days between payments! Try again...");
            }
        }
        Period periodBetweenPayments = Period.ofDays(daysBetweenPayments);
        Alert alert = ReadUtils.readAlert(scan);
        RecurringTransaction rt = new RecurringTransaction(btd.amount(), btd.description(), btd.tag(), startDate,
                periodBetweenPayments, alert);

        return rt;
    }

    public String toString() {
        return "Type: Recurring, Amount: " + amount + ", Description: " + description + ", Tag: " + tag
                + ", Next Date Due: " + getNextDueDate() + ", Days Between Payments: " + periodBetweenPayments.getDays()
                + ", Alert: " + alert;
    }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "{ \"objectType\": \"TRANSACTION\", \"type\": \"RECURRING\", \"amount\": " + getAmount()
                + ", \"description\": \"" + getDescription() + "\", \"tag\": " +
                "\"" + getTag() + "\"" + ", \"startDate\": \"" + startDate + "\", \"periodBetweenPayments\": \""
                + periodBetweenPayments + "\", \"alert\": " + getAlert().toJSON() + "}";
    }

    public static RecurringTransaction fromJSON(HashMap<String, String> json) {
        double amount = Double.parseDouble(json.get("amount"));
        String description = json.get("description");
        String tag = json.get("tag");
        LocalDate startDate = LocalDate.parse(json.get("startDate"));
        Period periodBetweenPayments = Period.parse(json.get("periodBetweenPayments"));
        Alert alert = Alert.fromJSON(json.get("alert"));
        return new RecurringTransaction(amount, description, tag, startDate, periodBetweenPayments, alert);
    }
}
