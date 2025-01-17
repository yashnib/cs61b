import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
public class ListExercises {

    /** Returns the total sum in a list of integers */
	public static int sum(List<Integer> L) {
        // TODO: Fill in this function.
        int sum = 0;
        for (Integer element : L){
            sum += element;
        }
        return sum;
    }

    /** Returns a list containing the even numbers of the given list */
    public static List<Integer> evens(List<Integer> L) {
        // TODO: Fill in this function.
        List<Integer> evenList = new ArrayList<>();
        for (Integer element : L){
            if (element % 2 == 0){
                evenList.add(element);
            }
        }
        return evenList;
    }

    /** Returns a list containing the common item of the two given lists */
    public static List<Integer> common(List<Integer> L1, List<Integer> L2) {
        // TODO: Fill in this function.
        Set<Integer> set1 = new HashSet<>(L1);
        set1.retainAll(L2);

        return new ArrayList<>(set1);
    }


    /** Returns the number of occurrences of the given character in a list of strings. */
    public static int countOccurrencesOfC(List<String> words, char c) {
        // TODO: Fill in this function.
        int numChar = 0;

        for (String word : words) {
            for (int i = 0; i < word.length(); i++){
                if (word.charAt(i) == c){
                    numChar++;
                }
            }
        }
        return numChar;
    }
}
