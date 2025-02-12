package ngrams;

import java.util.*;

/**
 * An object for mapping a year number (e.g. 1996) to numerical data. Provides
 * utility methods useful for data analysis.
 *
 * @author Josh Hug
 */
public class TimeSeries extends TreeMap<Integer, Double> {

    /** If it helps speed up your code, you can assume year arguments to your NGramMap
     * are between 1400 and 2100. We've stored these values as the constants
     * MIN_YEAR and MAX_YEAR here. */
    public static final int MIN_YEAR = 1400;
    public static final int MAX_YEAR = 2100;

    /**
     * Constructs a new empty TimeSeries.
     */
    public TimeSeries() {
        super();
    }

    /**
     * Creates a copy of TS, but only between STARTYEAR and ENDYEAR,
     * inclusive of both end points.
     */

    public TimeSeries(TimeSeries ts, int startYear, int endYear) {
        super();
        super.putAll(ts.subMap(startYear, true, endYear, true));
    }

    /**
     *  Returns all years for this time series in ascending order.
     */
    public List<Integer> years() {
        return new ArrayList<>(super.keySet());
    }

    /**
     *  Returns all data for this time series. Must correspond to the
     *  order of years().
     */
    public List<Double> data() {
        List<Integer> keyList = years();
        List<Double> dataList = new ArrayList<>();
        for (Integer key: keyList){
            dataList.add(super.get(key));
        }
        return dataList;
    }

    /**
     * Returns the year-wise sum of this TimeSeries with the given TS. In other words, for
     * each year, sum the data from this TimeSeries with the data from TS. Should return a
     * new TimeSeries (does not modify this TimeSeries).
     *
     * If both TimeSeries don't contain any years, return an empty TimeSeries.
     * If one TimeSeries contains a year that the other one doesn't, the returned TimeSeries
     * should store the value from the TimeSeries that contains that year.
     */
    public TimeSeries plus(TimeSeries ts) {
        if (ts.isEmpty()){
            return this;
        }

        TimeSeries sum = new TimeSeries();
        Iterator<Integer> tsKeyIterator = ts.keySet().iterator();
        Iterator<Integer> currentKeyIterator = super.keySet().iterator();
        Integer tsKey = tsKeyIterator.hasNext() ? tsKeyIterator.next() : null;
        Integer currentKey = currentKeyIterator.hasNext() ? currentKeyIterator.next() : null;
        while (tsKey != null && currentKey != null){
            int cmp = Integer.compare(tsKey, currentKey);
            if (cmp == 0){
                sum.put(tsKey, super.get(tsKey) + ts.get(tsKey));
                tsKey = tsKeyIterator.hasNext() ? tsKeyIterator.next() : null;
                currentKey = currentKeyIterator.hasNext() ? currentKeyIterator.next() : null;
            }
            else if (cmp < 0){
                sum.put(tsKey, ts.get(tsKey));
                tsKey = tsKeyIterator.hasNext() ? tsKeyIterator.next() : null;
            }
            else {
                sum.put(currentKey, super.get(currentKey));
                currentKey = currentKeyIterator.hasNext() ? currentKeyIterator.next() : null;
            }
        }

        while (tsKey != null){
            sum.put(tsKey, ts.get(tsKey));
            tsKey = tsKeyIterator.hasNext() ? tsKeyIterator.next() : null;
        }

        while (currentKey != null){
            sum.put(currentKey, super.get(currentKey));
            currentKey = currentKeyIterator.hasNext() ? currentKeyIterator.next() : null;
        }

        return sum;
    }

    /**
     * Returns the quotient of the value for each year this TimeSeries divided by the
     * value for the same year in TS. Should return a new TimeSeries (does not modify this
     * TimeSeries).
     *
     * If TS is missing a year that exists in this TimeSeries, throw an
     * IllegalArgumentException.
     * If TS has a year that is not in this TimeSeries, ignore it.
     */
    public TimeSeries dividedBy(TimeSeries ts) {
        TimeSeries quotient = new TimeSeries();
        Set<Integer> currentKeys = super.keySet();
        for (Integer key : currentKeys){
            Double tsVal = ts.get(key);
            Double currentVal = super.get(key);
            if (tsVal == null){
                throw new IllegalArgumentException();
            }
            quotient.put(key, currentVal/tsVal);
        }
        return quotient;
    }
}
