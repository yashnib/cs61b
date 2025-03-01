package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;

    // You should probably define some more!
    private int size;
    private int capacity;
    private double factor;

    /** Constructors */
    public MyHashMap() {
        size = 0;
        capacity = 16;
        factor = 0.75;
        buckets = new Collection[capacity];
    }

    public MyHashMap(int initialCapacity) {
        size = 0;
        capacity = initialCapacity;
        factor = 0.75;
        buckets = new Collection[capacity];
    }

    /**
     * MyHashMap constructor that creates a backing array of initialCapacity.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialCapacity initial size of backing array
     * @param loadFactor maximum load factor
     */
    public MyHashMap(int initialCapacity, double loadFactor) {
        size = 0;
        capacity = initialCapacity;
        factor = loadFactor;
        buckets = new Collection[capacity];
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *  Note that that this is referring to the hash table bucket itself,
     *  not the hash map itself.
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    private void resize(){
        int newCapacity = capacity * 2;
        Collection<Node>[] newBuckets = new Collection[newCapacity];
        for (int i = 0; i < capacity; i++){
            Collection<Node> curBucket = buckets[i];
            if (curBucket == null || curBucket.isEmpty()){
                continue;
            }

            for (Node curNode : curBucket){
                int curNodeHashCode = curNode.key.hashCode();
                int newBucketIndex = Math.floorMod(curNodeHashCode, newCapacity);
                if (newBuckets[newBucketIndex] == null) newBuckets[newBucketIndex] = createBucket();
                newBuckets[newBucketIndex].add(curNode);
            }
        }
        buckets = newBuckets;
        capacity = newCapacity;
    }

    @Override
    public void put(K key, V value) {
        int bucketIndex = Math.floorMod(key.hashCode(), capacity);
        if (containsKey(key)) {
            Collection<Node> curBucket = buckets[bucketIndex];
            for (Node curNode : curBucket) {
                if (curNode.key.equals(key)) {
                    curNode.value = value;
                    break;
                }
            }
            return;
        }

        if (buckets[bucketIndex] == null) buckets[bucketIndex] = createBucket();

        Node newNode = new Node(key, value);
        buckets[bucketIndex].add(newNode);
        size += 1;
        if ((double) size / capacity == factor){
            resize();
        }
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)){
            return null;
        }

        int bucketIndex = Math.floorMod(key.hashCode(), capacity);

        Collection<Node> curBucket = buckets[bucketIndex];
        for (Node curNode : curBucket){
            if (curNode.key.equals(key)){
                return curNode.value;
            }
        }

        return null;
    }

    @Override
    public boolean containsKey(K key) {
        int bucketIndex = Math.floorMod(key.hashCode(), capacity);
        Collection<Node> curBucket = buckets[bucketIndex];
        if (curBucket == null || curBucket.isEmpty()){
            return false;
        }

        for (Node curNode : curBucket){
            if (curNode.key.equals(key)){
                return true;
            }
        }

        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < capacity; i++){
            buckets[i] = null;
        }
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> retSet = new HashSet<>();
        for (int i = 0; i < capacity; i++){
            Collection<Node> curBucket = buckets[i];
            if (curBucket == null || curBucket.isEmpty()){
                continue;
            }
            for (Node curNode : curBucket){
                retSet.add(curNode.key);
            }
        }
        return retSet;
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)){
            return null;
        }

        int bucketIndex = Math.floorMod(key.hashCode(), capacity);
        Collection<Node> curBucket = buckets[bucketIndex];

        for (Node curNode : curBucket){
            if (curNode.key.equals(key)){
                V retValue = curNode.value;
                curBucket.remove(curNode);
                size -= 1;
                return retValue;
            }
        }

        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
