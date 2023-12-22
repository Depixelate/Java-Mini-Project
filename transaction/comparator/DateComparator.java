package transaction.comparator;
import transaction.TransactionRecord;
import java.util.Comparator;

public class DateComparator implements Comparator<TransactionRecord> {
    @Override
    public int compare(TransactionRecord t1, TransactionRecord t2) {
        return t1.getDate().compareTo(t2.getDate());
    }
}