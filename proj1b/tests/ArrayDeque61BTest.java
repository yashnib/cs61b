import jh61b.utils.Reflection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class ArrayDeque61BTest {

     @Test
     @DisplayName("ArrayDeque61B has no fields besides backing array and primitives")
     void noNonTrivialFields() {
         List<Field> badFields = Reflection.getFields(ArrayDeque61B.class)
                 .filter(f -> !(f.getType().isPrimitive() || f.getType().equals(Object[].class) || f.isSynthetic()))
                 .toList();

         assertWithMessage("Found fields that are not array or primitives").that(badFields).isEmpty();
     }

    @Test
    @DisplayName("Check addFirst and addLast methods")
    void testAddFirstandAddLast() {
        Deque61B<String> testArray =  new ArrayDeque61B<>();
        testArray.addLast("a");
        testArray.addLast("b");
        testArray.addFirst("c");
        testArray.addLast("d");
        testArray.addLast("e");
        testArray.addFirst("f");
        testArray.addLast("g");
        testArray.addLast("h");
        assertThat(testArray.toList()).containsExactly("f", "c", "a", "b", "d", "e", "g", "h").inOrder();
    }

    @Test
    @DisplayName("Check resizeArray method")
    void testInitialResizeUp() {
        Deque61B<String> testArray =  new ArrayDeque61B<>();
        testArray.addLast("a");
        testArray.addLast("b");
        testArray.addFirst("c");
        testArray.addLast("d");
        testArray.addLast("e");
        testArray.addFirst("f");
        testArray.addLast("g");
        testArray.addLast("h");
        assertThat(testArray.toList()).containsExactly("f", "c", "a", "b", "d", "e", "g", "h").inOrder();
        testArray.addLast("Z");
        assertThat(testArray.toList()).containsExactly("f", "c", "a", "b", "d", "e", "g", "h", "Z").inOrder();
    }

    @Test
    @DisplayName("Check get method")
    void testGet(){
        Deque61B<String> testArray =  new ArrayDeque61B<>();
        testArray.addLast("a");
        assertThat(testArray.get(0)).isEqualTo("a");
        testArray.addLast("b");
        testArray.addFirst("c");
        assertThat(testArray.get(0)).isEqualTo("c");
        assertThat(testArray.get(2)).isEqualTo("b");
        testArray.addLast("d");
        assertThat(testArray.get(3)).isEqualTo("d");
        testArray.addLast("e");
        assertThat(testArray.get(4)).isEqualTo("e");
        testArray.addFirst("f");
        assertThat(testArray.get(0)).isEqualTo("f");
        testArray.addLast("g");
        assertThat(testArray.get(6)).isEqualTo("g");
        testArray.addLast("h");
        assertThat(testArray.get(7)).isEqualTo("h");
    }

    @Test
    @DisplayName("Check isEmpty and size methods")
    void testSizeAndIsEmpty(){
        Deque61B<Integer> testArray = new ArrayDeque61B<>();
        assertThat(testArray.isEmpty()).isTrue();
        assertThat(testArray.size()).isEqualTo(0);

        testArray.addLast(0);
        testArray.addFirst(1);
        assertThat(testArray.isEmpty()).isFalse();
        assertThat(testArray.size()).isEqualTo(2);

        testArray.addLast(2);
        assertThat(testArray.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Check removeFirst and removeLast methods")
    public void removeFirstandRemoveLastTest(){
        Deque61B<Integer> testArray = new ArrayDeque61B<>();
        assertThat(testArray.removeFirst()).isNull();
        assertThat(testArray.removeLast()).isNull();

        testArray.addLast(0);   // [0]
        testArray.addLast(1);   // [0, 1]
        testArray.addFirst(-1); // [-1, 0, 1]
        testArray.addLast(2);   // [-1, 0, 1, 2]
        testArray.addFirst(-2); // [-2, -1, 0, 1, 2]
        testArray.addLast(0);   // [-2, -1, 0, 1, 2, 0]
        testArray.addLast(1);   // [-2, -1, 0, 1, 2, 0, 1]
        testArray.addFirst(-1); // [-1, -2, -1, 0, 1, 2, 0, 1]
        testArray.addLast(2);   // [-1, -2, -1, 0, 1, 2, 0, 1, 2]
        testArray.addFirst(-2); // [-2, -1, -2, -1, 0, 1, 2, 0, 1, 2]
        testArray.addLast(0);   // [-2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0]
        testArray.addLast(1);   // [-2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1]
        testArray.addFirst(-1); // [-1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1]
        testArray.addLast(2);   // [-1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2]
        testArray.addFirst(-2); // [-2, -1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2]
        testArray.addLast(0);   // [-2, -1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0]
        testArray.addLast(1);   // [-2, -1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1]
        testArray.addFirst(-1); // [-1, -2, -1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1]
        testArray.addLast(2);   // [-1, -2, -1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2]
        testArray.addFirst(-2); // [-2, -1, -2, -1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2]

        assertThat(testArray.removeLast()).isEqualTo(2);
        assertThat(testArray.toList()).containsExactly(-2, -1, -2, -1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1).inOrder();
        assertThat(testArray.removeLast()).isEqualTo(1);
        assertThat(testArray.toList()).containsExactly(-2, -1, -2, -1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0).inOrder();

        assertThat(testArray.removeFirst()).isEqualTo(-2);
        assertThat(testArray.toList()).containsExactly(-1, -2, -1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0).inOrder();
        assertThat(testArray.removeFirst()).isEqualTo(-1);
        assertThat(testArray.toList()).containsExactly(-2, -1, -2, -1, -2, -1, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0).inOrder();
        assertThat(testArray.removeFirst()).isEqualTo(-2);

        assertThat(testArray.removeFirst()).isEqualTo(-1);
        assertThat(testArray.removeFirst()).isEqualTo(-2);
        assertThat(testArray.removeFirst()).isEqualTo(-1);
        assertThat(testArray.removeFirst()).isEqualTo(-2);
        assertThat(testArray.removeFirst()).isEqualTo(-1);
        assertThat(testArray.removeFirst()).isEqualTo(0);
        assertThat(testArray.removeFirst()).isEqualTo(1);
        assertThat(testArray.removeFirst()).isEqualTo(2);
        assertThat(testArray.removeFirst()).isEqualTo(0);
        assertThat(testArray.removeFirst()).isEqualTo(1);
        assertThat(testArray.toList()).containsExactly(2, 0, 1, 2, 0).inOrder();
        assertThat(testArray.removeFirst()).isEqualTo(2);
        assertThat(testArray.removeFirst()).isEqualTo(0);
        assertThat(testArray.removeFirst()).isEqualTo(1);
        assertThat(testArray.removeFirst()).isEqualTo(2);
        assertThat(testArray.removeFirst()).isEqualTo(0);
        assertThat(testArray.removeFirst()).isNull();
    }

    @Test
    @DisplayName("Check the iterator method")
    public void testIterator(){
        Deque61B<Integer> testArray = new ArrayDeque61B<>();
        testArray.addLast(0);   // [0]
        testArray.addLast(1);   // [0, 1]
        testArray.addFirst(-1); // [-1, 0, 1]
        testArray.addLast(2);   // [-1, 0, 1, 2]
        testArray.addFirst(-2); // [-2, -1, 0, 1, 2]

        assertThat(testArray).containsExactly(-2, -1, 0, 1, 2);
    }

    @Test
    @DisplayName("Check the equals method")
    public void testEquals(){
        Deque61B<Integer> testArray1 = new ArrayDeque61B<>();
        testArray1.addLast(0);   // [0]
        testArray1.addLast(1);   // [0, 1]
        testArray1.addFirst(-1); // [-1, 0, 1]
        testArray1.addLast(2);   // [-1, 0, 1, 2]
        testArray1.addFirst(-2); // [-2, -1, 0, 1, 2]

        Deque61B<Integer> testArray2 = new ArrayDeque61B<>();
        testArray2.addLast(0);   // [0]
        testArray2.addLast(1);   // [0, 1]
        testArray2.addFirst(-1); // [-1, 0, 1]
        testArray2.addLast(2);   // [-1, 0, 1, 2]
        testArray2.addFirst(-2); // [-2, -1, 0, 1, 2]
        assertThat(testArray1).isEqualTo(testArray2);
    }

    @Test
    @DisplayName("Check the toString method")
    public void testToString() {
        Deque61B<String> testArray = new ArrayDeque61B<>();

        testArray.addLast("front");
        testArray.addLast("middle");
        testArray.addLast("back");

        System.out.println(testArray.toString());
    }
}
