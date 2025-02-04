import java.util.Comparator;

public class MaxArrayDeque61B<T> extends ArrayDeque61B<T> {
    ArrayDeque61B<T> newBackingArray;
    Comparator<T> inComparator;
    public MaxArrayDeque61B(Comparator<T> c) {
        newBackingArray = new ArrayDeque61B<>();
        inComparator = c;
    }

    public T max(){
        T maxVal;
        int arraySize = super.size();
        if (super.isEmpty()) {
            return null;
        }
        else if (arraySize == 1) {
            return super.get(0);
        }
        else {
            maxVal = super.get(0);
            for (int i = 1; i < arraySize; i++) {
                T compareVal = super.get(i);
                if (inComparator.compare(compareVal, maxVal) > 0) {
                    maxVal = compareVal;
                }
            }
        }
        return maxVal;
    }

    public T max(Comparator<T> c){
        T maxVal;
        int arraySize = super.size();
        if (super.isEmpty()) {
            return null;
        }
        else if (arraySize == 1) {
            return super.get(0);
        }
        else {
            maxVal = super.get(0);
            for (int i = 1; i < arraySize; i++) {
                T compareVal = super.get(i);
                if (c.compare(compareVal, maxVal) > 0) {
                    maxVal = compareVal;
                }
            }
        }
        return maxVal;
    }
}