package utils;

public class NegativeAmountException extends Exception {
    public NegativeAmountException() {
        super("Negative amount not allowed!");
    }
}
