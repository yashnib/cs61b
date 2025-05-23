package core;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class Dungeon extends Room {
    private Dungeon childA;
    private Dungeon childB;

    private long dungeonSeed;
    private Random dungeonRNG;

    private static List<Room> hallways;

    private Room room;

   // private Set<Room> hallwaysSet;

    private Set<Point> portalsSet;

  //  private Set<Room> roomsSet;

    private Point avatarPosition;

    private TETile[][] dungeonTiles;

    private TETile floor = Tileset.DUNGEON_FLOOR;
    private TETile wall = Tileset.WALL;
    private TETile portal = Tileset.PORTAL;
    private TETile avatar = Tileset.WARRIOR;

    public Dungeon(int width, int height, Point position, boolean drawOpposite, long seed) {
        super(width, height, position, drawOpposite);

        dungeonSeed = seed;
        dungeonRNG = new Random(seed);

        childA = null;
        childB = null;

        room = null;

      //  hallwaysSet = new HashSet<>();
        portalsSet = new HashSet<>();
      //  roomsSet = new HashSet<>();
    }

    public boolean isLeaf() {
        return childA == null && childB == null;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setDungeonTiles(TETile[][] tiles) {
        dungeonTiles = tiles;
    }

    public void initializeDungeon() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                dungeonTiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public TETile[][] getDungeonTiles() {
        return dungeonTiles;
    }

    private void splitDungeonVertical() {
        if (this == null) return;

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
        this.childA = new Dungeon(parentDungeonWidth, splitY - parentDungeonY, childAPosition, false, this.dungeonRNG.nextLong());
        this.childA = this.childA.splitDungeon();

        Point childBPosition = new Point(parentDungeonX, splitY);
        this.childB = new Dungeon(parentDungeonWidth, parentDungeonY + parentDungeonHeight - splitY, childBPosition, false, this.dungeonRNG.nextLong());
        this.childB = this.childB.splitDungeon();
    }

    private void splitDungeonHorizontal() {
        if (this == null) return;

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
        this.childA = new Dungeon(splitX - parentDungeonX, parentDungeonHeight, childAPosition, false, this.dungeonRNG.nextLong());
        this.childA = this.childA.splitDungeon();

        Point childBPosition = new Point(splitX, parentDungeonY);
        this.childB = new Dungeon(parentDungeonX + parentDungeonWidth - splitX, parentDungeonHeight, childBPosition, false, this.dungeonRNG.nextLong());
        this.childB = this.childB.splitDungeon();
    }

    public Dungeon splitDungeon() {
        if (this == null) throw new IllegalArgumentException("splitDungeon(): Dungeon instance is null");

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

    public void placePortals() {
        int numPortals = 10;
        int placed = 0;

        int dungeonWidth = getWidth();
        int dungeonHeight = getHeight();

        while (placed < numPortals) {
            int x = dungeonRNG.nextInt(dungeonWidth);
            int y = dungeonRNG.nextInt(dungeonHeight);

            if (dungeonTiles[x][y] == floor) {
                portalsSet.add(new Point(x, y));
                placed++;
            }
        }
    }

    public void drawPortals() {
        if (portalsSet == null || portalsSet.isEmpty()) {
            return;
        }

        for (Point p : portalsSet) {
            int x = p.getX();
            int y = p.getY();
            dungeonTiles[x][y] = portal;
        }
    }

    public Set<Point> getPortals() {
        return portalsSet;
    }

    public void setPortals(Set<Point> portals) {
        portalsSet = portals;
    }

    public void removePortal(Point portal) {
        if (portalsSet == null) return;
        portalsSet.remove(portal);
    }

    public Set<Room> createRoom(Set<Room> rooms) {
        if (this == null) {
            return rooms;
        }

        if (this.isLeaf()) {
            int roomWidth, roomHeight;
            roomWidth = this.getWidth() / 2;
            roomHeight = this.getHeight() / 2;

            int roomX = this.getX();
            int roomY = this.getY();

            Point roomPosition = new Point(roomX, roomY);
            this.room = new Room(roomWidth, roomHeight, roomPosition, false);
            rooms.add(this.room);
        } else {
            this.childA.createRoom(rooms);
            this.childB.createRoom(rooms);
        }
        return rooms;
    }

    public Set<Room> connectRooms(Room roomA, Room roomB, Set<Room> hallways) {
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
                hallways.add(new Room(1, top - bottom + 1, verticalHallwayPosition, false));
                Point horizontalHallwayPosition = new Point(roomAX, top);
                hallways.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false));
            } else {
                Point horizontalHallwayPosition = new Point(roomAX, roomAY);
                hallways.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false));
                Point verticalHallwayPosition = new Point(right, roomAY);
                hallways.add(new Room(1, top - bottom + 1, verticalHallwayPosition, true));
            }
        } else {
            if (bottom == roomBY) {
                Point verticalHallwayPosition = new Point(roomBX, roomBY);
                hallways.add(new Room(1, top - bottom + 1, verticalHallwayPosition, false));
                Point horizontalHallwayPosition = new Point(right, roomBY);
                hallways.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false));
            } else {
                Point horizontalHallwayPosition = new Point(roomBX, roomBY);
                hallways.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false));
                Point verticalHallwayPosition = new Point(right, roomBY);
                hallways.add(new Room(1, top - bottom + 1, verticalHallwayPosition, true));
            }
        }
        return hallways;
    }

    public void drawHallways(Set<Room> hallways) {
        if (dungeonTiles == null) {
            throw new IllegalArgumentException("drawHallways(): World cannot be null");
        }
        for (Room r : hallways) {
            if (r.getDrawOpposite()) {
                for (int i = r.getX(); i > r.getX() - r.getWidth(); i--) {
                    for (int j = r.getY(); j > r.getY() - r.getHeight(); j--) {
                        dungeonTiles[i][j] = floor;
                    }
                }
            } else {
                for (int i = r.getX(); i < r.getX() + r.getWidth(); i++) {
                    for (int j = r.getY(); j < r.getY() + r.getHeight(); j++) {
                        dungeonTiles[i][j] = floor;
                    }
                }
            }
        }
    }

    public void drawRooms(Set<Room> rooms) {
        if (dungeonTiles == null) {
            throw new IllegalArgumentException("drawRooms(): World cannot be null");
        }
        for (Room r : rooms) {
            for (int i = r.getX(); i < r.getX() + r.getWidth(); i++) {
                for (int j = r.getY(); j < r.getY() + r.getHeight(); j++) {
                    dungeonTiles[i][j] = floor;
                }
            }
        }
    }

    public Set<Room> createHallways(Set<Room> hallways) {
        if (this == null || this.isLeaf()) {
            return hallways;
        }

        Room childARoom = this.childA.getRoom();
        Room childBRoom = this.childB.getRoom();

        connectRooms(childARoom, childBRoom, hallways);

        this.childA.createHallways(hallways);
        this.childB.createHallways(hallways);
        return hallways;
    }

    public void drawWalls() {
        if (dungeonTiles == null) {
            return;
        }

        for (int i = 0; i < dungeonTiles.length; i++) {
            for (int j = 0; j < dungeonTiles[0].length; j++) {
                if (dungeonTiles[i][j] == floor) {
                    if (dungeonTiles[i - 1][j] == Tileset.NOTHING) {
                        dungeonTiles[i - 1][j] = wall;
                    }
                    if (dungeonTiles[i + 1][j] == Tileset.NOTHING) {
                        dungeonTiles[i + 1][j] = wall;
                    }
                    if (dungeonTiles[i][j - 1] == Tileset.NOTHING) {
                        dungeonTiles[i][j - 1] = wall;
                    }
                    if (dungeonTiles[i][j + 1] == Tileset.NOTHING) {
                        dungeonTiles[i][j + 1] = wall;
                    }
                }
            }
        }
    }

    public void placeAvatarRandom() {
        int avatarX = this.dungeonRNG.nextInt(1, this.getWidth());
        int avatarY = this.dungeonRNG.nextInt(1, this.getHeight());
        while (dungeonTiles[avatarX][avatarY] != floor) {
            avatarX = this.dungeonRNG.nextInt(1, this.getWidth());
            avatarY = this.dungeonRNG.nextInt(1, this.getHeight());
        }
        dungeonTiles[avatarX][avatarY] = avatar;
        avatarPosition = new Point(avatarX, avatarY);
    }

    public void placeAvatarManual(Point position) {
        avatarPosition = position;
    }

    public Point getAvatarPosition() {
        return avatarPosition;
    }

    public long getSeed() {
        return dungeonSeed;
    }
}

