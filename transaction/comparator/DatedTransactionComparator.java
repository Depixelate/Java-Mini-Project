package transaction.comparator;
import transaction.DatedTransaction;
import java.util.Comparator;

public class DatedTransactionComparator implements Comparator<DatedTransaction> {
    @Override
    public int compare(DatedTransaction t1, DatedTransaction t2) {
        return t1.getNextDueDate().compareTo(t2.getNextDueDate());
    }
}