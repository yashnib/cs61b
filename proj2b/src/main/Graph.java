package main;

import edu.princeton.cs.algs4.In;
import org.apache.hc.core5.annotation.Internal;

import java.net.Inet4Address;
import java.util.*;

public class Graph extends HashMap<Integer, List<Integer>> {
    private int numVertices;
    private int numEdges;

    public Graph() {
        super();
    }

    public void addEdge(int nodeA, int nodeB) {

        if (!super.containsKey(nodeA)) {
            super.computeIfAbsent(nodeA, k -> new ArrayList<>()).add(nodeB);
            numVertices+=2;
        } else {
            super.get(nodeA).add(nodeB);
            numVertices++;
        }
        numEdges++;
    }

    public List<Integer> getNeighbors(int node) {
        if (!super.containsKey(node)){
            return null;
        }
        return super.get(node);
    }

    public int V() {
        return numVertices;
    }

    public int E() {
        return numEdges;
    }
}
