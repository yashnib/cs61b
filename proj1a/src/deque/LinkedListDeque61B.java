package deque;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LinkedListDeque61B<T> implements Deque61B<T> {

    private class Node {
         public T item;
         public Node prev;
         public Node next;

         public Node(T i, Node p, Node n){
             item = i;
             prev = p;
             next = n;
         }
    }

    /* The first item (if it exists) is at sentinel.next */
    private final Node sentinel;
    private int size;

    public LinkedListDeque61B() {
        sentinel = new Node(null, null, null);
        size = 0;
    }

   /* public LinkedListDeque61B(T x){
        sentinel = new Node(null, sentinel, sentinel);
        sentinel.next = new Node(x, sentinel, sentinel);
        size = 1;
    } */

    @Override
    public void addFirst(T x) {
        size += 1;
        if (sentinel.prev == null) {
            sentinel.next = new Node(x, sentinel, sentinel);
            sentinel.prev = sentinel.next;
        }
        else {
            sentinel.next.prev = new Node(x, sentinel, sentinel.next);
            sentinel.next = sentinel.next.prev;
        }
    }

    @Override
    public void addLast(T x) {
        size += 1;
        if (sentinel.prev == null) {
            sentinel.next = new Node(x, sentinel, sentinel);
            sentinel.prev = sentinel.next;
        } else {
            sentinel.prev.next = new Node(x, sentinel.prev, sentinel);
            sentinel.prev = sentinel.prev.next;
        }
    }

    @Override
    public List<T> toList() {
        List<T> returnList = new ArrayList<>();
        Node p = sentinel.next;
        if (p == null) return returnList;

        while (p != sentinel){
            returnList.add(p.item);
            p = p.next;
        }

        return returnList;
    }

    @Override
    public boolean isEmpty() {
        return sentinel.next == null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        Node p = sentinel.next;
        if (p == null) return null;

        T firstItem = p.item;
        sentinel.next = p.next;
        return firstItem;
    }

    @Override
    public T removeLast() {
        Node p = sentinel.prev;
        if (p == null) return null;

        T lastItem = p.item;
        p.prev.next = sentinel;
        sentinel.prev = p.prev;
        return lastItem;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size){
            return null;
        }
        int x = 0;
        Node p = sentinel.next;
        while (x < index) {
            p = p.next;
            x++;
        }
        return p.item;
    }

    private T getRecursive(int index, Node p){
        if (index == 0) {
            return p.item;
        }
        else {
            return getRecursive(index - 1, p.next);
        }
    }

    @Override
    public T getRecursive(int index) {
        if (index < 0 || index >= size) return null;
        else {
            Node p = sentinel.next;
            return getRecursive(index, p);
        }
    }
}
