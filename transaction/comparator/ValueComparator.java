package transaction.comparator;
import transaction.TransactionRecord;
import java.util.Comparator;

public class ValueComparator implements Comparator<TransactionRecord> {
    @Override
    public int compare(TransactionRecord t1, TransactionRecord t2) {
        return Double.compare(t1.getAmount(), t2.getAmount());
    }
}
