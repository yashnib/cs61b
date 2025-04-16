package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    private static final int WORLD_WIDTH = 90;
    private static final int WORLD_HEIGHT = 50;
    private static final long INVALID_SEED = -1;
    private static final long LOAD_GAME_FLAG = -2;

    private static final TETile floor = Tileset.DUNGEON_FLOOR;
    private static final TETile wall = Tileset.WALL;
    private static final TETile avatar = Tileset.WARRIOR;
    private static final TETile portal = Tileset.PORTAL;
    private static final TETile coin = Tileset.COIN;

    // Function to get user input
    private static long getUserInput() {
        JFrame mainFrame = new JFrame("Dungeon Generator Menu");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 250);
        mainFrame.setLayout(new BorderLayout());

        AtomicLong seed = new AtomicLong(INVALID_SEED);
        CountDownLatch latch = new CountDownLatch(1);

        JLabel title = new JLabel("CS61B: BYOW", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        mainFrame.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton newGameButton = new JButton("(N) New Game");
        JButton loadGameButton = new JButton("(L) Load Game");
        JButton exitButton = new JButton("(Q) Quit Game");

        buttonPanel.add(newGameButton);
        buttonPanel.add(loadGameButton);
        buttonPanel.add(exitButton);
        mainFrame.add(buttonPanel, BorderLayout.CENTER);

        // Shared method to launch seed input frame
        Runnable showSeedInput = () -> {
            JFrame seedFrame = new JFrame("Enter Seed");
            seedFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            seedFrame.setSize(400, 150);
            seedFrame.setLayout(new BorderLayout());

            JLabel seedLabel = new JLabel("Enter a numeric seed followed by 'S':", SwingConstants.CENTER);
            JTextField inputField = new JTextField();

            inputField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {
                        try {
                            seed.set(Long.parseLong(inputField.getText()));
                            seedFrame.dispose();
                            mainFrame.dispose();
                            latch.countDown();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(seedFrame, "Invalid seed. Use numbers only.");
                        }
                    }
                }
            });

            seedFrame.add(seedLabel, BorderLayout.NORTH);
            seedFrame.add(inputField, BorderLayout.CENTER);
            seedFrame.setVisible(true);
            SwingUtilities.invokeLater(inputField::requestFocusInWindow);
        };

        // Button actions
        newGameButton.addActionListener(e -> showSeedInput.run());
        loadGameButton.addActionListener(e -> {
            seed.set(LOAD_GAME_FLAG);
            mainFrame.dispose();
            latch.countDown();
        });
        exitButton.addActionListener(e -> System.exit(0));

        // Keyboard shortcut support for N, L, Q
        mainFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char c = Character.toLowerCase(e.getKeyChar());
                switch (c) {
                    case 'n':
                        showSeedInput.run();
                        break;
                    case 'l':
                        seed.set(LOAD_GAME_FLAG);
                        mainFrame.dispose();
                        latch.countDown();
                        break;
                    case 'q':
                        System.exit(0);
                        break;
                }
            }
        });

        mainFrame.setFocusable(true);
        mainFrame.requestFocusInWindow();
        mainFrame.setVisible(true);

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return seed.get();
    }

    // Generates the world from the dungeon
    public static void generateWorld(Dungeon inputDungeon) {
        inputDungeon.splitDungeon();
        inputDungeon.createRoom();
        inputDungeon.createHallways();
        inputDungeon.drawRooms();
        inputDungeon.drawHallways();
        inputDungeon.drawWalls();
        inputDungeon.placePortals();
    }

    // Initializes the tiles
    public static void initializeWorld(TETile[][] inputTiles) {
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                inputTiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    // Loads the saved game
    public static void loadGame() {
        TERenderer ter = new TERenderer();
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
        initializeWorld(world);

        Dungeon savedDungeon = GameHandler.loadDungeon();
        savedDungeon.setDungeonTiles(world);
        generateWorld(savedDungeon);

        Point avatarPosition = savedDungeon.getAvatarPosition();
        world[avatarPosition.getX()][avatarPosition.getY()] = avatar;

        GameHandler myGameHandler = new GameHandler(savedDungeon);
        myGameHandler.playGame(ter);
    }

    //
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        initializeWorld(world);

        long seed = getUserInput();

        if (seed == -2) {
            loadGame();
        } else if (seed != -1) {
            ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);
            Point worldPosition = new Point(1, 1);
            Dungeon myDungeon = new Dungeon(WORLD_WIDTH, WORLD_HEIGHT, worldPosition, false, seed);
            myDungeon.setDungeonTiles(world);
            generateWorld(myDungeon);
            myDungeon.placeAvatarRandom();

            GameHandler myGameHandler = new GameHandler(myDungeon);
            myGameHandler.playGame(ter);
        }
    }
}