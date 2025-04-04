package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import core.Dungeon;
import java.util.*;

public class Main {
    private static final int WORLD_WIDTH = 100;
    private static final int WORLD_HEIGHT = 50;
    private static final long SEED = 2873123;

    public static void main(String[] args) {

        TERenderer ter = new TERenderer();
        ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];

        Random rng = new Random(SEED);
        Dungeon myDungeon = new Dungeon(0, WORLD_WIDTH,  WORLD_HEIGHT, 0, rng);
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        myDungeon.splitDungeon();
        myDungeon.trim();
        myDungeon.drawDungeon(world);

        ter.renderFrame(world);
    }
}
