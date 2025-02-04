import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayDeque61B<T> implements Deque61B<T> {
    private T[] backingArray;
    private int numElements = 8;
    private int nextFirst;
    private int nextLast;
    private int size;


    public ArrayDeque61B() {
        backingArray = (T[]) new Object[numElements];
        nextFirst = 0;
        nextLast = 1;
        size = 0;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof ArrayDeque61B a) {
            if (a.size != this.size) {
                return false;
            }

            for (int i = 0; i < size; i++){
                if (this.get(i) != a.get(i)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        return toList().toString();
    }

    private void resizeArray(int upOrDown){
        T[] a;
        if (upOrDown == 1) {
            a = (T[]) new Object[numElements * 2];
        }
        else {
            a = (T[]) new Object[numElements / 2];
        }

        for (int i = 0; i < size; i++){
            a[i] = get(i);
        }

        if (upOrDown == 1) {
            numElements *= 2;
        } else {
            numElements /= 2;
        }
        nextFirst = numElements - 1;
        nextLast = size;
        backingArray = a;
    }

    @Override
    public Iterator<T> iterator(){
        return new ArrayDeque61BIterator();
    }

    private class ArrayDeque61BIterator implements Iterator<T>{
        private int iteratorPos;
        public ArrayDeque61BIterator() {
            iteratorPos = 0;
        }
        @Override
        public boolean hasNext() {
            return iteratorPos < size;
        }

        @Override
        public T next() {
            T returnItem = get(iteratorPos);
            iteratorPos += 1;
            return returnItem;
        }
    }
    @Override
    public void addFirst(T x) {
        if (size == numElements) {
            resizeArray(1);
        }
        backingArray[nextFirst] = x;

        nextFirst = Math.floorMod(nextFirst-1, numElements);
        size += 1;
    }

    @Override
    public void addLast(T x) {
        if (size == numElements) {
            resizeArray(1);
        }
        backingArray[nextLast] = x;

        nextLast = Math.floorMod(nextLast+1, numElements);
        size += 1;
    }

    @Override
    public List<T> toList() {
        List<T> returnList = new ArrayList<>();
        int curFirst = Math.floorMod(nextFirst+1, numElements);
        for (int k = 0; k < size; k++) {
            returnList.add(backingArray[curFirst]);
            curFirst = Math.floorMod(curFirst+1, numElements);
        }
        return returnList;
    }

    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        nextFirst = Math.floorMod(nextFirst+1, numElements);
        T removeValue = backingArray[nextFirst];
        backingArray[nextFirst] = null;
        size-=1;
        if (size <= numElements/4 && numElements > 8){
            resizeArray(0);
        }
        return removeValue;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextLast = Math.floorMod(nextLast-1, numElements);
        T removeValue = backingArray[nextLast];
        backingArray[nextLast] = null;
        size-=1;
        if (size <= numElements/4 && numElements > 8){
            resizeArray(0);
        }
        return removeValue;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size){
            return null;
        }
        return backingArray[Math.floorMod(nextFirst + 1 + index, numElements)];
    }

    @Override
    public T getRecursive(int index) {
        throw new UnsupportedOperationException("No need to implement getRecursive for proj 1b");
    }
}
