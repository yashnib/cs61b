import org.junit.jupiter.api.*;

import java.util.Comparator;

import static com.google.common.truth.Truth.assertThat;

public class MaxArrayDeque61BTest {
    private static class StringLengthComparator implements Comparator<String> {
        public int compare(String a, String b) {
            return a.length() - b.length();
        }
    }

    private static class StringLengthComparatorReverse implements Comparator<String> {
        public int compare(String a, String b) {
            return b.length() - a.length();
        }
    }

    private static class IntegerComparatorReverse implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            return b - a;
        }
    }

    @Test
    public void basicTest() {
        MaxArrayDeque61B<String> testString = new MaxArrayDeque61B<>(new StringLengthComparator());
        testString.addFirst("");
        testString.addFirst("2");
        testString.addFirst("fury road");
        assertThat(testString.max()).isEqualTo("fury road");
    }

    @Test
    public void testAllMaxDequeMethods () {
        MaxArrayDeque61B<String> testString = new MaxArrayDeque61B<>(new StringLengthComparator());
        testString.addFirst("");
        testString.addFirst("2");
        testString.addFirst("fury road");
        assertThat(testString.max()).isEqualTo("fury road");
        assertThat(testString.max(new StringLengthComparatorReverse())).isEqualTo("");

        MaxArrayDeque61B<Integer> testIntegers = new MaxArrayDeque61B<Integer>(Comparator.naturalOrder());
        testIntegers.addFirst(5);
        testIntegers.addFirst(5);
        testIntegers.addFirst(2);
        testIntegers.addFirst(2);
        testIntegers.addFirst(3);
        assertThat(testIntegers.max()).isEqualTo(5);
        assertThat(testIntegers.max(new IntegerComparatorReverse())).isEqualTo(2);
    }
    
}
