import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int gridSize;
    Cell[][] cellGrid;
    int numOpenSites;
    WeightedQuickUnionUF cellDisjointSet;
    int virtualTopSetIndex;
    int virtualBottomSetIndex;

    private enum Cell {
        CLOSED, OPEN
    }

    public Percolation(int N) {
        if (N <= 0){
            throw new IllegalArgumentException();
        }

        gridSize = N;
        cellGrid = new Cell[N][N];
        numOpenSites = 0;
        cellDisjointSet = new WeightedQuickUnionUF(N*N + 2);
        virtualTopSetIndex = N*N;
        virtualBottomSetIndex = N*N + 1;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                cellGrid[i][j] = Cell.CLOSED;
            }
        }
    }

    public void open(int row, int col) {
        if (!isValidArgument(row, col)) {
            throw new IndexOutOfBoundsException();
        }

        if (isOpen(row, col)){
            return;
        }

        int index = getOneDimensionalIndex(row, col);

        // If element is in the top row, union it with the virtual top
        if (row == 0) {
            cellDisjointSet.union(index, virtualTopSetIndex);;
        }

        unionWithOpenNeighbors(row, col, index);

        // If element is in the bottom row and is connected to the virtual top after unionizing with its neighbors, union it with the virtual bottom
        if (row == gridSize - 1 && !percolates()) {
            cellDisjointSet.union(index, virtualBottomSetIndex);
        }

        cellGrid[row][col] = Cell.OPEN;
        numOpenSites++;
    }

    public boolean isOpen(int row, int col) {
        if (!isValidArgument(row, col)){
            throw new IndexOutOfBoundsException();
        }

        return cellGrid[row][col] == Cell.OPEN;
    }

    public boolean isFull(int row, int col) {
        if (!isValidArgument(row, col)) {
            throw new IndexOutOfBoundsException();
        }

        int index = getOneDimensionalIndex(row, col);
        return cellDisjointSet.connected(index, virtualTopSetIndex) ;
    }

    public int numberOfOpenSites() {
        return numOpenSites;
    }

    public boolean percolates() {
        return cellDisjointSet.connected(virtualTopSetIndex, virtualBottomSetIndex);
    }

    private int getOneDimensionalIndex(int row, int col) {
        return gridSize * row + col;
    }

    private void unionWithOpenNeighbors(int row, int col, int oneDimensionalIndex) {
        int upper = row >= 1 && isOpen(row - 1, col) ? oneDimensionalIndex - gridSize : -1;
        int lower = row < gridSize - 1 && isOpen(row + 1, col) ? oneDimensionalIndex + gridSize : -1;
        int left =  col >= 1 && isOpen(row, col - 1) ? oneDimensionalIndex - 1 : -1;
        int right = col < gridSize - 1 && isOpen(row, col + 1) ? oneDimensionalIndex + 1 : -1;

        if (upper != -1) {
            cellDisjointSet.union(oneDimensionalIndex, upper);
        }

        if (lower != -1) {
            cellDisjointSet.union(oneDimensionalIndex, lower);
        }

        if (left != -1) {
            cellDisjointSet.union(oneDimensionalIndex, left);
        }

        if (right != -1) {
            cellDisjointSet.union(oneDimensionalIndex, right);
        }
    }

    private boolean isValidArgument(int row, int col) {
        return row >= 0 && row < gridSize && col >= 0 && col < gridSize;
    }

}
