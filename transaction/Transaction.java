package transaction;

import json.JSONConvertable;
import java.util.LinkedHashMap;
import utils.ReadUtils;
import java.util.Scanner;
import java.util.Comparator;
import transaction.comparator.ValueComparator;
import transaction.comparator.TagComparator;
import java.util.Arrays;

public abstract class Transaction implements JSONConvertable {
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

    // public static LinkedHashMap<String, FilterView.ApplyFilter<? extends Transaction>> getFilterMap(Scanner scanner) {
    //     LinkedHashMap<String, FilterView.ApplyFilter<? extends Transaction>> filterMap = new LinkedHashMap<>();
    //     filterMap.put("Reverse", (filterView) -> {
    //         filterView.reverse();
    //     });
    //     filterMap.put("Filter By Amount", (filterView) -> {
    //         double min = ReadUtils.readDouble(scanner, "Enter Minimum Amount(Negative for Withdrawals, Positive for Deposits): ");
    //         double max = ReadUtils.readDouble(scanner, "Enter Maximum Amount(Negative for Withdrawals, Positive for Deposits): ");
    //         filterView.apply((transaction) -> {
    //             return transaction.getAmount() >= min && transaction.getAmount() <= max;
    //         });
    //         filterView.sort(new ValueComparator());
    //     });

    //     filterMap.put("Filter By Description", (filterView) -> {
    //         System.out.print("Enter description to filter by: ");
    //         String description = scanner.nextLine().trim().toLowerCase();
    //         filterView.apply((transaction) -> {
    //             return transaction.getDescription().toLowerCase().contains(description);
    //         });
    //     });

    //     filterMap.put("Filter By Tags", (filterView) -> {
    //         System.out.print("Enter tags to filter by [separated by TAB]: ");
    //         String[] tags = scanner.nextLine().trim().toLowerCase().split("\t");
    //         final String[] newTags = Arrays.stream(tags).filter(tag -> !tag.isBlank())
    //                                 .map(tag -> tag.trim()).toArray(String[]::new);
    //         filterView.apply(record -> {
    //             for (String tag : newTags) {
    //                 if (record.getTag().equals(tag)) {
    //                     return true;
    //                 }
    //             }
    //             return false;
    //         });
    //         filterView.sort(new TagComparator());
    //     });

    //     return filterMap;
    // }
}
