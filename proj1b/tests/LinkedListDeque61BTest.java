import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

/** Performs some basic linked list tests. */
public class LinkedListDeque61BTest {

     @Test
     /** In this test, we have three different assert statements that verify that addFirst works correctly. */
     public void addFirstTestBasic() {
         Deque61B<String> testLL = new LinkedListDeque61B<>();

         testLL.addFirst("back"); // after this call we expect: ["back"]
         assertThat(testLL.toList()).containsExactly("back").inOrder();

         testLL.addFirst("middle"); // after this call we expect: ["middle", "back"]
         assertThat(testLL.toList()).containsExactly("middle", "back").inOrder();

         testLL.addFirst("front"); // after this call we expect: ["front", "middle", "back"]
         assertThat(testLL.toList()).containsExactly("front", "middle", "back").inOrder();

         /* Note: The first two assertThat statements aren't really necessary. For example, it's hard
            to imagine a bug in your code that would lead to ["front"] and ["front", "middle"] failing,
            but not ["front", "middle", "back"].
          */
     }

     @Test
     /** In this test, we use only one assertThat statement. IMO this test is just as good as addFirstTestBasic.
      *  In other words, the tedious work of adding the extra assertThat statements isn't worth it. */
     public void addLastTestBasic() {
         Deque61B<String> testLL = new LinkedListDeque61B<>();

         testLL.addLast("front"); // after this call we expect: ["front"]
         testLL.addLast("middle"); // after this call we expect: ["front", "middle"]
         testLL.addLast("back"); // after this call we expect: ["front", "middle", "back"]
         assertThat(testLL.toList()).containsExactly("front", "middle", "back").inOrder();
     }

     @Test
     /** This test performs interspersed addFirst and addLast calls. */
     public void addFirstAndAddLastTest() {
         Deque61B<Integer> testLL = new LinkedListDeque61B<>();

         /* I've decided to add in comments the state after each call for the convenience of the
            person reading this test. Some programmers might consider this excessively verbose. */
         testLL.addLast(0);   // [0]
         testLL.addLast(1);   // [0, 1]
         testLL.addFirst(-1); // [-1, 0, 1]
         testLL.addLast(2);   // [-1, 0, 1, 2]
         testLL.addFirst(-2); // [-2, -1, 0, 1, 2]

         assertThat(testLL.toList()).containsExactly(-2, -1, 0, 1, 2).inOrder();
     }

    // Below, you'll write your own tests for LinkedListDeque61B.
    @Test
    public void sizeAndIsEmptyTest() {
         Deque61B<Integer> testLL = new LinkedListDeque61B<>();
         assertThat(testLL.isEmpty()).isTrue();
         assertThat(testLL.size()).isEqualTo(0);

         testLL.addLast(0);
         testLL.addFirst(1);
         assertThat(testLL.isEmpty()).isFalse();
         assertThat(testLL.size()).isEqualTo(2);

         testLL.addLast(2);
         assertThat(testLL.size()).isEqualTo(3);
    }

    @Test
    public void getTest(){
         Deque61B<Integer> testLL = new LinkedListDeque61B<>();
         assertThat(testLL.get(1)).isNull();

         testLL.addLast(0);
         testLL.addFirst(3);
         assertThat(testLL.get(1)).isEqualTo(0);
         assertThat(testLL.get(0)).isEqualTo(3);

         assertThat(testLL.get(testLL.size())).isNull();

         assertThat(testLL.get(-2)).isNull();
    }

    @Test
    public void getRecursiveTest(){
        Deque61B<Integer> testLL = new LinkedListDeque61B<>();
        assertThat(testLL.getRecursive(1)).isNull();

        testLL.addLast(0);
        testLL.addFirst(3);
        assertThat(testLL.getRecursive(1)).isEqualTo(0);
        assertThat(testLL.getRecursive(0)).isEqualTo(3);

        assertThat(testLL.getRecursive(testLL.size())).isNull();

        assertThat(testLL.getRecursive(-2)).isNull();
    }

    @Test
    public void removeFirstandRemoveLastTest(){
        Deque61B<Integer> testLL = new LinkedListDeque61B<>();
        assertThat(testLL.removeFirst()).isNull();
        assertThat(testLL.removeLast()).isNull();

        testLL.addLast(0);   // [0]
        testLL.addLast(1);   // [0, 1]
        testLL.addFirst(-1); // [-1, 0, 1]
        testLL.addLast(2);   // [-1, 0, 1, 2]
        testLL.addFirst(-2); // [-2, -1, 0, 1, 2]

        assertThat(testLL.removeLast()).isEqualTo(2);
        assertThat(testLL.toList()).containsExactly(-2, -1, 0, 1).inOrder();
        assertThat(testLL.removeLast()).isEqualTo(1);
        assertThat(testLL.toList()).containsExactly(-2, -1, 0).inOrder();

        assertThat(testLL.removeFirst()).isEqualTo(-2);
        assertThat(testLL.toList()).containsExactly( -1, 0).inOrder();
        assertThat(testLL.removeFirst()).isEqualTo(-1);
        assertThat(testLL.toList()).containsExactly(0).inOrder();
        assertThat(testLL.removeFirst()).isEqualTo(0);
        assertThat(testLL.toList()).isEmpty();

        testLL.addFirst(-2);
        assertThat(testLL.removeLast()).isEqualTo(-2);
        assertThat(testLL.toList()).isEmpty();
    }

    @Test
    @DisplayName("Check the iterator method")
    public void testIterator(){
        Deque61B<Integer> testLL = new LinkedListDeque61B<>();
        testLL.addLast(0);   // [0]
        testLL.addLast(1);   // [0, 1]
        testLL.addFirst(-1); // [-1, 0, 1]
        testLL.addLast(2);   // [-1, 0, 1, 2]
        testLL.addFirst(-2); // [-2, -1, 0, 1, 2]

        assertThat(testLL).containsExactly(-2, -1, 0, 1, 2);
    }

    @Test
    @DisplayName("Check the equals method")
    public void testEquals(){
        Deque61B<Integer> testLL1 = new LinkedListDeque61B<>();
        testLL1.addLast(0);   // [0]
        testLL1.addLast(1);   // [0, 1]
        testLL1.addFirst(-1); // [-1, 0, 1]
        testLL1.addLast(2);   // [-1, 0, 1, 2]
        testLL1.addFirst(-2); // [-2, -1, 0, 1, 2]

        Deque61B<Integer> testLL2 = new LinkedListDeque61B<>();
        testLL2.addLast(0);   // [0]
        testLL2.addLast(1);   // [0, 1]
        testLL2.addFirst(-1); // [-1, 0, 1]
        testLL2.addLast(2);   // [-1, 0, 1, 2]
        testLL2.addFirst(-2); // [-2, -1, 0, 1, 2]
        assertThat(testLL1).isEqualTo(testLL2);
    }

    @Test
    @DisplayName("Check the toString method")
    public void testToString() {
        Deque61B<String> testLL = new LinkedListDeque61B<>();

        testLL.addLast("front");
        testLL.addLast("middle");
        testLL.addLast("back");

        System.out.println(testLL.toString());
    }
}