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

    public Room getRoom() {
        return room;
    }

    private Dungeon splitDungeonVertical(Dungeon parentDungeon) {
        if (parentDungeon == null) return null;

        int parentDungeonHeight = parentDungeon.getHeight();
        int parentDungeonWidth = parentDungeon.getWidth();
        int parentDungeonY = parentDungeon.getY();
        int parentDungeonX = parentDungeon.getX();
        int minDimension = parentDungeon.getMinRoomDimension();

        if (parentDungeonHeight <= 3 * minDimension) {
            return parentDungeon;
        }

        int minSplitY = parentDungeonY + minDimension;
        int maxSplitY = parentDungeonY + parentDungeonHeight - minDimension;
        if (minSplitY >= maxSplitY) {
            return parentDungeon;
        }
        int splitY = parentDungeon.dungeonRNG.nextInt(minSplitY, maxSplitY);

        // Two children will be generated from the split with both having the same width but different heights
        Point childAPosition = new Point(parentDungeonX, parentDungeonY);
        Random childARNG = new Random(parentDungeon.dungeonRNG.nextLong());
        parentDungeon.childA = new Dungeon(parentDungeonWidth, splitY - parentDungeonY, childAPosition,false, childARNG);
        parentDungeon.childA = splitDungeon(parentDungeon.childA);

        Point childBPosition = new Point(parentDungeonX, splitY);
        Random childBRNG = new Random(parentDungeon.dungeonRNG.nextLong());
        parentDungeon.childB = new Dungeon(parentDungeonWidth, parentDungeonY + parentDungeonHeight - splitY, childBPosition, false, childBRNG);
        parentDungeon.childB = splitDungeon(parentDungeon.childB);

        return parentDungeon;
    }

    private Dungeon splitDungeonHorizontal(Dungeon parentDungeon) {
        if (parentDungeon == null) return null;

        int parentDungeonHeight = parentDungeon.getHeight();
        int parentDungeonWidth = parentDungeon.getWidth();
        int parentDungeonY = parentDungeon.getY();
        int parentDungeonX = parentDungeon.getX();
        int minDimension = parentDungeon.getMinRoomDimension();

        if (parentDungeonWidth <= 3 * minDimension) {
            return parentDungeon;
        }

        int minSplitX = parentDungeonX + minDimension;
        int maxSplitX = parentDungeonX + parentDungeonWidth - minDimension;
        int splitX = parentDungeon.dungeonRNG.nextInt(minSplitX, maxSplitX);

        if (minSplitX >= maxSplitX) {
            return parentDungeon;
        }

        // Two children will be generated from the split with both having the same height but different widths
        Point childAPosition = new Point(parentDungeonX, parentDungeonY);
        Random childARNG = new Random(parentDungeon.dungeonRNG.nextLong());
        parentDungeon.childA = new Dungeon(splitX - parentDungeonX, parentDungeonHeight, childAPosition, false, childARNG);
        parentDungeon.childA = splitDungeon(parentDungeon.childA);

        Point childBPosition = new Point(splitX, parentDungeonY);
        Random childBRNG = new Random(parentDungeon.dungeonRNG.nextLong());
        parentDungeon.childB = new Dungeon(parentDungeonX + parentDungeonWidth - splitX, parentDungeonHeight, childBPosition, false, childBRNG);
        parentDungeon.childB = splitDungeon(parentDungeon.childB);

        return parentDungeon;
    }

    public Dungeon splitDungeon(Dungeon parentDungeon) {
        if (parentDungeon == null) return null;

        int minDimension = parentDungeon.getMinRoomDimension();

        if ((parentDungeon.getWidth() <= 3 * minDimension && parentDungeon.getHeight() <= 3 * minDimension)) {
          //  parentDungeon.leaf = true;
            return parentDungeon;
        }

        enum Direction {
            VERTICAL,
            HORIZONTAL
        }

        Direction splitDirection;

        // Split the dungeon vertically if is narrower than 2 * minDimension and split is horizontally if is shorter than 2 * minDimension
        if (parentDungeon.getWidth() <= 3 * minDimension) {
            splitDirection = Direction.VERTICAL;
        } else if (parentDungeon.getHeight() <= 3 * minDimension) {
            splitDirection = Direction.HORIZONTAL;
        } else {
            splitDirection = parentDungeon.dungeonRNG.nextBoolean() ? Direction.VERTICAL : Direction.HORIZONTAL;
        }

        if (splitDirection == Direction.VERTICAL) {
            parentDungeon = splitDungeonVertical(parentDungeon);
        } else {
            parentDungeon = splitDungeonHorizontal(parentDungeon);
        }

        return parentDungeon;
    }

    public Room getRoom(Dungeon parentDungeon) {
        if (parentDungeon == null) {
            return null;
        }

        if (parentDungeon.isLeaf() && parentDungeon.room != null) {
            return parentDungeon.room;
        }

        return getRoom(parentDungeon.childB);
    }

    public void createRoom(Dungeon parentDungeon) {
        if (parentDungeon == null) {
            return;
        }

        if (parentDungeon.isLeaf()) {
            int roomWidth, roomHeight;
            roomWidth = parentDungeon.getWidth() / 2;
            roomHeight = parentDungeon.getHeight() / 2;

            int roomX = parentDungeon.getX();
            int roomY = parentDungeon.getY();

            Point roomPosition = new Point(roomX, roomY);
            parentDungeon.room = new Room(roomWidth, roomHeight, roomPosition, false);
            roomsSet.add(parentDungeon.room);
        } else {
            createRoom(parentDungeon.childA);
            createRoom(parentDungeon.childB);
        }
    }

    public void connectRooms(Room roomA, Room roomB) {

        if (roomA == null || roomB == null) {
            return;
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
        for (Room r : roomsSet) {
           // if (r.getWidth() == 1) continue;
            for (int i = r.getX(); i < r.getX() + r.getWidth(); i++) {
                for (int j = r.getY(); j < r.getY() + r.getHeight(); j++) {
                    tiles[i][j] = Tileset.FLOWER;
                }
            }
        }
    }

    public void createHallways(Dungeon parentDungeon) {
        if (parentDungeon == null || parentDungeon.isLeaf()) {
            return;
        }

        Room childARoom = getRoom(parentDungeon.childA);
        Room childBRoom = getRoom(parentDungeon.childB);

        connectRooms(childARoom, childBRoom);

        createHallways(parentDungeon.childA);
        createHallways(parentDungeon.childB);

    }

    public void drawWalls(TETile[][] tiles) {
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

