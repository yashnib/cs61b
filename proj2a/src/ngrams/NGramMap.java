package ngrams;

import edu.princeton.cs.algs4.In;
import java.util.*;

/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {

    HashMap<String, TimeSeries> wordsMap;
    TimeSeries countsMap;

    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {
        wordsMap = new HashMap<>();
        countsMap = new TimeSeries();
        In inWords = new In(wordsFilename);
        In inCount = new In(countsFilename);

        while (!inWords.isEmpty()){
            String nextLine = inWords.readLine();
            String[] splitLine = nextLine.split("\t");
            String word = splitLine[0];
            Integer year = Integer.parseInt(splitLine[1]);
            Double count = Double.parseDouble(splitLine[2]);
            wordsMap.computeIfAbsent(word, k -> new TimeSeries()).put(year, count);
        }

        while (!inCount.isEmpty()){
            String nextLine = inCount.readLine();
            String[] splitLine = nextLine.split(",");
            Integer year = Integer.parseInt(splitLine[0]);
            Double count = Double.parseDouble(splitLine[1]);
            countsMap.putIfAbsent(year, count);
        }
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {
        if (wordsMap.isEmpty() || !wordsMap.containsKey(word)){
            return new TimeSeries();
        }
        return new TimeSeries(wordsMap.get(word), startYear, endYear);
    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {
        if (wordsMap.isEmpty() || !wordsMap.containsKey(word)){
            return new TimeSeries();
        }
        TimeSeries countHistoryMap = new TimeSeries();
        countHistoryMap.putAll(wordsMap.get(word));
        return countHistoryMap;
    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */
    public TimeSeries totalCountHistory() {
        TimeSeries totalCountHistoryMap = new TimeSeries();
        totalCountHistoryMap.putAll(countsMap);
        return totalCountHistoryMap;
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        TimeSeries wordCountHistoryMap = countHistory(word, startYear, endYear);
        if (wordCountHistoryMap.isEmpty()){
            return new TimeSeries();
        }
        return wordCountHistoryMap.dividedBy(totalCountHistory());
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        TimeSeries wordCountHistoryMap = countHistory(word);
        if (wordCountHistoryMap.isEmpty()){
            return new TimeSeries();
        }
        return wordCountHistoryMap.dividedBy(totalCountHistory());
    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words,
                                          int startYear, int endYear) {
        TimeSeries summedWeightHistoryMap = new TimeSeries();
        for (String word : words){
            TimeSeries wordWeightHistoryMap = weightHistory(word, startYear, endYear);
            if (!wordWeightHistoryMap.isEmpty()) {
                summedWeightHistoryMap = summedWeightHistoryMap.plus(wordWeightHistoryMap);
            }
        }
        return summedWeightHistoryMap;
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        TimeSeries summedWeightHistoryMap = new TimeSeries();
        for (String word : words){
            TimeSeries wordWeightHistoryMap = weightHistory(word);
            if (!wordWeightHistoryMap.isEmpty()) {
                summedWeightHistoryMap = summedWeightHistoryMap.plus(wordWeightHistoryMap);
            }
        }
        return summedWeightHistoryMap;
    }
}
