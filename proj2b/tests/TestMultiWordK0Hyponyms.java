import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import edu.princeton.cs.algs4.StdRandom;
import main.AutograderBuddy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

/** Tests the case where the list of words is length greater than 1, but k is still zero. */
public class TestMultiWordK0Hyponyms {
    // this case doesn't use the NGrams dataset at all, so the choice of files is irrelevant
    // ngrams files
    public static final String VERY_SHORT_WORDS_FILE = "data/ngrams/very_short.csv";
    public static final String TOTAL_COUNTS_FILE = "data/ngrams/total_counts.csv";
    private static final String SMALL_WORDS_FILE = "data/ngrams/top_14377_words.csv";
    private static final String WORDS_FILE = "data/ngrams/top_49887_words.csv";

    // wordnet Files
    public static final String SMALL_SYNSET_FILE = "data/wordnet/synsets16.txt";
    public static final String SMALL_HYPONYM_FILE = "data/wordnet/hyponyms16.txt";
    public static final String LARGE_SYNSET_FILE = "data/wordnet/synsets.txt";
    public static final String LARGE_HYPONYM_FILE = "data/wordnet/hyponyms.txt";
    private static final String HYPONYMS_FILE_SUBSET = "data/wordnet/hyponyms1000-subgraph.txt";
    private static final String SYNSETS_FILE_SUBSET = "data/wordnet/synsets1000-subgraph.txt";

    // EECS files
    private static final String FREQUENCY_EECS_FILE = "data/ngrams/frequency-EECS.csv";
    private static final String HYPONYMS_EECS_FILE = "data/wordnet/hyponyms-EECS.txt";
    private static final String SYNSETS_EECS_FILE = "data/wordnet/synsets-EECS.txt";


    /** This is an example from the spec.*/
    @Test
    public void testOccurrenceAndChangeK0() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                VERY_SHORT_WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("occurrence");
        words.add("change");

        NgordnetQuery nq = new NgordnetQuery(words, 0, 0, 0);
        String actual = studentHandler.handle(nq);
        String expected = "[alteration, change, increase, jump, leap, modification, saltation, transition]";
        assertThat(actual).isEqualTo(expected);
    }

    // TODO: Add more unit tests (including edge case tests) here.
    @Test
    public void testOccurrenceAndChangeK0MultipleQueries() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                VERY_SHORT_WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("occurrence");
        words.add("change");

        NgordnetQuery nq0 = new NgordnetQuery(words, 0, 0, 0);
        String actual0 = studentHandler.handle(nq0);
        String expected0 = "[alteration, change, increase, jump, leap, modification, saltation, transition]";
        assertThat(actual0).isEqualTo(expected0);

        NgordnetQuery nq1 = new NgordnetQuery(words, 1472, 1485, 0);
        String actual1 = studentHandler.handle(nq1);
        String expected1 = "[alteration, change, increase, jump, leap, modification, saltation, transition]";
        assertThat(actual1).isEqualTo(expected1);
    }

    @Test
    public void testOccurrenceAndChangeKNot0() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("occurrence");
        words.add("change");

        NgordnetQuery nq = new NgordnetQuery(words, 2000, 2020, 5);
        String actual = studentHandler.handle(nq);
        String expected = "[change, increase, jump, modification, transition]";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testOccurrenceAndChangeKNot0MultipleQueries() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words0 = new ArrayList<>();
        words0.add("occurrence");
        words0.add("change");

        NgordnetQuery nq0 = new NgordnetQuery(words0, 2000, 2020, 4);
        String actual0 = studentHandler.handle(nq0);
        String expected0 = "[change, increase, jump, transition]";
        assertThat(actual0).isEqualTo(expected0);

        NgordnetQueryHandler studentHandler1 = AutograderBuddy.getHyponymsHandler(
                VERY_SHORT_WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words1 = new ArrayList<>();
        words1.add("occurrence");
        words1.add("change");

        NgordnetQuery nq1 = new NgordnetQuery(words1, 2000, 2020, 4);
        String actual1 = studentHandler1.handle(nq1);
        String expected1 = "[]";
        assertThat(actual1).isEqualTo(expected1);
    }
}
