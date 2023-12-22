package transaction;
import alert.Alert;
import utils.ReadUtils;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Scanner;

public abstract class DatedTransaction extends Transaction {
    protected Alert alert;

    public DatedTransaction(double amount, String description, String tag, Alert alert) {
        super(amount, description, tag);
        this.alert = alert;
    }

    abstract public LocalDate getNextDueDate();

    abstract public TransactionRecord payTransaction();

    public Alert getAlert() {
        return this.alert;
    }

    // public static LinkedHashMap<String, FilterView.ApplyFilter<? extends Transaction>> getFilterMap(Scanner scanner) {
    //     LinkedHashMap<String, FilterView.ApplyFilter<? extends Transaction>> filterMap = Transaction.getFilterMap(scanner);
    //     filterMap.put("Sort By Next Due Date", (filterView) -> {
    //         LocalDate min = ReadUtils.readDate(scanner, "Enter Minimum Due Date: ");
    //         LocalDate max = ReadUtils.readDate(scanner, "Enter Maximum Due Date: ");
    //         filterView.apply((transaction) -> {
    //             return transaction instanceof DatedTransaction && ((DatedTransaction) transaction).getNextDueDate().compareTo(min) >= 0 && ((DatedTransaction) transaction).getNextDueDate().compareTo(max) <= 0;
    //         });
    //     });
    //     return filterMap;
    // } 
}
