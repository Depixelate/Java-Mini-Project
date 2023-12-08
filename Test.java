import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class QuickSort {
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        quickSort(list, 0, list.size() - 1, comparator);
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, comparator);
            quickSort(list, low, pivotIndex - 1, comparator);
            quickSort(list, pivotIndex + 1, high, comparator);
        }
    }

    private static <T> int partition(List<T> list, int low, int high, Comparator<T> comparator) {
        T pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) < 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }
}

public class Test {
    public static void main(String[] args) {
        String[] names = {"John", "Paul", "George", "Ringo"};
        List<String> list = Arrays.asList(names);
        QuickSort.sort(list, (a, b) -> a.compareTo(b));
        System.out.println(list);
    }
}
