public class UnionFind {
    private int[] parents;
    private final int numElements;

    /* Creates a UnionFind data structure holding N items. Initially, all
       items are in disjoint sets. */
    public UnionFind(int N) {
        parents = new int[N];
        for (int i = 0; i < N; i++){
            parents[i] = -1;
        }
        numElements = N;
    }

    /* Returns the size of the set V belongs to. */
    public int sizeOf(int v) {
        int rootV = find(v);
        return -(parents[rootV]);
    }

    /* Returns the parent of V. If V is the root of a tree, returns the
       negative size of the tree for which V is the root. */
    public int parent(int v) {
        if (v >= numElements || v < 0){
            throw new IllegalArgumentException();
        }
        return parents[v];
    }

    /* Returns true if nodes/vertices V1 and V2 are connected. */
    public boolean connected(int v1, int v2) {
        int rootV1 = find(v1);
        int rootV2 = find(v2);
        return (rootV1 == rootV2);
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. If invalid items are passed into this
       function, throw an IllegalArgumentException. */
    public int find(int v) {
        if (v >= numElements || v < 0){
            throw new IllegalArgumentException();
        }
        else if (parents[v] < 0){
            return v;
        }

        int [] trackArray = new int[numElements];
        int trackIndex = 0;
        while (v >= 0){
            trackArray[trackIndex++] = v;
            v = parents[v];
        }
        int rootV = trackArray[trackIndex-1];
        for (int i = 0; i < trackIndex - 1; i++) {
            int currentIndex = trackArray[i];
            parents[currentIndex] = rootV;
        }
        return rootV;
    }

    /* Connects two items V1 and V2 together by connecting their respective
       sets. V1 and V2 can be any element, and a union-by-size heuristic is
       used. If the sizes of the sets are equal, tie break by connecting V1's
       root to V2's root. Union-ing an item with itself or items that are
       already connected should not change the structure. */

    public void union(int v1, int v2) {
        if (connected(v1, v2)) {
            return;
        }

        int sizeV1 = sizeOf(v1);
        int sizeV2 = sizeOf(v2);
        int rootV1 = find(v1);
        int rootV2 = find(v2);

        if (sizeV1 > sizeV2){
            parents[v2] = rootV1;
            parents[rootV2] = rootV1;
            parents[rootV1] -= sizeV2;
        }
        else {
            parents[v1] = rootV2;
            parents[rootV1] = rootV2;
            parents[rootV2] -= sizeV1;
        }
    }

}
