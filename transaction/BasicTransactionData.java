package transaction;

public class BasicTransactionData {
    private double amount;
    private String description;
    private String tag;

    public BasicTransactionData(double amount, String description, String tag) {
        this.amount = amount;
        this.description = description;
        this.tag = tag;
    }

    public double amount() {
        return this.amount;
    }

    public String description() {
        return this.description;
    }

    public String tag() {
        return this.tag;
    }
}
