package core;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class Dungeon extends Room {
    private Dungeon childA;
    private Dungeon childB;

    private Random dungeonRNG;

    private static List<Room> hallways;

    private Point dungeonPosition;

    private Room room;

    private static Set<Room> hallwaysSet = new HashSet<>();

    private static Set<Room> roomsSet = new HashSet<>();

    public Dungeon(int width, int height, Point position, boolean drawOpposite, Random rng) {
        super(width, height, position, drawOpposite);

        dungeonRNG = rng;

        childA = null;
        childB = null;

        dungeonPosition = position;

        room = null;
    }

    public boolean isLeaf() {
        return childA == null && childB == null;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    private void splitDungeonVertical() {
        if (this == null) throw new IllegalArgumentException("Dungeon instance is null");

        int parentDungeonHeight = this.getHeight();
        int parentDungeonWidth = this.getWidth();
        int parentDungeonY = this.getY();
        int parentDungeonX = this.getX();
        int minDimension = this.getMinRoomDimension();

        if (parentDungeonHeight <= 3 * minDimension) {
            return;
        }

        int minSplitY = parentDungeonY + minDimension;
        int maxSplitY = parentDungeonY + parentDungeonHeight - minDimension;
        if (minSplitY >= maxSplitY) {
            return;
        }
        int splitY = this.dungeonRNG.nextInt(minSplitY, maxSplitY);

        // Two children will be generated from the split with both having the same width but different heights
        Point childAPosition = new Point(parentDungeonX, parentDungeonY);
        Random childARNG = new Random(this.dungeonRNG.nextLong());
        this.childA = new Dungeon(parentDungeonWidth, splitY - parentDungeonY, childAPosition,false, childARNG);
        this.childA = this.childA.splitDungeon();

        Point childBPosition = new Point(parentDungeonX, splitY);
        Random childBRNG = new Random(this.dungeonRNG.nextLong());
        this.childB = new Dungeon(parentDungeonWidth, parentDungeonY + parentDungeonHeight - splitY, childBPosition, false, childBRNG);
        this.childB = this.childB.splitDungeon();
    }

    private void splitDungeonHorizontal() {
        if (this == null) throw new IllegalArgumentException("Dungeon instance is null");

        int parentDungeonHeight = this.getHeight();
        int parentDungeonWidth = this.getWidth();
        int parentDungeonY = this.getY();
        int parentDungeonX = this.getX();
        int minDimension = this.getMinRoomDimension();

        if (parentDungeonWidth <= 3 * minDimension) {
            return;
        }

        int minSplitX = parentDungeonX + minDimension;
        int maxSplitX = parentDungeonX + parentDungeonWidth - minDimension;
        int splitX = this.dungeonRNG.nextInt(minSplitX, maxSplitX);

        if (minSplitX >= maxSplitX) {
            return;
        }

        // Two children will be generated from the split with both having the same height but different widths
        Point childAPosition = new Point(parentDungeonX, parentDungeonY);
        Random childARNG = new Random(this.dungeonRNG.nextLong());
        this.childA = new Dungeon(splitX - parentDungeonX, parentDungeonHeight, childAPosition, false, childARNG);
        this.childA = this.childA.splitDungeon();

        Point childBPosition = new Point(splitX, parentDungeonY);
        Random childBRNG = new Random(this.dungeonRNG.nextLong());
        this.childB = new Dungeon(parentDungeonX + parentDungeonWidth - splitX, parentDungeonHeight, childBPosition, false, childBRNG);
        this.childB = this.childB.splitDungeon();
    }

    public Dungeon splitDungeon() {
        if (this== null) throw new IllegalArgumentException("splitDungeon(): Dungeon instance is null");

        int minDimension = this.getMinRoomDimension();

        if ((this.getWidth() <= 3 * minDimension && this.getHeight() <= 3 * minDimension)) {
          //  parentDungeon.leaf = true;
            return this;
        }

        enum Direction {
            VERTICAL,
            HORIZONTAL
        }

        Direction splitDirection;

        // Split the dungeon vertically if is narrower than 2 * minDimension and split is horizontally if is shorter than 2 * minDimension
        if (this.getWidth() <= 3 * minDimension) {
            splitDirection = Direction.VERTICAL;
        } else if (this.getHeight() <= 3 * minDimension) {
            splitDirection = Direction.HORIZONTAL;
        } else {
            splitDirection = this.dungeonRNG.nextBoolean() ? Direction.VERTICAL : Direction.HORIZONTAL;
        }

        if (splitDirection == Direction.VERTICAL) {
            this.splitDungeonVertical();
        } else {
            this.splitDungeonHorizontal();
        }

        return this;
    }

    public Room getRoom() {
        if (this == null) {
            throw new IllegalArgumentException("Dungeon instance is null");
        }

        if (this.isLeaf() && this.room != null) {
            return this.room;
        }

        return this.childB.getRoom();
    }

    public void createRoom() {
        if (this == null) {
            throw new IllegalArgumentException("createRoom(): Dungeon instance is null");
        }

        if (this.isLeaf()) {
            int roomWidth, roomHeight;
            roomWidth = this.getWidth() / 2;
            roomHeight = this.getHeight() / 2;

            int roomX = this.getX();
            int roomY = this.getY();

            Point roomPosition = new Point(roomX, roomY);
            this.room = new Room(roomWidth, roomHeight, roomPosition, false);
            roomsSet.add(this.room);
        } else {
            this.childA.createRoom();
            this.childB.createRoom();
        }
    }

    public void connectRooms(Room roomA, Room roomB) {
        if (roomA == null || roomB == null) {
            throw new IllegalArgumentException("connectRooms(): room arguments cannot be null");
        }

        int roomAX = roomA.getX() + 2;
        int roomBX = roomB.getX() + 2;
        int roomAY = roomA.getY() + 2;
        int roomBY = roomB.getY() + 2;

        int roomAWidth = roomA.getWidth();
        int roomAHeight = roomA.getHeight();
        int roomBWidth = roomB.getWidth();
        int roomBHeight = roomB.getHeight();

        int left = Math.min(roomAX, roomBX);
        int right = Math.max(roomAX, roomBX);
        int bottom = Math.min(roomAY, roomBY);
        int top = Math.max(roomAY, roomBY);

        if (left == roomAX) {
            if (bottom == roomAY) {
                Point verticalHallwayPosition = new Point(roomAX, roomAY);
                hallwaysSet.add(new Room(1, top - bottom + 1, verticalHallwayPosition, false));
                Point horizontalHallwayPosition = new Point(roomAX, top);
                hallwaysSet.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false));
            } else {
                Point horizontalHallwayPosition = new Point(roomAX, roomAY);
                hallwaysSet.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false));
                Point verticalHallwayPosition = new Point(right, roomAY);
                hallwaysSet.add(new Room(1, top - bottom + 1, verticalHallwayPosition, true));
            }
        } else {
            if (bottom == roomBY) {
                Point verticalHallwayPosition = new Point(roomBX, roomBY);
                hallwaysSet.add(new Room(1, top - bottom + 1, verticalHallwayPosition, false));
                Point horizontalHallwayPosition = new Point(right, roomBY);
                hallwaysSet.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false));
            } else {
                Point horizontalHallwayPosition = new Point(roomBX, roomBY);
                hallwaysSet.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false));
                Point verticalHallwayPosition = new Point(right, roomBY);
                hallwaysSet.add(new Room(1, top - bottom + 1, verticalHallwayPosition, true));
            }
        }
    }

    public void drawHallways(TETile[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("drawHallways(): World cannot be null");
        }
        for (Room r : hallwaysSet) {
            if (r.getDrawOpposite()) {
                for (int i = r.getX(); i > r.getX() - r.getWidth(); i--) {
                    for (int j = r.getY(); j > r.getY() - r.getHeight(); j--) {
                        tiles[i][j] = Tileset.FLOWER;
                    }
                }
            } else {
                for (int i = r.getX(); i < r.getX() + r.getWidth(); i++) {
                    for (int j = r.getY(); j < r.getY() + r.getHeight(); j++) {
                        tiles[i][j] = Tileset.FLOWER;
                    }
                 }
            }
        }
    }

   public void drawRooms(TETile[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("drawRooms(): World cannot be null");
        }
        for (Room r : roomsSet) {
           // if (r.getWidth() == 1) continue;
            for (int i = r.getX(); i < r.getX() + r.getWidth(); i++) {
                for (int j = r.getY(); j < r.getY() + r.getHeight(); j++) {
                    tiles[i][j] = Tileset.FLOWER;
                }
            }
        }
    }

    public void createHallways() {
       if (this == null || this.isLeaf()) {
            throw new IllegalArgumentException("createHallways(): Dungeon instance is null");
        }

        Room childARoom = this.childA.getRoom();
        Room childBRoom = this.childB.getRoom();

        connectRooms(childARoom, childBRoom);

        this.childA.createHallways();
        this.childB.createHallways();

    }

    public void drawWalls(TETile[][] tiles) {
        if (tiles == null) {
            return;
        }

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j] == Tileset.FLOWER) {
                    if (tiles[i - 1][j] == Tileset.NOTHING) {
                        tiles[i - 1][j] = Tileset.WALL;
                    }
                    if (tiles[i + 1][j] == Tileset.NOTHING) {
                        tiles[i + 1][j] = Tileset.WALL;
                    }
                    if (tiles[i][j - 1] ==  Tileset.NOTHING) {
                        tiles[i][j - 1] = Tileset.WALL;
                    }
                    if (tiles[i][j + 1] == Tileset.NOTHING) {
                        tiles[i][j + 1] = Tileset.WALL;
                    }
                }
             }
         }
    }
}

