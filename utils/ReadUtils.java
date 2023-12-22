package utils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import alert.Alert;
import alert.EmergencyAlert;
import alert.MildAlert;
import transaction.BasicTransactionData;

public class ReadUtils {
    public static Alert readAlert(Scanner scan) {
        Alert alert = null;
        while (true) {
            try {
                int alertTypeChoice = readInt(scan, "Choose Alert Type (1. Mild, 2. Emergency): ", 1, 2);
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

        return alert;
    }

    public static LocalDate readDate(Scanner scan, String msg, boolean allowEmpty) {
        while (true) {
            try {
                System.out.print(msg + " (yyyy-MM-dd): ");
                String dateStr = scan.nextLine().trim();
                if (dateStr.isBlank() && allowEmpty) {
                    return null;
                }
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateStr, dtf);
                return date;
            } catch (DateTimeParseException dtpe) {
                System.out.println("Error, not a valid date! Try again...");
            }
        }
    }

    public static LocalDate readDate(Scanner scan, String msg) {
        return readDate(scan, msg, false);
    }

    public static double readDouble(Scanner scan, String msg) {
        while (true) {
            try {
                System.out.print(msg);
                double input = Double.parseDouble(scan.nextLine());
                return input;
            } catch (NumberFormatException nfe) {
                System.out.println("Error, not a valid number! Try again...");
            }
        }
    }

    public static int readInt(Scanner scan, String msg, int min, int max) {
        while (true) {
            try {
                System.out.print(msg);
                int input = Integer.parseInt(scan.nextLine());
                if (input < min || input > max) {
                    throw new InvalidInputException();
                }
                return input;
            } catch (Exception e) {
                System.out.println("Error, not a valid input! Try again...");
            }
        }
    }

    public static BasicTransactionData readBasicTransactionData(Scanner scan, boolean allowNegative) {
        double amount = 0;
        while (true) {
            try {
                String msg = "Enter amount";
                if (allowNegative) {
                    msg += " (Positive for deposit, Negative for withdrawal)";
                }
                System.out.print(msg + ": ");
                // System.omut.print("Enter amount: ");
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

        System.out.print("Enter description: ");
        String description = scan.nextLine().trim();

        System.out.print("Enter tag: ");
        String tag = scan.nextLine().trim().toLowerCase();
        if (tag.isBlank()) {
            tag = "";
        }

        return new BasicTransactionData(amount, description, tag);
    }
}
