package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Dungeon extends Room {

    private static int MIN_DIMENSION = 5;
    private static int MAX_DIMENSION = 8;;

    private static int trimDungeon = 1;
    private static int corridorMargin = 1;
    private static int minCorridorThickness = 2;

    private boolean splitHorizontal;
    private boolean splitVertical;

    private Dungeon leftDungeon;
    private Dungeon rightDungeon;

    private Map<Dungeon, Dungeon> siblings;

    Random dungeonRNG;

    private int seed;

    List<Room> hallways;

    int left, right, top, bottom;

    public Dungeon(int left, int right, int top, int bottom, Random rng) {
        super(left, right, top, bottom);
        if (getHeight() < MIN_DIMENSION) {
            throw new IllegalArgumentException("Room too short");
        }
        if (getWidth() < MIN_DIMENSION) {
            throw new IllegalArgumentException("Room too thin");
        }

        this.dungeonRNG = rng;

        leftDungeon = null;
        rightDungeon = null;

        splitVertical = false;
        splitHorizontal = false;

        this.seed = seed;

        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public boolean isLeaf () {
        return !splitVertical && !splitHorizontal;
    }

    private void connectSiblingsHorizontal() {

    }

    private void horizontalSplit() {
        int left = getLeft();
        int right = getRight();
        int top = getTop();
        int bottom = getBottom();
        if (top - MIN_DIMENSION <= bottom + MIN_DIMENSION) return;
        int splitPoint = dungeonRNG.nextInt(bottom + MIN_DIMENSION, top - MIN_DIMENSION);
        Random leftRNG = new Random(dungeonRNG.nextLong());
        leftDungeon = new Dungeon(left, right, splitPoint, bottom, leftRNG);

        Random rightRNG = new Random(dungeonRNG.nextLong());
        rightDungeon = new Dungeon(left, right, top, splitPoint + 1, rightRNG);

        leftDungeon.splitDungeon();
        rightDungeon.splitDungeon();
        splitHorizontal = true;

        connectSiblingsHorizontal(leftDungeon, rightDungeon);
    }

    private void connectSiblingsVertical() {

    }

    private void verticalSplit() {
        int left = getLeft();
        int right = getRight();
        int top = getTop();
        int bottom = getBottom();
        if (right - MIN_DIMENSION <= left + MIN_DIMENSION) return;
        int splitPoint = dungeonRNG.nextInt(left + MIN_DIMENSION, right - MIN_DIMENSION);

        Random leftRNG = new Random(dungeonRNG.nextLong());
        leftDungeon = new Dungeon(left, splitPoint, top, bottom, leftRNG);

        Random rightRNG = new Random(dungeonRNG.nextLong());
        rightDungeon = new Dungeon(splitPoint + 1, right, top, bottom, rightRNG);
        leftDungeon.splitDungeon();
        rightDungeon.splitDungeon();
        splitVertical = true;

        connectSiblingsVertical(leftDungeon, rightDungeon);
    }

    public void splitDungeon() {
        float rand = dungeonRNG.nextFloat();
        if (rand < 0.5f && getWidth() >= 2 * MIN_DIMENSION) {
            verticalSplit();
            return;
        }
        else if (getHeight() >= 2 * MIN_DIMENSION) {
            horizontalSplit();
            return;
        }

        // Force split if there is too much space.
        if (getWidth() > MAX_DIMENSION) {
            verticalSplit();
            return;
        }

        if (getHeight() > MAX_DIMENSION) {
            horizontalSplit();
            return;
        }
    }

    public void drawDungeon(TETile[][] tiles) {
        if (isLeaf()) {
            for (int i = left; i < right; i++) {
                for (int j = bottom; j < top; j++) {
                    if (i >= 0 && i < tiles.length && j >= 0 && j < tiles[0].length) {
                        tiles[i][j] = Tileset.FLOWER;
                    }
                }
            }
        } else {
            leftDungeon.drawDungeon(tiles);
            rightDungeon.drawDungeon(tiles);
        }
    }

    public void trim() {
        left += trimDungeon;
        right -= trimDungeon;
        top -= trimDungeon;
        bottom += trimDungeon;

        if (leftDungeon != null) {
            leftDungeon.trim();
        }
        if (rightDungeon != null) {
            rightDungeon.trim();
        }
    }

    public void generateHallways() {
        if (leftDungeon.isLeaf() || rightDungeon.isLeaf()) {
            ;
        }
        else {
            leftDungeon.generateHallways();
            rightDungeon.generateHallways();
        }

        // Pick random points inside each room, avoiding walls (offset by +1 and -1)
        int firstPointX = dungeonRNG.nextInt(leftChildRoom.getX() + 1, firstRoom.getX() + firstRoom.getWidth() - 1);
        int firstPointY = dungeonRNG.nextInt(firstRoom.getY() + 1, firstRoom.getY() + firstRoom.getHeight() - 1);
        int secondPointX = dungeonRNG.nextInt(secondRoom.getX() + 1, secondRoom.getX() + secondRoom.getWidth() - 1);
        int secondPointY = dungeonRNG.nextInt(secondRoom.getY() + 1, secondRoom.getY() + secondRoom.getHeight() - 1);

        // Make sure leftPoint is truly to the left of rightPoint
        if (firstPointX > secondPointX) {
            int tempX = firstPointX;
            int tempY = firstPointY;
            firstPointX = secondPointX;
            firstPointY = secondPointY;
            secondPointX = tempX;
            secondPointY = tempY;
        }

        int dx = secondPointX - firstPointX;
        int dy = secondPointY - firstPointY;

        if (dungeonRNG.nextBoolean()) {
            // Horizontal then vertical
            Point hStart = new Point(firstPointX, firstPointY);
            hallways.add(new Room(dx + 1, 1, hStart)); // horizontal segment

            int vHeight = Math.abs(dy);
            int vStartY = Math.min(firstPointY, secondPointY);
            Point vStart = new Point(secondPointX, vStartY);
            hallways.add(new Room(1, vHeight, vStart)); // vertical segment
        } else {
            // Vertical then horizontal
            int vHeight = Math.abs(dy);
            int vStartY = Math.min(firstPointY, secondPointY);
            Point vStart = new Point(firstPointX, vStartY);
            hallways.add(new Room(1, vHeight, vStart)); // vertical segment

            Point hStart = new Point(firstPointX, secondPointY);
            hallways.add(new Room(dx + 1, 1, hStart)); // horizontal segment
        }
    }

    public int getSeed() {
        return this.seed;
    }

}
