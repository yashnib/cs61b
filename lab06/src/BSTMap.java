import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{

    private class BSTNode {
        public K key;
        public V value;
        public BSTNode left;
        public BSTNode right;

        public BSTNode(K k, V v){
            key = k;
            value = v;
            left = null;
            right = null;
        }
    }

    BSTNode root;
    private int size;
    Set<K> keys;

    public BSTMap() {
        root = new BSTNode(null, null);
        size = 0;
        keys = new TreeSet<>();
    }

    private BSTNode putHelper(BSTNode node, K key, V value){
        if (node == null) {
            node = new BSTNode(key, value);
            size += 1;
            keys.add(key);
            return node;
        } else if (node.key == null){
            node.key = key;
            node.value = value;
            size += 1;
            keys.add(key);
            return node;
        }

        int cmp = node.key.compareTo(key);

        if (cmp == 0){
            node.value = value;
        } else if (cmp < 0){
            node.right = putHelper(node.right, key, value);
        } else {
            node.left = putHelper(node.left, key, value);
        }
        return node;
    }
    @Override
    public void put(K key, V value) {
        if (root == null){
            return;
        } else if (root.key == null){
            root.key = key;
            root.value = value;
            size += 1;
            keys.add(key);
        }

        putHelper(root, key, value);
    }

    private V getHelper(BSTNode node, K key){
        if (node == null || node.key == null){
            return null;
        }
        int cmp = node.key.compareTo(key);

        if (cmp == 0) {
            return node.value;
        }
        return getHelper(cmp < 0 ? node.right : node.left, key);
    }

    @Override
    public V get(K key) {
        if (root == null || root.key == null){
            return null;
        }

        return getHelper(root, key);
    }

    private boolean containsKeyHelper(BSTNode node, K key){
        if (node == null){
            return false;
        }

        int cmp = node.key.compareTo(key);
        if (cmp == 0) {
            return true;
        }
        return containsKeyHelper(cmp < 0 ? node.right : node.left, key);
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null || root.key == null){
            return false;
        }

        return containsKeyHelper(root, key);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root.left = null;
        root.right = null;
        root.key = null;
        root.value = null;
        size -= size;
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    private BSTNode findPredecessor(BSTNode node){
        if (node.right == null){
            return node;
        }
        return findPredecessor(node.right);
    }

    private BSTNode replaceNode (BSTNode node){
        BSTNode predecessor;
        BSTNode retNode;
        size -= 1;
        if (node.right == null && node.left == null){
            retNode = null;
        } else if (node.right != null && node.left != null){
           predecessor = findPredecessor(node.left);
           predecessor.right = node.right;
           retNode = predecessor;
        } else if (node.right != null){
            retNode = node.right;
        } else {
            retNode = node.left;
        }
        return retNode;
    }

    private BSTNode removeNodeHelper(BSTNode node, K key){
        BSTNode retNode;
        if (node == null || node.key == null){
            return null;
        }

        int cmp = node.key.compareTo(key);

        if (cmp == 0){
            retNode = node;
            replaceNode(node);
        } else if (cmp < 0 && node.right != null && (node.right.key.compareTo(key) == 0)){
            retNode = node.right;
            node.right = replaceNode(node.right);
        } else if (cmp > 0 && node.left != null && (node.left.key.compareTo(key) == 0)){
            retNode = node.left;
            node.left = replaceNode(node.left);
        } else if (cmp < 0){
            retNode = removeNodeHelper(node.right, key);
        } else {
            retNode = removeNodeHelper(node.left, key);
        }

        return retNode;
    }

    @Override
    public V remove(K key) {
        BSTNode removedNode;
        V retValue;
        if (root == null || root.key == null){
            return null;
        }

        int cmp = root.key.compareTo(key);

        if (cmp == 0){
            retValue = root.value;
            root = replaceNode(root);
            return retValue;
        }
        removedNode = removeNodeHelper(root, key);
        if (removedNode == null || removedNode.value == null){
            return null;
        }
        return removedNode.value;
    }

    @Override
    public Iterator<K> iterator(){
        return keys.iterator();
    }
}
