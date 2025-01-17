import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapExercises {
    /** Returns a map from every lower case letter to the number corresponding to that letter, where 'a' is
     * 1, 'b' is 2, 'c' is 3, ..., 'z' is 26.
     */
    public static Map<Character, Integer> letterToNum() {
        // TODO: Fill in this function.
        Map<Character, Integer> characterIntegerMap = new TreeMap<>();
        for (char c = 'a'; c <= 'z'; c++){
            characterIntegerMap.put(c, c - 'a' + 1);
        }

        return characterIntegerMap;
    }

    /** Returns a map from the integers in the list to their squares. For example, if the input list
     *  is [1, 3, 6, 7], the returned map goes from 1 to 1, 3 to 9, 6 to 36, and 7 to 49.
     */
    public static Map<Integer, Integer> squares(List<Integer> nums) {
        // TODO: Fill in this function.
        Map<Integer, Integer> squareMap = new TreeMap<>();
        for (Integer element : nums) {
            squareMap.put(element, element * element);
        }
        return squareMap;
    }

    /** Returns a map of the counts of all words that appear in a list of words. */
    public static Map<String, Integer> countWords(List<String> words) {
        // TODO: Fill in this function.
        Map<String, Integer> countWordsMap = new TreeMap<>();
        for (String word : words) {
            countWordsMap.computeIfPresent(word, (k, v) -> v + 1);
            countWordsMap.putIfAbsent(word, 1);
        }
        return countWordsMap;
    }
}
