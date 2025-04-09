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

    private static final WeightedQuickUnionUF connectedRooms = new WeightedQuickUnionUF(500);

    private static Set<Room> intersectingHallwayRoomsStart = new HashSet<>();

    private static Set<Room> intersectingHallwayRoomsEnd = new HashSet<>();

    private static Set<Room> verticalHallwayRoomsStart = new HashSet<>();

    private static Set<Room> verticalHallwayRoomsEnd = new HashSet<>();

    private static Set<Room> horizontalHallwayRoomsStart = new HashSet<>();

    private static Set<Room> horizontalHallwayRoomsEnd = new HashSet<>();


    private static List<Room> hallwaysList = new ArrayList<>();

    private static Set<Room> rooms = new HashSet<>();

    public Dungeon(int width, int height, Point position, boolean isHorizontallySplit, boolean isVerticallySplit, Random rng) {
        super(width, height, position, isHorizontallySplit, isVerticallySplit);

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
        parentDungeon.childA = new Dungeon(parentDungeonWidth, splitY - parentDungeonY, childAPosition, parentDungeon.getHorizontalSplit(), parentDungeon.getVerticalSplit(), childARNG);
        parentDungeon.childA = splitDungeon(parentDungeon.childA);

        Point childBPosition = new Point(parentDungeonX, splitY);
        Random childBRNG = new Random(parentDungeon.dungeonRNG.nextLong());
        parentDungeon.childB = new Dungeon(parentDungeonWidth, parentDungeonY + parentDungeonHeight - splitY, childBPosition, parentDungeon.getHorizontalSplit(), parentDungeon.getVerticalSplit(), childBRNG);
        parentDungeon.childB = splitDungeon(parentDungeon.childB);

        parentDungeon.setVerticalSplit();
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
        parentDungeon.childA = new Dungeon(splitX - parentDungeonX, parentDungeonHeight, childAPosition, parentDungeon.getHorizontalSplit(), parentDungeon.getVerticalSplit(), childARNG);
        parentDungeon.childA = splitDungeon(parentDungeon.childA);

        Point childBPosition = new Point(splitX, parentDungeonY);
        Random childBRNG = new Random(parentDungeon.dungeonRNG.nextLong());
        parentDungeon.childB = new Dungeon(parentDungeonX + parentDungeonWidth - splitX, parentDungeonHeight, childBPosition, parentDungeon.getHorizontalSplit(), parentDungeon.getVerticalSplit(), childBRNG);
        parentDungeon.childB = splitDungeon(parentDungeon.childB);

        parentDungeon.setHorizontalSplit();
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

       // if (parentDungeon.childA != null) {
         //   return getRoom(parentDungeon.childA);
       // }

        return getRoom(parentDungeon.childB);
    }

    public void createRoom(Dungeon parentDungeon) {
        if (parentDungeon == null) {
            return;
        }

        if (parentDungeon.isLeaf()) {
            int roomWidth, roomHeight;
         /*  if (parentDungeon.dungeonRNG.nextDouble() < 0.3) {
                roomWidth = 1;
                roomHeight = 1;
            } else { */
                roomWidth = parentDungeon.getWidth() / 2;
                roomHeight = parentDungeon.getHeight() / 2;
            // }

          //  int roomWidth = 1;
          //  int roomHeight = 1;
            int roomX = parentDungeon.getX();
            int roomY = parentDungeon.getY();
            boolean roomHorizontalSplit = parentDungeon.getHorizontalSplit();
            boolean roomVerticalSplit = parentDungeon.getVerticalSplit();
            Point roomPosition = new Point(roomX, roomY);
            parentDungeon.room = new Room(roomWidth, roomHeight, roomPosition, roomHorizontalSplit, roomVerticalSplit);
            rooms.add(parentDungeon.room);
        } else {
            createRoom(parentDungeon.childA);
            createRoom(parentDungeon.childB);
        }
    }

    public void connectRooms(Room roomA, Room roomB) {

        if (roomA == null || roomB == null) {
            return;
        }

      //  if ((roomA.getWidth() == 1 && (!roomA.getHorizontalSplit() || !roomA.getVerticalSplit())) || (roomB.getWidth() == 1 && (!roomB.getHorizontalSplit() || !roomB.getVerticalSplit()))) {
      //      return;
      //  }

        boolean verticalOverlapStartA, verticalOverlapStartB, horizontalOverlapStartA, horizontalOverlapStartB;

        int roomAX = roomA.getX();
        int roomBX = roomB.getX();
        int roomAY = roomA.getY();
        int roomBY = roomB.getY();

        int roomAWidth = roomA.getWidth();
        int roomAHeight = roomA.getHeight();
        int roomBWidth = roomB.getWidth();
        int roomBHeight = roomB.getHeight();

     verticalOverlapStartA = (roomAY >= roomBY && roomAY <= roomBY + roomBHeight);
     verticalOverlapStartB = (roomBY >= roomAY && roomBY <= roomAY + roomAHeight);
      ////  if (verticalOverlapStartA) {
         //   horizontalHallwayRoomsStart.add(roomA);
        //    horizontalHallwayRoomsEnd.add(roomB);
      //  } else if (verticalOverlapStartB) {
       //     horizontalHallwayRoomsStart.add(roomB);
       //     horizontalHallwayRoomsEnd.add(roomA);
      //  }

       horizontalOverlapStartA = (roomAX >= roomBX && roomAX <= roomBX + roomBWidth);
       horizontalOverlapStartB = (roomBX >= roomAX && roomBX <= roomAX + roomAWidth);

       // if (horizontalOverlapStartA) {
        //    verticalHallwayRoomsStart.add(roomA);
        //    verticalHallwayRoomsEnd.add(roomB);
      //  } else if (horizontalOverlapStartB) {
         //   verticalHallwayRoomsStart.add(roomB);
         //   verticalHallwayRoomsEnd.add(roomA);
       // }

      //  int roomAX = this.dungeonRNG.nextInt(roomAX, roomAX + roomAWidth);
      //  int roomAY = this.dungeonRNG.nextInt(roomAY, roomAY + roomAHeight);
      //  int roomBX = this.dungeonRNG.nextInt(roomBX, roomBX + roomBWidth);
      //  int roomBY = this.dungeonRNG.nextInt(roomBY, roomBY + roomBHeight);

       // int left = Math.min(roomARandomX, roomBRandomX);
      //  int right = Math.max(roomARandomX, roomBRandomX);
      //  int bottom = Math.min(roomARandomY, roomBRandomY);
      //  int top = Math.max(roomARandomY, roomBRandomY);

      int left = Math.min(roomAX, roomBX);
      int right = Math.max(roomAX, roomBX);
      int bottom = Math.min(roomAY, roomBY);
      int top = Math.max(roomAY, roomBY);

      if (horizontalOverlapStartA) {
          Point verticalHallwayPosition;
          if (bottom == roomAY) {
              verticalHallwayRoomsStart.add(roomA);
              verticalHallwayRoomsEnd.add(roomB);
              verticalHallwayPosition = new Point(roomAX, roomAY);

          } else {
            //  verticalHallwayRoomsStart.add(roomB);
            //  verticalHallwayRoomsEnd.add(roomA);
              verticalHallwayPosition = new Point(roomBX, roomBY);
          }
          hallwaysList.add(new Room(1, top - bottom + 1, verticalHallwayPosition, false, false));
      } else if (horizontalOverlapStartB) {
          Point verticalHallwayPosition;
          if (bottom == roomBY) {
              verticalHallwayRoomsStart.add(roomB);
              verticalHallwayRoomsEnd.add(roomA);
              verticalHallwayPosition = new Point(roomBX, roomBY);

          } else {
             //  verticalHallwayRoomsStart.add(roomA);
             //  verticalHallwayRoomsEnd.add(roomB);
              verticalHallwayPosition = new Point(roomAX, roomAY);
          }
          hallwaysList.add(new Room(1, top - bottom + 1, verticalHallwayPosition, false, false));
      }

      if (verticalOverlapStartA) {
          Point horizontalHallwayPosition;
          if (left == roomAX) {
              horizontalHallwayRoomsStart.add(roomA);
              horizontalHallwayRoomsEnd.add(roomB);
              horizontalHallwayPosition = new Point(roomAX, roomAY);
          } else {
             // horizontalHallwayRoomsStart.add(roomB);
             // horizontalHallwayRoomsEnd.add(roomA);
              horizontalHallwayPosition = new Point(roomBX, roomBY);
          }
          hallwaysList.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false, false));
      } else if (verticalOverlapStartB) {
          Point horizontalHallwayPosition;
          if (left == roomBX) {
              horizontalHallwayRoomsStart.add(roomB);
              horizontalHallwayRoomsEnd.add(roomA);
              horizontalHallwayPosition = new Point(roomBX, roomBY);
          } else {
             // horizontalHallwayRoomsStart.add(roomA);
             // horizontalHallwayRoomsEnd.add(roomB);
              horizontalHallwayPosition = new Point(roomAX, roomAY);
          }
          hallwaysList.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false, false));
      }
     // if (verticalOverlapStartA) {
        //   int randomYPosition = this.dungeonRNG.nextInt(roomBY, roomBY + roomBHeight);
          // Point horizontalHallwayPosition = new Point(left, bottom);
          // Point horizontalHallwayPosition = (left == roomAX) ? new Point(roomAX, roomAY) : new Point(roomAY, roomBY);
          // hallwaysList.add(new Room(right - left,1, horizontalHallwayPosition, false, false));
         //  horizontalHallwayPositions.add(horizontalHallwayPosition);
      //  } else if (verticalOverlapStartB) {
         //  int randomYPosition = this.dungeonRNG.nextInt(roomAY, roomAY + roomAHeight);
          // Point horizontalHallwayPosition = new Point(left, roomBY);
        //   Point horizontalHallwayPosition = (left == roomAX) ? new Point(roomAX, roomAY) : new Point(roomBX, roomBY);
        //   hallwaysList.add(new Room(right - left + 1,1, horizontalHallwayPosition, false, false));
         //  horizontalHallwayPositions.add(horizontalHallwayPosition);
      //  }

    //  if (horizontalOverlapStartA) {
         //   int randomXPosition = this.dungeonRNG.nextInt(roomBX, roomBX + roomBWidth);
           // Point verticalHallwayPosition = new Point(roomAX, bottom);
           //Point verticalHallwayPosition = (bottom == roomAY) ? new Point(roomAX, roomAY) : new Point(roomBX, roomBY);
          //  hallwaysList.add(new Room( 1, top - bottom, verticalHallwayPosition, false, false));
         //   verticalHallwayPositions.add(verticalHallwayPosition);
      //  } else if (horizontalOverlapStartB) {
      //     // int randomXPosition = this.dungeonRNG.nextInt(roomAX, roomAX + roomAWidth);
            // Point verticalHallwayPosition = new Point(roomBX, bottom);
       //    Point verticalHallwayPosition = (bottom == roomAY) ? new Point(roomAX, roomAY) : new Point(roomBX, roomBY);
       //     hallwaysList.add(new Room(1, top - bottom + 1, verticalHallwayPosition, false, false));
           // verticalHallwayPositions.add(verticalHallwayPosition);
 //   }
       //else
         //  if (this.dungeonRNG.nextDouble() < 0.4) {
           //    Point horizontalHallwayPosition = new Point(left, bottom);
           //     hallwaysList.add(new Room(right - left + 1,1, horizontalHallwayPosition, false, false));
              //  for (int i = left; i <= right; i++) {
                   // if (tiles[i][bottom + 1] != Tileset.FLOWER || tiles[i][bottom - 1] != Tileset.FLOWER) {
             //           tiles[i][bottom] = Tileset.FLOWER;
               //         roomA.setHorizontalHallway();
              //          roomB.setHorizontalHallway();
                   // }
             //   }
          //   Point verticalHallwayPosition = new Point(right, bottom);
          //    hallwaysList.add(new Room(1, top - bottom + 1, verticalHallwayPosition, false, false));
              //  for (int i = bottom; i <= top; i++) {
                  //  if (tiles[right + 1][i] != Tileset.FLOWER || tiles[right - 1][i] != Tileset.FLOWER) {
                   //     tiles[right][i] = Tileset.FLOWER;
                  //      roomA.setVerticalHallway();
                  //      roomB.setVerticalHallway();
                  //  }
              //  }
              // hallwayIntersectionPoints.add(verticalHallwayPosition);
        /* else {
               Point verticalHallwayPosition = new Point(left, bottom);
               hallwaysList.add(new Room(1, top - bottom + 1, verticalHallwayPosition, false, false));
              //  for (int i = bottom; i <= top; i++) {
                   // if (tiles[left + 1][i] != Tileset.FLOWER || tiles[left - 1][i] != Tileset.FLOWER) {
                 //       tiles[left][i] = Tileset.FLOWER;
                   //     roomA.setVerticalHallway();
                   //     roomB.setVerticalHallway();
                   // }
              //  }
               Point horizontalHallwayPosition = new Point(left, top);
               hallwaysList.add(new Room(right - left + 1, 1, horizontalHallwayPosition, false, false));
               // for (int i = left; i <= right; i++) {
                  //  if (tiles[i][top + 1] != Tileset.FLOWER || tiles[i][top - 1] != Tileset.FLOWER) {
                   //     tiles[i][top] = Tileset.FLOWER;
                   //     roomA.setHorizontalHallway();
                   //     roomB.setHorizontalHallway();
                  //  }
             //   }
              // hallwayIntersectionPoints.add(horizontalHallwayPosition);
            } */

    }

    public List<Room> getHallwaysList() {
        return hallwaysList;
    }


    public void createLshapedHallways() {
        for (Room r : rooms) {
            if (intersectingHallwayRoomsStart.contains(r)) {
                r.makeIntersection();
            }
            else if (intersectingHallwayRoomsEnd.contains(r)) {
                r.makeIntersection();
            }
        }
    }

    public void getIntersectingHallwayRooms() {
        intersectingHallwayRoomsStart = horizontalHallwayRoomsStart;
        intersectingHallwayRoomsStart.retainAll(verticalHallwayRoomsStart);
        intersectingHallwayRoomsEnd = horizontalHallwayRoomsEnd;
        intersectingHallwayRoomsEnd.retainAll(verticalHallwayRoomsEnd);
    }

    public void drawHallways(TETile[][] tiles) {
        for (Room r : hallwaysList) {
            for (int i = r.getX(); i < r.getX() + r.getWidth(); i++) {
                for (int j = r.getY(); j < r.getY() + r.getHeight(); j++) {
                    tiles[i][j] = Tileset.FLOWER;
                }
            }
        }
    }

   public void drawRooms(TETile[][] tiles) {
        for (Room r : rooms) {
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

       // if (!(childARoom.getWidth() == 1 && (!childARoom.getHorizontalSplit() || !childARoom.getVerticalSplit())) || !(childBRoom.getWidth() == 1 && (!childBRoom.getHorizontalSplit() || !childBRoom.getVerticalSplit()))) {
            connectRooms(childARoom, childBRoom);
        //  }

        createHallways(parentDungeon.childA);
        createHallways(parentDungeon.childB);

    }

  /* public void drawRoom(TETile[][] tiles, Dungeon parentDungeon) {
        if (parentDungeon == null) {
            return;
        }

        if (parentDungeon.isLeaf() && parentDungeon.room != null) {
            int roomLeft = parentDungeon.room.getX();
            int roomRight = roomLeft + parentDungeon.room.getWidth();
            int roomBottom = parentDungeon.room.getY();
            int roomTop = roomBottom + parentDungeon.room.getHeight();
            for (int i = roomLeft; i < roomRight; i++) {
                for (int j = roomBottom; j < roomTop; j++) {
                    if (i >= 0 && i < tiles.length && j >=0 && j < tiles[0].length) {
                        tiles[i][j] = Tileset.FLOWER;
                    }
                }
            }
        }
        drawRoom(tiles, parentDungeon.childA);
        drawRoom(tiles, parentDungeon.childB);
    } */

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

