package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameHandler {
    private static final String SAVE_FILE = "src/save.txt";

    // Dimensions and game state
    private final int gameWidth;
    private final int gameHeight;
    private final TETile[][] gameTiles;
    private Point avatarPosition;
    private final long gameSeed;
    private final Dungeon gameDungeon;
    private final Random gameRNG;

    // Coin dungeon state
    private boolean enterCoinDungeon = false;
    private int coinDungeonWon = 0;

    // Tiles
    private final TETile floor = Tileset.DUNGEON_FLOOR;
    private final TETile wall = Tileset.WALL;
    private final TETile avatar = Tileset.WARRIOR;
    private final TETile portal = Tileset.PORTAL;
    private final TETile coin = Tileset.COIN;

    // Constructor initializes game state from a Dungeon
    public GameHandler(Dungeon game) {
        gameDungeon = game;
        gameTiles = game.getDungeonTiles();
        gameWidth = game.getWidth();
        gameHeight = game.getHeight();
        gameSeed = game.getSeed();
        gameRNG = new Random(gameSeed);
    }

    // Saves basic game state info to file
    public void saveGame() {
        StringBuilder boardState = new StringBuilder();
        boardState.append("width ").append(gameWidth).append("\n");
        boardState.append("height ").append(gameHeight).append("\n");
        boardState.append("seed ").append(gameSeed).append("\n");
        boardState.append("dungeonX ").append(gameDungeon.getX()).append("\n");
        boardState.append("dungeonY ").append(gameDungeon.getY()).append("\n");
        boardState.append("avatarX ").append(avatarPosition.getX()).append("\n");
        boardState.append("avatarY ").append(avatarPosition.getY()).append("\n");

        // Save portal positions
        boardState.append("portals ");
        for (Point p : gameDungeon.getPortals()) {
            boardState.append(p.getX()).append(",").append(p.getY()).append(" ");
        }
        boardState.append("\n");

        FileUtils.writeFile(SAVE_FILE, boardState.toString());
    }

    // Loads dungeon state from saved file and reconstructs a Dungeon instance
    public static Dungeon loadDungeon() {
        String boardState = FileUtils.readFile(SAVE_FILE);
        String[] lines = boardState.split("\n");

        int width = 0, height = 0, dungeonX = 0, dungeonY = 0, avatarX = 0, avatarY = 0;
        long seed = 0;
        Set<Point> portalPositions = new HashSet<>();

        for (String line : lines) {
            if (line.startsWith("width")) {
                width = Integer.parseInt(line.split(" ")[1]);
            } else if (line.startsWith("height")) {
                height = Integer.parseInt(line.split(" ")[1]);
            } else if (line.startsWith("seed")) {
                seed = Long.parseLong(line.split(" ")[1]);
            } else if (line.startsWith("dungeonX")) {
                dungeonX = Integer.parseInt(line.split(" ")[1]);
            } else if (line.startsWith("dungeonY")) {
                dungeonY = Integer.parseInt(line.split(" ")[1]);
            } else if (line.startsWith("avatarX")) {
                avatarX = Integer.parseInt(line.split(" ")[1]);
            } else if (line.startsWith("avatarY")) {
                avatarY = Integer.parseInt(line.split(" ")[1]);
            } else if (line.startsWith("portals")) {
                String[] parts = line.substring(8).trim().split(" ");
                for (String part : parts) {
                    String[] coords = part.split(",");
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    portalPositions.add(new Point(x, y));
                }
            }
        }

        // Rebuild the Dungeon
        Dungeon retDungeon = new Dungeon(width, height, new Point(dungeonX, dungeonY), false, seed);
        retDungeon.placeAvatarManual(new Point(avatarX, avatarY));

        // Assign portals in the Dungeon
        retDungeon.setPortals(portalPositions);

        return retDungeon;
    }

    private Point move(Point pos, int dx, int dy, TETile tile, TETile[][] tiles) {
        int x = pos.getX(), y = pos.getY();
        int newX = x + dx;
        int newY = y + dy;

        if (tiles[newX][newY] == wall) return pos;
        tiles[x][y] = floor;
        tiles[newX][newY] = tile;
        return new Point(newX, newY);
    }

    // Displays a temporary overlay message
    public void displayMessage(String message, Color color, int pauseTime) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 16));
        StdDraw.setPenColor(color);

        String[] lines = message.split("\n");
        double lineHeight = 1.0; // Adjust for spacing
        double startY = gameHeight / 2.0 + (lines.length / 2.0) * lineHeight;

        for (int i = 0; i < lines.length; i++) {
            double y = startY - i * lineHeight;
            StdDraw.text(gameWidth / 2.0, y, lines[i]);
        }

        StdDraw.show();
        StdDraw.pause(pauseTime); // Show for 4 seconds (increase for longer messages)
        StdDraw.clear();
    }

    // Renders a limited field of view around the avatar
    private void viewLineOfSight(TERenderer ter) {
        TETile[][] losTiles = new TETile[gameWidth][gameHeight];
        int losX = Math.max(5, Math.min(gameWidth - 5, avatarPosition.getX()));
        int losY = Math.max(5, Math.min(gameHeight - 5, avatarPosition.getY()));

        for (int i = 0; i < gameWidth; i++) {
            for (int j = 0; j < gameHeight; j++) {
                losTiles[i][j] = Tileset.NOTHING;
            }
        }
        for (int i = losX - 5; i < losX + 5; i++) {
            for (int j = losY - 5; j < losY + 5; j++) {
                losTiles[i][j] = gameTiles[i][j];
            }
        }
        ter.renderFrame(losTiles);
    }

    // Handles the 10-second coin dungeon mini-game
    private void playGameCoinDungeon(TERenderer ter) {
        char c;
        int score = 0;
        Point oldAvatarPosition = avatarPosition;
        avatarPosition = new Point(1, 1);

        // Generate coin dungeon
        TETile[][] cdTiles = new TETile[gameWidth][gameHeight];
        for (int i = 0; i < gameWidth; i++) {
            for (int j = 0; j < gameHeight; j++) {
                cdTiles[i][j] = Tileset.NOTHING;
            }
        }

        // Populate dungeon and place coins
        int placed = 0;
        int coinCount = 10;
        int coinDungeonDimension = 20;
        for (int i = 0; i < coinDungeonDimension; i++) {
            for (int j = 0; j < coinDungeonDimension; j++) {
                if (i == 0 || i == coinDungeonDimension - 1 || j == 0 || j == coinDungeonDimension - 1) {
                    cdTiles[i][j] = wall;
                } else if (placed < coinCount && gameRNG.nextDouble() < 0.1) {
                    cdTiles[i][j] = coin;
                    placed++;
                } else {
                    cdTiles[i][j] = floor;
                }
            }
        }

        long startTime = System.currentTimeMillis();
        long duration = 10_000; // 10 seconds
        int frameDurationMs	 = 16;

        // Coin collection loop
        while (System.currentTimeMillis() - startTime < duration) {
            int avatarX = avatarPosition.getX();
            int avatarY = avatarPosition.getY();

            while (StdDraw.hasNextKeyTyped()) {
                c = Character.toLowerCase(StdDraw.nextKeyTyped());
                int dx = 0, dy = 0;
                switch (c) {
                    case 'w': dy = 1; break;
                    case 'a': dx = -1; break;
                    case 's': dy = -1; break;
                    case 'd': dx = 1; break;
                    case 'q': System.exit(0); break;
                    default: continue;
                }

                int newX = avatarPosition.getX() + dx;
                int newY = avatarPosition.getY() + dy;

                if (cdTiles[newX][newY] == coin) {
                    score++;
                }
                avatarPosition = move(avatarPosition, dx, dy, avatar, cdTiles);
            }

            cdTiles[avatarPosition.getX()][avatarPosition.getY()] = avatar;
            ter.renderFrame(cdTiles);

            try { Thread.sleep(frameDurationMs); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Post-game feedback
        displayMessage(score == coinCount ? "You got them all!" : "So close.... But not enough!", Color.WHITE, 4000);
        if (score == coinCount) coinDungeonWon++;
        avatarPosition = oldAvatarPosition;
    }

    // Main game loop
    public void playGame(TERenderer ter) {
        avatarPosition = gameDungeon.getAvatarPosition();
        boolean colonPressed = false;
        boolean lineOfSight = false;
        int numPortals = 10;

        while (true) {
            int avatarX = avatarPosition.getX();
            int avatarY = avatarPosition.getY();

            while (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());

                int dx = 0, dy = 0;
                boolean moveAttempted = false;

                switch (c) {
                    case ':':
                        colonPressed = true;
                        break;
                    case 'q':
                        if (colonPressed) {
                            saveGame();
                            System.exit(0);
                        }
                        break;
                    case 'w': dy = 1; break;
                    case 'a': dx = -1; break;
                    case 's': dy = -1; break;
                    case 'd': dx = 1; break;
                    case 'l': lineOfSight = !lineOfSight; break;
                    default: break;
                }

                if (c != ':' && c != 'q') {
                    colonPressed = false;
                }

                int newX = avatarX + dx;
                int newY = avatarY + dy;

                if (gameDungeon.getPortals().isEmpty()) {
                    if (coinDungeonWon == numPortals) {
                        displayMessage("Victory! You’ve conquered the dungeon and claimed every coin.", Color.WHITE, 4000);
                    } else {
                        displayMessage("The dungeon devours the unworthy. Better luck next time.", Color.WHITE, 4000);
                    }
                    System.exit(0);
                }

                if (gameTiles[newX][newY] == portal) {
                    gameDungeon.removePortal(new Point(newX, newY));
                    enterCoinDungeon = true;
                }
                avatarPosition = move(avatarPosition, dx, dy, avatar, gameTiles);
            }

            if (enterCoinDungeon) {
                displayMessage("You've entered a coin dungeon and have 10 seconds to collect all the coins.", Color.WHITE, 4000);
                playGameCoinDungeon(ter);
                enterCoinDungeon = false;
            }

            if (lineOfSight) viewLineOfSight(ter);
            else ter.renderFrame(gameTiles);
        }
    }
}
