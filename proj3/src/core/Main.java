package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import core.Dungeon;
import java.util.*;

public class Main {
    private static final int WORLD_WIDTH = 100;
    private static final int WORLD_HEIGHT = 50;
    private static final long SEED = 34786;

    public static void main(String[] args) {

        TERenderer ter = new TERenderer();
        ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];

        Random rng = new Random(SEED);
        Point worldPosition = new Point(1, 1);
        Dungeon myDungeon = new Dungeon( WORLD_WIDTH,  WORLD_HEIGHT, worldPosition, false, rng);
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        myDungeon.splitDungeon(myDungeon);
        myDungeon.createRoom(myDungeon);
        myDungeon.createHallways(myDungeon);
        myDungeon.drawRooms(world);
        myDungeon.drawHallways(world);
        myDungeon.drawWalls(world);

        ter.renderFrame(world);
    }
}
