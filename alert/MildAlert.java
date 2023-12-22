package alert;
import transaction.DatedTransaction;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MildAlert extends Alert {

    @Override
    public void send(DatedTransaction transaction) {
        if (transaction.getNextDueDate().isBefore(LocalDate.now())) {
            System.out.println(
                    "Mild Alert: You have missed your bill payment for '" + transaction.getDescription() + "'!");
            System.out.println("Please pay your bill soon.");
            return;
        }
        System.out.println("Mild Alert: For '" + transaction.getDescription() + "' You have a bill due in "
                + ChronoUnit.DAYS.between(LocalDate.now(), transaction.getNextDueDate()) + " days.");
        System.out.println("Consider paying your bill soon.");
        // Add any additional logic needed for mild alerts
    }

    @Override
    public String toJSON() {
        // Implementation to convert to JSON format
        return "\"MILD\"";
    }

    public String toString() {
        return "MILD";
    }
}
