package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import core.Dungeon;
import java.util.*;

public class Main {
    private static final int WORLD_WIDTH = 80;
    private static final int WORLD_HEIGHT = 40;
    private static final int SEED = 2873123;

    public static void main(String[] args) {

        TERenderer ter = new TERenderer();
        ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];

        Point myDungeonPosition = new Point(0, 0);
        Dungeon myDungeon = new Dungeon(WORLD_WIDTH, WORLD_HEIGHT, myDungeonPosition, SEED);
        myDungeon.createBSP(myDungeon);
        myDungeon.createRoom(myDungeon);

        myDungeon.generateHallways(myDungeon);
       myDungeon.drawRooms(world, myDungeon);
      myDungeon.drawHallways(world, myDungeon);

        ter.renderFrame(world);
    }
}
