package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dungeon extends Room {
    private Dungeon firstChild, secondChild;
    private final Random dungeonRNG;
    private final int seed;
    private Room room;
    private List<Room> hallways;

    public Dungeon(int width, int height, Point position, int seed) {
        super(width, height, position);
        dungeonRNG = new Random(seed);
        firstChild = null;
        secondChild = null;
        this.seed = seed;
        room = null;
        hallways = new ArrayList<>();
    }

    public boolean isLeaf () {
        return firstChild == null && secondChild == null;
    }

    public Dungeon splitDungeon(int minRoomSize, Dungeon subDungeon, Random dungeonRNG) {
        if (!subDungeon.isLeaf()) {
            return subDungeon;
        }

        // To create the sub-dungeons, choose a vertical or horizontal split based on the proportions
        // of the main dungeon, if it is too wide, split horizontally, if it is too long, split vertically.
        // If the main dungeon is a square, split it horizontally or vertically.
        boolean splitVertical;
        int subDungeonWidth = subDungeon.getWidth();
        int subDungeonHeight = subDungeon.getHeight();
        int subDungeonX = subDungeon.getX();
        int subDungeonY = subDungeon.getY();

        if ((double) subDungeonWidth / subDungeonHeight >= 1.25) {
            splitVertical = false;
        } else if ((double) subDungeonHeight / subDungeonWidth >= 1.25) {
            splitVertical = true;
        } else {
            splitVertical = dungeonRNG.nextBoolean();
        }

        if (subDungeonWidth < 2 * minRoomSize || subDungeonHeight < 2 * minRoomSize) {
            return subDungeon;
        }

        if (splitVertical) {
            int splitDimension = dungeonRNG.nextInt(minRoomSize, subDungeonWidth - minRoomSize);

            Point firstChildPosition = new Point(subDungeonX, subDungeonY);
            subDungeon.firstChild = new Dungeon(subDungeonWidth, splitDimension, firstChildPosition, getSeed());

            Point secondChildPosition = new Point(subDungeonX, subDungeonY + splitDimension);
            subDungeon.secondChild = new Dungeon(subDungeonWidth, subDungeonHeight - splitDimension, secondChildPosition, getSeed());
        } else {
            int splitDimension = dungeonRNG.nextInt(minRoomSize, subDungeonHeight - minRoomSize);

            Point firstChildPosition = new Point(subDungeonX, subDungeonY);
            subDungeon.firstChild = new Dungeon(splitDimension, subDungeonHeight, firstChildPosition, getSeed());

            Point secondChildPosition = new Point(subDungeonX + splitDimension, subDungeonY);
            subDungeon.secondChild = new Dungeon(subDungeonWidth - splitDimension, subDungeonHeight, secondChildPosition, getSeed());
        }

        return true;
    }

   /* public void createBSP(Dungeon subDungeon) {
        if (subDungeon.isLeaf()) {
            int minRoomSize = subDungeon.getMinDimension();
            int dungeonWidth = subDungeon.getWidth();
            int dungeonHeight = subDungeon.getHeight();
            if (dungeonWidth > 2 * minRoomSize || dungeonHeight > 2 * minRoomSize) {
                if (splitDungeon(minRoomSize, subDungeon, subDungeon.getRNG())) {
                    createBSP(subDungeon.firstChild);
                    createBSP(subDungeon.secondChild);
                }
            } else  {
                return;
            }
        }
        return;
    } */

    public void createRoom(Dungeon subDungeon) {
        if (subDungeon.isLeaf()) {
            int dungeonWidth = subDungeon.getWidth();
            int dungeonHeight = subDungeon.getHeight();
            int dungeonX = subDungeon.getX();
            int dungeonY = subDungeon.getY();
            int roomWidth = dungeonRNG.nextInt(dungeonWidth / 2, dungeonWidth - 2);
            int roomHeight = dungeonRNG.nextInt(dungeonHeight / 2, dungeonHeight - 2);
            int roomX = dungeonRNG.nextInt(1, dungeonWidth - roomWidth - 1);
            int roomY = dungeonRNG.nextInt(1, dungeonHeight - roomHeight - 1);
            Point roomPosition = new Point(dungeonX + roomX, dungeonY + roomY);
            subDungeon.setRoom(new Room(roomWidth, roomHeight, roomPosition));
        } else {
            createRoom(subDungeon.firstChild);
            createRoom(subDungeon.secondChild);
        }
    }

    public Room getRoom(Dungeon subDungeon) {
        if (subDungeon.isLeaf()) {
            return getRoom(subDungeon);
        }

        Room leftRoom = getRoom(subDungeon.firstChild);
        if (leftRoom != null) {
            return leftRoom;
        }

        return getRoom(subDungeon.secondChild);
    }

    public void drawRooms(TETile[][] tiles, Dungeon subDungeon) {
        if (subDungeon == null) {
            return;
        }

        if (subDungeon.isLeaf()) {
            Room dungeonRoom = getRoom(subDungeon);
            int dungeonRoomX = dungeonRoom.getX();
            int dungeonRoomY = dungeonRoom.getY();
            int dungeonRoomWidth = dungeonRoom.getWidth();
            int dungeonRoomHeight = dungeonRoom.getHeight();
            for (int i = dungeonRoomX; i < dungeonRoomX + dungeonRoomWidth; i++) {
                for (int j = dungeonRoomY; j < dungeonRoomY + dungeonRoomHeight; j++) {
                    if (i >= 0 && i < tiles.length && j >= 0 && j < tiles[0].length) {
                        tiles[i][j] = Tileset.SAND;
                    }
                }
            }
        }
        else  {
            drawRooms(tiles, subDungeon.firstChild);
            drawRooms(tiles, subDungeon.firstChild);
        }
    }

    public void generateHallways(Dungeon subDungeon) {
        if (subDungeon == null || subDungeon.isLeaf()) {
            return;
        }

        Room firstRoom = getRoom(subDungeon.firstChild);
        Room secondRoom = getRoom(subDungeon.secondChild);

        // Pick random points inside each room, avoiding walls (offset by +1 and -1)
        int firstPointX = dungeonRNG.nextInt(firstRoom.getX() + 1, firstRoom.getX() + firstRoom.getWidth() - 1);
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

    public void drawHallways(TETile[][] tiles, Dungeon subDungeon) {
        if (subDungeon == null) {
            return;
        }

        drawHallways(tiles, subDungeon.firstChild);
        drawHallways(tiles, subDungeon.secondChild);

        for (Room hallway : subDungeon.getHallways()) {
            for (int i = hallway.getX(); i < hallway.getX() + hallway.getWidth(); i++) {
                for (int j = hallway.getY(); j < hallway.getY() + hallway.getHeight(); j++) {
                    if (i >= 0 && i < tiles.length && j >= 0 && j < tiles[0].length) {
                        tiles[i][j] = Tileset.WATER;
                    }
                }
            }
        }
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getSeed() {
        return seed;
    }

    public Random getRNG() {
        return dungeonRNG;
    }

    public List<Room> getHallways() {
        return hallways;
    }
}
