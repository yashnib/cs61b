package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;

public class GameHandler {
    private static final String SAVE_FILE = "src/save.txt";

    private int gameWidth;
    private int gameHeight;
    private final TETile[][] gameTiles;
    private Point avatarPosition;
    private long gameSeed;
    private Dungeon gameDungeon;

    public GameHandler(Dungeon game) {
        gameDungeon = game;
        gameTiles = game.getDungeonTiles();
        gameWidth = game.getWidth();
        gameHeight = game.getHeight();
        gameSeed = game.getSeed();
}
/**
 * Saves the state of the current state of the board into the
 * save.txt file (make sure it's saved into this specific file).
 * 0 represents NOTHING, 1 represents a CELL.
 */
public void saveGame() {
    StringBuilder boardState = new StringBuilder(gameWidth + " " + gameHeight + " " + gameSeed + " " + gameDungeon.getX() + " " + gameDungeon.getY() + " " + avatarPosition.getX() + " " + avatarPosition.getY() + "\n");

  //  for (int x = gameHeight - 1; x >= 0; x--) {
      //  for (int y = 0; y < gameWidth; y++) {
       //     int tileState = gameTiles[y][x].equals(Tileset.AVATAR) ? 3 : gameTiles[y][x].equals(Tileset.FLOWER) ? 2 : gameTiles[y][x].equals(Tileset.WALL) ? 1 : 0;
       //     boardState.append(tileState);
     //   }
    //    boardState.append("\n");
  //  }

    FileUtils.writeFile(SAVE_FILE, boardState.toString());
}

/**
 * Loads the board from filename and returns it in a 2D TETile array.
 * 0 represents NOTHING, 1 represents a CELL.
 */

public static Dungeon loadDungeon() {
    String filename = SAVE_FILE;
    File file = new File(SAVE_FILE);
    Dungeon retDungeon;

    // if (!FileUtils.fileExists(SAVE_FILE)) {
    //     throw new FileNotFoundException("loadBoard(): " + SAVE_FILE + " not found");
    //  };

    String boardState = FileUtils.readFile(filename);
    String[] fileLines = boardState.split("\n");

    String[] boardFeatures = fileLines[0].split(" ");
    int width = Integer.parseInt(boardFeatures[0]);
    int height = Integer.parseInt(boardFeatures[1]);
    long seed = Long.parseLong(boardFeatures[2]);
    int gameX = Integer.parseInt(boardFeatures[3]);
    int gameY = Integer.parseInt(boardFeatures[4]);
    int avatarX = Integer.parseInt(boardFeatures[5]);
    int avatarY = Integer.parseInt(boardFeatures[6]);

    //   if (width != gameWidth || height != gameHeight) {
    //       throw new IllegalArgumentException("loadBoard(): Loaded board dimensions don't match");
    //   }

    Point dungeonPosition = new Point(gameX, gameY);
    retDungeon = new Dungeon(width, height, dungeonPosition, false, seed);

    Point avatarPosition = new Point(avatarX, avatarY);
    retDungeon.placeAvatarManual(avatarPosition);
    //retDungeon.setDungeonTiles(loadTiles());
    return retDungeon;
}

public static TETile[][] loadTiles() {
    String filename = SAVE_FILE;
    File file = new File(SAVE_FILE);
    Dungeon retDungeon;

   // if (!FileUtils.fileExists(SAVE_FILE)) {
   //     throw new FileNotFoundException("loadBoard(): " + SAVE_FILE + " not found");
  //  };

    String boardState = FileUtils.readFile(filename);
    String[] fileLines = boardState.split("\n");

    String[] boardFeatures = fileLines[0].split(" ");
    int width = Integer.parseInt(boardFeatures[0]);
    int height = Integer.parseInt(boardFeatures[1]);

 //   if (width != gameWidth || height != gameHeight) {
 //       throw new IllegalArgumentException("loadBoard(): Loaded board dimensions don't match");
 //   }

    TETile[][] board = new TETile[width][height];

    for (int x = 0; x < height; x++) {
        String[] boardRow = fileLines[x + 1].split("");
        for (int y = 0; y < width; y++) {
            int boardRowVal = Integer.parseInt(boardRow[y]);
            board[y][x] = boardRowVal == 3 ? Tileset.AVATAR : boardRowVal == 2 ? Tileset.FLOWER : boardRowVal == 1 ? Tileset.WALL : Tileset.NOTHING;
        }
    }

    return board;
}

private Point moveUp(Point curPosition) {
    int curX = curPosition.getX();
    int curY = curPosition.getY();
    if (gameTiles[curX][curY + 1] == Tileset.WALL) {
        return curPosition;
    }

    gameTiles[curX][curY] = Tileset.FLOWER;
    gameTiles[curX][curY + 1] = Tileset.AVATAR;
    return new Point(curX, curY + 1);
}

private Point moveLeft(Point curPosition) {
    int curX = curPosition.getX();
    int curY = curPosition.getY();
    if (gameTiles[curX - 1][curY] == Tileset.WALL) {
        return curPosition;
    }
    gameTiles[curX][curY] = Tileset.FLOWER;
    gameTiles[curX - 1][curY] = Tileset.AVATAR;
    return new Point(curX - 1, curY);
}

private Point moveDown(Point curPosition) {
    int curX = curPosition.getX();
    int curY = curPosition.getY();
    if (gameTiles[curX][curY - 1] == Tileset.WALL) {
        return curPosition;
    }
    gameTiles[curX][curY] = Tileset.FLOWER;
    gameTiles[curX][curY - 1] = Tileset.AVATAR;
    return new Point(curX, curY - 1);
}

private Point moveRight(Point curPosition) {
    int curX = curPosition.getX();
    int curY = curPosition.getY();
    if (gameTiles[curX + 1][curY] == Tileset.WALL) {
        return curPosition;
    }
    gameTiles[curX][curY] = Tileset.FLOWER;
    gameTiles[curX + 1][curY] = Tileset.AVATAR;
    return new Point(curX + 1, curY);
}

public void playGame(Point curPosition, TERenderer ter) {
    char c; // Variable for saving the most recent character typed by the user.
    avatarPosition = curPosition;

    // This outer infinite-loop allows the game to continue indefinitely, until the user quits.
    boolean colonPressed = false;
    while (true) {

        // hasNextKeyTyped checks if the user has typed a key that we haven't processed.
        // This loop runs until all unprocessed keys are processed.
        // If there are no unprocessed keys, we go back to the outer infinite loop to wait for the next key.
        while (StdDraw.hasNextKeyTyped()) {

            // nextKeyTyped returns the next key to process.
            // Always check hasNextKeyTyped before calling nextKeyTyped.
            // If you call nextKeyTyped and there's no key to process, code will crash!
            c = StdDraw.nextKeyTyped();

            c = Character.toLowerCase(c);


            // Switch statements can be useful to replace long if-else statements!
            switch (c) {
                case ':':
                    colonPressed = true;
                    break;
                case 'q':  // Closes the game window and quits the game.
                    if (colonPressed) {
                        saveGame();
                        colonPressed = false;
                        System.exit(0);
                    }
                    break;
                case 'w':
                    avatarPosition = moveUp(avatarPosition);
                    colonPressed = false;
                    break;
                case 'a':
                    avatarPosition = moveLeft(avatarPosition);
                    colonPressed = false;
                    break;
                case 's':
                    avatarPosition = moveDown(avatarPosition);
                    colonPressed = false;
                    break;
                case 'd':
                    avatarPosition = moveRight(avatarPosition);
                    colonPressed = false;
                    break;
                default:
                    break;
            }

        }

        // Draws the world to the screen.
        // This is in the while(true) loop, because we want to frequently re-render the world.
        ter.renderFrame(gameTiles);
    }
}

}