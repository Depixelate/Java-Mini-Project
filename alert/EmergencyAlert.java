package alert;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import transaction.DatedTransaction;


public class EmergencyAlert extends Alert {
    @Override
    public void send(DatedTransaction transaction) {
        if (transaction.getNextDueDate().isBefore(LocalDate.now())) {
            System.out.printf("EMERGENCY ALERT: You have missed your bill payment for  '%s'!\n",
                    transaction.getDescription());
            System.out.println("This payment is very important, please pay your bill ASAP!");
            return;
        }

        System.out.println("EMERGENCY ALERT: You only have "
                + ChronoUnit.DAYS.between(LocalDate.now(), transaction.getNextDueDate())
                + " days to pay your bill for '" + transaction.getDescription() + "'!");
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

